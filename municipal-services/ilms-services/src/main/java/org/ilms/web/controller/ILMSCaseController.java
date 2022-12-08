package org.ilms.web.controller;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import org.ilms.service.ILMSCaseService;
import org.ilms.util.ResponseInfoFactory;
import org.ilms.web.model.CaseDetailsResponse;
import org.ilms.web.model.ChildCase;
import org.ilms.web.model.ChildCaseRequest;
import org.ilms.web.model.ILMSCase;
import org.ilms.web.model.ILMSCaseRequest;
import org.ilms.web.model.ILMSCaseResponse;
import org.ilms.web.model.ILMSCaseSearchCriteria;
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
public class ILMSCaseController {
    @Autowired
    private ResponseInfoFactory responseInfoFactory;

    @Autowired
    private ILMSCaseService ilmsCaseService;

    @PostMapping (value = "/_search")
    public ResponseEntity<ILMSCaseResponse> search(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
            @Valid @ModelAttribute ILMSCaseSearchCriteria criteria) {
        log.info("ILMSCaseController :: search() : START ");
        ILMSCaseResponse response = ilmsCaseService.ilmsCaseSearch(criteria, requestInfoWrapper.getRequestInfo());
        response.setResponseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true));
        log.info("ILMSCaseController :: search() : END With Response [ " + response + " ]");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping (value = "/_create")
    public ResponseEntity<ILMSCaseResponse> create(@Valid @RequestBody ILMSCaseRequest ilmsCaseRequest) {
        ILMSCase ilmsCase = ilmsCaseService.create(ilmsCaseRequest);
        List<ILMSCase> ilmsCaseList = new ArrayList<ILMSCase>();
        ilmsCaseList.add(ilmsCase);
        ILMSCaseResponse response = ILMSCaseResponse.builder().ilmsCases(ilmsCaseList).responseInfo(
                responseInfoFactory.createResponseInfoFromRequestInfo(ilmsCaseRequest.getRequestInfo(), true)).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping (value = "/_update")
    public ResponseEntity<ILMSCaseResponse> update(@Valid @RequestBody ILMSCaseRequest ilmsCaseRequest) {
        ILMSCaseResponse response = new ILMSCaseResponse();
        ILMSCase ilmsCase = ilmsCaseService.update(ilmsCaseRequest);
        List<ILMSCase> ilmsCaseList = new ArrayList<>();
        ilmsCaseList.add(ilmsCase);
        response.setIlmsCases(ilmsCaseList);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping (value = "/_caseDetails")
    public ResponseEntity<CaseDetailsResponse> CaseDetailsResponse(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
            @Valid @ModelAttribute ILMSCaseSearchCriteria criteria) {
        log.info("ILMSCaseController :: search() : START ");
        CaseDetailsResponse downloadResponse = ilmsCaseService.caseDetailsSearch(criteria, requestInfoWrapper.getRequestInfo());
        downloadResponse.setResponseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true));
        log.info("ILMSCaseController :: search() : END With Response [ " + downloadResponse + " ]");
        return new ResponseEntity<>(downloadResponse, HttpStatus.OK);
    }

    @RequestMapping (value = "/_generatePDF", method = RequestMethod.POST, produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<Resource> generatePDF(@ModelAttribute final ILMSCaseSearchCriteria criteria) throws FileNotFoundException {
        log.info("Genreate PDF : START : " + criteria);
        ByteArrayInputStream bis = ilmsCaseService.generatePDF(criteria);
        String pdfName = criteria.getId() + ".pdf";
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + pdfName).contentType(MediaType.APPLICATION_PDF)
                             .body(new InputStreamResource(bis));

    }

    @PostMapping (value = "/_addChildCases")
    public ChildCase addChildCases(@Valid @RequestBody ChildCaseRequest ilmschildCaseRequest) {
        ChildCase ilmsCaseIds = ilmsCaseService.addChildCases(ilmschildCaseRequest);
        return ilmsCaseIds;
    }
}
