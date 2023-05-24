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

    @JsonProperty("tenantId")
    private String tenantId;

    @JsonProperty("number")
    private String number;

    @JsonProperty("cnrNumber")
    private String cnrNumber;

    @JsonProperty("parentCaseId")
    private String parentCaseId;

    @JsonProperty("linkedCases")
    private JsonNode linkedCases;

    @JsonProperty("type")
    private String type;

    @JsonProperty("category")
    private String category;

    @JsonProperty("filingNumber")
    private String filingNumber;

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
