package org.ilms.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ilms.web.model.enums.PartyType;
import org.ilms.web.model.enums.Status;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Advocate {

    @JsonProperty ("id")
    private String id ;

    @JsonProperty ("partyId")
    private String partyId ;

    @JsonProperty ("hearingId")
    private String hearingId ;

    @JsonProperty ("firstName")
    private String firstName ;

    @JsonProperty ("lastName")
    private String lastName ;

    @JsonProperty ("contactNumber")
    private String contactNumber ;

    @JsonProperty ("partyType")
    private PartyType partyType ;

    @JsonProperty ("status")
    private Status status ;

    @JsonProperty ("auditDetails")
    private AuditDetails auditDetails ;
}
