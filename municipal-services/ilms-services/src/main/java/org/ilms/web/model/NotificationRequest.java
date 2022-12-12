package org.ilms.web.model;

import javax.validation.Valid;
import org.egov.common.contract.request.RequestInfo;
import org.ilms.service.ILMSCaseService;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRequest {
    @JsonProperty ("RequestInfo")
    private RequestInfo requestInfo;

    @JsonProperty("ilmsCaseService")
    @Valid
    private ILMSCaseService ilmsCaseService;
}
