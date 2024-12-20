package vn.baodh.cassandra_cdc.core;

import io.micrometer.common.util.StringUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.cassandra.config.Config;
import org.apache.cassandra.config.DatabaseDescriptor;
import org.apache.cassandra.schema.Schema;
import org.springframework.stereotype.Component;
import vn.baodh.cassandra_cdc.configuration.CDCConfiguration;

import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
@Component
@RequiredArgsConstructor
public class CDCLogWatcher {

    private final CDCConfiguration configuration;

    private final CDCLogReader reader;

    @PostConstruct
    public void init() {
        DatabaseDescriptor.toolInitialization();

        if (!DatabaseDescriptor.isCDCEnabled()) {
            log.error(
                    "[watcher] CDC is not enabled for this node. Enable CDC by editing cassandra.yaml and restarting the node.");
            return;
        }

        Schema.instance.loadFromDisk();

        //        log.info(Schema.instance.getKeyspaces().toString());

        var path = configuration.getLogPath();
        var cdcLocation = StringUtils.isEmpty(path) ? Paths.get(DatabaseDescriptor.getCDCLogLocation())
                                         : Paths.get(path);
        if (Files.notExists(cdcLocation)) {
            log.error("[watcher] CDC log location doesn't exist: {}", cdcLocation);
            return;
        }

        if (Files.isDirectory(cdcLocation)) reader.watch(cdcLocation);
        else reader.read(cdcLocation);
    }

}
