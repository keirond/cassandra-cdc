package vn.baodh.cassandra_cdc.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "cassandra.cdc")
public class CDCConfiguration {

    private String logPath;

}
