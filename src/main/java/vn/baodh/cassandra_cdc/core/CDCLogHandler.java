package vn.baodh.cassandra_cdc.core;

import lombok.extern.slf4j.Slf4j;
import org.apache.cassandra.config.DatabaseDescriptor;
import org.apache.cassandra.db.Mutation;
import org.apache.cassandra.db.commitlog.CommitLogDescriptor;
import org.apache.cassandra.db.commitlog.CommitLogPosition;
import org.apache.cassandra.db.commitlog.CommitLogReadHandler;
import org.apache.cassandra.db.commitlog.IntervalSet;
import org.apache.cassandra.schema.TableId;
import org.apache.cassandra.utils.FBUtilities;
import org.springframework.stereotype.Component;
import vn.baodh.cassandra_cdc.mutation.MutationParser;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class CDCLogHandler implements CommitLogReadHandler {

    private static final long MAX_OUTSTANDING_CDC_BYTES = 1024 * 1024 * 64;

    private static final int MAX_OUTSTANDING_CDC_COUNT = 1024;

    private final MutationParser mutationParser;

    private long pendingMutationBytes;

    private final Queue<Future<Integer>> futures;

    private final Map<TableId, IntervalSet<CommitLogPosition>> clpPersisted;

    private final AtomicInteger readCount;

    public CDCLogHandler(MutationParser mutationParser) {
        this.mutationParser = mutationParser;

        this.pendingMutationBytes = 0;
        this.futures              = new ArrayDeque<>();
        this.clpPersisted         = new HashMap<>();
        this.readCount            = new AtomicInteger(0);
    }

    /**
     * Handle an error during segment read, signaling whether you want the reader to skip the
     * remainder of the current segment on error.
     *
     * @param ex CommitLogReadException w/details on exception state
     *
     * @return boolean indicating whether to stop reading
     *
     * @exception IOException In the event the handler wants forceful termination of all processing,
     *                        throw IOException.
     */
    @Override
    public boolean shouldSkipSegmentOnError(CommitLogReadException ex) throws IOException {
        if (ex.permissible) log.error(
                "[handler] ignoring cdc log error likely due to incomplete flush to disk", ex);
        else log.error("[handler] ignoring cdc log error", ex);
        return false;
    }

    /**
     * In instances where we cannot recover from a specific error and don't care what the reader
     * thinks
     *
     * @param ex CommitLogReadException w/details on exception state
     *
     * @exception IOException
     */
    @Override
    public void handleUnrecoverableError(CommitLogReadException ex) throws IOException {
        // Don't care about return value, use this simply to throw exception as appropriate.
        shouldSkipSegmentOnError(ex);
    }

    /**
     * Process a deserialized mutation
     *
     * @param m             deserialized mutation
     * @param size          serialized size of the mutation
     * @param entryLocation filePointer offset inside the CommitLogSegment for the end of the
     *                      record
     * @param desc          CommitLogDescriptor for mutation being processed
     */
    @Override
    public void handleMutation(Mutation m, int size, int entryLocation, CommitLogDescriptor desc) {
        if (DatabaseDescriptor.isCDCEnabled() && m.trackedByCDC()) {
            pendingMutationBytes += size;
            futures.offer(mutationParser.handleMutation(m, desc.id, size, entryLocation, this));

            // If there are finished mutations, or too many outstanding bytes/mutations,
            // drain the futures in the queue
            while (futures.size() > MAX_OUTSTANDING_CDC_COUNT ||
                           pendingMutationBytes > MAX_OUTSTANDING_CDC_BYTES ||
                           (!futures.isEmpty() && futures.peek().isDone())) {
                var future = futures.poll();
                // Even if !futures.isEmpty() evaluates to true, futures.poll() could still return null because:
                // The element was removed, and another thread may have cleared it or marked it as complete.
                if (future != null) pendingMutationBytes -= FBUtilities.waitOnFuture(future);
            }
        }
    }

    public boolean shouldRead(TableId id, CommitLogPosition position) {
        if (!clpPersisted.containsKey(id) || !clpPersisted.get(id).contains(position)) {
            clpPersisted.put(id, new IntervalSet<>(CommitLogPosition.NONE, position));
            if (CDCLogReader.commitLogPosition.compareTo(position) < 0) {
                CDCLogReader.commitLogPosition =
                        new CommitLogPosition(position.segmentId, position.position + 1);
            }
            return true;
        }
        return false;
    }

    public void increaseReadCount() {
        readCount.incrementAndGet();
    }

}
