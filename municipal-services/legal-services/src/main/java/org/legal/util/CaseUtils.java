package org.legal.util;

import static org.legal.web.model.enums.Status.INACTIVE;
import org.egov.common.contract.request.User;
import org.legal.configs.LEGALConfiguration;
import org.legal.producer.Producer;
import org.legal.repository.AdvocateRepository;
import org.legal.repository.CaseRepository;
import org.legal.service.AdvocateService;
import org.legal.service.CaseEnrichmentService;
import org.legal.web.model.*;
import org.legal.web.model.enums.CreationReason;
import org.legal.web.model.workflow.ProcessInstance;
import org.legal.web.model.workflow.ProcessInstanceRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class CaseUtils {
    @Autowired
    private CaseEnrichmentService caseEnrichmentService;

    @Autowired
    private LEGALConfiguration legalConfiguration;

    @Autowired
    private CaseRepository caseRepository;

    @Autowired
    private CommonUtils commonUtils;

    @Autowired
    private AdvocateRepository advocateRepository;

    @Autowired
    private Producer producer;

    @Autowired
    private AdvocateService advocateService;

    public AuditDetails getAuditDetails(String by, Boolean isCreate) {
        Long time = System.currentTimeMillis();
        if (isCreate) {
            return AuditDetails.builder().createdBy(by).createdTime(time).build();
        } else {
            return AuditDetails.builder().lastModifiedBy(by).lastModifiedTime(time).build();
        }
    }

    public CaseRequest prepareObjectMapperForUpdate(Case oldData, CaseRequest caseRequest) {
        final CaseRequest request = new CaseRequest();

        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getTenantId())) {
            oldData.setTenantId(caseRequest.getCaseObj().getTenantId());
        }
        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getNumber())) {
            oldData.setNumber(caseRequest.getCaseObj().getNumber());
        }
        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getCnrNumber())) {
            oldData.setCnrNumber(caseRequest.getCaseObj().getCnrNumber());
        }
        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getParentCaseId())) {
            oldData.setParentCaseId(caseRequest.getCaseObj().getParentCaseId());
        }
        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getLinkedCases())) {
            oldData.setLinkedCases(caseRequest.getCaseObj().getLinkedCases());
        }

        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getType())) {
            oldData.setType(caseRequest.getCaseObj().getType());
        }
        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getCategory())) {
            oldData.setCategory(caseRequest.getCaseObj().getCategory());
        }

        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getFilingNumber())) {
            oldData.setFilingNumber(caseRequest.getCaseObj().getFilingNumber());
        }
        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getFilingDate())) {
            oldData.setFilingDate(caseRequest.getCaseObj().getFilingDate());
        }
//        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getRegistrationDate())) {
//            oldData.setRegistrationDate(caseRequest.getCaseObj().getRegistrationDate());
//        }
        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getSummary())) {
            oldData.setSummary(caseRequest.getCaseObj().getSummary());
        }
        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getArisingDetails())) {
            oldData.setArisingDetails(caseRequest.getCaseObj().getArisingDetails());
        }
        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getPolicyOrNonPolicyMatter())) {
            oldData.setPolicyOrNonPolicyMatter(caseRequest.getCaseObj().getPolicyOrNonPolicyMatter());
        }
//        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getApplicationNumber())) {
//            oldData.setApplicationNumber(caseRequest.getCaseObj().getApplicationNumber());
//        }
        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getCaseStatus())) {
            oldData.setCaseStatus(caseRequest.getCaseObj().getCaseStatus());
        }
//        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getSubStage())) {
//            oldData.setSubStage(caseRequest.getCaseObj().getSubStage());
//        }
        if (Objects.nonNull(caseRequest.getCaseObj().getPriority())) {
            if (!StringUtils.isEmpty(caseRequest.getCaseObj().getPriority())) {
                List<String> uuids = new ArrayList<>();
                uuids.add(caseRequest.getRequestInfo().getUserInfo().getUuid());
                if (commonUtils.isUserMO(uuids, caseRequest.getCaseObj().getTenantId(), "caseFlag")) {
                    oldData.setPriority(caseRequest.getCaseObj().getPriority());
                }
            }
        }
        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getRecommendOIC())) {
            oldData.setRecommendOIC(caseRequest.getCaseObj().getRecommendOIC());
        }
        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getRemarks())) {
            oldData.setRemarks(caseRequest.getCaseObj().getRemarks());
        }
        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getAdditionalDetails())) {
            oldData.setAdditionalDetails(caseRequest.getCaseObj().getAdditionalDetails());
        }
        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getStatus())) {
            oldData.setStatus(caseRequest.getCaseObj().getStatus());
        }
        //        Setting Petitioner Details
        if (Objects.nonNull(caseRequest.getCaseObj().getParties())) {
            for (Party party : caseRequest.getCaseObj().getParties()) {
                for (Party oldParty : oldData.getParties()) {
                    if (party.getId().equals(oldParty.getId())) {
                        if (Objects.nonNull(party.getDepartmentName())) {

                            if (!StringUtils.isEmpty(party.getCaseId())) {
                                oldParty.setCaseId(party.getCaseId());
                            }
                            if (!StringUtils.isEmpty(party.getFirstName())) {
                                oldParty.setFirstName(null);
                            }
                            if (!StringUtils.isEmpty(party.getLastName())) {
                                oldParty.setLastName(null);
                            }
                            if (!StringUtils.isEmpty(party.getGender())) {
                                oldParty.setGender(null);
                            }
                            if (!StringUtils.isEmpty(party.getPetitionerType())) {
                                oldParty.setPetitionerType(null);
                            }
                            if (!StringUtils.isEmpty(party.getAddress())) {
                                oldParty.setAddress(null);
                            }
                            if (!StringUtils.isEmpty(party.getDepartmentName())) {
                                oldParty.setDepartmentName(party.getDepartmentName());
                            }
                            if (!StringUtils.isEmpty(party.getContactNumber())) {
                                oldParty.setContactNumber(null);
                            }
                            if (!StringUtils.isEmpty(party.getPartyType())) {
                                oldParty.setPartyType(party.getPartyType());
                            }
                            if (!StringUtils.isEmpty(party.getStatus())) {
                                oldParty.setStatus(party.getStatus());
                            }
                        } else {
                            if (!StringUtils.isEmpty(party.getCaseId())) {
                                oldParty.setCaseId(party.getCaseId());
                            }
                            if (!StringUtils.isEmpty(party.getFirstName())) {
                                oldParty.setFirstName(party.getFirstName());
                            }
                            if (!StringUtils.isEmpty(party.getLastName())) {
                                oldParty.setLastName(party.getLastName());
                            }
                            if (!StringUtils.isEmpty(party.getGender())) {
                                oldParty.setGender(party.getGender());
                            }
                            if (!StringUtils.isEmpty(party.getPetitionerType())) {
                                oldParty.setPetitionerType(party.getPetitionerType());
                            }
                            if (!StringUtils.isEmpty(party.getAddress())) {
                                oldParty.setAddress(party.getAddress());
                            }
                            if (StringUtils.isEmpty(party.getDepartmentName())) {
                                oldParty.setDepartmentName(null);
                            }
                            if (!StringUtils.isEmpty(party.getContactNumber())) {
                                oldParty.setContactNumber(party.getContactNumber());
                            }
                            if (!StringUtils.isEmpty(party.getPartyType())) {
                                oldParty.setPartyType(party.getPartyType());
                            }
                            if (!StringUtils.isEmpty(party.getStatus())) {
                                oldParty.setStatus(party.getStatus());
                            }
                        }
                    }
                }
            }
            List<PartyAdv> partyAdvList = updatePartyAdvocates(caseRequest);
            oldData.setPartyAdv(partyAdvList);
        }
        //setting act details
        if (Objects.nonNull(caseRequest.getCaseObj().getAct())) {
            List<Act> actList = caseRequest.getCaseObj().getAct();
            List<Act> oldAct = oldData.getAct();
            for (Act act : actList) {
                for (Act oldActData : oldAct) {
                    if (oldActData.getId().equalsIgnoreCase(act.getId())) {

                        if (!StringUtils.isEmpty(act.getCaseId())) {
                            oldActData.setCaseId(act.getCaseId());
                        }
                        if (!StringUtils.isEmpty(act.getActName())) {
                            oldActData.setActName(act.getActName());
                        }
                        if (!StringUtils.isEmpty(act.getSectionNumber())) {
                            oldActData.setSectionNumber(act.getSectionNumber());
                        }
                        if (!StringUtils.isEmpty(act.getStatus())) {
                            oldActData.setStatus(act.getStatus());
                        }
                    }
                }

            }
        }
        //setting documents details
        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getDocuments())) {
            List<Document> documentList = caseRequest.getCaseObj().getDocuments();
            for (Document document : documentList) {
                for (Document oldDocData : oldData.getDocuments()) {
                    //                    oldData.getDocuments().forEach(oldDocData -> {
                    if (oldDocData.getId().equalsIgnoreCase(document.getId())) {
//                        if (!StringUtils.isEmpty(document.getCaseId())) {
//                            oldDocData.setCaseId(document.getCaseId());
//                        }
                        if (!StringUtils.isEmpty(document.getRemarks())) {
                            oldDocData.setRemarks(document.getRemarks());
                        }
//                        if (!StringUtils.isEmpty(document.getDocumentId())) {
//                            oldDocData.setDocumentId(document.getDocumentId());
//                        }
                        if (!StringUtils.isEmpty(document.getDocumentType())) {
                            oldDocData.setDocumentType(document.getDocumentType());
                        }
                        if (!StringUtils.isEmpty(document.getFileStoreId())) {
                            oldDocData.setFileStoreId(document.getFileStoreId());
                        }
                        if (!StringUtils.isEmpty(document.getStatus())) {
                            oldDocData.setStatus(document.getStatus());
                        }
                    }
                }
            }
        }
        request.setCaseObj(oldData);
        request.setRequestInfo(caseRequest.getRequestInfo());
        caseEnrichmentService.enrichCaseUpdateRequest(request);
        return request;
    }

    public ProcessInstanceRequest getWfForCaseCreate(CaseRequest request, CreationReason creationReasonForWorkflow) {

        Case aCase = request.getCaseObj();
        ProcessInstance wf = null != aCase.getWorkflow() ? aCase.getWorkflow() : new ProcessInstance();
        wf.setBusinessId(aCase.getId());

        switch (creationReasonForWorkflow) {
            case CREATE:
                wf.setBusinessService(legalConfiguration.getCreateCaseWfName());
                wf.setModuleName(legalConfiguration.getPropertyModuleName());

                wf.setAction("CREATE_CASE");
                wf.setTenantId(request.getCaseObj().getTenantId());
                List<User> userList = new ArrayList<>();
                User user = new User();
                user.setUuid(request.getRequestInfo().getUserInfo().getUuid());
                userList.add(user);
                wf.setAssignes(userList);

                break;

            case UPDATE:
                String caseId = request.getCaseObj().getId();
                CaseSearchCriteria criteria = CaseSearchCriteria.builder().id(Collections.singletonList(caseId)).build();
                CaseResponse caseResponse = caseRepository.getLegalCaseData(criteria);
                String tenantId = caseResponse.getCaseList().get(0).getTenantId();
                wf.setTenantId(tenantId);
                wf.setAssignes(request.getCaseObj().getWorkflow().getAssignes());
                break;

            default:
                break;
        }
        aCase.setWorkflow(wf);
        return ProcessInstanceRequest.builder().processInstances(Collections.singletonList(wf)).requestInfo(request.getRequestInfo()).build();
    }

    public ProcessInstanceRequest changeCaseWF(CaseRequest request, String action) {

        Case aCase = request.getCaseObj();
        ProcessInstance wf = null != aCase.getWorkflow() ? aCase.getWorkflow() : new ProcessInstance();
        wf.setBusinessId(aCase.getId());
        wf.setBusinessService(legalConfiguration.getCreateCaseWfName());
        wf.setModuleName(legalConfiguration.getPropertyModuleName());
        wf.setAction(action);
        wf.setTenantId(request.getCaseObj().getTenantId());
        wf.setAssignes(request.getCaseObj().getWorkflow().getAssignes());
        aCase.setWorkflow(wf);
        return ProcessInstanceRequest.builder().processInstances(Collections.singletonList(wf)).requestInfo(request.getRequestInfo()).build();
    }

    public List<PartyAdv> updatePartyAdvocates (CaseRequest caseRequest){
        List<PartyAdv> partyAdvList1 = new ArrayList();
        List<Party> parties = caseRequest.getCaseObj().getParties();
        String tenantId = caseRequest.getRequestInfo().getUserInfo().getTenantId();
        parties.forEach(party -> {
            if (Objects.nonNull(party.getAdvocate())) {
                List<String> advocatesMobileReq = party.getAdvocate().stream().map(Advocate::getContactNumber).collect(Collectors.toList());
                List<Advocate> advocatesReqPresentInDB = advocateRepository.getAdvocates(advocatesMobileReq);
                List<String> advocatesIdDBReq = advocatesReqPresentInDB.stream().map(Advocate::getId).collect(Collectors.toList());
                List<String> advocatesMobileDBReq = advocatesReqPresentInDB.stream().map(Advocate::getContactNumber).collect(Collectors.toList());

                List<PartyAdv> advocates = advocateRepository.getPartyCaseAdv(party.getId(), caseRequest.getCaseObj().getId());
                List<String> advocatesBridgeDB = advocates.stream().map(PartyAdv::getAdvocateId).collect(Collectors.toList());

                List<String> advocatesReqCopy = new ArrayList<>(advocatesIdDBReq);
                advocatesIdDBReq.removeAll(advocatesBridgeDB);
                advocatesBridgeDB.removeAll(advocatesReqCopy);
                if (advocatesBridgeDB.size() > 0) {
                    PartyMultipleAdvocates partyMultipleAdvocates = new PartyMultipleAdvocates();
                    partyMultipleAdvocates.setAdvocateId(advocatesBridgeDB);
                    partyMultipleAdvocates.setStatus(INACTIVE);
                    partyMultipleAdvocates.setAuditDetails(getAuditDetails(caseRequest.getRequestInfo().getUserInfo().getUuid(), false));
                    partyMultipleAdvocates.setPartyId(party.getId());
                    PartyAdvWrapper partyAdvWrapper = PartyAdvWrapper.builder().partyMultipleAdvocates(partyMultipleAdvocates).build();
                    producer.push(legalConfiguration.getUpdatePartyAdvocateBridgeTopic(), partyAdvWrapper);
                }
                advocatesMobileReq.removeAll(advocatesMobileDBReq);
                List<Advocate> advocateListRequestAbsentDB = party.getAdvocate().stream()
                                                                   .filter(advocateFilter -> advocatesMobileReq.contains(advocateFilter.getContactNumber()))
                                                                   .collect(Collectors.toList());
                List<Advocate> allAdvocates = new ArrayList<>();
                advocateListRequestAbsentDB.forEach(advocatesNotInDB -> {
                    AdvocateRequest advocateRequest = new AdvocateRequest();
                    advocatesNotInDB.setTenantId(tenantId);
                    advocateRequest.setAdvocate(advocatesNotInDB);
                    advocateRequest.setRequestInfo(caseRequest.getRequestInfo());
                    Advocate responseAdv = advocateService.create(advocateRequest);
                    allAdvocates.add(responseAdv);
                });
                allAdvocates.addAll(advocatesReqPresentInDB);

                if (allAdvocates.size() > 0) {
                    allAdvocates.forEach(advocate -> {
                        // we will first check if that advocate exists in advocate table
                        // if yes, then we make that entry in bridge table
                        // if no, we first make an entry in advocate table then in bridge table
                        PartyAdv partyAdv1 = caseEnrichmentService.createNewPartyAdvocateId(caseRequest.getRequestInfo(), tenantId,
                                advocate.getId(), caseRequest.getCaseObj().getId(), party.getId(), party.getPartyType());
                        partyAdvList1.add(partyAdv1);
                    });
                }
            }
        });
        return partyAdvList1;
    }
}
