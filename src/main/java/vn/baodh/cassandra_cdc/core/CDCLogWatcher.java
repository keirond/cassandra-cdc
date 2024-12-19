package vn.baodh.cassandra_cdc.core;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.cassandra.config.Config;
import org.apache.cassandra.config.DatabaseDescriptor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CDCLogWatcher {

    @PostConstruct
    public void init() {
        System.setProperty(Config.PROPERTY_PREFIX + "config.loader", CDCConfigLoader.class.getName());
        DatabaseDescriptor.toolInitialization();
    }
}
