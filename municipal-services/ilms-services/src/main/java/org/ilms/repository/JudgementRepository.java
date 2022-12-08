package org.ilms.repository;

import java.util.ArrayList;
import java.util.List;
import org.ilms.repository.querybuilder.JudgementQueryBuilder;
import org.ilms.repository.rowmapper.JudgementRowMapper;
import org.ilms.service.JudgementEnrichmentService;
import org.ilms.util.CaseUtils;
import org.ilms.web.model.Judgement;
import org.ilms.web.model.JudgementRequest;
import org.ilms.web.model.JudgementResponse;
import org.ilms.web.model.JudgementSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

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

    public JudgementResponse getJudgementData(JudgementSearchCriteria judgementSearchCriteria) {
        List<Object> preparedStmtList = new ArrayList<>();
        String query = judgementQueryBuilder.getFSMSearchQuery(judgementSearchCriteria, preparedStmtList);
        List<Judgement> judgements = jdbcTemplate.query(query, preparedStmtList.toArray(), judgementRowMapper);
        JudgementResponse judgementResponse = JudgementResponse.builder().judgements(judgements).totalCount(judgementRowMapper.getFull_count())
                                                               .build();
        return judgementResponse;
    }

    public JudgementRequest getMappedData(JudgementRequest request, Judgement oldJudgement) {
        JudgementRequest updatedJudgementRequest = new JudgementRequest();
        updatedJudgementRequest.setRequestInfo(request.getRequestInfo());
        updatedJudgementRequest.setWorkflow(request.getWorkflow());
        if (!StringUtils.isEmpty(request.getJudgement().getCaseId())) {
            oldJudgement.setCaseId(request.getJudgement().getCaseId());
        }
        if (!StringUtils.isEmpty(request.getJudgement().getOrderType())) {
            oldJudgement.setOrderType(request.getJudgement().getOrderType());
        }
        if (!StringUtils.isEmpty(request.getJudgement().getOrderDate())) {
            oldJudgement.setOrderDate(request.getJudgement().getOrderDate());
        }
        if (!StringUtils.isEmpty(request.getJudgement().getDecisionStatus())) {
            oldJudgement.setDecisionStatus(request.getJudgement().getDecisionStatus());
        }
        if (!StringUtils.isEmpty(request.getJudgement().getComplianceDate())) {
            oldJudgement.setComplianceDate(request.getJudgement().getComplianceDate());
        }
        if (!StringUtils.isEmpty(request.getJudgement().getRevisedComplianceDate())) {
            oldJudgement.setRevisedComplianceDate(request.getJudgement().getRevisedComplianceDate());
        }
        if (!StringUtils.isEmpty(request.getJudgement().getOrderNoOverride())) {
            oldJudgement.setOrderNoOverride(request.getJudgement().getOrderNoOverride());
        }
        if (!StringUtils.isEmpty(request.getJudgement().getRevisedComplainceReason())) {
            oldJudgement.setRevisedComplainceReason(request.getJudgement().getRevisedComplainceReason());
        }
        if (!StringUtils.isEmpty(request.getJudgement().getComplianceStatus())) {
            oldJudgement.setComplianceStatus(request.getJudgement().getComplianceStatus());
        }
        if (!StringUtils.isEmpty(request.getJudgement().getRemarks())) {
            oldJudgement.setRemarks(request.getJudgement().getRemarks());
        }
        if (!StringUtils.isEmpty(request.getJudgement().getAdditionalDetails())) {
            oldJudgement.setAdditionalDetails(request.getJudgement().getAdditionalDetails());
        }
        if (!StringUtils.isEmpty(request.getJudgement().getStatus())) {
            oldJudgement.setStatus(request.getJudgement().getStatus());
        }
        updatedJudgementRequest.setJudgement(oldJudgement);
        judgementEnrichmentService.enrichJugmentUpdateRequest(updatedJudgementRequest);
        return updatedJudgementRequest;
    }
}

