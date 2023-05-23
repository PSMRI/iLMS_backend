package org.legal.service;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.legal.configs.LEGALConfiguration;
import org.legal.producer.Producer;
import org.legal.repository.CaseRepository;
import org.legal.repository.HearingRepository;
import org.legal.repository.JudgementRepository;
import org.legal.util.CaseUtils;
import org.legal.util.LegalErrorConstants;
import org.legal.validator.JudgementValidator;
import org.legal.web.model.*;
import org.legal.web.model.enums.CreationReason;
import org.legal.web.model.enums.Status;
import org.legal.web.model.workflow.ProcessInstance;
import org.legal.web.model.workflow.ProcessInstanceRequest;
import org.legal.web.model.workflow.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Service
public class JudgementService {
    @Autowired
    private JudgementEnrichmentService judgementEnrichmentService;

    @Autowired
    private Producer producer;

    @Autowired
    private LEGALConfiguration legalConfiguration;

    @Autowired
    private JudgementRepository judgementRepository;

    @Autowired
    private JudgementValidator judgementValidator;

    @Autowired
    private HearingRepository hearingRepository;

    @Autowired
    private CaseRepository caseRepository;

    @Autowired
    private WorkflowService workflowService;

    @Autowired
    private CaseUtils caseUtils;

    public Judgement create(JudgementRequest judgementRequest) {
        HearingResponse hearingResponse = null;
        String action = "";
        CaseRequest caseRequest = new CaseRequest();
        HearingSearchCriteria criteria = HearingSearchCriteria.builder()
                .caseId(Collections.singletonList(judgementRequest.getJudgement().getCaseId())).build();
        hearingResponse = hearingRepository.getHearingDetails(criteria);
        if (!hearingResponse.getHearingList().isEmpty()) {
            String tenantId = hearingResponse.getHearingList().get(0).getTenantId();
            judgementRequest.getJudgement().setTenantId(tenantId);
            judgementRequest.getJudgement().setStatus(Status.ACTIVE);
            judgementValidator.createValidator(judgementRequest);
            judgementEnrichmentService.enrichJudgementCreateRequest(judgementRequest);

            String caseId = judgementRequest.getJudgement().getCaseId();
            CaseSearchCriteria caseCriteria = CaseSearchCriteria.builder().id(Collections.singletonList(caseId)).build();
            CaseResponse caseResponse = caseRepository.getLegalCaseData(caseCriteria);
            caseRequest.setRequestInfo(judgementRequest.getRequestInfo());
            caseRequest.setCaseObj(caseResponse.getCaseList().get(0));
            if (legalConfiguration.getIsWorkflowEnabled()) {
                workflowService.updateWorkflowForJudgement(judgementRequest, CreationReason.CREATE);
            }
            ProcessInstance wf = null != caseRequest.getCaseObj().getWorkflow() ? caseRequest.getCaseObj().getWorkflow() : new ProcessInstance();
            wf.setAssignes(judgementRequest.getJudgement().getWorkflow().getAssignes());
            caseRequest.getCaseObj().setWorkflow(wf);
            if (judgementRequest.getJudgement().getWorkflow().getAction().equalsIgnoreCase("JUDGEMENT_APPEALED_REVIEW")) {
                action = "REVIEW_JUDGEMENT";
                ProcessInstanceRequest workflowReq = caseUtils.changeCaseWF(caseRequest, action);
                workflowService.callWorkFlow(workflowReq);
            }
            if (judgementRequest.getJudgement().getWorkflow().getAction().equalsIgnoreCase("JUDGEMENT_COMPLETED")) {
                action = "COMPLY_JUDGEMENT";
                ProcessInstanceRequest workflowReq = caseUtils.changeCaseWF(caseRequest, action);
                workflowService.callWorkFlow(workflowReq);
            }

            producer.push(legalConfiguration.getCreateJudgementTopic(), judgementRequest);
        } else {
            throw new CustomException(LegalErrorConstants.HEARING_NOT_AVAILABLE, "Hearing is not Available for this Judgement" );
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
                producer.push(legalConfiguration.getUpdateJudgementTopic(), finalRequest);
            } else {
                throw new CustomException(LegalErrorConstants.JUDGEMENT_NOT_AVAILABLE, "Judgement is not Available");
            }
        } else {
            throw new CustomException(LegalErrorConstants.INVALID_TYPE_ERROR, "Id is mandatory");
        }
        return judgementRequest.getJudgement();
    }

    private void processUpdateForJudgement(JudgementRequest request, Judgement judgement) {
        if (legalConfiguration.getIsWorkflowEnabled()) {
            State state = workflowService.updateWorkflowForJudgement(request, CreationReason.UPDATE);
            if (state.getIsStartState() && state.getApplicationStatus().equalsIgnoreCase(Status.ACTIVE.toString()) && !judgement.getStatus()
                    .equals(Status.ACTIVE)) {
            }
        }
    }
}
