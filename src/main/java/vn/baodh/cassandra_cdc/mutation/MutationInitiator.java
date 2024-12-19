package vn.baodh.cassandra_cdc.mutation;

import vn.baodh.cassandra_cdc.core.CDCLogHandler;
import org.apache.cassandra.concurrent.Stage;
import org.apache.cassandra.db.Keyspace;
import org.apache.cassandra.db.Mutation;
import org.apache.cassandra.db.commitlog.CommitLogPosition;
import org.apache.cassandra.schema.Schema;
import org.apache.cassandra.utils.WrappedRunnable;
import org.springframework.stereotype.Component;

import java.util.concurrent.Future;

@Component
public class MutationInitiator {

    public Future<Integer> initiateMutation(Mutation m, long segmentId, int size,
            int entryLocation, CDCLogHandler handler) {
        var runnable = new WrappedRunnable() {
            @Override
            protected void runMayThrow() {
                if (Schema.instance.getKeyspaceInstance(m.getKeyspaceName()) == null) return;
                var keyspace = Keyspace.open(m.getKeyspaceName());

                Mutation.PartitionUpdateCollector newPUCollector = null;
                for (var update : m.getPartitionUpdates()) { // TODO filter mutation
                    if (Schema.instance.getTableMetadata(update.metadata().id) == null) continue;

                    var position = new CommitLogPosition(segmentId, entryLocation);
                    if (newPUCollector != null) {

                        // throw AssertionError if the collector is empty.
                        assert !newPUCollector.isEmpty();

                        Keyspace.open(newPUCollector.getKeyspaceName())
                                .apply(newPUCollector.build(), false, true, false);

                    }
                }
            }
        };

        return Stage.MUTATION.submit(runnable, size);
    }

}
