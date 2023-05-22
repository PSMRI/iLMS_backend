package org.legal.web.model;

import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.legal.web.model.enums.Status;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Court {

    @JsonProperty("id")
    private String id;

    @JsonProperty("caseId")
    private String caseId;

    @NotNull
    @JsonProperty("courtName")
    private String courtName;

    @NotNull
    @JsonProperty("district")
    private String district;

    @NotNull
    @JsonProperty("state")
    private String state;

    @NotNull
    @JsonProperty("division")
    private String division;

    @NotNull
    @JsonProperty("status")
    private Status status;

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;

}
