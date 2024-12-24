package vn.baodh.cassandra_cdc.mutation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.cassandra.concurrent.Stage;
import org.apache.cassandra.db.Keyspace;
import org.apache.cassandra.db.Mutation;
import org.apache.cassandra.db.commitlog.CommitLogPosition;
import org.apache.cassandra.db.marshal.UTF8Type;
import org.apache.cassandra.schema.Schema;
import org.apache.cassandra.utils.WrappedRunnable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import vn.baodh.cassandra_cdc.common.JsonMapper;
import vn.baodh.cassandra_cdc.configuration.CDCConfiguration;
import vn.baodh.cassandra_cdc.core.CDCLogHandler;
import vn.baodh.cassandra_cdc.messaging.producer.StandardProducer;

import java.util.HashMap;
import java.util.concurrent.Future;

@Slf4j
@Component
@RequiredArgsConstructor
public class MutationParser {

    private final CDCConfiguration cdcConfiguration;

    private final StandardProducer standardProducer;

    @Value("${messaging.producers.internal.alarm-data.topic}")
    private String alarmTopic;

    public Future<Integer> handleMutation(Mutation m, long segmentId, int size, int entryLocation,
            CDCLogHandler handler) {
        var runnable = new WrappedRunnable() {
            @Override
            protected void runMayThrow() {
                Keyspace.open(m.getKeyspaceName());
                if (Schema.instance.getKeyspaceInstance(m.getKeyspaceName()) == null) {
                    log.error("[mutation] keyspace {} is not found or not loaded",
                            m.getKeyspaceName());
                    return;
                }

                log.debug("[testing] {}", m);

                for (var update : m.getPartitionUpdates()) { // TODO filter mutation
                    var tableMetadata = Schema.instance.getTableMetadata(update.metadata().id);
                    if (tableMetadata == null) {
                        log.error("[mutation] table {} is not found or not loaded",
                                update.metadata().id);
                        continue; // dropped
                    }
                    if (!cdcConfiguration.getTableIncludes().contains(tableMetadata.name)) {
                        continue; // skipped
                    }
                    var position = new CommitLogPosition(segmentId, entryLocation);
                    if (handler.shouldRead(update.metadata().id, position)) {
                        log.info("[mutation] handling an update: {}, at position: {}",
                                update.metadata().id, position);

                        for (var row : update) {
                            var map = new HashMap<String, Object>();
                            map.put("keyspace", tableMetadata.keyspace);
                            map.put("table", tableMetadata.name);
                            map.put("partition_key",
                                    UTF8Type.instance.compose(update.partitionKey().getKey()));
                            map.put("clustering_key", row.clustering().toString(update.metadata()));
                            for (var cell : row.cells()) {
                                var col = cell.column();
                                map.put(col.name.toString(),
                                        col.type.getSerializer().deserialize(cell.buffer()));
                            }
                            var message = JsonMapper.toJson(map);
                            standardProducer.send(alarmTopic, message);
                            log.info("[mutation] handled mutation data: {}", message);
                        }
                        handler.increaseReadCount();
                    }
                }

            }
        };
        return Stage.MUTATION.submit(runnable, size);
    }

}
