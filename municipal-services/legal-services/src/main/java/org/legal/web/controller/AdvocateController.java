package org.legal.web.controller;

import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;

import org.egov.tracer.model.CustomException;
import org.legal.repository.AdvocateRepository;
import org.legal.service.AdvocateService;
import org.legal.util.LegalErrorConstants;
import org.legal.util.ResponseInfoFactory;
import org.legal.web.model.Advocate;
import org.legal.web.model.AdvocateRequest;
import org.legal.web.model.AdvocateResponse;
import org.legal.web.model.AdvocateSearchCriteria;
import org.legal.web.model.RequestInfoWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/advocate")
@Log4j2
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AdvocateController {

    @Autowired
    private ResponseInfoFactory responseInfoFactory;

    @Autowired
    private AdvocateService advocateService;

    @Autowired
    private AdvocateRepository advocateRepository;

    @PostMapping(value = "/_create")
    public ResponseEntity<AdvocateResponse> create(@Valid @RequestBody AdvocateRequest advocateRequest) {
        try {

            List<Advocate> advocateList = new ArrayList<Advocate>();
            Advocate savedAdvocate = advocateService.create(advocateRequest);
            advocateList.add(savedAdvocate);
            AdvocateResponse response = AdvocateResponse.builder().advocate(advocateList).responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(advocateRequest.getRequestInfo(), true)).build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(LegalErrorConstants.ADVOCATE_CREATE_FAILED, LegalErrorConstants.ADVOCATE_CREATE_FAILED_MSG);
            throw new CustomException(LegalErrorConstants.ADVOCATE_CREATE_FAILED, LegalErrorConstants.ADVOCATE_CREATE_FAILED_MSG);
        }
    }

    @PostMapping(value = "/_update")
    public ResponseEntity<AdvocateResponse> update(@Valid @RequestBody AdvocateRequest advocateRequest) {
        try {
            AdvocateResponse response = new AdvocateResponse();
            Advocate advocate = advocateService.update(advocateRequest);
            List<Advocate> advocateList = new ArrayList<>();
            advocateList.add(advocate);
            response.setAdvocate(advocateList);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(LegalErrorConstants.ADVOCATE_UPDATE_FAILED, LegalErrorConstants.ADVOCATE_UPDATE_FAILED_MSG);
            throw new CustomException(LegalErrorConstants.ADVOCATE_UPDATE_FAILED, LegalErrorConstants.ADVOCATE_UPDATE_FAILED_MSG);
        }
    }

    @PostMapping(value = "/_search")
    public ResponseEntity<AdvocateResponse> search(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
                                                   @Valid @ModelAttribute AdvocateSearchCriteria criteria) {
        try {
            AdvocateResponse response = advocateService.advocateSearch(criteria);
            response.setResponseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(LegalErrorConstants.ADVOCATE_NOT_AVAILABLE, LegalErrorConstants.ADVOCATE_NOT_AVAILABLE);
            throw new CustomException(LegalErrorConstants.ADVOCATE_NOT_AVAILABLE, LegalErrorConstants.ADVOCATE_NOT_AVAILABLE);
        }
    }
}
