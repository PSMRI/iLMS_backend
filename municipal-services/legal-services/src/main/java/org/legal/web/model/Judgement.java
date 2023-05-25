package org.legal.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.legal.web.model.enums.Status;
import org.legal.web.model.workflow.ProcessInstance;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Judgement {
    
    @JsonProperty("id")
    private String id;

    @JsonProperty("tenantId")
    private String tenantId;

    @JsonProperty("caseId")
    private String caseId;

    @JsonProperty("orderType")
    private String orderType;

    @JsonProperty("orderDate")
    private Long orderDate;

    @JsonProperty("decisionStatus")
    private String decisionStatus;

    @JsonProperty("complianceDate")
    private Long complianceDate;

    @JsonProperty("revisedComplianceDate")
    private Long revisedComplianceDate;

    @JsonProperty("orderNoOverride")
    private String orderNoOverride;

    @JsonProperty("revisedComplainceReason")
    private String revisedComplainceReason;

    @JsonProperty("complianceStatus")
    private String complianceStatus;

    @JsonProperty("applicationStatus")
    private String applicationStatus;

    @JsonProperty("remarks")
    private String remarks;

    @JsonProperty("status")
    private Status status;

    @JsonProperty("additionalDetails")
    private Object additionalDetails;

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;

}
