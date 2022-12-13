package org.ilms.web.model;

import org.ilms.web.model.enums.Status;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Hearing {
    @JsonProperty ("id")
    private String id;

    @JsonProperty ("hearingNumber")
    private String hearingNumber;

    @JsonProperty ("courtId")
    private String courtId;

    @JsonProperty ("court")
    private Court court;

    @JsonProperty ("caseId")
    private String caseId;

    @JsonProperty ("judgeName")
    private String judgeName;

    @JsonProperty ("hearingDate")
    private Long hearingDate;

    @JsonProperty ("businessDate")
    private Long businessDate;

    @JsonProperty ("hearingPurpose")
    private String hearingPurpose;

    @JsonProperty ("requiredOfficer")
    private String requiredOfficer;

    @JsonProperty ("affidavitFilingDate")
    private Long affidavitFilingDate;

    @JsonProperty ("affidavitFilingDueDate")
    private Long affidavitFilingDueDate;

    @JsonProperty ("caseNumber")
    private String caseNumber;

    @JsonProperty ("oathNumber")
    private String oathNumber;

    @JsonProperty ("firstHearingDate")
    private Long firstHearingDate;

    @JsonProperty ("previousHearingDate")
    private Long previousHearingDate;

    @JsonProperty ("nextHearingDate")
    private Long nextHearingDate;

    @JsonProperty ("isPresenceRequired")
    private Boolean isPresenceRequired;

    @JsonProperty ("hearingType")
    private String hearingType;

    @JsonProperty ("departmentOfficer")
    private String departmentOfficer;

    @JsonProperty ("remarks")
    private String remarks;

    @JsonProperty ("status")
    private Status status;

    @JsonProperty ("petitioner")
    private ILMSParty petitioner;

    @JsonProperty ("respondent")
    private ILMSParty respondent;

    @JsonProperty ("payment")
    private Payment payment;

    @JsonProperty ("additionalDetails")
    private Object additionalDetails;

    @JsonProperty ("auditDetails")
    private AuditDetails auditDetails;

}
