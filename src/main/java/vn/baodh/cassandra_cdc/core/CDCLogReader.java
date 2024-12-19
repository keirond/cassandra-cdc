package vn.baodh.cassandra_cdc.core;

import org.apache.cassandra.db.commitlog.CommitLogReadHandler;
import org.apache.cassandra.db.commitlog.CommitLogReader;
import org.apache.cassandra.io.util.File;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;

@Component
public class CDCLogReader {

    private final Logger log = LogManager.getLogger(this.getClass());

    private final AtomicBoolean running;

    private final CommitLogReader reader;

    private final CommitLogReadHandler handler;

    public CDCLogReader(CommitLogReadHandler handler) {
        this.running = new AtomicBoolean(false);
        this.reader  = new CommitLogReader();
        this.handler = handler;
    }

    public void watch(Path cdcDirectory) {
        try (var watchService = cdcDirectory.getFileSystem().newWatchService()) {
            var watchKey = cdcDirectory.register(watchService, ENTRY_CREATE);

            log.info("[reader] watching for directory: {}, watch key: {}", cdcDirectory, watchKey);
            running.set(true);
            while (running.get()) {
                var key = watchService.take();
                for (var event : key.pollEvents()) {
                    var kind = event.kind();
                    if (kind != ENTRY_CREATE) continue;

                    var relativePath = (Path) event.context();
                    var absolutePath = cdcDirectory.resolve(relativePath);
                    read(absolutePath);
                    Files.deleteIfExists(absolutePath);
                }
                watchKey.reset();
            }
        } catch (IOException | InterruptedException ex) {
            log.error("[reader] error when watching cdc log directory: {}", cdcDirectory, ex);
        } finally {
            log.info("[reader] stop watching for directory: {}", cdcDirectory);
            running.set(false);
        }
    }

    public void watch(File file) {}

    // TODO: CommitLogPosition position
    public void read(Path absolutePath) {
        try {
            var file = new File(absolutePath);
            reader.readCommitLogSegment(handler, file, false);
        } catch (IOException ex) {
            log.error("[reader] error when reading cdc log segment: {}", absolutePath, ex);
        } finally {
            if (!reader.getInvalidMutations().isEmpty()) log.error("[reader] invalid mutations: {}",
                    reader.getInvalidMutations());
        }
    }

}
