package org.ilms.repository;

import org.ilms.repository.querybuilder.JudgementQueryBuilder;
import org.ilms.repository.rowmapper.JudgementRowMapper;
import org.ilms.service.JudgementEnrichmentService;
import org.ilms.util.CaseUtils;
import org.ilms.util.CommonUtils;
import org.ilms.web.model.JudgementRequest;
import org.ilms.web.model.JudgementResponse;
import org.ilms.web.model.JudgementSearchCriteria;
import org.ilms.web.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Repository
public class JudgementRepository {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    CaseUtils caseUtils;

    @Autowired
    private JudgementQueryBuilder judgementQueryBuilder;

    @Autowired
    private JudgementRowMapper judgementRowMapper;

    @Autowired
    private JudgementEnrichmentService judgementEnrichmentService;

    @Autowired
    private CommonUtils commonUtils;

    public JudgementResponse getJudgementData(JudgementSearchCriteria judgementSearchCriteria) {
        List<Object> preparedStmtList = new ArrayList<>();
        String query = judgementQueryBuilder.getFSMSearchQuery(judgementSearchCriteria, preparedStmtList);
        List<Order> orders = jdbcTemplate.query(query, preparedStmtList.toArray(), judgementRowMapper);
        JudgementResponse judgementResponse = JudgementResponse.builder().orderList(orders).totalCount(judgementRowMapper.getFull_count())
                .build();
        return judgementResponse;
    }

    public JudgementRequest getMappedData(JudgementRequest request, Order oldOrder) {
        final String tenantId = getTenantIdFromJudgement(request.getOrder().getId());
        JudgementRequest updatedJudgementRequest = new JudgementRequest();
        updatedJudgementRequest.setRequestInfo(request.getRequestInfo());
        updatedJudgementRequest.setWorkflow(request.getWorkflow());
        if (!StringUtils.isEmpty(request.getOrder().getCaseId())) {
            oldOrder.setCaseId(request.getOrder().getCaseId());
        }
        if (!StringUtils.isEmpty(request.getOrder().getTenantId())) {
            oldOrder.setTenantId(request.getOrder().getTenantId());
        }
        if (!StringUtils.isEmpty(request.getOrder().getOrderType())) {
            oldOrder.setOrderType(request.getOrder().getOrderType());
        }
        if (!StringUtils.isEmpty(request.getOrder().getOrderDate())) {
            oldOrder.setOrderDate(request.getOrder().getOrderDate());
        }
        if (!StringUtils.isEmpty(request.getOrder().getDecisionStatus())) {
            List<String> uuids = new ArrayList<>();
            uuids.add(request.getRequestInfo().getUserInfo().getUuid());
            if (commonUtils.isUserOIC(uuids, tenantId, "DecisionStatus")) {
                oldOrder.setDecisionStatus(request.getOrder().getDecisionStatus());
            }
        }
        if (!StringUtils.isEmpty(request.getOrder().getComplianceDate())) {
            oldOrder.setComplianceDate(request.getOrder().getComplianceDate());
        }
        if (!StringUtils.isEmpty(request.getOrder().getRevisedComplianceDate())) {
            oldOrder.setRevisedComplianceDate(request.getOrder().getRevisedComplianceDate());
        }
        if (!StringUtils.isEmpty(request.getOrder().getOrderNoOverride())) {
            oldOrder.setOrderNoOverride(request.getOrder().getOrderNoOverride());
        }
        if (!StringUtils.isEmpty(request.getOrder().getRevisedComplainceReason())) {
            oldOrder.setRevisedComplainceReason(request.getOrder().getRevisedComplainceReason());
        }
        if (!StringUtils.isEmpty(request.getOrder().getComplianceStatus())) {
            List<String> uuids = new ArrayList<>();
            uuids.add(request.getRequestInfo().getUserInfo().getUuid());
            if (commonUtils.isUserOIC(uuids, tenantId, "ComplianceStatus")) {
                oldOrder.setComplianceStatus(request.getOrder().getComplianceStatus());
            }
        }
        if (!StringUtils.isEmpty(request.getOrder().getRemarks())) {
            oldOrder.setRemarks(request.getOrder().getRemarks());
        }
        if (!StringUtils.isEmpty(request.getOrder().getAdditionalDetails())) {
            oldOrder.setAdditionalDetails(request.getOrder().getAdditionalDetails());
        }
        if (!StringUtils.isEmpty(request.getOrder().getStatus())) {
            oldOrder.setStatus(request.getOrder().getStatus());
        }
        updatedJudgementRequest.setOrder(oldOrder);
        judgementEnrichmentService.enrichJugmentUpdateRequest(updatedJudgementRequest);
        return updatedJudgementRequest;
    }

    public String getTenantIdFromJudgement(String id) {

        List<String> preparedStmtList = new ArrayList<>();
        preparedStmtList.add(id);
        List<String> tenantId = jdbcTemplate.query(judgementQueryBuilder.getTenantIdFromHearingQuery(), preparedStmtList.toArray(),
                new SingleColumnRowMapper<>(String.class));
        return tenantId.get(0);
    }
}

