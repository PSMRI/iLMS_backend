package org.legal.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class JudgementSearchCriteria {
    @JsonProperty("offset")
    private Integer offset;

    @JsonProperty("limit")
    private Integer limit;

    @JsonProperty("id")
    private List<String> id;

    @JsonProperty("caseId")
    private List<String> caseId;

    @JsonProperty("sortBy")
    private SortBy sortBy;

    @JsonProperty("sortOrder")
    private SortOrder sortOrder;

    public enum SortOrder {
        ASC,
        DESC
    }

    public enum SortBy {
        createdTime,
        modifiedTime
    }

}