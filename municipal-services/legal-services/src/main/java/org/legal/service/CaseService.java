package org.legal.service;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.legal.configs.LEGALConfiguration;
import org.legal.producer.Producer;
import org.legal.repository.CaseRepository;
import org.legal.repository.HearingRepository;
import org.legal.repository.JudgementRepository;
import org.legal.util.CaseUtils;
import org.legal.util.HearingUtils;
import org.legal.util.LegalErrorConstants;
import org.legal.validator.CaseValidator;
import org.legal.web.model.*;
import org.legal.web.model.enums.CreationReason;
import org.legal.web.model.enums.PartyType;
import org.legal.web.model.enums.Status;
import org.legal.web.model.workflow.ProcessInstance;
import org.legal.web.model.workflow.ProcessInstanceRequest;
import org.legal.web.model.workflow.ProcessInstanceSearchCriteria;
import org.legal.web.model.workflow.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CaseService {
    @Autowired
    private CaseRepository caseRepository;

    @Autowired
    private HearingRepository hearingRepository;

    @Autowired
    private JudgementRepository judgementRepository;

    @Autowired
    private CaseValidator caseValidator;

    @Autowired
    private Producer producer;

    @Autowired
    private CaseEnrichmentService caseEnrichmentService;

    @Autowired
    private LEGALConfiguration legalConfiguration;

    @Autowired
    private CaseUtils caseUtils;

    @Autowired
    private WorkflowService workflowService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private HearingUtils hearingUtils;

    public CaseService() {
    }

    public CaseResponse legalCaseSearch(CaseSearchCriteria criteria, RequestInfo requestInfo, ProcessInstanceSearchCriteria processInstanceSearchCriteria) {
        List<Case> caseList = new ArrayList<>();
        CaseResponse caseResponse = null;
        criteria.setUuid(requestInfo.getUserInfo().getUuid());
        List<HashMap<String, Object>> statusCountMap = workflowService.getProcessStatusCount(requestInfo, processInstanceSearchCriteria);
        caseResponse = caseRepository.getLegalCaseData(criteria);
        CaseResponse finalResult = new CaseResponse();
        String userRole = requestInfo.getUserInfo().getRoles().get(0).getCode();
        Integer total = null;
        Integer dec = null;
        Integer ro = null;
        Integer oica = null;
        Integer ao = null;
        Integer oic = null;

        OfficersCount officersCount = new OfficersCount();
        CaseSearchCriteria criteria1 = new CaseSearchCriteria();
        criteria1.setUuid(criteria.getUuid());
        total = caseRepository.getCaseCount(criteria1);
        officersCount.setTOTAL(total);

        if (userRole.equals("DEC")) {
            dec = caseRepository.getCountOfUser("DEC");
            officersCount.setDEC(dec);
        } else if (userRole.equals("RO")) {
            dec = caseRepository.getCountOfUser("DEC");
            ro = caseRepository.getCountOfUser("RO");
            officersCount.setDEC(dec);
            officersCount.setRO(ro);
        } else if (userRole.equals("OICA")) {
            dec = caseRepository.getCountOfUser("DEC");
            ro = caseRepository.getCountOfUser("RO");
            oica = caseRepository.getCountOfUser("OICA");
            officersCount.setDEC(dec);
            officersCount.setRO(ro);
            officersCount.setOICA(oica);
        } else if (userRole.equals("AO")) {
            dec = caseRepository.getCountOfUser("DEC");
            ro = caseRepository.getCountOfUser("RO");
            oica = caseRepository.getCountOfUser("OICA");
            ao = caseRepository.getCountOfUser("AO");
            officersCount.setDEC(dec);
            officersCount.setRO(ro);
            officersCount.setOICA(oica);
            officersCount.setAO(ao);
        } else if (userRole.equals("OIC") || userRole.equals("MO")) {

            dec = caseRepository.getCountOfUser("DEC");
            ro = caseRepository.getCountOfUser("RO");
            oica = caseRepository.getCountOfUser("OICA");
            ao = caseRepository.getCountOfUser("AO");
            oic = caseRepository.getCountOfUser("OIC");
            officersCount.setDEC(dec);
            officersCount.setRO(ro);
            officersCount.setOICA(oica);
            officersCount.setAO(ao);
            officersCount.setOIC(oic);
        }
        List<Hearing> hearingList = new ArrayList<>();
        List<Judgement> judgementList = new ArrayList<>();
        HearingResponse hearingResponse = null;
        JudgementResponse judgementResponse = null;
        if (!caseResponse.getCaseList().isEmpty()) {
            HearingSearchCriteria hearingCriteria = HearingSearchCriteria.builder()
                    .caseId(Collections.singletonList(caseResponse.getCaseList().get(0).getId()))
                    .build();
            hearingResponse = hearingRepository.getHearingDetails(hearingCriteria);
            JudgementSearchCriteria judgementSearchCriteria = JudgementSearchCriteria.builder().caseId(Collections.singletonList(
                    caseResponse.getCaseList().get(0).getId())).build();
            judgementResponse = judgementRepository.getJudgementData(judgementSearchCriteria);
            caseResponse.getCaseList().forEach(caseObj -> {
                if (caseObj.getStatus() == Status.ACTIVE) {
                    caseList.add(caseObj);
                }
            });
            hearingResponse.getHearingList().forEach(hearing -> {
                if (hearing.getStatus() == Status.ACTIVE) {
                    hearingList.add(hearing);
                }
            });
            judgementResponse.getJudgementList().forEach(judgement -> {
                if (judgement.getStatus() == Status.ACTIVE) {
                    judgementList.add(judgement);
                }
            });

            finalResult.setTotalCount(caseResponse.getTotalCount());
            finalResult.setCaseList(caseList);
            finalResult.setStatusMap(statusCountMap);
            finalResult.setOfficersCount(officersCount);
            finalResult.setHearingList(hearingList);
            finalResult.setJudgementList(judgementList);
        }
        return finalResult;

    }

    public Case create(CaseRequest caseRequest) {
        if (Objects.nonNull(caseRequest.getCaseObj().getCourt())) {
            caseRequest.getCaseObj().getCourt().setStatus(Status.ACTIVE);
        }
        for (Party party : caseRequest.getCaseObj().getParties()) {
            if (party.getPartyType().equals(PartyType.PETITIONER.toString())) {
                if (Objects.nonNull(party.getDepartmentName())) {
                    party.setFirstName(null);
                    party.setLastName(null);
                    party.setGender(null);
                    party.setPetitionerType(null);
                    party.setAddress(null);
                    party.setStatus(Status.ACTIVE);
                    party.setContactNumber(null);
                    party.setDepartmentName(party.getDepartmentName());
                } else {
                    party.setDepartmentName(null);
                    party.setStatus(Status.ACTIVE);
                }
                party.setPartyType(PartyType.PETITIONER.toString());
                party.setStatus(Status.ACTIVE);
                if (Objects.nonNull(party.getAdvocate())) {
                    for (Advocate advocate : party.getAdvocate()) {
                        advocate.setStatus(Status.ACTIVE);
                    }
                }
            } else {
                if (Objects.nonNull(party.getDepartmentName())) {
                    party.setFirstName(null);
                    party.setLastName(null);
                    party.setGender(null);
                    party.setPetitionerType(null);
                    party.setAddress(null);
                    party.setContactNumber(null);
                    party.setStatus(Status.ACTIVE);
                    party.setDepartmentName(party.getDepartmentName());
                } else {
                    party.setDepartmentName(null);
                    party.setStatus(Status.ACTIVE);
                }
                party.setPartyType(PartyType.RESPONDENT.toString());
                party.setStatus(Status.ACTIVE);
                if (Objects.nonNull(party.getAdvocate())) {
                    for (Advocate advocate : party.getAdvocate()) {
                        advocate.setStatus(Status.ACTIVE);
                    }
                }
            }
        }
        if (Objects.nonNull(caseRequest.getCaseObj().getAct())) {
            for (Act act : caseRequest.getCaseObj().getAct()) {
                act.setStatus(Status.ACTIVE);
            }
        }
        caseRequest.getCaseObj().setStatus(Status.ACTIVE);

        caseValidator.validateCreate(caseRequest);
        caseValidator.caseNumberDuplicacyCheck(caseRequest);
        caseEnrichmentService.enrichCaseCreateRequest(caseRequest);
        if (legalConfiguration.getIsWorkflowEnabled()) {
            workflowService.updateWorkflow(caseRequest, CreationReason.CREATE);
            notificationService.process(legalConfiguration.getCreateCaseTopic(), caseRequest);
        }
        producer.push(legalConfiguration.getCreateCaseTopic(), caseRequest);
        return caseRequest.getCaseObj();
    }

    /**
     * Updates the legal_case
     *
     * @param caseRequest The update Request
     * @return Updated legalCase
     */
    public Case update(CaseRequest caseRequest) {
        if (caseRequest.getCaseObj().getId() != null) {
            HearingSearchCriteria hearingSearchCriteria = null;
            String action = "";
            HearingRequest request = new HearingRequest();
            CaseSearchCriteria criteria = CaseSearchCriteria.builder().id(Collections.singletonList(caseRequest.getCaseObj().getId())).build();
            CaseResponse caseResponse = caseRepository.getLegalCaseData(criteria);
            if (!caseResponse.getCaseList().isEmpty()) {
                CaseRequest updatedCaseRequest = caseUtils.prepareObjectMapperForUpdate(caseResponse.getCaseList().get(0), caseRequest);
                Case cases = caseResponse.getCaseList().get(0);
                caseValidator.validateUpdate(cases, caseRequest);
                producer.push(legalConfiguration.getUpdateCaseTopic(), updatedCaseRequest);
                //                todo : notification has send to all the officers who has worked on this case.
                if (Objects.nonNull(caseRequest.getCaseObj().getWorkflow())) {
                    processCaseUpdate(caseRequest, updatedCaseRequest.getCaseObj());
                    String caseId = caseRequest.getCaseObj().getId();
                    hearingSearchCriteria = HearingSearchCriteria.builder().caseId(Collections.singletonList(caseId)).build();
                    HearingResponse hearingResponse = hearingRepository.getHearingDetails(hearingSearchCriteria);
                    request.setRequestInfo(caseRequest.getRequestInfo());
                    for (Hearing hearing : hearingResponse.getHearingList()) {
                        request.setHearing(hearing);
                        ProcessInstance wf = null != hearing.getWorkflow() ? hearing.getWorkflow() : new ProcessInstance();
                        wf.setAssignes(caseRequest.getCaseObj().getWorkflow().getAssignes());
                        hearing.setWorkflow(wf);
                        if (caseRequest.getCaseObj().getWorkflow().getAction().equalsIgnoreCase("FORWARD_TO_RO")) {
                            action = "ASSIGNED_TO_RO";
                            ProcessInstanceRequest workflowReq = hearingUtils.hearingWFThroughCase(request, action);
                            workflowService.callWorkFlow(workflowReq);
                        }
                        if (caseRequest.getCaseObj().getWorkflow().getAction().equalsIgnoreCase("INACTIVATE")) {
                            action = "DEACTIVATE";
                            ProcessInstanceRequest workflowReq = hearingUtils.hearingWFThroughCase(request, action);
                            workflowService.callWorkFlow(workflowReq);
                        }
                        for (Document document : caseRequest.getCaseObj().getDocuments()) {
                            if (document.getDocumentType().equalsIgnoreCase("ILMS_DOCS_COUNTER_AFFIDAVIT") && caseRequest.getCaseObj().getWorkflow().getAction().equalsIgnoreCase("SUBMIT_COUNTER_AFFIDAVIT")) {
                                action = "ASSIGNED_TO_APPOINTED_OIC";
                                ProcessInstanceRequest workflowReq = hearingUtils.hearingWFThroughCase(request, action);
                                workflowService.callWorkFlow(workflowReq);
                            }
                        }
                    }
                    notificationService.process(legalConfiguration.getUpdateCaseTopic(), caseRequest);
                }
                caseRequest.setCaseObj(updatedCaseRequest.getCaseObj());
            } else {
                throw new CustomException(LegalErrorConstants.CASE_NOT_AVAILABLE, "Case is not Available");
            }
        } else {
            throw new CustomException(LegalErrorConstants.CASE_NOT_AVAILABLE, "id is mandatory");
        }
        return caseRequest.getCaseObj();
    }

    private void processCaseUpdate(CaseRequest request, Case cases) {
        if (legalConfiguration.getIsWorkflowEnabled()) {
            State state = workflowService.updateWorkflow(request, CreationReason.UPDATE);
            if (state.getIsStartState() && state.getApplicationStatus().equalsIgnoreCase(Status.ACTIVE.toString()) && !cases.getStatus()
                    .equals(Status.ACTIVE)) {
            }
        }
    }
}

