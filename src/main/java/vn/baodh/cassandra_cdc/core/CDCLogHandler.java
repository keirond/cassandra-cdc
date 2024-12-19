package vn.baodh.cassandra_cdc.core;

import vn.baodh.cassandra_cdc.mutation.MutationInitiator;
import org.apache.cassandra.config.DatabaseDescriptor;
import org.apache.cassandra.db.Mutation;
import org.apache.cassandra.db.commitlog.CommitLogDescriptor;
import org.apache.cassandra.db.commitlog.CommitLogReadHandler;
import org.apache.cassandra.utils.FBUtilities;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.Future;

@Component
public class CDCLogHandler implements CommitLogReadHandler {

    private final Logger log = LogManager.getLogger(this.getClass());

    private static final long MAX_OUTSTANDING_CDC_BYTES = 1024 * 1024 * 64;

    private static final int MAX_OUTSTANDING_CDC_COUNT = 1024;

    private final MutationInitiator mutationInitiator;

    private boolean sawCDCMutation;

    private long pendingMutationBytes;

    private final Queue<Future<Integer>> futures;

    public CDCLogHandler(MutationInitiator mutationInitiator) {
        this.mutationInitiator = mutationInitiator;

        this.sawCDCMutation       = false;
        this.pendingMutationBytes = 0;
        this.futures              = new ArrayDeque<>();
    }

    /**
     * Handle an error during segment read, signaling whether you want the reader to skip the
     * remainder of the current segment on error.
     *
     * @param exception CommitLogReadException w/details on exception state
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
     * @param exception CommitLogReadException w/details on exception state
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
        if (DatabaseDescriptor.isCDCEnabled() && m.trackedByCDC()) sawCDCMutation = true;
        log.info("[handler] handling mutation: {}", m);

        pendingMutationBytes += size;
        futures.offer(mutationInitiator.initiateMutation(m, desc.id, size, entryLocation, this));

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
