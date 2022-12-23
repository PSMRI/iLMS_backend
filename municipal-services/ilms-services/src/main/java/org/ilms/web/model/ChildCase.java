package org.ilms.web.model;

import java.util.List;
import org.ilms.web.model.enums.CaseHierarchy;
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
public class ChildCase {
    @JsonProperty ("parentCaseId")
    private String parentCaseId;

    //    @NotBlank (message = "caseHierarchy can not be null")
    @JsonProperty ("caseHierarchy")
    private CaseHierarchy caseHierarchy;

    @JsonProperty ("caseIds")
    private List<CaseIds> caseIds;
}

