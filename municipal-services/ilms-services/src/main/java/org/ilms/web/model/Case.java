package org.ilms.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.ilms.web.model.enums.CreationReason;
import org.ilms.web.model.enums.Status;
import org.ilms.web.model.workflow.ProcessInstance;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.validation.constraints.NotNull;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Case {
    @JsonProperty("id")
    private String id;

    @JsonProperty("tenantId")
    private String tenantId;

    @JsonProperty("caseNumber")
    private String caseNumber;

    @JsonProperty("cnrNumber")
    private String cnrNumber;

    @JsonProperty("parentCaseId")
    private String parentCaseId;


    @JsonProperty("state")
    private String state;
    @JsonProperty("district")
    private String district;
    @JsonProperty("division")
    private String division;
    @JsonProperty("courtName")
    private String courtName;

    @JsonProperty("caseType")
    private String caseType;

    @JsonProperty("caseCategory")
    private String caseCategory;

    @JsonProperty("filingNumber")
    private String filingNumber;

    @JsonProperty("filingDate")
    private Long filingDate;

    @JsonProperty("registrationDate")
    private Long registrationDate;

    @JsonProperty("caseSummary")
    private String caseSummary;

    @JsonProperty("arisingDetails")
    private String arisingDetails;

    @JsonProperty("applicationNo")
    private String applicationNumber;

    @JsonProperty("policyOrNonPolicyMatter")
    private String policyOrNonPolicyMatter;


    @JsonProperty("caseStatus")
    private String caseStatus;

    @JsonProperty("caseStage")
    private String caseStage;

    @JsonProperty("caseSubStage")
    private String caseSubStage;

    @JsonProperty("priority")
    private String priority;

    @JsonProperty("departmentName")
    private String departmentName;

    @JsonProperty("recommendOic")
    private String recommendOIC;

    @JsonProperty("remarks")
    private String remarks;

    @JsonProperty("additionalDetails")
    private Object additionalDetails;

    @JsonProperty("status")
    private Status status;

    @JsonProperty("petitioner")
    private Party petitioner;

    @JsonProperty("respondent")
    private Party respondent;

    @JsonProperty("act")
    private Act act;

    @JsonProperty("documents")
    private List<Document> documents;

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;

    @JsonProperty("creationReason")
    @NotNull(message = "The value provided is either Invald or null")
    private CreationReason creationReason;

    @JsonProperty("workflow")
    @DiffIgnore
    private ProcessInstance workflow;

}
