package org.ilms.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.egov.common.contract.response.ResponseInfo;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CaseResponse {
    @JsonProperty("responseInfo")
    private ResponseInfo responseInfo = null;

    @JsonProperty("case")
    private Case caseObj = null;

}
