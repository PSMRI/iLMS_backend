package org.ilms.web.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CaseSearchCriteria {
    @JsonProperty ("offset")
    private Integer offset;

    @JsonProperty ("limit")
    private Integer limit;

    @JsonProperty ("caseNumber")
    private List<String> caseNumber;

    @JsonProperty ("cnrNumber")
    private String cnrNumber;

    @JsonProperty ("id")
    private List<String> id;

    @JsonProperty ("parentCaseId")
    private List<String> parentCaseId;

    @JsonProperty ("sortBy")
    private SortBy sortBy;

    @JsonProperty ("sortOrder")
    private SortOrder sortOrder;

    public enum SortOrder {
        ASC,
        DESC
    }

    public enum SortBy {
        caseNumber,
        cnrNumber
    }

  /*  public boolean isEmpty() {
        // TODO Auto-generated method stub
        return (this.offset == null && this.limit == null && CollectionUtils.isEmpty(Collections.singleton(this.caseNumber))
                && CollectionUtils.isEmpty(this.crnNumber));
    }*/
}
