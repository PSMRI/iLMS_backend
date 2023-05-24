package org.legal.web.model;

import javax.validation.constraints.NotNull;
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

    @NotNull
    @JsonProperty("tenantId")
    private String tenantId;

    @JsonProperty("caseId")
    private String caseId;

    @NotNull
    @JsonProperty("orderType")
    private String orderType;

    @NotNull
    @JsonProperty("orderDate")
    private Long orderDate;

    @NotNull
    @JsonProperty("decisionStatus")
    private String decisionStatus;

    @NotNull
    @JsonProperty("complianceDate")
    private Long complianceDate;

    @NotNull
    @JsonProperty("revisedComplianceDate")
    private Long revisedComplianceDate;

    @JsonProperty("orderNoOverride")
    private String orderNoOverride;

    @JsonProperty("revisedComplainceReason")
    private String revisedComplainceReason;

    @NotNull
    @JsonProperty("complianceStatus")
    private String complianceStatus;

    @JsonProperty("applicationStatus")
    private String applicationStatus;

    @JsonProperty("remarks")
    private String remarks;

    @NotNull
    @JsonProperty("status")
    private Status status;

    @JsonProperty("additionalDetails")
    private Object additionalDetails;

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;

}
