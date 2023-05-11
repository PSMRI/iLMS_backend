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
import org.legal.validator.CaseValidator;
import org.legal.web.model.*;
import org.legal.web.model.enums.CreationReason;
import org.legal.web.model.enums.PartyType;
import org.legal.web.model.enums.Status;
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
    private LEGALConfiguration ilmsConfiguration;

    @Autowired
    private CaseUtils caseUtils;

    @Autowired
    private WorkflowService workflowService;

    @Autowired
    private NotificationService notificationService;

    public CaseService() {
    }

    public CaseResponse ilmsCaseSearch(CaseSearchCriteria criteria, RequestInfo requestInfo, ProcessInstanceSearchCriteria processInstanceSearchCriteria) {
        List<Case> caseList = new ArrayList<>();
        CaseResponse caseResponse = null;
        criteria.setUuid(requestInfo.getUserInfo().getUuid());
        List<HashMap<String, Object>> statusCountMap = workflowService.getProcessStatusCount(requestInfo, processInstanceSearchCriteria);
        caseResponse = caseRepository.getLegalCaseData(criteria);
        if (!caseResponse.getCaseList().isEmpty()) {
            caseResponse.getCaseList().forEach(caseObj -> {
                caseList.add(caseObj);
            });
        } else {
            throw new CustomException(LegalErrorConstants.CASE_NOT_AVAILABLE, "Case is not Available");
        }
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
        HearingResponse hearingResponse=null;
        JudgementResponse judgementResponse=null;
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
        return finalResult;
    }

    public CaseDetailsResponse caseDetailsSearch(CaseSearchCriteria criteria, RequestInfo requestInfo) {
        CaseDetailsResponse downloadResponse = new CaseDetailsResponse();
        List<Case> caseList = new ArrayList<>();
        List<Hearing> hearingList = new ArrayList<>();
        List<Judgement> judgementList = new ArrayList<>();
        CaseResponse caseResponse = null;
        HearingResponse hearingResponse = null;
        JudgementResponse judgementResponse = null;
        criteria.setUuid(requestInfo.getUserInfo().getUuid());
        caseResponse = caseRepository.getLegalCaseData(criteria);
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
        downloadResponse.setCaseList(caseList);
        downloadResponse.setHearingList(hearingList);
        downloadResponse.setJudgementList(judgementList);
        downloadResponse.setTotalCount(caseResponse.getTotalCount());
        downloadResponse.setResponseInfo(caseResponse.getResponseInfo());
        return downloadResponse;
    }

    public Case create(CaseRequest caseRequest) {
        if (Objects.nonNull(caseRequest.getCaseObj().getCourt())) {
            caseRequest.getCaseObj().getCourt().setStatus(Status.ACTIVE);
        }
        for (Party parties : caseRequest.getCaseObj().getParties()) {
            if (parties.getPartyType().equals(PartyType.PETITIONER.toString())) {
                parties.setStatus(Status.ACTIVE);
                parties.setAdvocateId("");
                if (Objects.nonNull(parties.getAdvocate())) {
                   List<Advocate> advocates=caseRepository.getAdvocateById(parties.getAdvocate().getId());
                    for (Advocate advocate: advocates){
                        if(parties.getAdvocate().getId().equals(advocate.getId())){
                            parties.setAdvocateId(parties.getAdvocate().getId());
                            parties.setAdvocate(null);
                        }
                    }
                }
            } else if (parties.getPartyType().equals(PartyType.RESPONDENT.toString())) {
                parties.setStatus(Status.ACTIVE);
                if (Objects.nonNull(parties.getAdvocate())) {
                    List<Advocate> advocates=caseRepository.getAdvocateById(parties.getAdvocate().getId());
                    for (Advocate advocate: advocates){
                        if(parties.getAdvocate().getId().equals(advocate.getId())){
                            parties.setAdvocateId(parties.getAdvocate().getId());
                            parties.setAdvocate(null);
                        }
                    }
                }
            }
            if (Objects.nonNull(caseRequest.getCaseObj().getAct())) {
                for (Act act : caseRequest.getCaseObj().getAct()) {
                    act.setStatus(Status.ACTIVE);
                }
            }
            if (parties.getPartyType().equals(PartyType.PETITIONER.toString())) {
                if (Objects.nonNull(parties.getDepartmentName())) {
                    parties.setFirstName(null);
                    parties.setLastName(null);
                    parties.setGender(null);
                    parties.setPetitionerType(null);
                    parties.setAddress(null);
                    parties.setContactNumber(null);
                    parties.setDepartmentName(parties.getDepartmentName());
                } else {
                    parties.setDepartmentName(null);
                }
                parties.setPartyType(PartyType.PETITIONER.toString());
                parties.setStatus(Status.ACTIVE);
                if (Objects.nonNull(parties.getAdvocate())) {
                    parties.getAdvocate().setStatus(Status.ACTIVE);
                }
            } else {
                if (Objects.nonNull(parties.getDepartmentName())) {
                    parties.setFirstName(null);
                    parties.setLastName(null);
                    parties.setGender(null);
                    parties.setPetitionerType(null);
                    parties.setAddress(null);
                    parties.setContactNumber(null);
                    parties.setDepartmentName(parties.getDepartmentName());
                } else {
                    parties.setDepartmentName(null);
                }
                parties.setPartyType(PartyType.RESPONDENT.toString());
                parties.setStatus(Status.ACTIVE);
                if (Objects.nonNull(parties.getAdvocate())) {
                    parties.getAdvocate().setStatus(Status.ACTIVE);
                }
            }
        }
        caseRequest.getCaseObj().setStatus(Status.ACTIVE);

        caseValidator.validateCreate(caseRequest);
        caseValidator.caseNumberDuplicacyCheck(caseRequest);
        caseEnrichmentService.enrichCaseCreateRequest(caseRequest);
        if (ilmsConfiguration.getIsWorkflowEnabled()) {
            workflowService.updateWorkflow(caseRequest, CreationReason.CREATE);
            notificationService.process(ilmsConfiguration.getCreateCaseTopic(), caseRequest);
        }
        //caseRequest.getCaseObj().setTransactionCode("yolo");
        producer.push(ilmsConfiguration.getCreateCaseTopic(), caseRequest);
        return caseRequest.getCaseObj();
    }

    /**
     * Updates the ilms_case
     *
     * @param caseRequest The update Request
     * @return Updated ilmsCase
     */
    public Case update(CaseRequest caseRequest) {
        if (caseRequest.getCaseObj().getId() != null) {
            CaseSearchCriteria criteria = CaseSearchCriteria.builder().id(Collections.singletonList(caseRequest.getCaseObj().getId())).build();
            CaseResponse caseResponse = caseRepository.getLegalCaseData(criteria);
            if (!caseResponse.getCaseList().isEmpty()) {
                CaseRequest updatedCaseRequest = caseUtils.prepareObjectMapperForUpdate(caseResponse.getCaseList().get(0), caseRequest);
                Case cases = caseResponse.getCaseList().get(0);
                caseValidator.validateUpdate(cases, caseRequest);
                producer.push(ilmsConfiguration.getUpdateCaseTopic(), updatedCaseRequest);
                //                todo : notification has send to all the officers who has worked on this case.
                if (Objects.nonNull(caseRequest.getCaseObj().getWorkflow())) {
                    processCaseUpdate(caseRequest, updatedCaseRequest.getCaseObj());
                    notificationService.process(ilmsConfiguration.getUpdateCaseTopic(), caseRequest);
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
        if (ilmsConfiguration.getIsWorkflowEnabled()) {
            State state = workflowService.updateWorkflow(request, CreationReason.UPDATE);
            if (state.getIsStartState() && state.getApplicationStatus().equalsIgnoreCase(Status.ACTIVE.toString()) && !cases.getStatus()
                    .equals(Status.ACTIVE)) {
            }
        }
    }
}

