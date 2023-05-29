package org.legal.service;

import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.legal.configs.LEGALConfiguration;
import org.legal.producer.Producer;
import org.legal.repository.CaseRepository;
import org.legal.repository.HearingRepository;
import org.legal.repository.JudgementRepository;
import org.legal.util.CaseUtils;
import org.legal.util.Constants;
import org.legal.util.LegalErrorConstants;
import org.legal.validator.JudgementValidator;
import org.legal.web.model.*;
import org.legal.web.model.enums.CreationReason;
import org.legal.web.model.enums.Status;
import org.legal.web.model.workflow.ProcessInstance;
import org.legal.web.model.workflow.ProcessInstanceRequest;
import org.legal.web.model.workflow.ProcessInstanceResponse;
import org.legal.web.model.workflow.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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

    public JudgementRequest create(JudgementRequest judgementRequest) {
        try {


            HearingResponse hearingResponse = null;
            HearingSearchCriteria criteria = HearingSearchCriteria.builder()
                    .caseId(Collections.singletonList(judgementRequest.getJudgement().getCaseId())).build();
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
        }catch (CustomException e) {
            throw e;
        } catch (Exception e) {
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
        }catch (CustomException e) {
            throw e;
        }
        catch (Exception e) {
            log.error(LegalErrorConstants.JUDGEMENT_SEARCH_FAILED_MSG, e.getMessage());
            throw new CustomException(LegalErrorConstants.JUDGEMENT_SEARCH_FAILED, LegalErrorConstants.JUDGEMENT_SEARCH_FAILED_MSG);
        }
    }

    public JudgementRequest updateJudgement(JudgementRequest judgementRequest) {
        try {

            String action = "";
            CaseRequest caseRequest = new CaseRequest();
            if (judgementRequest.getJudgement().getId() != null) {
                JudgementSearchCriteria criteria = JudgementSearchCriteria.builder().id(Collections.singletonList(judgementRequest.getJudgement().getId()))
                        .build();
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

                    RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(judgementRequest.getRequestInfo()).build();
                    String judgementId = judgementRequest.getJudgement().getId();
                    String appStatus = judgementRequest.getJudgement().getApplicationStatus();
                    if (appStatus.equalsIgnoreCase("Pending at OIC for Decision") ||
                            appStatus.equalsIgnoreCase("Judgement Initiated")) {
                        StringBuilder searchUrl = getProcessInstanceSearchURL(legalConfiguration.getTenantId(), StringUtils.join(judgementId, ','));
                        Object result = judgementRepository.fetchResult(searchUrl, requestInfoWrapper);
                        ProcessInstanceResponse processInstanceResponse = mapper.convertValue(result, ProcessInstanceResponse.class);
                        if (!processInstanceResponse.getProcessInstances().isEmpty()) {
                            if (!judgementRequest.getRequestInfo().getUserInfo().getUuid()
                                            .equals(processInstanceResponse.getProcessInstances().get(0).getAssignes().get(0).getUuid())) {
                                throw new CustomException("PARSING ERROR", "You can't take action on this judgement");
                            }
                        }
                        throw new CustomException("PARSING ERROR", "Failed to parse response of workflow processInstance search");
                    }

                    if (Objects.nonNull(judgementRequest.getWorkflow())) {
                        if (legalConfiguration.getIsWorkflowEnabled()) {
                            judgementRequest.getWorkflow().setBusinessService(legalConfiguration.getCreateJudgementWfName());
                            workflowService.updateJudgementWorkflowStatus(judgementRequest);
                        }
                    }
                    Workflow workflow = new Workflow();
                    workflow.setAssignes(judgementRequest.getWorkflow().getAssignes());
                    caseRequest.setWorkflow(workflow);
                    CaseRequest caseAppStatus = new CaseRequest();
                    Case caseApp = new Case();
                    caseAppStatus.setCaseObj(caseApp);
                    if (judgementRequest.getWorkflow().getAction().equalsIgnoreCase(Constants.JUDGEMENT_APPEALED_REVIEW)) {
                        String applicationStatus = workflowService.updateCaseWorkflow(caseRequest, Constants.REVIEW_JUDGEMENT);
                        caseAppStatus.getCaseObj().setApplicationStatus(applicationStatus);
                        caseAppStatus.getCaseObj().setTenantId(legalConfiguration.getTenantId());
                        caseAppStatus.getCaseObj().setId(caseRequest.getCaseObj().getId());
                        caseAppStatus.getCaseObj().setAuditDetails(caseUtils.getAuditDetails(judgementRequest.getRequestInfo().getUserInfo().getUuid(), false));
                        producer.push(legalConfiguration.getUpdateCaseApplicationStatusTopic(), caseAppStatus);
                    }
                    if (judgementRequest.getWorkflow().getAction().equalsIgnoreCase(Constants.JUDGEMENT_COMPLETED)) {
                        String applicationStatus = workflowService.updateCaseWorkflow(caseRequest, Constants.COMPLY_JUDGEMENT);
                        caseAppStatus.getCaseObj().setApplicationStatus(applicationStatus);
                        caseAppStatus.getCaseObj().setTenantId(legalConfiguration.getTenantId());
                        caseAppStatus.getCaseObj().setId(caseRequest.getCaseObj().getId());
                        caseAppStatus.getCaseObj().setAuditDetails(caseUtils.getAuditDetails(judgementRequest.getRequestInfo().getUserInfo().getUuid(), false));
                        producer.push(legalConfiguration.getUpdateCaseApplicationStatusTopic(), caseAppStatus);
                    }
                    producer.push(legalConfiguration.getUpdateJudgementTopic(), finalRequest);
                } else {
                    throw new CustomException(LegalErrorConstants.JUDGEMENT_NOT_AVAILABLE, "Judgement is not Available");
                }
            } else {
                throw new CustomException(LegalErrorConstants.INVALID_TYPE_ERROR, "Id is mandatory");
            }
            return judgementRequest;
        }catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error(LegalErrorConstants.JUDGEMENT_UPDATE_FAILED_MSG, e.getMessage());
            throw new CustomException(LegalErrorConstants.JUDGEMENT_UPDATE_FAILED, LegalErrorConstants.JUDGEMENT_UPDATE_FAILED_MSG);
        }
    }

    public StringBuilder getProcessInstanceSearchURL(String tenantId, String judgementId) {

        StringBuilder url = new StringBuilder(legalConfiguration.getWfHost());
        url.append(legalConfiguration.getWfProcessInstanceSearchPath());
        url.append("?tenantId=");
        url.append(tenantId);
        url.append("&businessIds=");
        url.append(judgementId);
        return url;

    }
}
