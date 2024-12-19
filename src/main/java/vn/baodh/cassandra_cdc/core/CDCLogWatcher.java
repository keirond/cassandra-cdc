package vn.baodh.cassandra_cdc.core;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.cassandra.config.Config;
import org.apache.cassandra.config.DatabaseDescriptor;
import org.apache.cassandra.schema.Schema;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CDCLogWatcher {

    private final Logger log = LogManager.getLogger(this.getClass());

    @PostConstruct
    public void init() {
        System.setProperty(Config.PROPERTY_PREFIX + "config.loader", CDCConfigLoader.class.getName());
        DatabaseDescriptor.toolInitialization();

        Schema.instance.loadFromDisk();

        log.info(Schema.instance.getKeyspaces().toString());
    }
}
