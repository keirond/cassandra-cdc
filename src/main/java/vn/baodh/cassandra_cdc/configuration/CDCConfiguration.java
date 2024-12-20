package vn.baodh.cassandra_cdc.configuration;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.apache.cassandra.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import vn.baodh.cassandra_cdc.core.CDCConfigLoader;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "cassandra.cdc")
public class CDCConfiguration {

    private String configClassLoader = CDCConfigLoader.class.getName();

    private String logPath;

    @PostConstruct
    public void init() {
        System.setProperty(Config.PROPERTY_PREFIX + "config.loader", configClassLoader);
    }

}
