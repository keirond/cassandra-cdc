package vn.baodh.cassandra_cdc.model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;
import org.springframework.lang.NonNull;

import java.util.Collections;
import java.util.List;

/**
 * Represents an alarm entity in the alarm management system.
 * This class maps to the "alarm" table in Cassandra and includes various attributes
 * related to the alarm such as its ID, type, severity, and timestamps indicating when
 * the alarm was raised, received, acknowledged, and cleared.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table("alarm")
@ToString
public class Alarm {

    @PrimaryKeyColumn(name = "id", type = PrimaryKeyType.PARTITIONED)
    @NonNull
    private String id;
    @PrimaryKeyColumn(name="raisedat",ordinal =0, type=PrimaryKeyType.CLUSTERED)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private String raisedAt;
    private String nodeId;
    private String externalId;
    @NonNull
    private String type;
    @NonNull
    private String category;
    @NonNull
    private String severity;
    @NonNull
    private String name;
    @NonNull
    private String description;
    @NonNull
    private String rawContent;
    private String location;
    private String direction;

    @NonNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private String receivedAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private String reRaisedAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private String reReceivedAt;
    @NonNull
    private String priority;
    private String memo;
    @NonNull
    private boolean acknowledged;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private String acknowledgedAt;
    private String acknowledgedById;
    private String acknowledgedByName;
    @NonNull
    private boolean cleared;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private String clearedAt;
    private String lastOperationCmt;
    private String lastOperationByName;
    private String lastOperationById;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private String lastOperationAt;
    private String lastOperation;
    @NonNull
    private String pluginName;
    @NonNull
    private String pluginId;
    @NonNull
    private String duration;
    @NonNull
    private boolean archived;
    @NonNull
    private boolean suppressed;
    @NonNull
    private boolean masked;
    private String maskedUntil;
    private List<String> maintenanceNameList = Collections.emptyList();
    private List<String> maintenanceIdList = Collections.emptyList();
    @NonNull
    private boolean inMaintenance;
    private String ungroupPolicy;
    private boolean allowManualUngroup;
    private String clearedById;
    private String clearedByName;
    private String ownerType;
    private String ownerId;
    private String ownerName;
    @NonNull
    private String nodeName;
    @NonNull
    private String nodeDescription;
    @NonNull
    private String nodeType;
    @NonNull
    private String nodeVendorName;
    @NonNull
    private String nodeProductName;
    @NonNull
    private String nodeGroupId;
    @NonNull
    private String nodeGroupName;
    @NonNull
    private String entityId;
    @NonNull
    private String entityName;
    @NonNull
    private String entityDescription;
    @NonNull
    private boolean serviceAffecting;
    private String serviceId;
    private String serviceName;
    private String serviceDescription;
    private String serviceType;
    private String linkId;
    private String linkName;
    private String linkDescription;
    private String correlationPolicyId;
    private String correlationPolicyName;
    @NonNull
    private boolean groupOwner;
    @NonNull
    private boolean groupMember;
    private String groupOwnerId;
    private String customAttributes;
}