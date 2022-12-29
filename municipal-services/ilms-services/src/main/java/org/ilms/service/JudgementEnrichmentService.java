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
import org.ilms.repository.CaseRepository;
import org.ilms.repository.IdGenRepository;
import org.ilms.util.CaseUtils;
import org.ilms.util.ILMSErrorConstants;
import org.ilms.web.model.AuditDetails;
import org.ilms.web.model.CaseResponse;
import org.ilms.web.model.CaseSearchCriteria;
import org.ilms.web.model.Judgement;
import org.ilms.web.model.JudgementRequest;
import org.ilms.web.model.JudgementSearchCriteria;
import org.ilms.web.model.idGen.IdResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class JudgementEnrichmentService {
    @Autowired
    private ILMSConfiguration config;

    @Autowired
    private CaseUtils caseUtils;

    @Autowired
    private IdGenRepository idGenRepository;

    @Autowired
    private CaseRepository caseRepository;

    public void enrichJudgementCreateRequest(JudgementRequest judgementRequest) {
        RequestInfo requestInfo = judgementRequest.getRequestInfo();
        Judgement judgement = judgementRequest.getJudgement();
        setIdgenIds(judgementRequest);
        AuditDetails auditDetails = caseUtils.getAuditDetails(judgementRequest.getRequestInfo().getUserInfo().getUserName(), true);
        judgementRequest.getJudgement().setAuditDetails(auditDetails);
        judgement.setAuditDetails(auditDetails);
    }

    public void enrichJugmentUpdateRequest(JudgementRequest request) {
        RequestInfo requestInfo = request.getRequestInfo();
        Judgement judgement = request.getJudgement();
        AuditDetails auditDetails = caseUtils.getAuditDetails(request.getJudgement().getId(), false);
        request.getJudgement().setAuditDetails(auditDetails);
        judgement.setAuditDetails(auditDetails);
    }

    private void setIdgenIds(JudgementRequest request) {
        RequestInfo requestInfo = request.getRequestInfo();
        CaseSearchCriteria criteria = CaseSearchCriteria.builder().id(Collections.singletonList(request.getJudgement().getCaseId())).build();
        CaseResponse caseResponse = caseRepository.getILMSCaseData(criteria);
        String tenantId = caseResponse.getCaseList().get(0).getTenantId();
        Judgement judgement = request.getJudgement();
        List<String> caseId = getIdList(requestInfo, tenantId, config.getJudgementIdgenName(), config.getJudgementIdgenFormat(), 1);
        ListIterator<String> caseItr = caseId.listIterator();
        Map<String, String> errorMap = new HashMap<>();
        if (!errorMap.isEmpty()) {
            throw new CustomException(errorMap);
        }
        judgement.setId(caseItr.next());
    }

    private List<String> getIdList(RequestInfo requestInfo, String tenantId, String idName, String idformat, int count) {
        List<IdResponse> idResponses = idGenRepository.getId(requestInfo, tenantId, idName, idformat, count).getIdResponses();
        if (CollectionUtils.isEmpty(idResponses)) {
            throw new CustomException(ILMSErrorConstants.IDGEN_ERROR, "No ids returned from idgen Service");
        }
        return idResponses.stream().map(IdResponse::getId).collect(Collectors.toList());
    }

    public void enrichJudgementSearch() {
        JudgementSearchCriteria judgementSearchCriteria = new JudgementSearchCriteria();
        judgementSearchCriteria.setId(judgementSearchCriteria.getId());
        judgementSearchCriteria.setCaseId(judgementSearchCriteria.getCaseId());
    }
}


