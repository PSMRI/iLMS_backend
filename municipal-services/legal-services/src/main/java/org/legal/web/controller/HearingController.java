package org.legal.web.controller;

import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;

import org.egov.tracer.model.CustomException;
import org.legal.service.HearingService;
import org.legal.util.LegalErrorConstants;
import org.legal.util.ResponseInfoFactory;
import org.legal.web.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/hearing")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class HearingController {
    @Autowired
    private ResponseInfoFactory responseInfoFactory;

    @Autowired
    private HearingService hearingService;

    @PostMapping(value = "/_create")
    public ResponseEntity<HearingResponse> create(@Valid @RequestBody HearingRequest hearingRequest) {
        try {
            HearingRequest hearingReq = hearingService.create(hearingRequest);
            Hearing hearing = hearingReq.getHearing();
            Workflow workflow = hearingReq.getWorkflow();
            List<Hearing> hearingList = new ArrayList<Hearing>();
            hearingList.add(hearing);
            HearingResponse response = HearingResponse.builder().hearingList(hearingList).workflow(workflow).responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(hearingRequest.getRequestInfo(), true)).build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        }  catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            if(e instanceof CustomException){
                throw e;
            }
            e.printStackTrace();
            log.error(LegalErrorConstants.HEARING_CREATE_FAILED_MSG, e.getMessage());
            throw new CustomException(LegalErrorConstants.HEARING_CREATE_FAILED, LegalErrorConstants.HEARING_CREATE_FAILED_MSG);
        }
    }

    @PostMapping(value = "/_search")
    public ResponseEntity<HearingResponse> search(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
                                                  @Valid @ModelAttribute HearingSearchCriteria criteria) {
        try {
            HearingResponse response = hearingService.hearingSearch(criteria, requestInfoWrapper.getRequestInfo());
            response.setResponseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true));
            return new ResponseEntity<>(response, HttpStatus.OK);
        }  catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            if(e instanceof CustomException){
                throw e;
            }
            e.printStackTrace();
            log.error(LegalErrorConstants.HEARING_SEARCH_FAILED_MSG, e.getMessage());
            throw new CustomException(LegalErrorConstants.HEARING_SEARCH_FAILED, LegalErrorConstants.HEARING_SEARCH_FAILED_MSG);
        }
    }


    @PostMapping(value = "/_update")
    public ResponseEntity<HearingResponse> update(@Valid @RequestBody HearingRequest hearingDetailsRequest) {
        try {
            HearingRequest hearingReq = hearingService.update(hearingDetailsRequest);
            Hearing hearing = hearingReq.getHearing();
            Workflow workflow = hearingReq.getWorkflow();
            List<Hearing> hearingDetailsList = new ArrayList<Hearing>();
            hearingDetailsList.add(hearing);
            HearingResponse response = HearingResponse.builder().hearingList(hearingDetailsList).workflow(workflow).responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(hearingDetailsRequest.getRequestInfo(), true)).build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        }  catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            if(e instanceof CustomException){
                throw e;
            }
            e.printStackTrace();
            log.error(LegalErrorConstants.HEARING_UPDATE_FAILED_MSG, e.getMessage());
            throw new CustomException(LegalErrorConstants.HEARING_UPDATE_FAILED, LegalErrorConstants.HEARING_UPDATE_FAILED_MSG);
        }
    }
}
