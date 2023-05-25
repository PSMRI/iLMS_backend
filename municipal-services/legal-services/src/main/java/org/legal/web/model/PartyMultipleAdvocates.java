package org.legal.web.model;

import java.util.List;
import org.legal.web.model.enums.Status;
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
public class PartyMultipleAdvocates {


    @JsonProperty("caseId")
    private String caseId;

    @JsonProperty("partyId")
    private String partyId;

    @JsonProperty("advocateId")
    private String advocateId;

    @JsonProperty("status")
    private Status status;

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;

}
