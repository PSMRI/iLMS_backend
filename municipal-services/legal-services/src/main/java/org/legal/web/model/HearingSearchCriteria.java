package org.legal.web.model;

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
public class HearingSearchCriteria {

    @JsonProperty("offset")
    private Integer offset;

    @JsonProperty("limit")
    private Integer limit;

    @JsonProperty("caseId")
    private String caseId;

    @JsonProperty("id")
    private String id;

    @JsonProperty("sortBy")
    private SortBy sortBy;

    @JsonProperty("sortOrder")
    private SortOrder sortOrder;

    @JsonProperty("status")
    private String status;

    public enum SortOrder {
        ASC,
        DESC
    }

    public enum SortBy {
        modifiedTime,
        createdTime
    }

}
