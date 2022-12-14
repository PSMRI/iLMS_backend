package org.ilms.web.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ilms.web.model.enums.CaseHierarchy;
import org.ilms.web.model.enums.Status;
import org.ilms.web.model.enums.CreationReason;
import org.ilms.web.model.workflow.ProcessInstance;
import org.javers.core.metamodel.annotation.DiffIgnore;
import javax.validation.constraints.NotNull;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Case {
    @JsonProperty ("id")
    private String id;

    @JsonProperty ("tenantId")
    private String tenantId;

    @JsonProperty ("caseNumber")
    private String caseNumber;

    @JsonProperty ("cnrNumber")
    private String cnrNumber;

    @JsonProperty ("parentCaseId")
    private String parentCaseId;

    @JsonProperty ("caseHierarchy")
    private CaseHierarchy caseHierarchy;

    @JsonProperty ("caseType")
    private String caseType;

    @JsonProperty ("caseCategory")
    private String caseCategory;

    @JsonProperty ("caseYear")
    private Long caseYear;

    @JsonProperty ("filingNumber")
    private String filingNumber;

    @JsonProperty ("filingDate")
    private Long filingDate;

    @JsonProperty ("registrationDate")
    private Long registrationDate;

    @JsonProperty ("caseSummary")
    private String caseSummary;

    @JsonProperty ("arisingDetails")
    private String arisingDetails;

    @JsonProperty ("policyOrNonPolicyMatter")
    private String policyOrNonPolicyMatter;

    @JsonProperty ("applicationNo")
    private String applicationNumber;

    @JsonProperty ("isCaseNumberCorrect")
    private Boolean isCaseNumberCorrect;

    @JsonProperty ("caseStatus")
    private String caseStatus;

    @JsonProperty ("firstHearingDate")
    private Long firstHearingDate;

    @JsonProperty ("previousHearingDate")
    private Long previousHearingDate;

    @JsonProperty ("nextHearingDate")
    private Long nextHearingDate;

    @JsonProperty ("caseStage")
    private String caseStage;

    @JsonProperty ("caseSubStage")
    private String caseSubStage;

    @JsonProperty ("caseFlag")
    private String caseFlag;

    @JsonProperty ("departmentName")
    private String departmentName;

    @JsonProperty ("recommendOic")
    private String recommendOIC;

    @JsonProperty ("remarks")
    private String remarks;

    @JsonProperty ("assignedOfficerId")
    private String assignedOfficerId;

    @JsonProperty ("additionalDetails")
    private Object additionalDetails;

    @JsonProperty ("status")
    private Status status;

    @JsonProperty ("petitioner")
    private Party petitioner;

    @JsonProperty ("respondent")
    private Party respondent;

    @JsonProperty ("act")
    private Act act;

    @JsonProperty ("documents")
    private List<Document> documents;

    @JsonProperty ("auditDetails")
    private AuditDetails auditDetails;

    @JsonProperty ("creationReason")
    @NotNull (message = "The value provided is either Invald or null")
    private CreationReason creationReason;

    @JsonProperty ("workflow")
    @DiffIgnore
    private ProcessInstance workflow;

}
