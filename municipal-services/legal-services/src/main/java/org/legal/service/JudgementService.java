package org.legal.service;

import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.legal.configs.LEGALConfiguration;
import org.legal.producer.Producer;
import org.legal.repository.CaseRepository;
import org.legal.repository.HearingRepository;
import org.legal.repository.JudgementRepository;
import org.legal.repository.ServiceRepository;
import org.legal.util.CaseUtils;
import org.legal.util.CommonUtils;
import org.legal.util.Constants;
import org.legal.util.LegalErrorConstants;
import org.legal.validator.JudgementValidator;
import org.legal.web.model.*;
import org.legal.web.model.enums.Status;
import org.legal.web.model.workflow.ProcessInstanceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Transactional
@Slf4j
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

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private CaseService caseService;

    @Autowired
    private CommonUtils commonUtils;

    @Autowired
    private ServiceRepository serviceRepository;

    public JudgementRequest create(JudgementRequest judgementRequest) {
        try {
            HearingResponse hearingResponse = null;
            HearingSearchCriteria criteria = HearingSearchCriteria.builder()
                    .caseId(judgementRequest.getJudgement().getCaseId()).build();
            hearingResponse = hearingRepository.getHearingDetails(criteria);
            if (!hearingResponse.getHearingList().isEmpty()) {
                String tenantId = hearingResponse.getHearingList().get(0).getTenantId();
                judgementRequest.getJudgement().setTenantId(tenantId);
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
        }  catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            if(e instanceof CustomException){
                throw e;
            }
            e.printStackTrace();
            log.error(LegalErrorConstants.JUDGEMENT_CREATE_FAILED_MSG, e.getMessage());
            throw new CustomException(LegalErrorConstants.JUDGEMENT_CREATE_FAILED, LegalErrorConstants.JUDGEMENT_CREATE_FAILED_MSG);
        }
    }


    public JudgementResponse JudgementSearch(JudgementSearchCriteria criteria, RequestInfo requestInfo) {
        try {

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
        }  catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            if(e instanceof CustomException){
                throw e;
            }
            e.printStackTrace();
            log.error(LegalErrorConstants.JUDGEMENT_SEARCH_FAILED_MSG, e.getMessage());
            throw new CustomException(LegalErrorConstants.JUDGEMENT_SEARCH_FAILED, LegalErrorConstants.JUDGEMENT_SEARCH_FAILED_MSG);
        }
    }

    public JudgementRequest updateJudgement(JudgementRequest judgementRequest) {
        try {

            String action = "";
            CaseRequest caseRequest = new CaseRequest();
            JudgementRequest finalRequest;
            if (judgementRequest.getJudgement().getId() != null) {
                JudgementSearchCriteria criteria = JudgementSearchCriteria.builder().id(Collections.singletonList(judgementRequest.getJudgement().getId()))
                        .build();
                JudgementResponse judgementResponse = judgementRepository.getJudgementData(criteria);
                if (!judgementResponse.getJudgementList().isEmpty()) {
                    List<Judgement> judgements = judgementResponse.getJudgementList();
                    Judgement oldJudgement = judgements.get(0);
                    finalRequest = judgementRepository.getMappedData(judgementRequest, oldJudgement);
                    judgementValidator.updateValidator(finalRequest.getJudgement(), judgementRequest);

                    String caseId = oldJudgement.getCaseId();
                    CaseSearchCriteria caseCriteria = CaseSearchCriteria.builder().id(Collections.singletonList(caseId)).build();
                    CaseResponse caseResponse = caseRepository.getLegalCaseData(caseCriteria);
                    caseRequest.setRequestInfo(judgementRequest.getRequestInfo());
                    caseRequest.setCaseObj(caseResponse.getCaseList().get(0));

                    RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(judgementRequest.getRequestInfo()).build();
                    String judgementId = judgementRequest.getJudgement().getId();
                    String appStatus = finalRequest.getJudgement().getApplicationStatus();
                    if (appStatus.equalsIgnoreCase(Constants.Pending_at_OIC_for_Decision) ||
                            appStatus.equalsIgnoreCase(Constants.Judgement_Initiated)) {
                        StringBuilder URL = commonUtils.getProcessInstanceSearchURL(legalConfiguration.getTenantId(), judgementId);
                        URL.append("&").append("history=true");
                        Object result = serviceRepository.fetchUserResult(URL, requestInfoWrapper);
                        ProcessInstanceResponse processInstanceResponse = mapper.convertValue(result, ProcessInstanceResponse.class);
                        if (!processInstanceResponse.getProcessInstances().isEmpty()) {
                            if (!judgementRequest.getRequestInfo().getUserInfo().getUuid()
                                    .equals(processInstanceResponse.getProcessInstances().get(0).getAssignes().get(0).getUuid())) {
                                throw new CustomException("PARSING ERROR", "You can't take action on this judgement");
                            }
                        } else {
                            throw new CustomException("PARSING ERROR", "Failed to parse response of workflow processInstance search");
                        }
                    }

                    if (Objects.nonNull(judgementRequest.getWorkflow())) {
                        if (legalConfiguration.getIsWorkflowEnabled()) {
                            judgementRequest.getWorkflow().setBusinessService(legalConfiguration.getCreateJudgementWfName());
                            workflowService.updateJudgementWorkflowStatus(finalRequest);
                        }
                    }
                    Workflow workflow = new Workflow();
                    workflow.setAssignes(judgementRequest.getWorkflow().getAssignes());
                    caseRequest.setWorkflow(workflow);
                    CaseRequest caseAppStatus = new CaseRequest();
                    Case caseApp = new Case();
                    caseAppStatus.setCaseObj(caseApp);
                    if (judgementRequest.getWorkflow().getAction().equalsIgnoreCase(Constants.JUDGEMENT_APPEALED_REVIEW)) {
                        if (!judgementRequest.getJudgement().getDecisionStatus().isEmpty()) {
                            ObjectMapper objectMapper = new ObjectMapper();
                            Object additionalDetailsObj = judgementRequest.getJudgement().getAdditionalDetails();

                            if (additionalDetailsObj instanceof Map) {
                                Map<String, Object> additionalDetailsMap = (Map<String, Object>) additionalDetailsObj;
                                String caseNumber = (String) additionalDetailsMap.get("caseNumber");
                                additionalDetailsMap.put("action", Constants.JUDGEMENT_APPEALED_REVIEW);
                                additionalDetailsMap.put("decisionStatus", judgementRequest.getJudgement().getDecisionStatus());
                                additionalDetailsMap.put("caseNumber", caseNumber);
                                JsonNode additionalDetailsJsonNode = objectMapper.valueToTree(additionalDetailsMap);
                                caseRequest.getCaseObj().setAdditionalDetails(additionalDetailsJsonNode);
                            }
                            caseRequest.getWorkflow().setAction(Constants.REVIEW_JUDGEMENT);
                            CaseRequest caseRequestObj = caseService.updateCase(caseRequest);
                            finalRequest.getJudgement().setAdditionalDetails(caseRequestObj.getCaseObj().getId());
                        }
                    }
                    if (judgementRequest.getWorkflow().getAction().equalsIgnoreCase(Constants.JUDGEMENT_COMPLETED)) {
                        caseRequest.getWorkflow().setAction(Constants.COMPLY_JUDGEMENT);
                        caseService.updateCase(caseRequest);
                    }
                    producer.push(legalConfiguration.getUpdateJudgementTopic(), finalRequest);
                } else {
                    throw new CustomException(LegalErrorConstants.JUDGEMENT_NOT_AVAILABLE, "Judgement is not Available");
                }
            } else {
                throw new CustomException(LegalErrorConstants.INVALID_TYPE_ERROR, "Id is mandatory");
            }
            return finalRequest;
        }  catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            if(e instanceof CustomException){
                throw e;
            }
            e.printStackTrace();
            log.error(LegalErrorConstants.JUDGEMENT_UPDATE_FAILED_MSG, e.getMessage());
            throw new CustomException(LegalErrorConstants.JUDGEMENT_UPDATE_FAILED, LegalErrorConstants.JUDGEMENT_UPDATE_FAILED_MSG);
        }
    }
}
