package org.legal.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleDto {
    @JsonProperty
    private String name;
    @JsonProperty
    private String code;
    @JsonProperty
    private String description;
    @JsonProperty
    private String tenantId;


}
