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

    @NotNull(message = "tenantId is mandatory")
    @JsonProperty("tenantId")
    private String tenantId;

    @JsonProperty("hearingNumber")
    private String hearingNumber;


    @JsonProperty("courtRoomNumber")
    private String courtRoomNumber;


    @JsonProperty("bench")
    private String bench;

    @JsonProperty("caseId")
    private String caseId;

    @JsonProperty("judgeName")
    private JsonNode judgeName;

    @JsonProperty("hearingDate")
    private Long hearingDate;


    @JsonProperty("businessDate")
    private Long businessDate;

    @JsonProperty("hearingPurpose")
    private String hearingPurpose;

    @JsonProperty("applicationStatus")
    private String applicationStatus;

    @JsonProperty("requiredOfficer")
    private String requiredOfficer;


    @JsonProperty("affidavitFilingDate")
    private Long affidavitFilingDate;

    @JsonProperty("affidavitFilingDueDate")
    private Long affidavitFilingDueDate;


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

    @NotNull(message = "hearingType is mandatory")
    @JsonProperty("hearingType")
    private String hearingType;

    @JsonProperty("departmentOfficer")
    private String departmentOfficer;

    @JsonProperty("remarks")
    private String remarks;

    @JsonProperty("status")
    private Status status;

    @JsonProperty("respondentAdvocate")
    private Advocate respondentAdvocate;

    @JsonProperty("petitionerAdvocate")
    private Advocate petitionerAdvocate;

    @JsonProperty("payment")
    private Payment payment;

    @JsonProperty("documents")
    private List<Document> documents;

    @JsonProperty("additionalDetails")
    private Object additionalDetails;

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;


}
