package com.fujitsu.fnc.vta.cassandra_cdc.controller;

import com.fujitsu.fnc.vta.cassandra_cdc.core.CDCLogReader;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.apache.cassandra.io.util.File;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/control")
public class ControlCenter {

    private final Logger log = LogManager.getLogger(this.getClass());

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
            Path path) {
        log.info("[control] read specified log file");
        reader.read(new File(path));
        return ResponseEntity.ok(true);
    }

}
