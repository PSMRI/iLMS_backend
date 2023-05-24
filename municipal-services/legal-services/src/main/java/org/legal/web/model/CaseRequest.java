package org.legal.web.model;

import org.egov.common.contract.request.RequestInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.legal.web.model.workflow.ProcessInstance;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CaseRequest {
    @JsonProperty("RequestInfo")
    private RequestInfo RequestInfo;

    @JsonProperty("case")
    private Case caseObj;

    @JsonProperty("workflow")
    @DiffIgnore
    private Workflow workflow;

}
