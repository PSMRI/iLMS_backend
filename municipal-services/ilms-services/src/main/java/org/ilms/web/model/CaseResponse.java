package org.ilms.web.model;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.*;
import org.egov.common.contract.response.ResponseInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.stereotype.Service;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CaseResponse {
    @JsonProperty ("responseInfo")
    private ResponseInfo responseInfo = null;

    @JsonProperty("DEC")
    private Integer DEC = null;

    @JsonProperty("RO")
    private Integer RO = null;

    @JsonProperty("OICA")
    private Integer OICA = null;

    @JsonProperty("AO")
    private Integer AO = null;

    @JsonProperty("OIC")
    private Integer OIC = null;

    @JsonProperty("MO")
    private Integer MO = null;

    @JsonProperty ("totalCount")
    private Integer totalCount = null;

    @JsonProperty("statusMap")
    private List<HashMap<String,Object>> statusMap = null;

    @JsonProperty ("caseList")
    private List<Case> caseList = null;
    
}
