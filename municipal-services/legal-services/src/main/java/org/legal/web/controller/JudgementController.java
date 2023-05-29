package org.legal.web.controller;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.tracer.model.CustomException;
import org.legal.service.JudgementService;
import org.legal.util.LegalErrorConstants;
import org.legal.util.ResponseInfoFactory;
import org.legal.web.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
        try {
            JudgementRequest judgementReq = judgementService.create(judgementRequest);
            Judgement judgement = judgementReq.getJudgement();
            Workflow workflow = judgementReq.getWorkflow();
            List<Judgement> judgements = new ArrayList<Judgement>();
            judgements.add(judgement);
            JudgementResponse response = JudgementResponse.builder().judgementList(judgements).workflow(workflow).responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(judgementRequest.getRequestInfo(), true)).build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (Exception e) {
            log.error(LegalErrorConstants.JUDGEMENT_CREATE_FAILED_MSG, e.getMessage());
            throw new CustomException(LegalErrorConstants.JUDGEMENT_CREATE_FAILED, LegalErrorConstants.JUDGEMENT_CREATE_FAILED_MSG);
        }
    }

    @PostMapping(value = "/_search")
    public ResponseEntity<JudgementResponse> search(@Valid @RequestBody JudgementRequest judgementRequest,
                                                    @Valid @ModelAttribute JudgementSearchCriteria criteria) {
        try {
            JudgementResponse response = judgementService.JudgementSearch(criteria, judgementRequest.getRequestInfo());
            response.setResponseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(judgementRequest.getRequestInfo(), true));
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (Exception e) {
            log.error(LegalErrorConstants.JUDGEMENT_SEARCH_FAILED_MSG, e.getMessage());
            throw new CustomException(LegalErrorConstants.JUDGEMENT_SEARCH_FAILED, LegalErrorConstants.JUDGEMENT_SEARCH_FAILED_MSG);
        }
    }

    @PostMapping(value = "/_update")
    public ResponseEntity<JudgementResponse> update(@Valid @RequestBody JudgementRequest judgementRequest) {
        try {
            JudgementRequest judgementReq = judgementService.updateJudgement(judgementRequest);
            Judgement judgement = judgementReq.getJudgement();
            Workflow workflow = judgementReq.getWorkflow();
            ResponseInfo resInfo = responseInfoFactory.createResponseInfoFromRequestInfo(judgementRequest.getRequestInfo(), true);
            JudgementResponse response = JudgementResponse.builder().judgementList(Collections.singletonList(judgement)).workflow(workflow).responseInfo(resInfo).build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (Exception e) {
            log.error(LegalErrorConstants.JUDGEMENT_UPDATE_FAILED_MSG, e.getMessage());
            throw new CustomException(LegalErrorConstants.JUDGEMENT_UPDATE_FAILED, LegalErrorConstants.JUDGEMENT_UPDATE_FAILED_MSG);
        }
    }

}
