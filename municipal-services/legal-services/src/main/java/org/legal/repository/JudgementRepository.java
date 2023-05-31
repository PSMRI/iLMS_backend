package org.legal.repository;

import org.egov.tracer.model.ServiceCallException;
import org.legal.repository.querybuilder.JudgementQueryBuilder;
import org.legal.repository.rowmapper.JudgementRowMapper;
import org.legal.service.JudgementEnrichmentService;
import org.legal.util.CaseUtils;
import org.legal.util.CommonUtils;
import org.legal.web.model.JudgementRequest;
import org.legal.web.model.JudgementResponse;
import org.legal.web.model.JudgementSearchCriteria;
import org.legal.web.model.Judgement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private RestTemplate restTemplate;

    public JudgementResponse getJudgementData(JudgementSearchCriteria judgementSearchCriteria) {
        List<Object> preparedStmtList = new ArrayList<>();
        String query = judgementQueryBuilder.getJudgementSearchQuery(judgementSearchCriteria, preparedStmtList);
        List<Judgement> judgements = jdbcTemplate.query(query, preparedStmtList.toArray(), judgementRowMapper);
        JudgementResponse judgementResponse = JudgementResponse.builder().judgementList(judgements).totalCount(judgementRowMapper.getFull_count())
                .build();
        return judgementResponse;
    }

    public JudgementRequest getMappedData(JudgementRequest request, Judgement oldJudgement) {
        final String tenantId = getTenantIdFromJudgement(request.getJudgement().getId());
        JudgementRequest updatedJudgementRequest = new JudgementRequest();
        updatedJudgementRequest.setRequestInfo(request.getRequestInfo());
        updatedJudgementRequest.setWorkflow(request.getWorkflow());
        if (!StringUtils.isEmpty(request.getJudgement().getCaseId())) {
            oldJudgement.setCaseId(request.getJudgement().getCaseId());
        }
        if (!StringUtils.isEmpty(request.getJudgement().getTenantId())) {
            oldJudgement.setTenantId(request.getJudgement().getTenantId());
        }
        if (!StringUtils.isEmpty(request.getJudgement().getOrderType())) {
            oldJudgement.setOrderType(request.getJudgement().getOrderType());
        }
        if (Objects.nonNull(request.getJudgement().getOrderDate())) {
            oldJudgement.setOrderDate(request.getJudgement().getOrderDate());
        }
        if (!StringUtils.isEmpty(request.getJudgement().getDecisionStatus())) {
            List<String> uuids = new ArrayList<>();
            uuids.add(request.getRequestInfo().getUserInfo().getUuid());
            if (commonUtils.isUserOIC(uuids, tenantId, "DecisionStatus")) {
                oldJudgement.setDecisionStatus(request.getJudgement().getDecisionStatus());
            }
        }
        if (Objects.nonNull(request.getJudgement().getComplianceDate())) {
            oldJudgement.setComplianceDate(request.getJudgement().getComplianceDate());
        }
        if (Objects.nonNull(request.getJudgement().getRevisedComplianceDate())) {
            oldJudgement.setRevisedComplianceDate(request.getJudgement().getRevisedComplianceDate());
        }
        if (!StringUtils.isEmpty(request.getJudgement().getOrderNoOverride())) {
            oldJudgement.setOrderNoOverride(request.getJudgement().getOrderNoOverride());
        }
        if (!StringUtils.isEmpty(request.getJudgement().getRevisedComplainceReason())) {
            oldJudgement.setRevisedComplainceReason(request.getJudgement().getRevisedComplainceReason());
        }
        if (!StringUtils.isEmpty(request.getJudgement().getComplianceStatus())) {
            List<String> uuids = new ArrayList<>();
            uuids.add(request.getRequestInfo().getUserInfo().getUuid());
            if (commonUtils.isUserOIC(uuids, tenantId, "ComplianceStatus")) {
                oldJudgement.setComplianceStatus(request.getJudgement().getComplianceStatus());
            }
        }
        if (!StringUtils.isEmpty(request.getJudgement().getRemarks())) {
            oldJudgement.setRemarks(request.getJudgement().getRemarks());
        }
        if (Objects.nonNull(request.getJudgement().getAdditionalDetails())) {
            oldJudgement.setAdditionalDetails(request.getJudgement().getAdditionalDetails());
        }
        if (!StringUtils.isEmpty(request.getJudgement().getStatus())) {
            oldJudgement.setStatus(request.getJudgement().getStatus());
        }
        updatedJudgementRequest.setJudgement(oldJudgement);
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

