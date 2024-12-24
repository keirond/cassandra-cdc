package vn.baodh.cassandra_cdc.messaging.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;
import vn.baodh.cassandra_cdc.messaging.config.ProducerKafkaConfiguration;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static java.lang.Math.abs;

@Slf4j
@Service
public class StandardProducer {

    private static final int SENDER_COUNT = 3;

    private final List<KafkaProducer<String, String>> producers;

    private final AtomicInteger next = new AtomicInteger(0);

    public StandardProducer(ProducerKafkaConfiguration producerConfiguration) {
        var config = producerConfiguration.getProducerConfig();
        this.producers = IntStream.range(0, SENDER_COUNT)
                                 .mapToObj(i -> new KafkaProducer<String, String>(config)).toList();
    }

    public void send(String topic, String value) {
        send(topic, null, value);
    }

    public void send(String topic, String key, String value) {
        send(topic, Map.of(), key, value);
    }

    public void send(String topic, Map<String, String> headers, String key, String value) {
        var pRecord = new ProducerRecord<>(topic, key, value);
        headers.forEach((k, v) -> pRecord.headers().add(k, v.getBytes(StandardCharsets.UTF_8)));
        getProducer().send(pRecord, (metadata, ex) -> {
            if (ex != null) {
                log.error("Error while producing message: key {}, value {}", key, value, ex);
            } else {
                log.info("Message sent to topic {}, partition {}, offset {}, key {}, value {}",
                        metadata.topic(), metadata.partition(), metadata.offset(), key, value);
            }
        });
    }

    public KafkaProducer<String, String> getProducer() {
        return producers.get(abs(next.incrementAndGet() % SENDER_COUNT));
    }

}