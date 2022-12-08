package org.ilms.web.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.validation.Valid;
import org.egov.common.contract.response.ResponseInfo;
import org.ilms.service.JudgementService;
import org.ilms.util.ResponseInfoFactory;
import org.ilms.web.model.Judgement;
import org.ilms.web.model.JudgementRequest;
import org.ilms.web.model.JudgementResponse;
import org.ilms.web.model.JudgementSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping ("/judgement")
@CrossOrigin (origins = "*", allowedHeaders = "*")
public class JudgementController {
    @Autowired
    private JudgementService judgementService;

    @Autowired
    private ResponseInfoFactory responseInfoFactory;

    @PostMapping (value = "/_create")
    public ResponseEntity<JudgementResponse> create(@Valid @RequestBody JudgementRequest judgementRequest) {
        Judgement judgement = judgementService.create(judgementRequest);
        List<Judgement> judgements = new ArrayList<Judgement>();
        judgements.add(judgement);
        JudgementResponse response = JudgementResponse.builder().judgements(judgements).responseInfo(
                responseInfoFactory.createResponseInfoFromRequestInfo(judgementRequest.getRequestInfo(), true)).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping (value = "/_search")
    public ResponseEntity<JudgementResponse> search(@Valid @RequestBody JudgementRequest judgementRequest,
            @Valid @ModelAttribute JudgementSearchCriteria criteria) {
        JudgementResponse response = judgementService.JudgementSearch(criteria, judgementRequest.getRequestInfo());
        response.setResponseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(judgementRequest.getRequestInfo(), true));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping (value = "/_update")
    public ResponseEntity<JudgementResponse> update(@Valid @RequestBody JudgementRequest judgementRequest) {
        Judgement judgement = judgementService.updateJudgement(judgementRequest);
        ResponseInfo resInfo = responseInfoFactory.createResponseInfoFromRequestInfo(judgementRequest.getRequestInfo(), true);
        JudgementResponse response = JudgementResponse.builder().judgements(Arrays.asList(judgement)).responseInfo(resInfo).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
