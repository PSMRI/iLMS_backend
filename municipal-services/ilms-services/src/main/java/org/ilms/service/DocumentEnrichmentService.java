package org.ilms.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.stream.Collectors;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.ilms.configs.ILMSConfiguration;
import org.ilms.repository.ILMSCaseRepository;
import org.ilms.repository.IdGenRepository;
import org.ilms.util.CaseUtils;
import org.ilms.util.ILMSErrorConstants;
import org.ilms.web.model.AuditDetails;
import org.ilms.web.model.Document;
import org.ilms.web.model.ILMSCaseResponse;
import org.ilms.web.model.ILMSCaseSearchCriteria;
import org.ilms.web.model.IlmsDocumentRequest;
import org.ilms.web.model.idGen.IdResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DocumentEnrichmentService {
    @Autowired
    CaseUtils caseUtils;

    @Autowired
    private ILMSConfiguration config;

    @Autowired
    private IdGenRepository idGenRepository;

    @Autowired
    private ILMSCaseRepository ilmsCaseRepository;

    public void enrichmentDocumentCreateRequest(IlmsDocumentRequest request) {
        RequestInfo requestInfo = request.getRequestInfo();
        request.getDocument().forEach(document -> {
        setIdgenIds(request);
        AuditDetails auditDetails = caseUtils.getAuditDetails(requestInfo.getUserInfo().getUserName(), true);
       document.setAuditDetails(auditDetails);
        document.setAuditDetails(auditDetails);
            document.setAuditDetails(auditDetails);
        });
    }

    public void setIdgenIds(IlmsDocumentRequest request) {
        RequestInfo requestInfo = request.getRequestInfo();
        request.getDocument().forEach(document -> {
        ILMSCaseSearchCriteria criteria = ILMSCaseSearchCriteria.builder().id(Collections.singletonList(document.getCaseId())).build();
        ILMSCaseResponse ilmsCaseResponse = ilmsCaseRepository.getILMSCaseData(criteria);
            if (ilmsCaseResponse.getIlmsCases().size() <= 0){
                throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                        "caseDetails Not Found [ " + ilmsCaseResponse.getIlmsCases()+ " ]");
            }
        String tenantId = ilmsCaseResponse.getIlmsCases().get(0).getTenantId();
        List<String> caseId = getIdList(requestInfo, tenantId, config.getDocumentIdGenName(), config.getDocumentIdGenFormat(), 1);
        ListIterator<String> caseItr = caseId.listIterator();
        Map<String, String> errorMap = new HashMap<>();
        if (!errorMap.isEmpty()) {
            throw new CustomException(errorMap);
        }
        document.setId(caseItr.next());
        });
    }

    private List<String> getIdList(RequestInfo requestInfo, String tenantId, String idName, String idformat, int count) {
        List<IdResponse> idResponses = idGenRepository.getId(requestInfo, tenantId, idName, idformat, count).getIdResponses();
        if (CollectionUtils.isEmpty(idResponses)) {
            throw new CustomException(ILMSErrorConstants.IDGEN_ERROR, "No ids returned from idgen Service");
        }
        return idResponses.stream().map(IdResponse::getId).collect(Collectors.toList());
    }
}
