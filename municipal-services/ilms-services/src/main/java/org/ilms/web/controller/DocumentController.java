package org.ilms.web.controller;

import java.util.List;
import javax.validation.Valid;
import org.ilms.service.DocumentService;
import org.ilms.util.ResponseInfoFactory;
import org.ilms.web.model.Document;
import org.ilms.web.model.DocumentResponse;
import org.ilms.web.model.DocumentSearchCriteria;
import org.ilms.web.model.CaseSearchCriteria;
import org.ilms.web.model.DocumentRequest;
import org.ilms.web.model.RequestInfoWrapper;
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
@RequestMapping ("/document")
@Log4j2
@CrossOrigin (origins = "*", allowedHeaders = "*")
public class DocumentController {
    @Autowired
    private ResponseInfoFactory responseInfoFactory;

    @Autowired
    private DocumentService documentService;

    @PostMapping (value = "/_search")
    public ResponseEntity<DocumentResponse> search(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
            @Valid @ModelAttribute DocumentSearchCriteria criteria) {
        log.info("DocumentController :: search() : START ");
        DocumentResponse response = documentService.documentSearch(criteria, requestInfoWrapper.getRequestInfo());
        response.setResponseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true));
        log.info("DocumentController :: search() : END With Response [ " + response + " ]");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping (value = "/_create")
    public ResponseEntity<DocumentResponse> createDocumet(@Valid @RequestBody DocumentRequest request,
            @Valid @ModelAttribute CaseSearchCriteria criteria) {
        List<Document> document = documentService.createDocument(request, criteria);
        DocumentResponse response = DocumentResponse.builder().documents(document).build();
        log.info("DocumentController :: search() : END With Response [ " + response + " ]");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
