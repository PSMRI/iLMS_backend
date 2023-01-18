package org.ilms.service;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.ilms.configs.ILMSConfiguration;
import org.ilms.repository.CaseRepository;
import org.ilms.repository.IdGenRepository;
import org.ilms.util.CaseUtils;
import org.ilms.util.ILMSErrorConstants;
import org.ilms.web.model.*;
import org.ilms.web.model.idGen.IdResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderEnrichmentService {
    @Autowired
    private ILMSConfiguration config;

    @Autowired
    private CaseUtils caseUtils;

    @Autowired
    private IdGenRepository idGenRepository;

    @Autowired
    private CaseRepository caseRepository;

    public void enrichOrderCreateRequest(OrderRequest orderRequest) {
        RequestInfo requestInfo = orderRequest.getRequestInfo();
        Order order = orderRequest.getOrder();
        setIdgenIds(orderRequest);
        AuditDetails auditDetails = caseUtils.getAuditDetails(orderRequest.getRequestInfo().getUserInfo().getUserName(), true);
        orderRequest.getOrder().setAuditDetails(auditDetails);
        order.setAuditDetails(auditDetails);
    }

    public void enrichJugmentUpdateRequest(OrderRequest request) {
        RequestInfo requestInfo = request.getRequestInfo();
        Order order = request.getOrder();
        AuditDetails auditDetails = caseUtils.getAuditDetails(request.getOrder().getId(), false);
        request.getOrder().setAuditDetails(auditDetails);
        order.setAuditDetails(auditDetails);
    }

    private void setIdgenIds(OrderRequest request) {
        RequestInfo requestInfo = request.getRequestInfo();
        CaseSearchCriteria criteria = CaseSearchCriteria.builder().id(Collections.singletonList(request.getOrder().getCaseId())).build();
        CaseSearchResponse caseResponse = caseRepository.getILMSCaseData(criteria);
        String tenantId = caseResponse.getCaseList().get(0).getTenantId();
        Order order = request.getOrder();
        List<String> caseId = getIdList(requestInfo, tenantId, config.getJudgementIdgenName(), config.getJudgementIdgenFormat(), 1);
        ListIterator<String> caseItr = caseId.listIterator();
        Map<String, String> errorMap = new HashMap<>();
        if (!errorMap.isEmpty()) {
            throw new CustomException(errorMap);
        }
        order.setId(caseItr.next());
    }

    private List<String> getIdList(RequestInfo requestInfo, String tenantId, String idName, String idformat, int count) {
        List<IdResponse> idResponses = idGenRepository.getId(requestInfo, tenantId, idName, idformat, count).getIdResponses();
        if (CollectionUtils.isEmpty(idResponses)) {
            throw new CustomException(ILMSErrorConstants.IDGEN_ERROR, "No ids returned from idgen Service");
        }
        return idResponses.stream().map(IdResponse::getId).collect(Collectors.toList());
    }

    public void enrichOrderSearch() {
        OrderSearchCriteria judgementSearchCriteria = new OrderSearchCriteria();
        judgementSearchCriteria.setId(judgementSearchCriteria.getId());
        judgementSearchCriteria.setCaseId(judgementSearchCriteria.getCaseId());
    }
}


