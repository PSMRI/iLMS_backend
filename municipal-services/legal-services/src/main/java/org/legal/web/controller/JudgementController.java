package org.legal.web.controller;

import org.egov.common.contract.response.ResponseInfo;
import org.legal.service.JudgementService;
import org.legal.util.ResponseInfoFactory;
import org.legal.web.model.JudgementRequest;
import org.legal.web.model.JudgementResponse;
import org.legal.web.model.JudgementSearchCriteria;
import org.legal.web.model.Judgement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/judgement")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class JudgementController {
    @Autowired
    private JudgementService judgementService;

    @Autowired
    private ResponseInfoFactory responseInfoFactory;

    @PostMapping(value = "/_create")
    public ResponseEntity<JudgementResponse> create(@Valid @RequestBody JudgementRequest judgementRequest) {
        Judgement judgement = judgementService.create(judgementRequest);
        List<Judgement> judgements = new ArrayList<Judgement>();
        judgements.add(judgement);
        JudgementResponse response = JudgementResponse.builder().judgementList(judgements).responseInfo(
                responseInfoFactory.createResponseInfoFromRequestInfo(judgementRequest.getRequestInfo(), true)).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/_search")
    public ResponseEntity<JudgementResponse> search(@Valid @RequestBody JudgementRequest judgementRequest,
                                                    @Valid @ModelAttribute JudgementSearchCriteria criteria) {
        JudgementResponse response = judgementService.JudgementSearch(criteria, judgementRequest.getRequestInfo());
        response.setResponseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(judgementRequest.getRequestInfo(), true));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/_update")
    public ResponseEntity<JudgementResponse> update(@Valid @RequestBody JudgementRequest judgementRequest) {
        Judgement judgement = judgementService.updateJudgement(judgementRequest);
        ResponseInfo resInfo = responseInfoFactory.createResponseInfoFromRequestInfo(judgementRequest.getRequestInfo(), true);
        JudgementResponse response = JudgementResponse.builder().judgementList(Collections.singletonList(judgement)).responseInfo(resInfo).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
