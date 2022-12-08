package org.ilms.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.egov.common.contract.request.RequestInfo;

import javax.validation.Valid;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class HearingRequest {

    @JsonProperty ("RequestInfo")
    private RequestInfo RequestInfo;

    @Valid
    @JsonProperty("hearingDetails")
    private Hearing hearing;

    @JsonProperty("workflow")
    private Workflow workflow ;
}
