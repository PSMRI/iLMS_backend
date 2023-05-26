package org.legal.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;
import org.legal.web.model.enums.CreationReason;
import org.legal.web.model.enums.Status;
import org.legal.web.model.workflow.ProcessInstance;
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

    @NotNull
    @JsonProperty("tenantId")
    private String tenantId;

    @NotNull(message = "caseNumber is mandatory")
    @JsonProperty("caseNumber")
    private String caseNumber;

    @NotNull(message = "cnrNumber is mandatory")
    @JsonProperty("cnrNumber")
    private String cnrNumber;

    @JsonProperty("parentCaseId")
    private String parentCaseId;

    @JsonProperty("linkedCases")
    private JsonNode linkedCases;

    @NotNull(message = "type is mandatory")
    @JsonProperty("type")
    private String type;

    @NotNull(message = "category is mandatory")
    @JsonProperty("category")
    private String category;

    @NotNull(message = "filingNumber is mandatory")
    @JsonProperty("filingNumber")
    private String filingNumber;

    @NotNull(message = "filingDate is mandatory")
    @JsonProperty("filingDate")
    private Long filingDate;

    @JsonProperty("summary")
    private String summary;

    @JsonProperty("arisingDetails")
    private String arisingDetails;

    @JsonProperty("applicationStatus")
    private String applicationStatus;

    @JsonProperty("policyOrNonPolicyMatter")
    private String policyOrNonPolicyMatter;

    @JsonProperty("caseStatus")
    private String caseStatus;

    @JsonProperty("priority")
    private String priority;

    @JsonProperty("recommendOic")
    private String recommendOIC;

    @JsonProperty("remarks")
    private String remarks;

    @JsonProperty("additionalDetails")
    private JsonNode additionalDetails;

    @NotNull
    @JsonProperty("status")
    private Status status;

    @JsonProperty("parties")
    private List<Party> parties;

    @JsonProperty("act")
    private List<Act> act;

    @JsonProperty("court")
    private Court court;

    @JsonProperty("documents")
    private List<Document> documents;

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;

    @JsonProperty("PartyAdv")
    private List<PartyAdv> partyAdv;
}
