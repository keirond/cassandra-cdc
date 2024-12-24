package vn.baodh.cassandra_cdc.messaging.config;

import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration class for Kafka producer settings.
 * <p>
 * This class defines properties for configuring Kafka producers and provides a utility method to
 * generate a configuration map compatible with Kafka's Producer API.
 * <p>
 * Properties are loaded from the application's configuration file with the prefix
 * <code>messaging.kafka.client.producer</code>.
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "messaging.client.producer")
@SuppressWarnings("java:S1068")
public class ProducerKafkaConfiguration {

    /**
     * <code>bootstrap.servers</code>
     * <p>
     * A list of host/port pairs to use for establishing the initial connection to the Kafka
     * cluster.
     */
    private String bootstrapServers = "localhost:9092";

    /**
     * <code>acks</code>
     * <p>
     * The number of acknowledgments the producer requires the leader to have received before
     * considering a request complete.
     */
    private String acks = "all";

    /**
     * <code>linger.ms</code>
     * <p>
     * The producer groups together any records that arrive in between request transmissions into a
     * single batched request.
     */
    private long lingerMs = 5;

    /**
     * <code>retries</code>
     * <p>
     * Setting a value greater than zero will cause the client to resend any record whose send fails
     * with a potentially transient error.
     */
    private int retries = 3;

    /**
     * <code>retry.backoff.ms</code>
     * <p>
     * The amount of time to wait before attempting to retry a failed request to a given topic
     * partition. This avoids repeatedly sending requests in a tight loop under some failure
     * scenarios.
     */
    private long retryBackoffMs = 100;

    /**
     * <code>enable.idempotence</code>
     * <p>
     * When set to ‘true’, the producer will ensure that exactly one copy of each message is written
     * in the stream.
     */
    private boolean enableIdempotence = true;

    public Map<String, Object> getProducerConfig() {
        var config = new HashMap<String, Object>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.ACKS_CONFIG, acks);

        config.put(ProducerConfig.LINGER_MS_CONFIG, lingerMs);

        config.put(ProducerConfig.RETRIES_CONFIG, retries);
        config.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, retryBackoffMs);
        config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, enableIdempotence);

        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return config;
    }

}