package org.ilms.service;

import java.util.ArrayList;
import java.util.List;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.ilms.configs.ILMSConfiguration;
import org.ilms.producer.Producer;
import org.ilms.repository.DocumentRepository;
import org.ilms.repository.ILMSCaseRepository;
import org.ilms.util.ILMSErrorConstants;
import org.ilms.validator.DocumentValidator;
import org.ilms.web.model.Document;
import org.ilms.web.model.DocumentResponse;
import org.ilms.web.model.DocumentSearchCriteria;
import org.ilms.web.model.ILMSCaseResponse;
import org.ilms.web.model.ILMSCaseSearchCriteria;
import org.ilms.web.model.IlmsDocumentRequest;
import org.ilms.web.model.enums.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DocumentService {
    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private DocumentValidator documentValidator;

    @Autowired
    private ILMSCaseRepository ilmsCaseRepository;

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

    public Document createDocument(IlmsDocumentRequest request, ILMSCaseSearchCriteria criteria) {
        documentValidator.createDocumentValidator(request);
        List<String> ids = new ArrayList<>();
        ids.add(request.getDocument().getCaseId());
        ILMSCaseSearchCriteria criteria1 = ILMSCaseSearchCriteria.builder().id(ids).build();
        ILMSCaseResponse caseResponse = ilmsCaseRepository.getILMSCaseData(criteria1);
        if (!caseResponse.getIlmsCases().isEmpty()) {
            caseResponse.getIlmsCases().forEach(ilmsCase -> {
                if (!request.getDocument().getCaseId().equalsIgnoreCase(ilmsCase.getId())) {
                    throw new CustomException(ILMSErrorConstants.CASE_NOT_AVAILABLE,
                            "Case Not Found For The CaseId  [ " + request.getDocument().getCaseId() + " ]");
                }
                request.getDocument().setStatus(Status.ACTIVE);
                documentEnrichmentService.enrichmentDocumentCreateRequest(request);
                producer.push(ilmsConfiguration.getCreateDocumentTopic(), request);
            });
        } else {
            throw new CustomException(ILMSErrorConstants.CASE_NOT_AVAILABLE,
                    "CaseList Not Found In The System [ " + caseResponse.getIlmsCases() + " ]");
        }
        return request.getDocument();
    }
}
