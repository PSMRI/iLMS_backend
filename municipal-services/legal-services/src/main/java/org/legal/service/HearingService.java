package org.legal.service;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.legal.configs.LEGALConfiguration;
import org.legal.producer.Producer;
import org.legal.repository.CaseRepository;
import org.legal.repository.HearingRepository;
import org.legal.util.CaseUtils;
import org.legal.util.HearingUtils;
import org.legal.util.LegalErrorConstants;
import org.legal.validator.HearingValidator;
import org.legal.web.model.*;
import org.legal.web.model.enums.CreationReason;
import org.legal.web.model.enums.PartyType;
import org.legal.web.model.enums.Status;
import org.legal.web.model.workflow.ProcessInstance;
import org.legal.web.model.workflow.ProcessInstanceRequest;
import org.legal.web.model.workflow.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class HearingService {
    @Autowired
    HearingRepository hearingRepository;

    @Autowired
    private Producer producer;

    @Autowired
    private HearingEnrichmentService hearingEnrichmentService;

    @Autowired
    private LEGALConfiguration legalConfiguration;

    @Autowired
    private HearingRepository hearingDetailsRepository;

    @Autowired
    private HearingValidator hearingDetailsValidator;

    @Autowired
    private HearingUtils hearingUtils;

    @Autowired
    private CaseRepository caseRepository;

    @Autowired
    private WorkflowService workflowService;

    @Autowired
    private CaseUtils caseUtils;

    public Hearing create(HearingRequest hearingRequest) {
        CaseResponse caseResponse = null;
        CaseSearchCriteria criteria = CaseSearchCriteria.builder().id(Collections.singletonList(hearingRequest.getHearing().getCaseId())).build();
        caseResponse = caseRepository.getLegalCaseData(criteria);
        if (caseResponse.getCaseList().isEmpty()) {
            throw new CustomException(LegalErrorConstants.CASE_NOT_AVAILABLE, "Case is not Available.");
        }
     //   List<Party> partyList = hearingDetailsRepository.getGetFromPartyQuery(hearingRequest.getHearing().getCaseId());
//        for (Party party : partyList) {
//            if (party.getPartyType().equals(PartyType.RESPONDENT.toString())) {
//                respondentId = party.getId();
//            } else {
//                petitionerId = party.getId();
//            }
//        }
        if (Objects.nonNull(caseResponse.getCaseList())) {
            if (caseResponse.getCaseList().get(0).getNumber().equals(hearingRequest.getHearing().getCaseNumber())) {
                hearingRequest.getHearing().setStatus(Status.ACTIVE);
                if (Objects.nonNull(hearingRequest.getHearing().getPayment())) {
                    hearingRequest.getHearing().getPayment().setStatus(Status.ACTIVE);
                }
                hearingRequest.getHearing().setHearingNumber(hearingDetailsRepository.getMaxValueOfHearing(hearingRequest.getHearing().getCaseId()));
                hearingDetailsValidator.createValidator(hearingRequest);
                hearingEnrichmentService.enrichHearingCreateRequest(hearingRequest);
                if (legalConfiguration.getIsWorkflowEnabled()) {
                    workflowService.updateWorkflowForHearing(hearingRequest, CreationReason.CREATE);
                }
                producer.push(legalConfiguration.getCreateHearingTopic(), hearingRequest);
            } else {
                throw new CustomException(LegalErrorConstants.INVALID_TYPE_ERROR, "CaseNumber Invalid");
            }
        } else {
            throw new CustomException(LegalErrorConstants.CASE_NOT_AVAILABLE, "Case is not Available for this Hearing");
        }
        return hearingRequest.getHearing();
    }

    public HearingResponse hearingSearch(HearingSearchCriteria criteria, RequestInfo requestInfo) {
        List<Hearing> legalHearingList = new ArrayList<>();
        HearingResponse hearingResponse = null;
        hearingResponse = hearingDetailsRepository.getHearingDetails(criteria);
        if (!hearingResponse.getHearingList().isEmpty()) {

            return hearingResponse;

        } else {
            throw new CustomException(LegalErrorConstants.HEARING_NOT_AVAILABLE, "Hearing is not Available");
        }

    }

    public Hearing update(HearingRequest hearingDetailsRequest) {
        String action = "";
        if (hearingDetailsRequest.getHearing().getId() != null) {
            CaseRequest caseRequest = new CaseRequest();
            HearingSearchCriteria criteria = HearingSearchCriteria.builder().id((hearingDetailsRequest.getHearing().getCaseId())).build();
            HearingResponse hearingDetailsResponse = hearingDetailsRepository.getHearingDetails(criteria);
            HearingRequest updatedRequest = new HearingRequest();
            HearingRequest request = new HearingRequest();
            request.setRequestInfo(hearingDetailsRequest.getRequestInfo());
            if (!hearingDetailsResponse.getHearingList().isEmpty()) {
                List<Hearing> hearingList = hearingDetailsResponse.getHearingList();
                for (Hearing oldHearing : hearingList) {
                    request.setHearing(oldHearing);
                    if (oldHearing.getId().equals(hearingDetailsRequest.getHearing().getId())) {
                        updatedRequest = hearingUtils.prepareHearingDetailsModalForUpdate(hearingDetailsRequest, oldHearing);
                        hearingDetailsValidator.updateValidator(updatedRequest.getHearing(), hearingDetailsRequest);
                        if (Objects.nonNull(hearingDetailsRequest.getHearing().getWorkflow())) {
                            processUpdateForHearing(hearingDetailsRequest, updatedRequest.getHearing());
                        }
                    }
                    if (hearingDetailsRequest.getHearing().getWorkflow().getAction().equalsIgnoreCase("ASSIGNED_TO_RO") && oldHearing.getApplicationStatus().equalsIgnoreCase("PENDING_AT_DEC_FOR_NEXT_HEARING")) {
                        action = "REVIEW_TO_RO";
                        ProcessInstanceRequest workflowReq = hearingUtils.hearingWFUpdate(request, action);
                        workflowService.callWorkFlow(workflowReq);
                    }
                    if (hearingDetailsRequest.getHearing().getWorkflow().getAction().equalsIgnoreCase("ASSIGNED_TO_APPOINTED_OIC") && oldHearing.getApplicationStatus().equalsIgnoreCase("NEXT_HEARING_REVIEW_AT_RO")) {
                        action = "Approved";
                        ProcessInstanceRequest workflowReq = hearingUtils.hearingWFUpdate(request, action);
                        workflowService.callWorkFlow(workflowReq);
                    }
                    if (hearingDetailsRequest.getHearing().getWorkflow().getAction().equalsIgnoreCase("REVIEW_AND_ASSIGN_BACK_TO_DEC") && oldHearing.getApplicationStatus().equalsIgnoreCase("NEXT_HEARING_REVIEW_AT_RO")) {
                        action = "Reject";
                        ProcessInstanceRequest workflowReq = hearingUtils.hearingWFUpdate(request, action);
                        workflowService.callWorkFlow(workflowReq);
                    }
                }
                String caseId = hearingDetailsRequest.getHearing().getCaseId();
                CaseSearchCriteria caseCriteria = CaseSearchCriteria.builder().id(Collections.singletonList(caseId)).build();
                CaseResponse caseResponse = caseRepository.getLegalCaseData(caseCriteria);
                caseRequest.setRequestInfo(hearingDetailsRequest.getRequestInfo());
                caseRequest.setCaseObj(caseResponse.getCaseList().get(0));
                ProcessInstance wf = null != caseRequest.getCaseObj().getWorkflow() ? caseRequest.getCaseObj().getWorkflow() : new ProcessInstance();
                wf.setAssignes(hearingDetailsRequest.getHearing().getWorkflow().getAssignes());
                caseRequest.getCaseObj().setWorkflow(wf);
                if (hearingDetailsRequest.getHearing().getWorkflow().getAction().equalsIgnoreCase("Approved") && !hearingDetailsRequest.getHearing().getHearingType().equalsIgnoreCase("Final_Hearing")) {
                    action = "SUBMIT_SUPPLEMENTARY_AFFIDAVIT";
                    ProcessInstanceRequest workflowReq = caseUtils.changeCaseWF(caseRequest, action);
                    workflowService.callWorkFlow(workflowReq);
                } else if (hearingDetailsRequest.getHearing().getWorkflow().getAction().equalsIgnoreCase("Approved") && hearingDetailsRequest.getHearing().getHearingType().equalsIgnoreCase("Final_Hearing")) {
                    action = "PROCEED_WITH_JUDGEMENT";
                    ProcessInstanceRequest workflowReq = caseUtils.changeCaseWF(caseRequest, action);
                    workflowService.callWorkFlow(workflowReq);
                }
                producer.push(legalConfiguration.getUpdateHearingTopic(), updatedRequest);
            } else {
                throw new CustomException(LegalErrorConstants.HEARING_NOT_AVAILABLE, "Hearing is not Available");
            }
        } else {
            throw new CustomException(LegalErrorConstants.INVALID_TYPE_ERROR, "Id is mandatory");
        }
        return hearingDetailsRequest.getHearing();
    }

    private void processUpdateForHearing(HearingRequest request, Hearing hearing) {
        if (legalConfiguration.getIsWorkflowEnabled()) {
            State state = workflowService.updateWorkflowForHearing(request, CreationReason.UPDATE);
            if (state.getIsStartState() && state.getApplicationStatus().equalsIgnoreCase(Status.ACTIVE.toString()) && !hearing.getStatus()
                    .equals(Status.ACTIVE)) {
            }
        }
    }
}


