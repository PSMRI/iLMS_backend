package org.legal.service;

import static org.legal.util.LegalErrorConstants.CASE_UPDATE_FAILED_MSG;
import static org.legal.util.LegalErrorConstants.HEARING_CREATE_FAILED_MSG;
import static org.legal.util.LegalErrorConstants.HEARING_SEARCH_FAILED_MSG;

import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.legal.configs.LEGALConfiguration;
import org.legal.producer.Producer;
import org.legal.repository.CaseRepository;
import org.legal.repository.HearingRepository;
import org.legal.util.CaseUtils;
import org.legal.util.Constants;
import org.legal.util.HearingUtils;
import org.legal.util.LegalErrorConstants;
import org.legal.validator.HearingValidator;
import org.legal.web.model.*;
import org.legal.web.model.enums.PartyType;
import org.legal.web.model.enums.Status;
import org.legal.web.model.workflow.ProcessInstance;
import org.legal.web.model.workflow.ProcessInstanceRequest;
import org.legal.web.model.workflow.ProcessInstanceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Transactional
@Slf4j
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

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private CaseService caseService;


    public HearingRequest create(HearingRequest hearingRequest) {
        try {
            CaseResponse caseResponse = null;
            CaseSearchCriteria criteria = CaseSearchCriteria.builder().id(Collections.singletonList(hearingRequest.getHearing().getCaseId())).build();
            caseResponse = caseRepository.getLegalCaseData(criteria);
            if (caseResponse.getCaseList().isEmpty()) {
                throw new CustomException(LegalErrorConstants.CASE_NOT_AVAILABLE, "Case is not Available.");
            }
            if (Objects.nonNull(caseResponse.getCaseList())) {
                if (caseResponse.getCaseList().get(0).getCaseNumber().equals(hearingRequest.getHearing().getCaseNumber())) {
                    hearingRequest.getHearing().setStatus(Status.ACTIVE);
                    if (Objects.nonNull(hearingRequest.getHearing().getPayment())) {
                        hearingRequest.getHearing().getPayment().setStatus(Status.ACTIVE);
                    }
                    hearingRequest.getHearing().setHearingNumber(hearingDetailsRepository.getMaxValueOfHearing(hearingRequest.getHearing().getCaseId()));
                    hearingDetailsValidator.createValidator(hearingRequest);
                    hearingEnrichmentService.enrichHearingCreateRequest(hearingRequest);
                    if (legalConfiguration.getIsWorkflowEnabled()) {
                        if (hearingRequest.getWorkflow().getAssignes() == null) {
                            List<String> users = new ArrayList<>();
                            users.add(hearingRequest.getRequestInfo().getUserInfo().getUuid());
                            hearingRequest.getWorkflow().setAssignes(users);
                        }
                        hearingRequest.getWorkflow().setBusinessService(legalConfiguration.getCreateHearingWfName());
                        workflowService.updateHearingWorkflowStatus(hearingRequest);
                    }
                    producer.push(legalConfiguration.getCreateHearingTopic(), hearingRequest);
                } else {
                    throw new CustomException(LegalErrorConstants.INVALID_TYPE_ERROR, "CaseNumber Invalid");
                }
            } else {
                throw new CustomException(LegalErrorConstants.CASE_NOT_AVAILABLE, "Case is not Available for this Hearing");
            }
            return hearingRequest;
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            if (e instanceof CustomException) {
                throw e;
            }
            e.printStackTrace();
            log.error(HEARING_CREATE_FAILED_MSG, e.getMessage());
            throw new CustomException(LegalErrorConstants.HEARING_CREATE_FAILED, HEARING_CREATE_FAILED_MSG + " " + e.getMessage());
        }
    }

    public HearingResponse hearingSearch(HearingSearchCriteria criteria, RequestInfo requestInfo) {
        try {
            HearingResponse hearingResponse = null;
            hearingResponse = hearingDetailsRepository.getHearingDetails(criteria);
            if (!hearingResponse.getHearingList().isEmpty()) {

                return hearingResponse;

            } else {
                throw new CustomException(LegalErrorConstants.HEARING_NOT_AVAILABLE, "Hearing is not Available");
            }
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            if (e instanceof CustomException) {
                throw e;
            }
            e.printStackTrace();
            log.error(HEARING_SEARCH_FAILED_MSG, e.getMessage());
            throw new CustomException(LegalErrorConstants.HEARING_SEARCH_FAILED, HEARING_SEARCH_FAILED_MSG + " " + e.getMessage());
        }
    }


    public HearingRequest update(HearingRequest hearingDetailsRequest) {
        try {
            String action = "";
            HearingRequest updatedRequest = new HearingRequest();
            if (hearingDetailsRequest.getHearing().getId() != null) {
                CaseRequest caseRequest = new CaseRequest();
                HearingSearchCriteria criteria = HearingSearchCriteria.builder().caseId(Collections.singletonList((hearingDetailsRequest.getHearing().getCaseId()))).id(hearingDetailsRequest.getHearing().getId()).build();
                HearingResponse hearingDetailsResponse = hearingDetailsRepository.getHearingDetails(criteria);
                if (!hearingDetailsResponse.getHearingList().isEmpty()) {
                    HearingRequest request = new HearingRequest();
                    request.setRequestInfo(hearingDetailsRequest.getRequestInfo());
                    HearingRequest hearingAppStatus = new HearingRequest();
                    Hearing hearingApp = new Hearing();
                    hearingAppStatus.setHearing(hearingApp);

                    List<Hearing> hearingList = hearingDetailsResponse.getHearingList();
                    for (Hearing oldHearing : hearingList) {
                        request.setHearing(oldHearing);
                        if (oldHearing.getId().equals(hearingDetailsRequest.getHearing().getId())) {
                            updatedRequest = hearingUtils.prepareHearingDetailsModalForUpdate(hearingDetailsRequest, oldHearing);
                            updatedRequest.setWorkflow(hearingDetailsRequest.getWorkflow());
                            hearingDetailsValidator.updateValidator(updatedRequest.getHearing(), hearingDetailsRequest);

                            RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(hearingDetailsRequest.getRequestInfo()).build();
                            String hearingId = updatedRequest.getHearing().getId();
                            String applicationStatus = updatedRequest.getHearing().getApplicationStatus();
                            if (applicationStatus.equalsIgnoreCase(Constants.SOF_APPROVED_BY_AO) ||
                                    applicationStatus.equalsIgnoreCase(Constants.Pending_at_OIC)) {
                                StringBuilder URL = getProcessInstanceSearchURL(legalConfiguration.getTenantId(), hearingId);
                                URL.append("&").append("history=true");
                                Object result = hearingRepository.fetchResult(URL, requestInfoWrapper);
                                ProcessInstanceResponse processInstanceResponse = mapper.convertValue(result, ProcessInstanceResponse.class);
                                if (!processInstanceResponse.getProcessInstances().isEmpty()) {
                                    if (!hearingDetailsRequest.getRequestInfo().getUserInfo().getUuid()
                                            .equals(processInstanceResponse.getProcessInstances().get(0).getAssignes().get(0).getUuid())) {
                                        throw new CustomException("PARSING ERROR", "You can't take action on this hearing");
                                    }
                                } else {
                                    throw new CustomException("PARSING ERROR", "Failed to parse response of workflow processInstance search");
                                }
                            }
                            if (Objects.nonNull(updatedRequest.getWorkflow())) {
                                if (legalConfiguration.getIsWorkflowEnabled()) {
                                    updatedRequest.getWorkflow().setBusinessService(legalConfiguration.getCreateHearingWfName());
                                    workflowService.updateHearingWorkflowStatus(updatedRequest);
                                }
                            }
                        }
                        Workflow oldHearingWorkflow = new Workflow();
                        oldHearingWorkflow.setAssignes(hearingDetailsRequest.getWorkflow().getAssignes());
                        request.setWorkflow(oldHearingWorkflow);
                        if (hearingDetailsRequest.getWorkflow().getAction().equalsIgnoreCase(Constants.ASSIGNED_TO_RO) && oldHearing.getApplicationStatus().equalsIgnoreCase(
                                Constants.Pending_At_DEC_for_next_hearing)) {
                            request.getWorkflow().setAction(Constants.REVIEW_TO_RO);
                            request.getWorkflow().setBusinessService(legalConfiguration.getCreateHearingWfName());
                            workflowService.updateHearingWorkflowStatus(request);
                            producer.push(legalConfiguration.getUpdateHearingTopic(), request);
                        }
                        if (hearingDetailsRequest.getWorkflow().getAction().equalsIgnoreCase(Constants.ASSIGNED_TO_APPOINTED_OIC) && oldHearing.getApplicationStatus().equalsIgnoreCase(
                                Constants.Pending_at_RO_for_Next_Hearing_Review)) {
                            request.getWorkflow().setAction(Constants.Approved);
                            request.getWorkflow().setBusinessService(legalConfiguration.getCreateHearingWfName());
                            workflowService.updateHearingWorkflowStatus(request);
                            producer.push(legalConfiguration.getUpdateHearingTopic(), request);
                        }
                        if (hearingDetailsRequest.getWorkflow().getAction().equalsIgnoreCase(Constants.REVIEW_AND_ASSIGN_BACK_TO_DEC) && oldHearing.getApplicationStatus()
                                .equalsIgnoreCase(
                                        Constants.Pending_at_RO_for_Next_Hearing_Review)) {
                            request.getWorkflow().setAction(Constants.Reject);
                            request.getWorkflow().setBusinessService(legalConfiguration.getCreateHearingWfName());
                            workflowService.updateHearingWorkflowStatus(request);
                            producer.push(legalConfiguration.getUpdateHearingTopic(), request);
                        }
                    }
                    String caseId = hearingDetailsRequest.getHearing().getCaseId();
                    CaseSearchCriteria caseCriteria = CaseSearchCriteria.builder().id(Collections.singletonList(caseId)).build();
                    CaseResponse caseResponse = caseRepository.getLegalCaseData(caseCriteria);
                    caseRequest.setRequestInfo(hearingDetailsRequest.getRequestInfo());
                    caseRequest.setCaseObj(caseResponse.getCaseList().get(0));
                    Workflow workflow = new Workflow();
                    workflow.setAssignes(hearingDetailsRequest.getWorkflow().getAssignes());
                    request.setWorkflow(workflow);
                    if (hearingDetailsRequest.getWorkflow().getAction().equalsIgnoreCase(Constants.Approved) && !hearingDetailsRequest.getHearing().getHearingType()
                            .equalsIgnoreCase(
                                    Constants.Final_Hearing)) {
                        caseRequest.getWorkflow().setAction(Constants.SUBMIT_COUNTER_AFFIDAVIT);
                        caseService.updateCase(caseRequest);
                    } else if (hearingDetailsRequest.getWorkflow().getAction().equalsIgnoreCase(Constants.Approved) && hearingDetailsRequest.getHearing().getHearingType()
                            .equalsIgnoreCase(
                                    Constants.Final_Hearing)) {
                        caseRequest.getWorkflow().setAction(Constants.PROCEED_WITH_JUDGEMENT);
                        caseService.updateCase(caseRequest);
                    }
                    producer.push(legalConfiguration.getUpdateHearingTopic(), updatedRequest);
                } else {
                    throw new CustomException(LegalErrorConstants.HEARING_NOT_AVAILABLE, "Hearing is not Available");
                }
            } else {
                throw new CustomException(LegalErrorConstants.INVALID_TYPE_ERROR, "Id is mandatory");
            }
            return updatedRequest;
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            if (e instanceof CustomException) {
                throw e;
            }
            e.printStackTrace();
            log.error(LegalErrorConstants.HEARING_UPDATE_FAILED_MSG, e.getMessage());
            throw new CustomException(LegalErrorConstants.HEARING_UPDATE_FAILED, LegalErrorConstants.HEARING_UPDATE_FAILED_MSG);
        }
    }

    public StringBuilder getProcessInstanceSearchURL(String tenantId, String hearingId) {

        StringBuilder url = new StringBuilder(legalConfiguration.getWfHost());
        url.append(legalConfiguration.getWfProcessInstanceSearchPath());
        url.append("?tenantId=");
        url.append(tenantId);
        url.append("&businessIds=");
        url.append(hearingId);
        return url;

    }
}


