package org.legal.web.model;

import java.util.List;
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
public class Act {

    @JsonProperty("id")
    private String id;

    @JsonProperty("caseId")
    private String caseId;

    @JsonProperty("actName")
    private String actName;

    @JsonProperty("sectionNumber")
    private List<String> sectionNumber;

    @JsonProperty("status")
    private Status status;

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;
}
