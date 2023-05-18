package org.legal.web.controller;

import java.io.IOException;
import javax.validation.Valid;
import org.egov.common.contract.request.RequestInfo;
import org.legal.service.EscalationService;
import org.legal.web.model.RequestInfoWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import lombok.extern.slf4j.Slf4j;

@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2020-07-15T11:35:33.568+05:30")

@Controller
@RequestMapping ("/cron")
@Slf4j
public class AutoJobController {

    private EscalationService escalationService;


    @Autowired
    public AutoJobController(EscalationService escalationService) {
        this.escalationService = escalationService;
    }


    @RequestMapping(value="/request/_auto", method = RequestMethod.POST)
    public void requestsCreatePost(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper) throws IOException {
        RequestInfo requestInfo = requestInfoWrapper.getRequestInfo();
        escalationService.fetchSLAs(requestInfo);
    }

}
