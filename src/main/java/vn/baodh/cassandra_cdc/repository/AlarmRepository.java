package vn.baodh.cassandra_cdc.repository;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import vn.baodh.cassandra_cdc.model.Alarm;

import java.util.Optional;

public interface AlarmRepository extends CassandraRepository<Alarm, String> {

    @Query("SELECT * FROM alarm WHERE id = ?0")
    Optional<Alarm> findByAlarmId(String id);

}
