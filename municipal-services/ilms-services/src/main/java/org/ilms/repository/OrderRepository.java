package org.ilms.repository;

import org.ilms.repository.querybuilder.JudgementQueryBuilder;
import org.ilms.repository.rowmapper.JudgementRowMapper;
import org.ilms.service.OrderEnrichmentService;
import org.ilms.util.CaseUtils;
import org.ilms.util.CommonUtils;
import org.ilms.web.model.Order;
import org.ilms.web.model.OrderRequest;
import org.ilms.web.model.OrderSearchCriteria;
import org.ilms.web.model.OrderSearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Repository
public class OrderRepository {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    CaseUtils caseUtils;

    @Autowired
    private JudgementQueryBuilder judgementQueryBuilder;

    @Autowired
    private JudgementRowMapper judgementRowMapper;

    @Autowired
    private OrderEnrichmentService orderEnrichmentService;

    @Autowired
    private CommonUtils commonUtils;

    public OrderSearchResponse getOrderData(OrderSearchCriteria judgementSearchCriteria) {
        List<Object> preparedStmtList = new ArrayList<>();
        String query = judgementQueryBuilder.getFSMSearchQuery(judgementSearchCriteria, preparedStmtList);
        List<Order> orders = jdbcTemplate.query(query, preparedStmtList.toArray(), judgementRowMapper);
        OrderSearchResponse orderResponse = OrderSearchResponse.builder().orderList(orders).totalCount(judgementRowMapper.getFull_count())
                .build();
        return orderResponse;
    }

    public OrderRequest getMappedData(OrderRequest request, Order oldOrder) {
        final String tenantId = getTenantIdFromJudgement(request.getOrder().getId());
        OrderRequest updatedOrderRequest = new OrderRequest();
        updatedOrderRequest.setRequestInfo(request.getRequestInfo());
        updatedOrderRequest.setWorkflow(request.getWorkflow());
        if (!StringUtils.isEmpty(request.getOrder().getCaseId())) {
            oldOrder.setCaseId(request.getOrder().getCaseId());
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
        updatedOrderRequest.setOrder(oldOrder);
        orderEnrichmentService.enrichJugmentUpdateRequest(updatedOrderRequest);
        return updatedOrderRequest;
    }

    public String getTenantIdFromJudgement(String id) {

        List<String> preparedStmtList = new ArrayList<>();
        preparedStmtList.add(id);
        List<String> tenantId = jdbcTemplate.query(judgementQueryBuilder.getTenantIdFromHearingQuery(), preparedStmtList.toArray(),
                new SingleColumnRowMapper<>(String.class));
        return tenantId.get(0);
    }
}

