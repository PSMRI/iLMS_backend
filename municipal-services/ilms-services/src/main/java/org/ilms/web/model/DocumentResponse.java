package org.ilms.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.egov.common.contract.response.ResponseInfo;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentResponse {

    @JsonProperty("responseInfo")
    private ResponseInfo responseInfo = null;

    @JsonProperty ("totalCount")
    private Integer totalCount =null;

    @JsonProperty("documentList")
    private List<Document> documents = null;


    public DocumentResponse responseInfo(ResponseInfo responseInfo) {
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

    public DocumentResponse documents(List<Document> document){
        this.documents = document;
        return this;
    }

    /**
     * Get documents
     * @return documents
     **/
    @NotNull

    @Valid
    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DocumentResponse documentResponse = (DocumentResponse) o;
        return Objects.equals(this.responseInfo, documentResponse.responseInfo) &&
                Objects.equals(this.documents, documentResponse.documents);
    }

    @Override
    public int hashCode() {
        return Objects.hash(responseInfo, documents);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class DocumentResponse {\n");

        sb.append("    responseInfo: ").append(toIndentedString(responseInfo)).append("\n");
        sb.append("    documents: ").append(toIndentedString(documents)).append("\n");
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
