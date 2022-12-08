package org.ilms.web.model;

import java.util.List;
import javax.validation.Valid;
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
public class Workflow {

    @JsonProperty ("action")
    private String action = null;

    @JsonProperty ("assignes")
    @Valid
    private List<String> assignes = null;

    @JsonProperty ("comments")
    private String comments = null;

    @JsonProperty ("verificationDocuments")
    @Valid
    private List<Document> verificationDocuments = null;

    @JsonProperty ("rating")
    private Integer rating = null;
}

