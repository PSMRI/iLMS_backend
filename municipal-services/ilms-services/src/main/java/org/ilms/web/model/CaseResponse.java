package org.ilms.web.model;

import java.util.List;
import java.util.Objects;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.egov.common.contract.response.ResponseInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CaseResponse {
    @JsonProperty ("responseInfo")
    private ResponseInfo responseInfo = null;

    @JsonProperty ("totalCount")
    private Integer totalCount = null;

    @JsonProperty ("caseList")
    private List<Case> Cases = null;

    public CaseResponse responseInfo(ResponseInfo responseInfo) {
        this.responseInfo = responseInfo;
        return this;
    }

    /**
     * Get responseInfo
     *
     * @return responseInfo
     **/
    @NotNull

    @Valid
    public ResponseInfo getResponseInfo() {
        return responseInfo;
    }

    public void setResponseInfo(ResponseInfo responseInfo) {
        this.responseInfo = responseInfo;
    }

    public CaseResponse ilms(List<Case> ilms) {
        this.Cases = ilms;
        return this;
    }

    /**
     * Get ilms cases
     *
     * @return ilms cases
     **/
    @NotNull

    @Valid
    public List<Case> getCases() {
        return Cases;
    }

    public void setCases(List<Case> ilms) {
        this.Cases = ilms;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CaseResponse caseResponse = (CaseResponse) o;
        return Objects.equals(this.responseInfo, caseResponse.responseInfo) && Objects.equals(this.Cases, caseResponse.Cases);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ILMSCaseResponse {\n");

        sb.append("    responseInfo: ").append(toIndentedString(responseInfo)).append("\n");
        sb.append("    ilmsCases: ").append(toIndentedString(Cases)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

}
