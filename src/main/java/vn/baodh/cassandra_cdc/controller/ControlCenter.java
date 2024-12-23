package vn.baodh.cassandra_cdc.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.baodh.cassandra_cdc.core.CDCLogReader;

import java.nio.file.Paths;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/control")
public class ControlCenter {

    private final CDCLogReader reader;

    //
    //    @PostMapping("/v1/watcher/off")
    //    @Operation(summary = "Turn off cdc directory watcher")
    //    public ResponseEntity<?> turnOffWatcher() {
    //        log.info("[control] turn off cdc directory watcher");
    //        watcher.stop();
    //        return ResponseEntity.ok(true);
    //    }
    //
    //    @PostMapping("/v1/watcher/resume")
    //    @Operation(summary = "Resume cdc directory watcher")
    //    public ResponseEntity<?> resumeWatcher() {
    //        log.info("[control] resume cdc directory watcher");
    //        watcher.resume();
    //        return ResponseEntity.ok(true);
    //    }
    //
    @PostMapping("/v1/watcher/read")
    @Operation(summary = "Read specified log file")
    public ResponseEntity<?> readLogFile(
            @RequestBody
            String path) {
        log.info("[control] read specified log file: {}", path);
        reader.read(Paths.get(path));
        return ResponseEntity.ok(true);
    }

}
