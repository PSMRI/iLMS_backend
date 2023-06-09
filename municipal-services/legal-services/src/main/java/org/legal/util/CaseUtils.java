package org.legal.util;

import static org.legal.web.model.enums.Status.ACTIVE;
import static org.legal.web.model.enums.Status.INACTIVE;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.legal.configs.LEGALConfiguration;
import org.legal.producer.Producer;
import org.legal.repository.AdvocateRepository;
import org.legal.repository.CaseRepository;
import org.legal.service.AdvocateService;
import org.legal.service.CaseEnrichmentService;
import org.legal.web.model.*;
import org.legal.web.model.enums.CreationReason;
import org.legal.web.model.enums.PartyType;
import org.legal.web.model.enums.Status;
import org.legal.web.model.workflow.ProcessInstance;
import org.legal.web.model.workflow.ProcessInstanceRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
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
    @Autowired
    private CaseUtils caseUtils;

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
        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getCaseNumber())) {
            oldData.setCaseNumber(caseRequest.getCaseObj().getCaseNumber());
        }
        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getCnrNumber())) {
            oldData.setCnrNumber(caseRequest.getCaseObj().getCnrNumber());
        }
        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getParentCaseId())) {
            oldData.setParentCaseId(caseRequest.getCaseObj().getParentCaseId());
        }
        if (Objects.nonNull(caseRequest.getCaseObj().getLinkedCases())) {
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
        if (Objects.nonNull(caseRequest.getCaseObj().getFilingDate())) {
            oldData.setFilingDate(caseRequest.getCaseObj().getFilingDate());
        }
        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getSummary())) {
            oldData.setSummary(caseRequest.getCaseObj().getSummary());
        }
        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getArisingDetails())) {
            oldData.setArisingDetails(caseRequest.getCaseObj().getArisingDetails());
        }
        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getPolicyOrNonPolicyMatter())) {
            oldData.setPolicyOrNonPolicyMatter(caseRequest.getCaseObj().getPolicyOrNonPolicyMatter());
        }

        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getCaseStatus())) {
            oldData.setCaseStatus(caseRequest.getCaseObj().getCaseStatus());
        }
        if (Objects.nonNull(caseRequest.getCaseObj().getPriority())) {
            if (!StringUtils.isEmpty(caseRequest.getCaseObj().getPriority())) {
                List<String> uuids = new ArrayList<>();
                uuids.add(caseRequest.getRequestInfo().getUserInfo().getUuid());
                if (commonUtils.isUserMO(uuids, caseRequest.getCaseObj().getTenantId(), Constants.caseFlag)) {
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
        if (Objects.nonNull(caseRequest.getCaseObj().getAdditionalDetails())) {
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
//        if (Objects.nonNull(caseRequest.getCaseObj().getDocuments())) {
//            List<Document> documentList = caseRequest.getCaseObj().getDocuments();
//            for (Document document : documentList) {
//                for (Document oldDocData : oldData.getDocuments()) {
//                    //                    oldData.getDocuments().forEach(oldDocData -> {
//                    if (oldDocData.getId().equalsIgnoreCase(document.getId())) {
//
//                        if (!StringUtils.isEmpty(document.getRemarks())) {
//                            oldDocData.setRemarks(document.getRemarks());
//                        }
//
//                        if (!StringUtils.isEmpty(document.getDocumentType())) {
//                            oldDocData.setDocumentType(document.getDocumentType());
//                        }
//                        if (!StringUtils.isEmpty(document.getFileStoreId())) {
//                            oldDocData.setFileStoreId(document.getFileStoreId());
//                        }
//                        if (!StringUtils.isEmpty(document.getStatus())) {
//                            oldDocData.setStatus(document.getStatus());
//                        }
//                    }
//                }
//            }
//        }
        if (!CollectionUtils.isEmpty(caseRequest.getCaseObj().getDocuments())) {
            List<Document> documents = new ArrayList<>();
            for (Document docs : caseRequest.getCaseObj().getDocuments()) {
                List<String> docId = caseEnrichmentService.getIdList(caseRequest.getRequestInfo(), caseRequest.getCaseObj().getTenantId(), legalConfiguration.getDocumentIdgenName(),
                        legalConfiguration.getDocumentIdgenFormat(), 1);
                docs.setId(docId.get(0));
                docs.setAuditDetails(caseUtils.getAuditDetails(caseRequest.getRequestInfo().getUserInfo().getUuid(), false));
                docs.setId(docId.get(0));
                docs.setCaseId(caseRequest.getCaseObj().getId());
                docs.setStatus(Status.ACTIVE);
                documents.add(docs);
            }
            oldData.setDocuments(documents);
        }
        request.setCaseObj(oldData);
        request.setRequestInfo(caseRequest.getRequestInfo());
        caseEnrichmentService.enrichCaseUpdateRequest(request);
        return request;
    }

    public ProcessInstance changeCaseWF(CaseRequest request, String action) {
        Case caseObj = request.getCaseObj();
        Workflow workflow = request.getWorkflow();
        ProcessInstance processInstance = new ProcessInstance();
        processInstance.setBusinessId(caseObj.getId());
        processInstance.setAction(action);
        processInstance.setModuleName(legalConfiguration.getModuleName());
        processInstance.setTenantId(caseObj.getTenantId());
        processInstance.setBusinessService(legalConfiguration.getCreateCaseWfName());
        processInstance.setComment(workflow.getComments());
        if (!CollectionUtils.isEmpty(workflow.getAssignes())) {
            List<User> users = new ArrayList<>();
            workflow.getAssignes().forEach(uuid -> {
                User user = new User();
                user.setUuid(uuid);
                users.add(user);
            });
            processInstance.setAssignes(users);
        }
        return processInstance;
    }

    public List<PartyAdv> updatePartyAdvocates(CaseRequest caseRequest) {
        List<PartyAdv> partyAdvList1 = new ArrayList<>();
        String tenantId = caseRequest.getRequestInfo().getUserInfo().getTenantId();
        if (caseRequest.getCaseObj().getParties()!=null) {
            caseRequest.getCaseObj().getParties().forEach(party -> {
                if (Objects.nonNull(party.getAdvocate()) && (party.getPartyType().equals(PartyType.PETITIONER.toString()) || party.getPartyType().equals(PartyType.RESPONDENT.toString()))) {
                    List<String> advocatesIdsReq = party.getAdvocate().stream().map(Advocate::getId).collect(Collectors.toList());
                    List<Advocate> advocatesPresentInDB = advocateRepository.getAdvocatesById(advocatesIdsReq);
                    //create new advocates in the main advocate table whichever is not present
                    party.getAdvocate().forEach(advocatesReq -> {
                        if (advocatesReq.getId() == null) {
                            AdvocateRequest advocateRequest = new AdvocateRequest();
                            advocatesReq.setTenantId(tenantId);
                            advocateRequest.setAdvocate(advocatesReq);
                            advocateRequest.setRequestInfo(caseRequest.getRequestInfo());
                            Advocate responseAdv = advocateService.create(advocateRequest);
                            advocatesPresentInDB.add(responseAdv);
                        }
                    });
                    List<PartyAdv> advocatesBridge = advocateRepository.getPartyAdvByCaseIdAndPartyId(party.getId(), caseRequest.getCaseObj().getId());
                    List advocatesDbIds = advocatesPresentInDB.stream().map(Advocate::getId).collect(Collectors.toList());
                    advocatesBridge.forEach(partyAdv -> {
                        //all the advocates which are present in the bridge table and have been sent in the request, will be made active
                        if (advocatesDbIds.contains(partyAdv.getAdvocateId()) && !partyAdv.getStatus().equals(ACTIVE)) {
                            partyAdv.setStatus(ACTIVE);
                            partyAdv.setAuditDetails(getAuditDetails(caseRequest.getRequestInfo().getUserInfo().getUuid(), false));
                            PartyAdvWrapper partyAdvWrapper = PartyAdvWrapper.builder().partyAdv(partyAdv).build();
                            producer.push(legalConfiguration.getUpdatePartyAdvocateBridgeTopic(), partyAdvWrapper);
                        }
                        //the advocates which are absent in the request but present in the bridge table for this particular case and party will be made inactive
                        else if (!advocatesDbIds.contains(partyAdv.getAdvocateId()) && !partyAdv.getStatus().equals(INACTIVE)) {
                            partyAdv.setStatus(INACTIVE);
                            partyAdv.setAuditDetails(getAuditDetails(caseRequest.getRequestInfo().getUserInfo().getUuid(), false));
                            PartyAdvWrapper partyAdvWrapper = PartyAdvWrapper.builder().partyAdv(partyAdv).build();
                            producer.push(legalConfiguration.getUpdatePartyAdvocateBridgeTopic(), partyAdvWrapper);
                        }
                    });
                    List<String> advocatesBridgeIds = advocatesBridge.stream().map(PartyAdv::getAdvocateId).collect(Collectors.toList());
                    //all the advocates which are present in the bridge table and have been sent in the request, will be made active
                    advocatesPresentInDB.forEach(advocate -> {
                        if (!advocatesBridgeIds.contains(advocate.getId())) {
                            PartyAdv partyAdv1 = caseEnrichmentService.createNewPartyAdvocateId(caseRequest.getRequestInfo(), advocate.getId(),
                                    caseRequest.getCaseObj().getId(), party.getId(), party.getPartyType());
                            partyAdvList1.add(partyAdv1);
                        }
                    });
                }
            });
        }
        return partyAdvList1;
    }

    public List<Party> setParty(CaseRequest caseRequest) {
        List<Party> modifiedParties = new ArrayList<>();
        if (caseRequest.getCaseObj().getParties()!=null) {
            for (Party party : caseRequest.getCaseObj().getParties()) {
                if (party.getPartyType().equals(PartyType.PETITIONER.toString())) {
                    if (Objects.nonNull(party.getDepartmentName())) {
                        party.setFirstName(null);
                        party.setLastName(null);
                        party.setGender(null);
                        party.setPetitionerType(null);
                        party.setAddress(null);
                        party.setContactNumber(null);
                        party.setDepartmentName(party.getDepartmentName());
                    } else {
                        party.setDepartmentName(null);
                    }
                    party.setPartyType(PartyType.PETITIONER.toString());
                } else {
                    if (Objects.nonNull(party.getDepartmentName())) {
                        party.setFirstName(null);
                        party.setLastName(null);
                        party.setGender(null);
                        party.setPetitionerType(null);
                        party.setAddress(null);
                        party.setContactNumber(null);
                        party.setDepartmentName(party.getDepartmentName());
                    } else {
                        party.setDepartmentName(null);
                    }
                    party.setPartyType(PartyType.RESPONDENT.toString());
                }
                modifiedParties.add(party);
            }
        }
        return modifiedParties;
    }
}
