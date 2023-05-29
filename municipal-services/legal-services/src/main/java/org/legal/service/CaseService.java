package org.legal.service;

import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.tracer.model.CustomException;
import org.legal.configs.LEGALConfiguration;
import org.legal.producer.Producer;
import org.legal.repository.AdvocateRepository;
import org.legal.repository.CaseRepository;
import org.legal.repository.HearingRepository;
import org.legal.repository.JudgementRepository;
import org.legal.util.CaseUtils;
import org.legal.util.Constants;
import org.legal.util.HearingUtils;
import org.legal.util.LegalErrorConstants;
import org.legal.validator.CaseValidator;
import org.legal.web.model.*;
import org.legal.web.model.enums.CreationReason;
import org.legal.web.model.enums.PartyType;
import org.legal.web.model.enums.Status;
import org.legal.web.model.workflow.ProcessInstance;
import org.legal.web.model.workflow.ProcessInstanceRequest;
import org.legal.web.model.workflow.ProcessInstanceResponse;
import org.legal.web.model.workflow.ProcessInstanceSearchCriteria;
import org.legal.web.model.workflow.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Transactional
@Slf4j
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

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private AdvocateRepository advocateRepository;

    public CaseService() {
    }

    /**
     * Updates the legal_case
     *
     * @param caseRequest The update Request
     * @return Updated legalCase
     */

    public CaseRequest updateCase(CaseRequest caseRequest) {
        try {
            if (caseRequest.getCaseObj().getId() != null) {
                HearingSearchCriteria hearingSearchCriteria = null;
                HearingRequest request = new HearingRequest();
                CaseSearchCriteria criteria = CaseSearchCriteria.builder().id(Collections.singletonList(caseRequest.getCaseObj().getId())).build();
                CaseResponse caseResponse = caseRepository.getLegalCaseData(criteria);
                if (!caseResponse.getCaseList().isEmpty()) {
                    CaseRequest updatedCaseRequest = caseUtils.prepareObjectMapperForUpdate(caseResponse.getCaseList().get(0), caseRequest);
                    updatedCaseRequest.setWorkflow(caseRequest.getWorkflow());
                    Case cases = caseResponse.getCaseList().get(0);
                    caseValidator.validateUpdate(cases, updatedCaseRequest);
                    //                todo : notification has send to all the officers who has worked on this case.
                    RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(caseRequest.getRequestInfo()).build();
                    String caseId = updatedCaseRequest.getCaseObj().getId();
                    if (Objects.nonNull(updatedCaseRequest.getWorkflow())) {
                        if (legalConfiguration.getIsWorkflowEnabled()) {
                            updatedCaseRequest.getWorkflow().setBusinessService(legalConfiguration.getCreateCaseWfName());
                            workflowService.updateCaseWorkflowStatus(updatedCaseRequest);
                        }
                        hearingSearchCriteria = HearingSearchCriteria.builder().caseId(Collections.singletonList(caseId)).build();
                        HearingResponse hearingResponse = hearingRepository.getHearingDetails(hearingSearchCriteria);
                        if (!hearingResponse.getHearingList().isEmpty()) {
                            request.setRequestInfo(caseRequest.getRequestInfo());
                            HearingRequest hearingAppStatus = new HearingRequest();
                            Hearing hearingApp = new Hearing();
                            hearingAppStatus.setHearing(hearingApp);
                            for (Hearing hearing : hearingResponse.getHearingList()) {
                                request.setHearing(hearing);
                                Workflow workflow = new Workflow();
                                workflow.setAssignes(caseRequest.getWorkflow().getAssignes());
                                request.setWorkflow(workflow);
                                if (caseRequest.getWorkflow().getAction().equalsIgnoreCase(Constants.FORWARD_TO_RO)) {
                                    String applicationStatus = workflowService.updateHearingWorkflow(request, Constants.ASSIGNED_TO_RO);
                                    hearingAppStatus.getHearing().setApplicationStatus(applicationStatus);
                                    hearingAppStatus.getHearing().setTenantId(legalConfiguration.getTenantId());
                                    hearingAppStatus.getHearing().setId(request.getHearing().getId());
                                    hearingAppStatus.getHearing().setAuditDetails(caseUtils.getAuditDetails(caseRequest.getRequestInfo().getUserInfo().getUuid(), false));
                                    producer.push(legalConfiguration.getUpdateHearingApplicationStatusTopic(), hearingAppStatus);
                                }
                                if (caseRequest.getWorkflow().getAction().equalsIgnoreCase(Constants.INACTIVATE)) {
                                    String applicationStatus = workflowService.updateHearingWorkflow(request, Constants.DEACTIVATE);
                                    hearingAppStatus.getHearing().setApplicationStatus(applicationStatus);
                                    hearingAppStatus.getHearing().setTenantId(legalConfiguration.getTenantId());
                                    hearingAppStatus.getHearing().setId(request.getHearing().getId());
                                    hearingAppStatus.getHearing().setAuditDetails(caseUtils.getAuditDetails(caseRequest.getRequestInfo().getUserInfo().getUuid(), false));
                                    producer.push(legalConfiguration.getUpdateHearingApplicationStatusTopic(), hearingAppStatus);

                                }

                                for (Document document : caseRequest.getCaseObj().getDocuments()) {
                                    if (document.getDocumentType() != null) {
                                        if (document.getDocumentType().equalsIgnoreCase(Constants.LEGAL_DOCS_COUNTER_AFFIDAVIT) && caseRequest.getWorkflow().getAction()
                                                .equalsIgnoreCase(
                                                        Constants.SUBMIT_COUNTER_AFFIDAVIT)) {
                                            String applicationStatus = workflowService.updateHearingWorkflow(request, Constants.ASSIGNED_TO_APPOINTED_OIC);
                                            hearingAppStatus.getHearing().setApplicationStatus(applicationStatus);
                                            hearingAppStatus.getHearing().setTenantId(legalConfiguration.getTenantId());
                                            hearingAppStatus.getHearing().setId(request.getHearing().getId());
                                            hearingAppStatus.getHearing().setAuditDetails(caseUtils.getAuditDetails(caseRequest.getRequestInfo().getUserInfo().getUuid(), false));
                                            producer.push(legalConfiguration.getUpdateHearingApplicationStatusTopic(), hearingAppStatus);
                                        }
                                    }
                                }
                            }
                            notificationService.process(legalConfiguration.getUpdateCaseTopic(), caseRequest);
                        }
                    }
                    caseRequest.setCaseObj(updatedCaseRequest.getCaseObj());
                    producer.push(legalConfiguration.getUpdateCaseTopic(), updatedCaseRequest);
                } else {
                    throw new CustomException(LegalErrorConstants.CASE_NOT_AVAILABLE, "Case is not Available");
                }
            } else {
                throw new CustomException(LegalErrorConstants.CASE_NOT_AVAILABLE, "id is mandatory");
            }
            return caseRequest;
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error(LegalErrorConstants.CASE_UPDATE_FAILED_MSG, e.getMessage());
            throw new CustomException(LegalErrorConstants.CASE_UPDATE_FAILED, LegalErrorConstants.CASE_UPDATE_FAILED_MSG);
        }
    }

    public CaseResponse legalCaseSearch(CaseSearchCriteria criteria, RequestInfo requestInfo, ProcessInstanceSearchCriteria processInstanceSearchCriteria) {
        try {

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
            if (!caseResponse.getCaseList().isEmpty()) {
                OfficersCount officersCount = new OfficersCount();
                CaseSearchCriteria criteria1 = new CaseSearchCriteria();
                criteria1.setUuid(criteria.getUuid());
                total = caseRepository.getCaseCount(criteria1);
                officersCount.setTOTAL(total);

                if (userRole.equals(Constants.DEC)) {
                    dec = caseRepository.getCountOfUser(Constants.DEC);
                    officersCount.setDEC(dec);
                } else if (userRole.equals(Constants.RO)) {
                    dec = caseRepository.getCountOfUser(Constants.DEC);
                    ro = caseRepository.getCountOfUser(Constants.RO);
                    officersCount.setDEC(dec);
                    officersCount.setRO(ro);
                } else if (userRole.equals(Constants.OICA)) {
                    dec = caseRepository.getCountOfUser(Constants.DEC);
                    ro = caseRepository.getCountOfUser(Constants.RO);
                    oica = caseRepository.getCountOfUser(Constants.OICA);
                    officersCount.setDEC(dec);
                    officersCount.setRO(ro);
                    officersCount.setOICA(oica);
                } else if (userRole.equals(Constants.AO)) {
                    dec = caseRepository.getCountOfUser(Constants.DEC);
                    ro = caseRepository.getCountOfUser(Constants.RO);
                    oica = caseRepository.getCountOfUser(Constants.OICA);
                    ao = caseRepository.getCountOfUser(Constants.AO);
                    officersCount.setDEC(dec);
                    officersCount.setRO(ro);
                    officersCount.setOICA(oica);
                    officersCount.setAO(ao);
                } else if (userRole.equals(Constants.OIC) || userRole.equals(Constants.MO)) {

                    dec = caseRepository.getCountOfUser(Constants.DEC);
                    ro = caseRepository.getCountOfUser(Constants.RO);
                    oica = caseRepository.getCountOfUser(Constants.OICA);
                    ao = caseRepository.getCountOfUser(Constants.AO);
                    oic = caseRepository.getCountOfUser(Constants.OIC);
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
                HearingSearchCriteria hearingCriteria = HearingSearchCriteria.builder().caseId(Collections.singletonList(
                        caseResponse.getCaseList().get(0).getId())).build();
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
            } else {
                throw new CustomException(LegalErrorConstants.CASE_NOT_AVAILABLE, LegalErrorConstants.CASE_NOT_AVAILABLE);
            }
            return finalResult;
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error(LegalErrorConstants.CASE_SEARCH_FAILED_MSG, e.getMessage());
            throw new CustomException(LegalErrorConstants.CASE_SEARCH_FAILED, LegalErrorConstants.CASE_SEARCH_FAILED_MSG);
        }
    }

    public CaseRequest create(CaseRequest caseRequest) {
        try {

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
                if (caseRequest.getWorkflow().getAssignes() == null) {
                    List<String> users = new ArrayList<>();
                    users.add(caseRequest.getRequestInfo().getUserInfo().getUuid());
                    caseRequest.getWorkflow().setAssignes(users);
                }
                caseRequest.getWorkflow().setBusinessService(legalConfiguration.getCreateCaseWfName());
                workflowService.updateCaseWorkflowStatus(caseRequest);
                //            notificationService.process(legalConfiguration.getCreateCaseTopic(), caseRequest);
            }
            producer.push(legalConfiguration.getCreateCaseTopic(), caseRequest);
            caseRequest.getCaseObj().getParties().forEach(party -> {
                if (Objects.nonNull(party.getAdvocate()) && (party.getPartyType().equals(PartyType.PETITIONER.toString()) || party.getPartyType().equals(PartyType.RESPONDENT.toString()))) {
                    List<String> advocatesIdsReq = party.getAdvocate().stream().map(Advocate::getContactNumber).collect(Collectors.toList());
                    AdvocateSearchCriteria criteria = new AdvocateSearchCriteria();
                    List<Advocate> advocateList = new ArrayList<>();

                    for (String advContact : advocatesIdsReq) {
                        criteria.setContactNumber(advContact);
                        AdvocateResponse advocatesPresentInDB = advocateRepository.getAdvocateDetails(criteria);

                        if (!advocatesPresentInDB.getAdvocate().isEmpty()) {
                            advocateList.add(advocatesPresentInDB.getAdvocate().get(0));
                        }
                    }

                    party.setAdvocate(advocateList);
                }
            });

            caseRequest.getCaseObj().setParties(caseRequest.getCaseObj().getParties());

            return caseRequest;
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error(LegalErrorConstants.CASE_CREATE_FAILED_MSG, e.getMessage());
            throw new CustomException(LegalErrorConstants.CASE_CREATE_FAILED, LegalErrorConstants.CASE_CREATE_FAILED_MSG);
        }
    }


    public Map<String, Integer> count(CaseSearchCriteria criteria) {
        criteria.setIsPlainSearch(false);
        Map<String, Integer> statusCountMap = new HashMap<>();
        Set<String> applicationStatus = new HashSet<>();
        for (String status : criteria.getApplicationStatus()) {
            if (!status.isEmpty()) {
                applicationStatus.clear();
                applicationStatus.add(status);
                criteria.setApplicationStatus(applicationStatus);
                Integer count = caseRepository.getCount(criteria);
                statusCountMap.put(status, count);
            }
        }
        return statusCountMap;
    }
}

