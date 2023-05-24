package org.legal.web.model;

import org.legal.web.model.enums.Status;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PartyAdv {
    @JsonProperty("id")
    private String id;

    @JsonProperty("caseId")
    private String caseId;

    @JsonProperty("partyId")
    private String partyId;

    @JsonProperty("partyType")
    private String partyType;

    @JsonProperty("advocateId")
    private String advocateId;

    @JsonProperty("advocateContactNumber")
    private String advocateContactNumber;

    @JsonProperty("status")
    private Status status = Status.ACTIVE;

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;

}
