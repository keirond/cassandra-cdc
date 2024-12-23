package vn.baodh.cassandra_cdc.core;

import lombok.extern.slf4j.Slf4j;
import org.apache.cassandra.config.Config;
import org.apache.cassandra.config.ConfigurationLoader;
import org.apache.cassandra.config.DatabaseDescriptor;
import org.apache.cassandra.exceptions.ConfigurationException;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.File;
import java.io.IOException;
import java.net.URL;

@Slf4j
@Component
public class CDCConfigLoader implements ConfigurationLoader {

    private static final String DEFAULT_CONFIGURATION = "cassandra.yaml";

    private URL storageConfigURL;

    private URL getStorageConfigURL() throws ConfigurationException {
        var configUrl = System.getProperty("cassandra.config");
        if (configUrl == null) configUrl = DEFAULT_CONFIGURATION;

        URL url;
        try {
            url = new URL(configUrl);
            url.openStream().close(); // catches well-formed but bogus URLs
        } catch (Exception e) {
            var loader = DatabaseDescriptor.class.getClassLoader();
            url = loader.getResource(configUrl);
            if (url == null) {
                String required = getString(configUrl);
                throw new ConfigurationException("Cannot locate " + configUrl +
                                                         ".  If this is a local file, please confirm you've provided " +
                                                         required + File.separator +
                                                         " as a URI prefix.");
            }
        }

        log.info("Configuration location: {}", url);

        return url;
    }

    private String getString(String configUrl) {
        var required = "file:" + File.separator + File.separator;
        if (!configUrl.startsWith(required)) throw new ConfigurationException(String.format(
                "Expecting URI in variable: [cassandra.config]. Found[%s]. Please prefix the file with [%s%s] for local " +
                        "files and [%s<server>%s] for remote files. If you are executing this from an external tool, it needs " +
                        "to set Config.setClientMode(true) to avoid loading configuration.",
                configUrl, required, File.separator, required, File.separator));
        return required;
    }

    @Override
    public Config loadConfig() throws ConfigurationException {
        if (storageConfigURL == null) storageConfigURL = getStorageConfigURL();
        return loadConfig(storageConfigURL);
    }

    public Config loadConfig(URL url) throws ConfigurationException {
        try {
            log.debug("Loading settings from {}", url);
            var yaml = new Yaml();
            return yaml.loadAs(url.openStream(), Config.class);
        } catch (IOException | YAMLException ex) {
            throw new ConfigurationException("Invalid yaml: " + url + " Error: " + ex.getMessage(),
                    false);
        }
    }

}
