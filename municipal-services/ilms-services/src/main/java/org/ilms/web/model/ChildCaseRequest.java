package org.ilms.web.model;

import org.egov.common.contract.request.RequestInfo;
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

public class ChildCaseRequest {
    @JsonProperty ("RequestInfo")
    private RequestInfo RequestInfo;

    @JsonProperty ("childCase")
    private ChildCase childCase;
}
