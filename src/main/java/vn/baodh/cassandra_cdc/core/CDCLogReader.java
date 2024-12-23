package vn.baodh.cassandra_cdc.core;

import lombok.extern.slf4j.Slf4j;
import org.apache.cassandra.db.commitlog.CommitLogDescriptor;
import org.apache.cassandra.db.commitlog.CommitLogPosition;
import org.apache.cassandra.db.commitlog.CommitLogReadHandler;
import org.apache.cassandra.db.commitlog.CommitLogReader;
import org.apache.cassandra.io.util.File;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

@Slf4j
@Component
public class CDCLogReader {

    private final AtomicBoolean running;

    private final CommitLogReader reader;

    private final CommitLogReadHandler handler;

    public static CommitLogPosition commitLogPosition = CommitLogPosition.NONE;

    public CDCLogReader(CDCLogHandler handler) {
        this.running = new AtomicBoolean(false);
        this.reader  = new CommitLogReader();
        this.handler = handler;
    }

    public void watch(Path cdcDirectory) {
        try (var watchService = cdcDirectory.getFileSystem().newWatchService()) {
            var watchKey = cdcDirectory.register(watchService, ENTRY_MODIFY);

            log.info("[reader] watching for directory: {}, watch key: {}", cdcDirectory, watchKey);
            running.set(true);
            while (running.get()) {
                var key = watchService.take();
                for (var event : key.pollEvents()) {
                    var kind = event.kind();
                    log.info("[reader] watch event context: {}, kind: {}", event.context(), kind);
                    if (kind != ENTRY_MODIFY) continue;
                    var relativePath = (Path) event.context();
                    var absolutePath = cdcDirectory.resolve(relativePath).toString()
                                               .replace("_cdc.idx", ".log");
                    var file = new File(absolutePath);
                    if (CommitLogDescriptor.isValid(file.name()) && Files.exists(file.toPath())) {
                        read(file);
                    } else {
                        log.error("[reader] cdc log file {} is not exists", absolutePath);
                    }
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

    public void read(Path absolutePath) {
        read(new File(absolutePath));
    }

    public void read(File file) {
        try {
            reader.readCommitLogSegment(handler, file, commitLogPosition, false);
        } catch (IOException ex) {
            log.error("[reader] error when reading cdc log segment: {}", file.absolutePath(), ex);
        } finally {
            if (!reader.getInvalidMutations().isEmpty()) log.error("[reader] invalid mutations: {}",
                    reader.getInvalidMutations());
        }
    }

}
