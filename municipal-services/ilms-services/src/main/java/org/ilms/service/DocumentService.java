package org.ilms.service;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.ilms.configs.ILMSConfiguration;
import org.ilms.producer.Producer;
import org.ilms.repository.CaseRepository;
import org.ilms.repository.DocumentRepository;
import org.ilms.util.ILMSErrorConstants;
import org.ilms.validator.DocumentValidator;
import org.ilms.web.model.*;
import org.ilms.web.model.enums.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentService {
    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private DocumentValidator documentValidator;

    @Autowired
    private CaseRepository caseRepository;

    @Autowired
    private DocumentEnrichmentService documentEnrichmentService;

    @Autowired
    private Producer producer;

    @Autowired
    private ILMSConfiguration ilmsConfiguration;

    public DocumentResponse documentSearch(DocumentSearchCriteria criteria, RequestInfo requestInfo) {

        List<Document> documentList = new ArrayList<>();
        DocumentResponse documentResponse = null;
        documentValidator.validDocSearch(criteria);
        documentResponse = documentRepository.getDocumentsData(criteria);
        documentList = documentResponse.getDocuments();
        return documentResponse;
    }

    public List<Document> createDocument(DocumentRequest request, CaseSearchCriteria criteria) {
        documentValidator.createDocumentValidator(request);
        request.getDocument().forEach(document -> {
            List<String> ids = new ArrayList<>();
            ids.add(document.getCaseId());
            CaseSearchCriteria criteria1 = CaseSearchCriteria.builder().id(ids).build();
            CaseSearchResponse caseResponse = caseRepository.getILMSCaseData(criteria1);
            if (!caseResponse.getCaseList().isEmpty()) {
                caseResponse.getCaseList().forEach(ilmsCase -> {
                    if (!document.getCaseId().equalsIgnoreCase(ilmsCase.getId())) {
                        throw new CustomException(ILMSErrorConstants.CASE_NOT_AVAILABLE,
                                "Case Not Found For The CaseId  [ " + document.getCaseId() + " ]");
                    }
                    document.setStatus(Status.ACTIVE);
                    documentEnrichmentService.enrichmentDocumentCreateRequest(request);
                });
            } else {
                throw new CustomException(ILMSErrorConstants.CASE_NOT_AVAILABLE,
                        "CaseList Not Found In The System [ " + caseResponse.getCaseList() + " ]");
            }
        });
        producer.push(ilmsConfiguration.getCreateDocumentTopic(), request);
        return request.getDocument();
    }
}
