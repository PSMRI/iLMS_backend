package org.ilms.web.controller;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import org.ilms.service.CaseService;
import org.ilms.util.ResponseInfoFactory;
import org.ilms.web.model.Case;
import org.ilms.web.model.CaseDetailsResponse;
import org.ilms.web.model.CaseRequest;
import org.ilms.web.model.CaseResponse;
import org.ilms.web.model.CaseSearchCriteria;
import org.ilms.web.model.ChildCase;
import org.ilms.web.model.ChildCaseRequest;
import org.ilms.web.model.RequestInfoWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping ("/case")
@Log4j2
@CrossOrigin (origins = "*", allowedHeaders = "*")
public class CaseController {
    @Autowired
    private ResponseInfoFactory responseInfoFactory;

    @Autowired
    private CaseService caseService;

    @PostMapping (value = "/_search")
    public ResponseEntity<CaseResponse> search(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
            @Valid @ModelAttribute CaseSearchCriteria criteria) {
        log.info("ILMSCaseController :: search() : START ");
        CaseResponse response = caseService.ilmsCaseSearch(criteria, requestInfoWrapper.getRequestInfo());
        response.setResponseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true));
        log.info("ILMSCaseController :: search() : END With Response [ " + response + " ]");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping (value = "/_create")
    public ResponseEntity<CaseResponse> create(@Valid @RequestBody CaseRequest caseRequest) {
        Case caseObj = caseService.create(caseRequest);
        List<Case> caseList = new ArrayList<Case>();
        caseList.add(caseObj);
        CaseResponse response = CaseResponse.builder().caseList(caseList)
                                            .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(caseRequest.getRequestInfo(), true))
                                            .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping (value = "/_update")
    public ResponseEntity<CaseResponse> update(@Valid @RequestBody CaseRequest caseRequest) {
        CaseResponse response = new CaseResponse();
        Case caseObj = caseService.update(caseRequest);
        List<Case> caseList = new ArrayList<>();
        caseList.add(caseObj);
        response.setCaseList(caseList);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping (value = "/_caseDetails")
    public ResponseEntity<CaseDetailsResponse> CaseDetailsResponse(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
            @Valid @ModelAttribute CaseSearchCriteria criteria) {
        log.info("ILMSCaseController :: search() : START ");
        CaseDetailsResponse downloadResponse = caseService.caseDetailsSearch(criteria, requestInfoWrapper.getRequestInfo());
        downloadResponse.setResponseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true));
        log.info("ILMSCaseController :: search() : END With Response [ " + downloadResponse + " ]");
        return new ResponseEntity<>(downloadResponse, HttpStatus.OK);
    }

    @RequestMapping (value = "/_generatePDF", method = RequestMethod.POST, produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<Resource> generatePDF(@ModelAttribute final CaseSearchCriteria criteria) throws FileNotFoundException {
        log.info("Genreate PDF : START : " + criteria);
        ByteArrayInputStream bis = caseService.generatePDF(criteria);
        String pdfName = criteria.getId() + ".pdf";
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + pdfName).contentType(MediaType.APPLICATION_PDF)
                             .body(new InputStreamResource(bis));
    }

    @PostMapping (value = "/_addChildCases")
    public ChildCase addChildCases(@Valid @RequestBody ChildCaseRequest ilmschildCaseRequest) {
        ChildCase ilmsCaseIds = caseService.addChildCases(ilmschildCaseRequest);
        return ilmsCaseIds;
    }
}
