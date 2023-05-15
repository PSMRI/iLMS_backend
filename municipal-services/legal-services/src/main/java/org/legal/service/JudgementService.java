package org.legal.service;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.legal.configs.LEGALConfiguration;
import org.legal.producer.Producer;
import org.legal.repository.HearingRepository;
import org.legal.repository.JudgementRepository;
import org.legal.util.LegalErrorConstants;
import org.legal.validator.JudgementValidator;
import org.legal.web.model.*;
import org.legal.web.model.enums.CreationReason;
import org.legal.web.model.enums.Status;
import org.legal.web.model.workflow.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Service
public class JudgementService {
    @Autowired
    JudgementEnrichmentService judgementEnrichmentService;

    @Autowired
    Producer producer;

    @Autowired
    LEGALConfiguration ilmsConfiguration;

    @Autowired
    JudgementRepository judgementRepository;

    @Autowired
    JudgementValidator judgementValidator;

    @Autowired
    HearingRepository hearingRepository;

    @Autowired
    private WorkflowService workflowService;

    public Judgement create(JudgementRequest judgementRequest) {
        HearingResponse hearingResponse = null;
        HearingSearchCriteria criteria = HearingSearchCriteria.builder()
                .caseId(Collections.singletonList(judgementRequest.getJudgement().getCaseId())).build();
        hearingResponse = hearingRepository.getHearingDetails(criteria);
        String tenantId = hearingResponse.getHearingList().get(0).getTenantId();
        judgementRequest.getJudgement().setTenantId(tenantId);
        if (!hearingResponse.getHearingList().isEmpty()) {
            judgementRequest.getJudgement().setStatus(Status.ACTIVE);
            judgementValidator.createValidator(judgementRequest);
            judgementEnrichmentService.enrichJudgementCreateRequest(judgementRequest);
            if (ilmsConfiguration.getIsWorkflowEnabled()) {
                workflowService.updateWorkflowForJudgement(judgementRequest, CreationReason.CREATE);
            }
            producer.push(ilmsConfiguration.getCreateJudgementTopic(), judgementRequest);
        } else {
            throw new CustomException(LegalErrorConstants.HEARING_NOT_AVAILABLE, "Hearing is not Available for this Judgement");
        }
        return judgementRequest.getJudgement();
    }

    public JudgementResponse JudgementSearch(JudgementSearchCriteria criteria, RequestInfo requestInfo) {
        List<Judgement> judgements = new LinkedList<>();
        JudgementResponse judgementResponse = null;
        judgementResponse = judgementRepository.getJudgementData(criteria);
        judgements = judgementResponse.getJudgementList();
        if (!judgements.isEmpty()) {
            judgementEnrichmentService.enrichJudgementSearch();
        } else {
            throw new CustomException(LegalErrorConstants.JUDGEMENT_NOT_AVAILABLE, "Judgement is not Available");
        }
        return judgementResponse;
    }

    public Judgement updateJudgement(JudgementRequest judgementRequest) {
        if (judgementRequest.getJudgement().getId() != null) {
            JudgementSearchCriteria criteria = JudgementSearchCriteria.builder()
                    .id(Collections.singletonList(judgementRequest.getJudgement().getId())).build();
            JudgementResponse judgementResponse = judgementRepository.getJudgementData(criteria);
            if (!judgementResponse.getJudgementList().isEmpty()) {
                List<Judgement> judgements = judgementResponse.getJudgementList();
                Judgement oldJudgement = judgements.get(0);
                JudgementRequest finalRequest = judgementRepository.getMappedData(judgementRequest, oldJudgement);
                judgementValidator.updateValidator(finalRequest.getJudgement(), judgementRequest);
                if (Objects.nonNull(judgementRequest.getJudgement().getWorkflow())) {
                    processUpdateForJudgement(judgementRequest, finalRequest.getJudgement());
                }
                    producer.push(ilmsConfiguration.getUpdateJudgementTopic(), finalRequest);
            }else {
                throw new CustomException(LegalErrorConstants.JUDGEMENT_NOT_AVAILABLE, "Judgement is not Available");
            }
        } else {
            throw new CustomException(LegalErrorConstants.INVALID_TYPE_ERROR, "Id is mandatory");
        }
        return judgementRequest.getJudgement();
    }

    private void processUpdateForJudgement(JudgementRequest request, Judgement judgement) {
        if (ilmsConfiguration.getIsWorkflowEnabled()) {
            State state = workflowService.updateWorkflowForJudgement(request, CreationReason.UPDATE);
            if (state.getIsStartState() && state.getApplicationStatus().equalsIgnoreCase(Status.ACTIVE.toString()) && !judgement.getStatus()
                                                                                                                            .equals(Status.ACTIVE)) {
            }
        }
    }
}
