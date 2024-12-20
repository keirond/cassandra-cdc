package vn.baodh.cassandra_cdc.mutation;

import lombok.extern.slf4j.Slf4j;
import org.apache.cassandra.concurrent.Stage;
import org.apache.cassandra.db.Keyspace;
import org.apache.cassandra.db.Mutation;
import org.apache.cassandra.db.commitlog.CommitLogPosition;
import org.apache.cassandra.db.commitlog.IntervalSet;
import org.apache.cassandra.db.rows.Cell;
import org.apache.cassandra.db.rows.ColumnData;
import org.apache.cassandra.schema.Schema;
import org.apache.cassandra.schema.TableId;
import org.apache.cassandra.utils.WrappedRunnable;
import org.springframework.stereotype.Component;
import vn.baodh.cassandra_cdc.core.CDCLogHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class MutationInitiator {


    public Future<Integer> initiateMutation(Mutation m, long segmentId, int size, int entryLocation, CDCLogHandler handler) {
        var runnable = new WrappedRunnable() {
            @Override
            protected void runMayThrow() {
                var finalizedData = new HashMap<String, List<Map<String, Object>>>();
                log.info("[mutation] handling mutation: {}", m);


                final var keyspace = Keyspace.open(m.getKeyspaceName());
                if (Schema.instance.getKeyspaceInstance(m.getKeyspaceName()) == null) {
                    log.error("[mutation] keyspace {} is not found or not loaded", m.getKeyspaceName());
                    return;
                }

//                Mutation.PartitionUpdateCollector newPUCollector = null;
                for (var update : m.getPartitionUpdates()) { // TODO filter mutation
                    log.info("[mutation] handling update: {}, table_id: {}", update, update.metadata().id);

                    if (Schema.instance.getTableMetadata(update.metadata().id) == null) {
                        log.error("[mutation] table {} is not found or not loaded", update.metadata().id);
                        continue; // dropped
                    }

                    var position = new CommitLogPosition(segmentId, entryLocation);
                    if (handler.shouldRead(update.metadata().id, position)) {

//                        if (newPUCollector == null) newPUCollector =
//                                                            new Mutation.PartitionUpdateCollector(
//                                                                    m.getKeyspaceName(), m.key());

//                        newPUCollector.add(update);
                        log.info("[mutation] handling this update: {}, at the position: {}", update, position);
                        log.info("[testing] update: {}, columns: {}", update, update.metadata().columns());
                        for (var row: update) {
                            log.info("[testing] row: {}", row);
                            for (var cd : row) {
                                var col = cd.column();
                                log.info("[testing] column: {}", cd.column().isSimple());
                                visitCell((Cell) cd);
                            }
                        }
                        handler.increaseReadCount();
                    }

//                    if (newPUCollector != null)
//                    {
//                        assert !newPUCollector.isEmpty();
//
//                        Keyspace.open(newPUCollector.getKeyspaceName()).apply(newPUCollector.build(), false, true, false);
//                        handler.keyspacesReplayed.add(keyspace);
//                    }

                }

            }
        };
        return Stage.MUTATION.submit(runnable, size);
    }

    public void visitCell(Cell cell) {

    }


}
