package com.fujitsu.fnc.vta.cassandra_cdc.core;

import org.apache.cassandra.db.commitlog.CommitLogReadHandler;
import org.apache.cassandra.db.commitlog.CommitLogReader;
import org.apache.cassandra.io.util.File;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CDCLogReader {

    private final Logger log = LogManager.getLogger(this.getClass());

    private final CommitLogReader reader;

    private final CommitLogReadHandler handler;

    public CDCLogReader(CommitLogReadHandler handler) {
        this.reader  = new CommitLogReader();
        this.handler = handler;
    }

    // TODO: CommitLogPosition position
    public void read(File file) {
        try {
            reader.readCommitLogSegment(handler, file, false);
        } catch (IOException e) {
            log.error("[reader] error when reading cdc log segment: {}", file.absolutePath());
        }
    }

}
