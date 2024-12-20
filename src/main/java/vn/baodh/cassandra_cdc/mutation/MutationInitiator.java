package vn.baodh.cassandra_cdc.mutation;

import lombok.extern.slf4j.Slf4j;
import org.apache.cassandra.concurrent.Stage;
import org.apache.cassandra.config.DatabaseDescriptor;
import org.apache.cassandra.db.Keyspace;
import org.apache.cassandra.db.Mutation;
import org.apache.cassandra.db.commitlog.CommitLogPosition;
import org.apache.cassandra.schema.Schema;
import org.apache.cassandra.utils.WrappedRunnable;
import org.springframework.stereotype.Component;
import vn.baodh.cassandra_cdc.common.JsonMapper;
import vn.baodh.cassandra_cdc.core.CDCLogHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

@Slf4j
@Component
public class MutationInitiator {

    public Future<Integer> initiateMutation(Mutation m, long segmentId, int size, int entryLocation,
            CDCLogHandler handler) {
        var runnable = new WrappedRunnable() {
            @Override
            protected void runMayThrow() {
                var finalizedData = new ArrayList<Map<String, Object>>();

                Keyspace.open(m.getKeyspaceName());
                if (Schema.instance.getKeyspaceInstance(m.getKeyspaceName()) == null) {
                    log.error("[mutation] keyspace {} is not found or not loaded",
                            m.getKeyspaceName());
                    return;
                }

                log.info("[testing] {}", m);

                for (var update : m.getPartitionUpdates()) { // TODO filter mutation
                    if (Schema.instance.getTableMetadata(update.metadata().id) == null) {
                        log.error("[mutation] table {} is not found or not loaded",
                                update.metadata().id);
                        continue; // dropped
                    }

                    var position = new CommitLogPosition(segmentId, entryLocation);
                    if (handler.shouldRead(update.metadata().id, position)) {
                        log.info("[mutation] handling this update: {}, at the position: {}", update.metadata().id,
                                position);

                        for (var row : update) {
                            var map = new HashMap<String, Object>();
                            for (var cell : row.cells()) {
                                var col = cell.column();
                                map.put("keyspace", col.ksName);
                                map.put("table", col.cfName);
                                map.put(col.name.toString(),
                                        col.type.getSerializer().deserialize(cell.buffer()));
                            }
                            finalizedData.add(map);
                        }
                        handler.increaseReadCount();
                    }
                }

                log.info("[mutation] handled mutation data: {}", JsonMapper.toJson(finalizedData));

            }
        };
        return Stage.MUTATION.submit(runnable, size);
    }

}
