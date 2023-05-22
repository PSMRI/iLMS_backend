package org.legal.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.legal.web.model.enums.Status;

import java.util.List;
import javax.validation.constraints.NotNull;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Party {
    @JsonProperty("id")
    private String id;

    @JsonProperty("caseId")
    private String caseId;

    @JsonProperty("advocateId")
    private List<String> advocateId;

    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("lastName")
    private String lastName;

    @JsonProperty("gender")
    private String gender;

    @JsonProperty("petitionerType")
    private String petitionerType;

    @JsonProperty("address")
    private String address;

    @JsonProperty("departmentName")
    private String departmentName;

    @JsonProperty("contactNumber")
    private String contactNumber;

    @NotNull
    @JsonProperty("partyType")
    private String partyType;

    @JsonProperty("advocate")
    private List<Advocate> advocate;

    @NotNull
    @JsonProperty("status")
    private Status status;

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;
}
