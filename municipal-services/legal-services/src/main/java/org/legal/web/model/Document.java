package org.legal.web.model;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.legal.web.model.enums.Status;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Document {

    @JsonProperty("id")
    private String id;

    @JsonProperty("caseId")
    private String caseId;

    @JsonProperty("hearingId")
    private String hearingId;

    @NotNull
    @JsonProperty("documentType")
    private String documentType;

    @NotNull
    @JsonProperty("fileStoreId")
    private String fileStoreId;

    @NotNull
    @JsonProperty("status")
    private Status status;

    @JsonProperty("remarks")
    private String remarks;

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;

}
