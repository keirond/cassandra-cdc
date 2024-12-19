import vn.baodh.cassandra_cdc.core.CDCConfigLoader;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class LabTest {

    @Test
    void test() {
        log.info(String.valueOf(CDCConfigLoader.class.getName()));
    }
}
