package org.legal.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.egov.common.contract.request.RequestInfo;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class JudgementRequest {

    @JsonProperty("RequestInfo")
    private RequestInfo RequestInfo;

    @JsonProperty("judgement")
    private Judgement judgement;

    @JsonProperty("workflow")
    private Workflow workflow;
}
