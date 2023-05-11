package org.legal.web.model;

import java.util.List;
import org.egov.common.contract.response.ResponseInfo;
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
public class AdvocateResponse {
    @JsonProperty ("responseInfo")
    private ResponseInfo responseInfo = null;

    @JsonProperty ("advocate")
    private List<Advocate> advocate = null;

    @JsonProperty("totalCount")
    private Integer totalCount = null;

}
