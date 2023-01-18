package org.ilms.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.egov.common.contract.request.RequestInfo;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderRequest {

    @JsonProperty("RequestInfo")
    private RequestInfo RequestInfo;

    @JsonProperty("order")
    private Order order;

    @JsonProperty("workflow")
    private Workflow workflow;
}
