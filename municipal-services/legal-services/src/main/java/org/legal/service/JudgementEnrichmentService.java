package org.legal.service;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.legal.configs.LEGALConfiguration;
import org.legal.repository.CaseRepository;
import org.legal.repository.IdGenRepository;
import org.legal.util.CaseUtils;
import org.legal.util.LegalErrorConstants;
import org.legal.web.model.*;
import org.legal.web.model.idGen.IdResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class JudgementEnrichmentService {
    @Autowired
    private LEGALConfiguration config;

    @Autowired
    private CaseUtils caseUtils;

    @Autowired
    private IdGenRepository idGenRepository;

    @Autowired
    private CaseRepository caseRepository;

    public void enrichJudgementCreateRequest(JudgementRequest judgementRequest) {
        Judgement judgement = judgementRequest.getJudgement();
        setIdgenIds(judgementRequest);
        AuditDetails auditDetails = caseUtils.getAuditDetails(judgementRequest.getRequestInfo().getUserInfo().getUuid(), true);
        judgementRequest.getJudgement().setAuditDetails(auditDetails);
        judgement.setAuditDetails(auditDetails);
    }

    public void enrichJudgmentUpdateRequest(JudgementRequest request) {

        Judgement judgement = request.getJudgement();
        AuditDetails auditDetails = caseUtils.getAuditDetails(request.getRequestInfo().getUserInfo().getUuid(), false);
        request.getJudgement().setAuditDetails(auditDetails);
        judgement.setAuditDetails(auditDetails);
    }

    private void setIdgenIds(JudgementRequest request) {
        RequestInfo requestInfo = request.getRequestInfo();
        CaseSearchCriteria criteria = CaseSearchCriteria.builder().id(Collections.singletonList(request.getJudgement().getCaseId())).build();
        CaseResponse caseResponse = caseRepository.getLegalCaseData(criteria);
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
            throw new CustomException(LegalErrorConstants.IDGEN_ERROR, "No ids returned from idgen Service");
        }
        return idResponses.stream().map(IdResponse::getId).collect(Collectors.toList());
    }

    public void enrichJudgementSearch() {
        JudgementSearchCriteria judgementSearchCriteria = new JudgementSearchCriteria();
        judgementSearchCriteria.setId(judgementSearchCriteria.getId());
        judgementSearchCriteria.setCaseId(judgementSearchCriteria.getCaseId());
    }
}


