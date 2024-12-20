package vn.baodh.cassandra_cdc.mutation;

import lombok.extern.slf4j.Slf4j;
import org.apache.cassandra.concurrent.Stage;
import org.apache.cassandra.db.Keyspace;
import org.apache.cassandra.db.Mutation;
import org.apache.cassandra.db.commitlog.CommitLogPosition;
import org.apache.cassandra.schema.Schema;
import org.apache.cassandra.utils.WrappedRunnable;
import org.springframework.stereotype.Component;
import vn.baodh.cassandra_cdc.core.CDCLogHandler;

import java.util.concurrent.Future;

@Slf4j
@Component
public class MutationInitiator {

    public Future<Integer> initiateMutation(Mutation m, long segmentId, int size, int entryLocation,
            CDCLogHandler handler) {
        var runnable = new WrappedRunnable() {
            @Override
            protected void runMayThrow() {
                log.info("[mutation] handling mutation: {}", m);

                log.info("[testing] {}", Schema.instance.getKeyspaces());

                if (Schema.instance.getKeyspaceInstance(m.getKeyspaceName()) == null) {
                    log.error("[mutation] keyspace {} is not found or not loaded",
                            m.getKeyspaceName());
                    return;
                }
                final var keyspace = Keyspace.open(m.getKeyspaceName());
                //
                //                Mutation.PartitionUpdateCollector newPUCollector = null;
                for (var update : m.getPartitionUpdates()) { // TODO filter mutation
                    log.info("[mutation] handling update: {}", update);
                    if (Schema.instance.getTableMetadata(update.metadata().id) == null) {
                        log.error("[mutation] table {} is not found or not loaded",
                                update.metadata().id);
                        continue; // dropped
                    }

                    var position = new CommitLogPosition(segmentId, entryLocation);

                    log.info("[mutation] handling at position: {}", position);
                }

            }
        };
        return Stage.MUTATION.submit(runnable, size);
    }

}
