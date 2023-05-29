package org.legal.service;

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


    public HearingRequest create(HearingRequest hearingRequest) {
        try {
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
        }catch (CustomException e) {
            throw e;
        }
        catch (Exception e) {
            log.error(LegalErrorConstants.HEARING_CREATE_FAILED_MSG, e.getMessage());
            throw new CustomException(LegalErrorConstants.HEARING_CREATE_FAILED, LegalErrorConstants.HEARING_CREATE_FAILED_MSG);
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
        }
        catch (Exception e) {
            log.error(LegalErrorConstants.HEARING_SEARCH_FAILED_MSG, e.getMessage());
            throw new CustomException(LegalErrorConstants.HEARING_SEARCH_FAILED, LegalErrorConstants.HEARING_SEARCH_FAILED_MSG);
        }
    }


    public HearingRequest update(HearingRequest hearingDetailsRequest) {
        try {
            String action = "";
            if (hearingDetailsRequest.getHearing().getId() != null) {
                CaseRequest caseRequest = new CaseRequest();
                HearingSearchCriteria criteria = HearingSearchCriteria.builder().caseId(Collections.singletonList((hearingDetailsRequest.getHearing().getCaseId()))).build();
                HearingResponse hearingDetailsResponse = hearingDetailsRepository.getHearingDetails(criteria);
                if (!hearingDetailsResponse.getHearingList().isEmpty()) {
                    HearingRequest updatedRequest = new HearingRequest();
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
                            String applicationStatus = hearingDetailsRequest.getHearing().getApplicationStatus();
                            if (applicationStatus.equalsIgnoreCase("SOF_APPROVED_BY_AO") ||
                                    applicationStatus.equalsIgnoreCase("Pending at OIC")) {
                                StringBuilder searchUrl = getProcessInstanceSearchURL(legalConfiguration.getTenantId(), StringUtils.join(hearingId, ','));
                                Object result = hearingRepository.fetchResult(searchUrl, requestInfoWrapper);
                                ProcessInstanceResponse processInstanceResponse = mapper.convertValue(result, ProcessInstanceResponse.class);
                                if (!processInstanceResponse.getProcessInstances().isEmpty()) {
                                    if (!hearingDetailsRequest.getRequestInfo().getUserInfo().getUuid()
                                                    .equals(processInstanceResponse.getProcessInstances().get(0).getAssignes().get(0).getUuid())) {
                                        throw new CustomException("PARSING ERROR", "You can't take action on this hearing");
                                    }
                                }
                                throw new CustomException("PARSING ERROR", "Failed to parse response of workflow processInstance search");
                            }
                            if (Objects.nonNull(updatedRequest.getWorkflow())) {
                                if (legalConfiguration.getIsWorkflowEnabled()) {
                                    hearingDetailsResponse.getWorkflow().setBusinessService(legalConfiguration.getCreateHearingWfName());
                                    workflowService.updateHearingWorkflowStatus(updatedRequest);
                                }
                            }
                        }
                        if (hearingDetailsRequest.getWorkflow().getAction().equalsIgnoreCase(Constants.ASSIGNED_TO_RO) && oldHearing.getApplicationStatus().equalsIgnoreCase(
                                Constants.Pending_At_DEC_for_next_hearing)) {
                            String applicationStatus = workflowService.updateHearingWorkflow(request, Constants.REVIEW_TO_RO);
                            hearingAppStatus.getHearing().setApplicationStatus(applicationStatus);
                            hearingAppStatus.getHearing().setTenantId(legalConfiguration.getTenantId());
                            hearingAppStatus.getHearing().setId(request.getHearing().getId());
                            hearingAppStatus.getHearing().setAuditDetails(caseUtils.getAuditDetails(hearingDetailsRequest.getRequestInfo().getUserInfo().getUuid(), false));
                            producer.push(legalConfiguration.getUpdateHearingApplicationStatusTopic(), hearingAppStatus);
                        }
                        if (hearingDetailsRequest.getWorkflow().getAction().equalsIgnoreCase(Constants.ASSIGNED_TO_APPOINTED_OIC) && oldHearing.getApplicationStatus().equalsIgnoreCase(
                                Constants.Pending_at_RO_for_Next_Hearing_Review)) {
                            String applicationStatus = workflowService.updateHearingWorkflow(request, Constants.Approved);
                            hearingAppStatus.getHearing().setApplicationStatus(applicationStatus);
                            hearingAppStatus.getHearing().setTenantId(legalConfiguration.getTenantId());
                            hearingAppStatus.getHearing().setId(request.getHearing().getId());
                            hearingAppStatus.getHearing().setAuditDetails(caseUtils.getAuditDetails(hearingDetailsRequest.getRequestInfo().getUserInfo().getUuid(), false));
                            producer.push(legalConfiguration.getUpdateHearingApplicationStatusTopic(), hearingAppStatus);
                        }
                        if (hearingDetailsRequest.getWorkflow().getAction().equalsIgnoreCase(Constants.REVIEW_AND_ASSIGN_BACK_TO_DEC) && oldHearing.getApplicationStatus()
                                .equalsIgnoreCase(
                                        Constants.Pending_at_RO_for_Next_Hearing_Review)) {
                            String applicationStatus = workflowService.updateHearingWorkflow(request, Constants.Reject);
                            hearingAppStatus.getHearing().setApplicationStatus(applicationStatus);
                            hearingAppStatus.getHearing().setTenantId(legalConfiguration.getTenantId());
                            hearingAppStatus.getHearing().setId(request.getHearing().getId());
                            hearingAppStatus.getHearing().setAuditDetails(caseUtils.getAuditDetails(hearingDetailsRequest.getRequestInfo().getUserInfo().getUuid(), false));
                            producer.push(legalConfiguration.getUpdateHearingApplicationStatusTopic(), hearingAppStatus);
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
                    CaseRequest caseAppStatus = new CaseRequest();
                    Case caseApp = new Case();
                    caseAppStatus.setCaseObj(caseApp);
                    if (hearingDetailsRequest.getWorkflow().getAction().equalsIgnoreCase(Constants.Approved) && !hearingDetailsRequest.getHearing().getHearingType()
                            .equalsIgnoreCase(
                                    Constants.Final_Hearing)) {
                        String applicationStatus = workflowService.updateCaseWorkflow(caseRequest, Constants.SUBMIT_COUNTER_AFFIDAVIT);
                        caseAppStatus.getCaseObj().setApplicationStatus(applicationStatus);
                        caseAppStatus.getCaseObj().setTenantId(legalConfiguration.getTenantId());
                        caseAppStatus.getCaseObj().setId(caseRequest.getCaseObj().getId());
                        caseAppStatus.getCaseObj().setAuditDetails(caseUtils.getAuditDetails(hearingDetailsRequest.getRequestInfo().getUserInfo().getUuid(), false));
                        producer.push(legalConfiguration.getUpdateCaseApplicationStatusTopic(), caseAppStatus);
                    } else if (hearingDetailsRequest.getWorkflow().getAction().equalsIgnoreCase(Constants.Approved) && hearingDetailsRequest.getHearing().getHearingType()
                            .equalsIgnoreCase(
                                    Constants.Final_Hearing)) {
                        String applicationStatus = workflowService.updateCaseWorkflow(caseRequest, Constants.PROCEED_WITH_JUDGEMENT);
                        caseAppStatus.getCaseObj().setApplicationStatus(applicationStatus);
                        caseAppStatus.getCaseObj().setTenantId(legalConfiguration.getTenantId());
                        caseAppStatus.getCaseObj().setId(caseRequest.getCaseObj().getId());
                        caseAppStatus.getCaseObj().setAuditDetails(caseUtils.getAuditDetails(hearingDetailsRequest.getRequestInfo().getUserInfo().getUuid(), false));
                        producer.push(legalConfiguration.getUpdateCaseApplicationStatusTopic(), caseAppStatus);
                    }
                    producer.push(legalConfiguration.getUpdateHearingTopic(), updatedRequest);
                } else {
                    throw new CustomException(LegalErrorConstants.HEARING_NOT_AVAILABLE, "Hearing is not Available");
                }
            } else {
                throw new CustomException(LegalErrorConstants.INVALID_TYPE_ERROR, "Id is mandatory");
            }
            return hearingDetailsRequest;
        }catch (CustomException e) {
            throw e;
        }
        catch (Exception e) {
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


