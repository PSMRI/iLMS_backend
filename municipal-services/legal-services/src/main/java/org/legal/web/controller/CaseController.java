package org.legal.web.controller;

import static org.legal.util.LegalErrorConstants.CASE_CREATE_FAILED_MSG;
import static org.legal.util.LegalErrorConstants.CASE_SEARCH_FAILED_MSG;
import static org.legal.util.LegalErrorConstants.CASE_UPDATE_FAILED_MSG;

import lombok.extern.log4j.Log4j2;
import org.egov.common.contract.response.ResponseInfo;
import org.egov.tracer.model.CustomException;
import org.legal.service.CaseService;
import org.legal.util.LegalErrorConstants;
import org.legal.util.ResponseInfoFactory;
import org.legal.web.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/case")
@Log4j2
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CaseController {
    @Autowired
    private ResponseInfoFactory responseInfoFactory;

    @Autowired
    private CaseService caseService;

    @PostMapping(value = "/_search")
    public ResponseEntity<CaseResponse> search(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
                                               @Valid @ModelAttribute CaseSearchCriteria criteria) {
        try {
            log.info("LEGALCaseController :: search() : START ");
            CaseResponse response = caseService.legalCaseSearch(criteria, requestInfoWrapper.getRequestInfo(), requestInfoWrapper.getProcessSearchCriteria());
            response.setResponseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true));
            log.info("LEGALCaseController :: search() : END With Response [ " + response + " ]");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            if (e instanceof CustomException) {
                throw e;
            }
            e.printStackTrace();
            log.error(CASE_SEARCH_FAILED_MSG, e.getMessage());
            throw new CustomException(LegalErrorConstants.CASE_SEARCH_FAILED, CASE_SEARCH_FAILED_MSG + "  " + e.getMessage());
        }
    }

    @PostMapping(value = "/_create")
    public ResponseEntity<CaseResponse> create(@Valid @RequestBody CaseRequest caseRequest) {
        try {
            CaseRequest caseReq = caseService.create(caseRequest);
            Workflow workflow = caseReq.getWorkflow();
            List<Case> caseList = new ArrayList<Case>();
            caseList.add(caseReq.getCaseObj());
            CaseResponse response = CaseResponse.builder().caseList(caseList).workflow(workflow).responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(caseRequest.getRequestInfo(), true)).build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            if (e instanceof CustomException) {
                throw e;
            }
            e.printStackTrace();
            log.error(CASE_CREATE_FAILED_MSG, e.getMessage());
            throw new CustomException(LegalErrorConstants.CASE_CREATE_FAILED, CASE_CREATE_FAILED_MSG + " " + e.getMessage());
        }
    }

    @PostMapping(value = "/_update")
    public ResponseEntity<CaseResponse> update(@Valid @RequestBody CaseRequest caseRequest) {
        try {
            CaseRequest caseReq = caseService.updateCase(caseRequest);
            Case caseObj = caseReq.getCaseObj();
            Workflow workflow = caseReq.getWorkflow();
            List<Case> caseList = new ArrayList<>();
            caseList.add(caseObj);
            CaseResponse response = CaseResponse.builder().caseList(caseList).workflow(workflow).responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(caseRequest.getRequestInfo(), true)).build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            if (e instanceof CustomException) {
                throw e;
            }
            e.printStackTrace();
            log.error(CASE_UPDATE_FAILED_MSG, e.getMessage());
            throw new CustomException(LegalErrorConstants.CASE_UPDATE_FAILED, CASE_UPDATE_FAILED_MSG + " " + e.getMessage());
        }
    }

    @RequestMapping(value = "/_count", method = RequestMethod.POST)
    public ResponseEntity<CountResponse> requestsCountPost(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
                                                           @Valid @ModelAttribute CaseSearchCriteria criteria) {
        try {
            Map<String, Integer> countMap = caseService.count(criteria);
            ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true);
            CountResponse response = CountResponse.builder().responseInfo(responseInfo).statusCountMap(countMap).build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            if (e instanceof CustomException) {
                throw e;
            }
            log.error(LegalErrorConstants.COUNT_SEARCH_FAILED_MSG, e.getMessage());
            throw new CustomException(LegalErrorConstants.COUNT_SEARCH_FAILED, LegalErrorConstants.COUNT_SEARCH_FAILED_MSG);
        }
    }
}
