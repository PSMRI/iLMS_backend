package org.ilms.web.model;

import java.util.List;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    @JsonProperty ("requestInfo")
    private RequestInfo requestInfo;

    @JsonProperty ("user")
    private List<User> user;

}
