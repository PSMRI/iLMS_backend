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
    private LEGALConfiguration ilmsConfiguration;

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
        String petitionerId = null;
        String respondentId = null;
        CaseResponse caseResponse = null;
        CaseSearchCriteria criteria = CaseSearchCriteria.builder().id(Collections.singletonList(hearingRequest.getHearing().getCaseId())).build();
        caseResponse = caseRepository.getLegalCaseData(criteria);
        if (caseResponse.getCaseList().isEmpty()) {
            throw new CustomException(LegalErrorConstants.CASE_NOT_AVAILABLE, "Case is not Available.");
        }
        List<Party> partyList = hearingDetailsRepository.getGetFromPartyQuery(hearingRequest.getHearing().getCaseId());
        for (Party party : partyList) {
            if (party.getPartyType().equals(PartyType.RESPONDENT.toString())) {
                respondentId = party.getId();
            } else {
                petitionerId = party.getId();
            }
        }
        if (Objects.nonNull(caseResponse.getCaseList())) {
            if (caseResponse.getCaseList().get(0).getNumber().equals(hearingRequest.getHearing().getCaseNumber())) {
                hearingRequest.getHearing().setStatus(Status.ACTIVE);
                for (Party party : hearingRequest.getHearing().getParties()) {
                    if (party.getPartyType().equals(PartyType.PETITIONER.toString())) {
                        party.setStatus(Status.ACTIVE);
                        party.setCaseId(hearingRequest.getHearing().getCaseId());
                        if (Objects.nonNull(party.getAdvocate())) {
                            for (Advocate advocate : party.getAdvocate()) {
                                advocate.setStatus(Status.ACTIVE);
                            }
                        }
                    } else if (party.getPartyType().equals(PartyType.RESPONDENT.toString())) {
                        party.setStatus(Status.ACTIVE);
                        party.setCaseId(hearingRequest.getHearing().getCaseId());
                        if (Objects.nonNull(party.getAdvocate())) {
                            for (Advocate advocate : party.getAdvocate()) {
                                advocate.setStatus(Status.ACTIVE);
                            }

                        }
                    }
                }
                if (Objects.nonNull(hearingRequest.getHearing().getPayment())) {
                    hearingRequest.getHearing().getPayment().setStatus(Status.ACTIVE);
                }

                hearingRequest.getHearing().setHearingNumber(hearingDetailsRepository.getMaxValueOfHearing(hearingRequest.getHearing().getCaseId()));
                hearingDetailsValidator.createValidator(hearingRequest);
                hearingEnrichmentService.enrichHearingCreateRequest(hearingRequest);
                if (ilmsConfiguration.getIsWorkflowEnabled()) {
                    workflowService.updateWorkflowForHearing(hearingRequest, CreationReason.CREATE);
                }
                producer.push(ilmsConfiguration.getCreateHearingTopic(), hearingRequest);
            } else {
                throw new CustomException(LegalErrorConstants.INVALID_TYPE_ERROR, "CaseNumber Invalid");
            }
        } else {
            throw new CustomException(LegalErrorConstants.CASE_NOT_AVAILABLE, "Case is not Available for this Hearing");
        }
        return hearingRequest.getHearing();
    }

    public HearingResponse hearingSearch(HearingSearchCriteria criteria, RequestInfo requestInfo) {
        List<Hearing> ilmsHearingList = new ArrayList<>();
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
            HearingSearchCriteria criteria = HearingSearchCriteria.builder().id((hearingDetailsRequest.getHearing().getId())).build();
            HearingResponse hearingDetailsResponse = hearingDetailsRepository.getHearingDetails(criteria);
            if (!hearingDetailsResponse.getHearingList().isEmpty()) {
                List<Hearing> hearingList = hearingDetailsResponse.getHearingList();
                Hearing oldHearing = hearingList.get(0);
                HearingRequest updatedRequest = hearingUtils.prepareHearingDetailsModalForUpdate(hearingDetailsRequest, oldHearing);
                hearingDetailsValidator.updateValidator(updatedRequest.getHearing(), hearingDetailsRequest);
                if (Objects.nonNull(hearingDetailsRequest.getHearing().getWorkflow())) {
                    processUpdateForHearing(hearingDetailsRequest, updatedRequest.getHearing());
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
                producer.push(ilmsConfiguration.getUpdateHearingTopic(), updatedRequest);
            } else {
                throw new CustomException(LegalErrorConstants.HEARING_NOT_AVAILABLE, "Hearing is not Available");
            }
        } else {
            throw new CustomException(LegalErrorConstants.INVALID_TYPE_ERROR, "Id is mandatory");
        }
        return hearingDetailsRequest.getHearing();
    }

    private void processUpdateForHearing(HearingRequest request, Hearing hearing) {
        if (ilmsConfiguration.getIsWorkflowEnabled()) {
            State state = workflowService.updateWorkflowForHearing(request, CreationReason.UPDATE);
            if (state.getIsStartState() && state.getApplicationStatus().equalsIgnoreCase(Status.ACTIVE.toString()) && !hearing.getStatus()
                    .equals(Status.ACTIVE)) {
            }
        }
    }
}


