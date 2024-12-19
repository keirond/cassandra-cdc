package com.fujitsu.fnc.vta.cassandra_cdc.core;

import com.fujitsu.fnc.vta.cassandra_cdc.common.JsonMapper;
import org.apache.cassandra.db.Mutation;
import org.apache.cassandra.db.commitlog.CommitLogDescriptor;
import org.apache.cassandra.db.commitlog.CommitLogReadHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CDCLogReadHandler implements CommitLogReadHandler {

    private final Logger log = LogManager.getLogger(this.getClass());

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
    public boolean shouldSkipSegmentOnError(CommitLogReadException exception) throws IOException {
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
    public void handleUnrecoverableError(CommitLogReadException exception) throws IOException {

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
        log.info(
                "[read handler] handling mutation: {}, size: {}, entryLocation: {}, descriptor: {}",
                JsonMapper.toJson(m), size, entryLocation, JsonMapper.toJson(desc));
    }

}
