package org.ilms.web.model;

import java.util.List;
import java.util.Objects;

import org.egov.common.contract.response.ResponseInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ILMSCaseResponse {

    @JsonProperty("responseInfo")
    private ResponseInfo responseInfo = null;

    @JsonProperty ("totalCount")
    private Integer totalCount =null;

    @JsonProperty("caseList")
    private List<ILMSCase> ilmsCases = null;

    @JsonProperty("workflow")
    private Workflow workflow = null;

    public ILMSCaseResponse responseInfo(ResponseInfo responseInfo) {
        this.responseInfo = responseInfo;
        return this;
    }

    /**
     * Get responseInfo
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

    public ILMSCaseResponse ilms(List<ILMSCase> ilms){
        this.ilmsCases = ilms;
        return this;
    }

    /**
     * Get ilms cases
     * @return ilms cases
     **/
    @NotNull

    @Valid
    public List<ILMSCase> getIlmsCases() {
        return ilmsCases;
    }

    public void setIlmsCases(List<ILMSCase> ilms) {
        this.ilmsCases = ilms;
    }

    public ILMSCaseResponse workflow(Workflow workflow) {
        this.workflow = workflow;
        return this;
    }

    /**
     * Get workflow
     * @return workflow
     **/

    @Valid
    public Workflow getWorkflow() {
        return workflow;
    }

    public void setWorkflow(Workflow workflow) {
        this.workflow = workflow;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ILMSCaseResponse ilmsCaseResponse = (ILMSCaseResponse) o;
        return Objects.equals(this.responseInfo, ilmsCaseResponse.responseInfo) &&
                Objects.equals(this.ilmsCases, ilmsCaseResponse.ilmsCases) &&
                Objects.equals(this.workflow, ilmsCaseResponse.workflow);
    }

    @Override
    public int hashCode() {
        return Objects.hash(responseInfo, ilmsCases, workflow);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ILMSCaseResponse {\n");

        sb.append("    responseInfo: ").append(toIndentedString(responseInfo)).append("\n");
        sb.append("    ilmsCases: ").append(toIndentedString(ilmsCases)).append("\n");
        sb.append("    workflow: ").append(toIndentedString(workflow)).append("\n");
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
