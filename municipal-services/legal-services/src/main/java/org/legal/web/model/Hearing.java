package org.legal.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.legal.web.model.enums.Status;
import org.legal.web.model.workflow.ProcessInstance;
import java.util.List;
import javax.validation.constraints.NotNull;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Hearing {
    @JsonProperty("id")
    private String id;

    @NotNull
    @JsonProperty("tenantId")
    private String tenantId;

    @JsonProperty("hearingNumber")
    private String hearingNumber;

    @NotNull
    @JsonProperty("courtNumber")
    private String courtNumber;

    @NotNull
    @JsonProperty("bench")
    private String bench;

    @JsonProperty("caseId")
    private String caseId;

    @JsonProperty("judgeName")
    private JsonNode judgeName;

    @NotNull
    @JsonProperty("hearingDate")
    private Long hearingDate;

    @NotNull
    @JsonProperty("businessDate")
    private Long businessDate;

    @JsonProperty("hearingPurpose")
    private String hearingPurpose;

    @JsonProperty("applicationStatus")
    private String applicationStatus;

    @JsonProperty("requiredOfficer")
    private String requiredOfficer;

    @NotNull
    @JsonProperty("affidavitFilingDate")
    private Long affidavitFilingDate;

    @NotNull
    @JsonProperty("affidavitFilingDueDate")
    private Long affidavitFilingDueDate;

    @NotNull
    @JsonProperty("caseNumber")
    private String caseNumber;

    @JsonProperty("oathNumber")
    private String oathNumber;

    @JsonProperty("nextHearingDate")
    private Long nextHearingDate;

    @JsonProperty("firstHearingDate")
    private Long firstHearingDate;

    @JsonProperty("isPresenceRequired")
    private Boolean isPresenceRequired;

    @JsonProperty("hearingType")
    private String hearingType;

    @JsonProperty("departmentOfficer")
    private String departmentOfficer;

    @JsonProperty("remarks")
    private String remarks;

    @NotNull
    @JsonProperty("status")
    private Status status;

    @JsonProperty("parties")
    private List<Party> parties;

    @JsonProperty("payment")
    private Payment payment;

    @JsonProperty("workflow")
    @DiffIgnore
    private ProcessInstance workflow;

    @JsonProperty("additionalDetails")
    private Object additionalDetails;

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;

    @JsonProperty("PartyAdv")
    private List<PartyAdv> partyAdv;

}
