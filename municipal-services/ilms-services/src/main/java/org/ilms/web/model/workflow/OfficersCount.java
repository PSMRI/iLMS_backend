package org.ilms.web.model.workflow;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OfficersCount {

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
}
