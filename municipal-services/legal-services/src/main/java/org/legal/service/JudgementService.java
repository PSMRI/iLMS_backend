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
import org.springframework.stereotype.Service;

import java.util.*;

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

    public JudgementRequest create(JudgementRequest judgementRequest) {
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
            if (legalConfiguration.getIsWorkflowEnabled()) {
                if (judgementRequest.getWorkflow().getAssignes() == null) {
                    List<String> users = new ArrayList<>();
                    users.add(judgementRequest.getRequestInfo().getUserInfo().getUuid());
                    judgementRequest.getWorkflow().setAssignes(users);
                }
                judgementRequest.getWorkflow().setBusinessService(legalConfiguration.getCreateJudgementWfName());
                workflowService.updateJudgementWorkflowStatus(judgementRequest);
            }
            producer.push(legalConfiguration.getCreateJudgementTopic(), judgementRequest);
        } else {
            throw new CustomException(LegalErrorConstants.HEARING_NOT_AVAILABLE, "Hearing is not Available for this Judgement");
        }
        return judgementRequest;
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

    public JudgementRequest updateJudgement(JudgementRequest judgementRequest) {
        String action = "";
        CaseRequest caseRequest = new CaseRequest();
        if (judgementRequest.getJudgement().getId() != null) {
            JudgementSearchCriteria criteria = JudgementSearchCriteria.builder()
                    .id(Collections.singletonList(judgementRequest.getJudgement().getId())).build();
            JudgementResponse judgementResponse = judgementRepository.getJudgementData(criteria);
            if (!judgementResponse.getJudgementList().isEmpty()) {
                List<Judgement> judgements = judgementResponse.getJudgementList();
                Judgement oldJudgement = judgements.get(0);
                JudgementRequest finalRequest = judgementRepository.getMappedData(judgementRequest, oldJudgement);
                judgementValidator.updateValidator(finalRequest.getJudgement(), judgementRequest);

                String caseId = judgementRequest.getJudgement().getCaseId();
                CaseSearchCriteria caseCriteria = CaseSearchCriteria.builder().id(Collections.singletonList(caseId)).build();
                CaseResponse caseResponse = caseRepository.getLegalCaseData(caseCriteria);
                caseRequest.setRequestInfo(judgementRequest.getRequestInfo());
                caseRequest.setCaseObj(caseResponse.getCaseList().get(0));

                if (Objects.nonNull(judgementRequest.getWorkflow())) {
                    if (legalConfiguration.getIsWorkflowEnabled()) {
                        judgementRequest.getWorkflow().setBusinessService(legalConfiguration.getCreateJudgementWfName());
                        workflowService.updateJudgementWorkflowStatus(judgementRequest);
                    }
                }
                Workflow workflow = new Workflow();
                workflow.setAssignes(judgementRequest.getWorkflow().getAssignes());
                caseRequest.setWorkflow(workflow);
                if (judgementRequest.getWorkflow().getAction().equalsIgnoreCase("JUDGEMENT_APPEALED_REVIEW")) {
                    action = "REVIEW_JUDGEMENT";
                    ProcessInstance workflowReq = caseUtils.changeCaseWF(caseRequest, action);
                    ProcessInstanceRequest workflowRequest = new ProcessInstanceRequest(caseRequest.getRequestInfo(), Collections.singletonList(workflowReq));
                    workflowService.callWorkFlow(workflowRequest);
                }
                if (judgementRequest.getWorkflow().getAction().equalsIgnoreCase("JUDGEMENT_COMPLETED")) {
                    action = "COMPLY_JUDGEMENT";
                    ProcessInstance workflowReq = caseUtils.changeCaseWF(caseRequest, action);
                    ProcessInstanceRequest workflowRequest = new ProcessInstanceRequest(caseRequest.getRequestInfo(), Collections.singletonList(workflowReq));
                    workflowService.callWorkFlow(workflowRequest);
                }
                producer.push(legalConfiguration.getUpdateJudgementTopic(), finalRequest);
            } else {
                throw new CustomException(LegalErrorConstants.JUDGEMENT_NOT_AVAILABLE, "Judgement is not Available");
            }
        } else {
            throw new CustomException(LegalErrorConstants.INVALID_TYPE_ERROR, "Id is mandatory");
        }
        return judgementRequest;
    }
}
