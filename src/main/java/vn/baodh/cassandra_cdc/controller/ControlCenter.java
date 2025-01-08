package vn.baodh.cassandra_cdc.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.baodh.cassandra_cdc.core.CDCLogReader;
import vn.baodh.cassandra_cdc.model.Alarm;
import vn.baodh.cassandra_cdc.repository.AlarmRepository;

import java.nio.file.Paths;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/control")
public class ControlCenter {

    private final CDCLogReader reader;

    private final AlarmRepository alarmRepository;

    //    @PostMapping("/v1/watcher/off")
    //    @Operation(summary = "Turn off cdc directory watcher")
    //    public ResponseEntity<?> turnOffWatcher() {
    //        log.info("[control] turn off cdc directory watcher");
    //        watcher.stop();
    //        return ResponseEntity.ok(true);
    //    }

    //    @PostMapping("/v1/watcher/resume")
    //    @Operation(summary = "Resume cdc directory watcher")
    //    public ResponseEntity<?> resumeWatcher() {
    //        log.info("[control] resume cdc directory watcher");
    //        watcher.resume();
    //        return ResponseEntity.ok(true);
    //    }

    @PostMapping("/v1/watcher/read")
    @Operation(summary = "Read specified log file")
    public ResponseEntity<?> readLogFile(
            @RequestBody
            String path) {
        log.info("[control] read specified log file: {}", path);
        reader.read(Paths.get(path));
        return ResponseEntity.ok(true);
    }

    @PostMapping("/v1/testCreate")
    @Operation(summary = "Read specified log file")
    public ResponseEntity<?> testCreate(
            @RequestBody
            String id) {

        Alarm alarm = new Alarm();
        alarm.setId(id);
        alarm.setType("Error");
        alarm.setCategory("System");
        alarm.setSeverity("severity");
        alarm.setName("Test Insert Alarm ");
        alarm.setDescription("Description ");
        alarm.setRawContent("Raw content Test Insert for dummy alarm ");
        alarm.setLocation("Server Room");
        alarm.setDirection("Inbound");
        alarm.setRaisedAt("2024-06-25T10:00:00Z");
        alarm.setReceivedAt("2024-06-25T10:00:10Z");
        alarm.setReRaisedAt("2024-06-25T10:01:00Z");
        alarm.setReReceivedAt("2024-06-25T10:01:10Z");
        alarm.setPriority("priority"); // Random priority
        alarm.setMemo("Memo for Test Insert dummy alarm ");
        alarm.setAcknowledged(true);
        alarm.setAcknowledgedAt("2024-06-25T10:05:00Z");
        alarm.setAcknowledgedById("acknowledgedbyid");
        alarm.setAcknowledgedByName("John Doe");
        alarm.setCleared(true);
        alarm.setClearedAt("2024-06-25T10:10:00Z");
        alarm.setLastOperationCmt("Cleared by operator");
        alarm.setLastOperationByName("Jane Smith");
        alarm.setLastOperationById("lastoperationbyid");
        alarm.setLastOperationAt("2024-06-25T10:15:00Z");
        alarm.setLastOperation("Clear Alarm Test Insert ");
        alarm.setPluginName("Plugin A");
        alarm.setPluginId("pluginid");
        alarm.setDuration("600");
        alarm.setArchived(false);
        alarm.setSuppressed(false);
        alarm.setMasked(false);
        alarm.setMaskedUntil("Mask Until");
        alarm.setMaintenanceNameList(List.of("charSequenceNameList"));
        alarm.setMaintenanceIdList(List.of("charSequenceIdList"));
        alarm.setInMaintenance(false);
        alarm.setUngroupPolicy("None");
        alarm.setAllowManualUngroup(true);
        alarm.setClearedById("clearedbyid");
        alarm.setClearedByName("Emma Brown");
        alarm.setOwnerType("OwnerType1");
        alarm.setOwnerId("123456789");
        alarm.setOwnerName("Owner Name");
        alarm.setNodeId("10e0496a-1641-418a-98c1-c1581c4f04c6");
        alarm.setNodeName("Node Name Test Insert ");
        alarm.setNodeDescription("Node Description Test Insert ");
        alarm.setNodeType("Node Type");
        alarm.setNodeVendorName("Vendor Name Test Insert ");
        alarm.setNodeProductName("Product Name Test Insert ");
        alarm.setNodeGroupId("10e0496a-1641-418a-98c1-c1581c4f04c1");
        alarm.setNodeGroupName("Group Name Test Insert ");
        alarm.setEntityId("entityid");
        alarm.setEntityName("Entity Name Test Insert ");
        alarm.setEntityDescription("Entity Description ");
        alarm.setServiceAffecting(false);
        alarm.setServiceId("serviceid");
        alarm.setServiceName("Service Name");
        alarm.setServiceDescription("Service Description ");
        alarm.setServiceType("Service Type Test Insert ");
        alarm.setLinkId("linkid");
        alarm.setLinkName("Link Name Test Insert ");
        alarm.setLinkDescription("Link Description Test Insert ");
        alarm.setCorrelationPolicyId("correlationpolicyid");
        alarm.setCorrelationPolicyName("Policy Name Test Insert ");
        alarm.setGroupOwner(false);
        alarm.setGroupMember(false);
        alarm.setGroupOwnerId("groupownerid");
        alarm.setExternalId("ExternalId ");
        alarmRepository.save(alarm);
        return ResponseEntity.ok(true);
    }

    @PostMapping("/v1/testUpdate")
    @Operation(summary = "Read specified log file")
    public ResponseEntity<?> testUpdate(
            @RequestParam
            String id,
            @RequestParam
            String name) {

        Alarm alarm = new Alarm();
        alarm.setId(id);
        alarm.setType("Error");
        alarm.setCategory("System");
        alarm.setSeverity("severity");
        alarm.setName(name);
        alarm.setDescription("Description ");
        alarm.setRawContent("Raw content Test Insert for dummy alarm ");
        alarm.setLocation("Server Room");
        alarm.setDirection("Inbound");
        alarm.setRaisedAt("2024-06-25T10:00:00Z");
        alarm.setReceivedAt("2024-06-25T10:00:10Z");
        alarm.setReRaisedAt("2024-06-25T10:01:00Z");
        alarm.setReReceivedAt("2024-06-25T10:01:10Z");
        alarm.setPriority("priority"); // Random priority
        alarm.setMemo("Memo for Test Insert dummy alarm ");
        alarm.setAcknowledged(true);
        alarm.setAcknowledgedAt("2024-06-25T10:05:00Z");
        alarm.setAcknowledgedById("acknowledgedbyid");
        alarm.setAcknowledgedByName("John Doe");
        alarm.setCleared(true);
        alarm.setClearedAt("2024-06-25T10:10:00Z");
        alarm.setLastOperationCmt("Cleared by operator");
        alarm.setLastOperationByName("Jane Smith");
        alarm.setLastOperationById("lastoperationbyid");
        alarm.setLastOperationAt("2024-06-25T10:15:00Z");
        alarm.setLastOperation("Clear Alarm Test Insert ");
        alarm.setPluginName("Plugin A");
        alarm.setPluginId("pluginid");
        alarm.setDuration("600");
        alarm.setArchived(false);
        alarm.setSuppressed(false);
        alarm.setMasked(false);
        alarm.setMaskedUntil("Mask Until");
        alarm.setMaintenanceNameList(List.of("charSequenceNameList"));
        alarm.setMaintenanceIdList(List.of("charSequenceIdList"));
        alarm.setInMaintenance(false);
        alarm.setUngroupPolicy("None");
        alarm.setAllowManualUngroup(true);
        alarm.setClearedById("clearedbyid");
        alarm.setClearedByName("Emma Brown");
        alarm.setOwnerType("OwnerType1");
        alarm.setOwnerId("123456789");
        alarm.setOwnerName("Owner Name");
        alarm.setNodeId("10e0496a-1641-418a-98c1-c1581c4f04c6");
        alarm.setNodeName("Node Name Test Insert ");
        alarm.setNodeDescription("Node Description Test Insert ");
        alarm.setNodeType("Node Type");
        alarm.setNodeVendorName("Vendor Name Test Insert ");
        alarm.setNodeProductName("Product Name Test Insert ");
        alarm.setNodeGroupId("10e0496a-1641-418a-98c1-c1581c4f04c1");
        alarm.setNodeGroupName("Group Name Test Insert ");
        alarm.setEntityId("entityid");
        alarm.setEntityName("Entity Name Test Insert ");
        alarm.setEntityDescription("Entity Description ");
        alarm.setServiceAffecting(false);
        alarm.setServiceId("serviceid");
        alarm.setServiceName("Service Name");
        alarm.setServiceDescription("Service Description ");
        alarm.setServiceType("Service Type Test Insert ");
        alarm.setLinkId("linkid");
        alarm.setLinkName("Link Name Test Insert ");
        alarm.setLinkDescription("Link Description Test Insert ");
        alarm.setCorrelationPolicyId("correlationpolicyid");
        alarm.setCorrelationPolicyName("Policy Name Test Insert ");
        alarm.setGroupOwner(false);
        alarm.setGroupMember(false);
        alarm.setGroupOwnerId("groupownerid");
        alarm.setExternalId("ExternalId ");
        alarmRepository.save(alarm);
        return ResponseEntity.ok(true);
    }

}
