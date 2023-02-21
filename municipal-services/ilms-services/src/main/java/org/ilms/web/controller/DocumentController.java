package org.ilms.web.controller;

import lombok.extern.log4j.Log4j2;
import org.ilms.service.DocumentService;
import org.ilms.util.ResponseInfoFactory;
import org.ilms.web.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/document")
@Log4j2
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class DocumentController {
    @Autowired
    private ResponseInfoFactory responseInfoFactory;

    @Autowired
    private DocumentService documentService;

    @PostMapping(value = "/_search")
    public ResponseEntity<DocumentResponse> search(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
                                                   @Valid @ModelAttribute DocumentSearchCriteria criteria) {
        log.info("DocumentController :: search() : START ");
        DocumentResponse response = documentService.documentSearch(criteria, requestInfoWrapper.getRequestInfo());
        response.setResponseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true));
        log.info("DocumentController :: search() : END With Response [ " + response + " ]");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/_create")
    public ResponseEntity<DocumentResponse> create(@Valid @RequestBody DocumentRequest request,
                                                   @Valid @ModelAttribute CaseSearchCriteria criteria) {
        List<Document> document = documentService.createDocument(request, criteria);
        DocumentResponse response = DocumentResponse.builder().documents(document).build();
        log.info("DocumentController :: search() : END With Response [ " + response + " ]");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/_update")
    public ResponseEntity<DocumentResponse> update(@Valid @RequestBody DocumentRequest request, @Valid @ModelAttribute DocumentSearchCriteria criteria) {
        List<Document> document = documentService.updateDocument(request, criteria);
        DocumentResponse response = DocumentResponse.builder().documents(document).build();
        log.info("DocumentController :: search() : END With Response [ " + response + " ]");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
