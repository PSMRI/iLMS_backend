package org.legal.util;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.legal.configs.LEGALConfiguration;
import org.legal.producer.Producer;
import org.legal.repository.AdvocateRepository;
import org.legal.repository.HearingRepository;
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
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
public class HearingUtils {
    @Autowired
    private CaseEnrichmentService caseEnrichmentService;

    @Autowired
    private CommonUtils commonUtils;

    @Autowired
    private HearingRepository hearingRepository;

    @Autowired
    private LEGALConfiguration configuration;

    @Autowired
    private AdvocateService advocateService;

    @Autowired
    private AdvocateRepository advocateRepository;

    @Autowired
    private CaseUtils caseUtils;

    @Autowired
    private Producer producer;

    public HearingRequest prepareHearingDetailsModalForUpdate(HearingRequest hearingDetailsRequest, Hearing oldHearingRequest) {
        HearingRequest updatedRequest = new HearingRequest();
        String tenantId = hearingRepository.getTenantIdFromHearing(hearingDetailsRequest.getHearing().getId());
        if (tenantId == null) {
            tenantId = hearingDetailsRequest.getHearing().getTenantId();
        }
        updatedRequest.setRequestInfo(hearingDetailsRequest.getRequestInfo());
        if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getCaseId())) {
            oldHearingRequest.setCaseId(hearingDetailsRequest.getHearing().getCaseId());
        }
        if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getTenantId())) {
            oldHearingRequest.setTenantId(hearingDetailsRequest.getHearing().getTenantId());
        }
        if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getHearingNumber())) {
            oldHearingRequest.setHearingNumber(hearingDetailsRequest.getHearing().getHearingNumber());
        }
        if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getFirstHearingDate())) {
            oldHearingRequest.setFirstHearingDate(hearingDetailsRequest.getHearing().getFirstHearingDate());
        }
        if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getJudgeName())) {
            oldHearingRequest.setJudgeName(hearingDetailsRequest.getHearing().getJudgeName());
        }
        if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getHearingDate())) {
            oldHearingRequest.setHearingDate(hearingDetailsRequest.getHearing().getHearingDate());
        }
        if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getBusinessDate())) {
            oldHearingRequest.setBusinessDate(hearingDetailsRequest.getHearing().getBusinessDate());
        }
        if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getCourtNumber())) {
            oldHearingRequest.setCourtNumber(hearingDetailsRequest.getHearing().getCourtNumber());
        }
        if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getBench())) {
            oldHearingRequest.setBench(hearingDetailsRequest.getHearing().getBench());
        }
        if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getHearingPurpose())) {
            oldHearingRequest.setHearingPurpose(hearingDetailsRequest.getHearing().getHearingPurpose());
        }
        if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getRequiredOfficer())) {
            oldHearingRequest.setRequiredOfficer(hearingDetailsRequest.getHearing().getRequiredOfficer());
        }
        if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getAffidavitFilingDate())) {
            List<String> uuids = new ArrayList<>();
            uuids.add(hearingDetailsRequest.getRequestInfo().getUserInfo().getUuid());
            if (commonUtils.isUserOIC(uuids, tenantId, "AffidavitFilingDate")) {
                oldHearingRequest.setAffidavitFilingDate(hearingDetailsRequest.getHearing().getAffidavitFilingDate());
            }
        }
        if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getAffidavitFilingDueDate())) {
            oldHearingRequest.setAffidavitFilingDueDate(hearingDetailsRequest.getHearing().getAffidavitFilingDueDate());
        }
        if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getCaseNumber())) {
            oldHearingRequest.setCaseNumber(hearingDetailsRequest.getHearing().getCaseNumber());
        }
        if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getOathNumber())) {
            List<String> uuids = new ArrayList<>();
            uuids.add(hearingDetailsRequest.getRequestInfo().getUserInfo().getUuid());
            if (commonUtils.isUserOIC(uuids, tenantId, "OathNumber")) {
                oldHearingRequest.setOathNumber(hearingDetailsRequest.getHearing().getOathNumber());
            }
        }
        if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getNextHearingDate())) {
            oldHearingRequest.setNextHearingDate(hearingDetailsRequest.getHearing().getNextHearingDate());

        }
        if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getIsPresenceRequired())) {
            oldHearingRequest.setIsPresenceRequired(hearingDetailsRequest.getHearing().getIsPresenceRequired());
        }

        if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getHearingType())) {
            oldHearingRequest.setHearingType(hearingDetailsRequest.getHearing().getHearingType());
        }
        if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getDepartmentOfficer())) {
            oldHearingRequest.setDepartmentOfficer(hearingDetailsRequest.getHearing().getDepartmentOfficer());
        }
        if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getRemarks())) {
            oldHearingRequest.setRemarks(hearingDetailsRequest.getHearing().getRemarks());
        }
        if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getStatus())) {
            oldHearingRequest.setStatus(hearingDetailsRequest.getHearing().getStatus());
        }
        //checking respondent details
        for (Party party : hearingDetailsRequest.getHearing().getParties()) {
            List<PartyAdv> partyAdvList=new ArrayList<>();
            for (Party oldParty : oldHearingRequest.getParties()) {
                if (party.getPartyType().equals(PartyType.RESPONDENT.toString()) && party.getId().equals(oldParty.getId())) {
                    if (!StringUtils.isEmpty(party.getCaseId())) {
                        oldParty.setCaseId(party.getCaseId());
                    }
                    if (!StringUtils.isEmpty(party.getFirstName())) {
                        oldParty.setFirstName(party.getFirstName());
                    }
                    if (!StringUtils.isEmpty(party.getAdvocateId())) {
                        oldParty.setAdvocateId(party.getAdvocateId());
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
                    if (!StringUtils.isEmpty(party.getDepartmentName())) {
                        oldParty.setDepartmentName(party.getDepartmentName());
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
                    //Setting Data For Respondent Advocate
                    if (!StringUtils.isEmpty(party.getAdvocate())) {

                        for (Advocate advocate : party.getAdvocate()) {
                //            setAdvocatesForHearing(oldHearingRequest,party,oldParty,hearingDetailsRequest.getHearing().getCaseId(),tenantId,hearingDetailsRequest.getRequestInfo());

                            AdvocateSearchCriteria criteria = AdvocateSearchCriteria.builder().contactNumber(advocate.getContactNumber()).build();
                            AdvocateResponse advocateResponse = null;
                            advocateResponse = advocateRepository.getAdvocateDetails(criteria);
                            if (!advocateResponse.getAdvocate().isEmpty()) {
                                List<Advocate> oldAdvocate1=advocateResponse.getAdvocate();
                                Advocate oldAdvocate=oldAdvocate1.get(0);
                                if (!StringUtils.isEmpty(advocate.getFirstName())) {
                                    oldAdvocate.setFirstName(advocate.getFirstName());
                                }
                                if (!StringUtils.isEmpty(advocate.getLastName())) {
                                    oldAdvocate.setLastName(advocate.getLastName());
                                }
                                if (!StringUtils.isEmpty(advocate.getContactNumber())) {
                                    oldAdvocate.setContactNumber(advocate.getContactNumber());
                                }

                                if (!StringUtils.isEmpty(advocate.getStatus())) {
                                    oldAdvocate.setStatus(advocate.getStatus());
                                }
                                    List<PartyAdv> partyAdvList1=advocateRepository.getPartyAdv(oldAdvocate.getId(),hearingDetailsRequest.getHearing().getCaseId());

                                    if (partyAdvList1.isEmpty()){

                                        PartyAdv partyAdv1 = new PartyAdv();
                                        partyAdv1.setId(UUID.randomUUID().toString());
                                        partyAdv1.setCaseId(hearingDetailsRequest.getHearing().getCaseId());
                                        partyAdv1.setAdvocateId(oldAdvocate.getId());
                                        partyAdv1.setPartyId(oldParty.getId());
                                        partyAdv1.setPartyType(party.getPartyType());
                                        partyAdv1.setAuditDetails(caseUtils.getAuditDetails(hearingDetailsRequest.getRequestInfo().getUserInfo().getUuid(), true));
                                        partyAdvList.add(partyAdv1);
                                        oldHearingRequest.setPartyAdv(partyAdvList);
                                    }
                                }
                                else {

                                    AdvocateRequest advocateRequest=new AdvocateRequest();
                                    advocate.setTenantId(oldHearingRequest.getTenantId());
                                    advocateRequest.setAdvocate(advocate);
                                    advocateRequest.setRequestInfo(hearingDetailsRequest.getRequestInfo());
                                    Advocate responseAdv= advocateService.create(advocateRequest);
                                    PartyAdv partyAdv1 = caseEnrichmentService.createNewPartyAdvocateId(hearingDetailsRequest.getRequestInfo(), oldHearingRequest.getTenantId(),
                                            responseAdv.getId(), hearingDetailsRequest.getHearing().getCaseId(), oldParty.getId(), party.getPartyType());
                                    partyAdvList.add(partyAdv1);
                                    oldHearingRequest.setPartyAdv(partyAdvList);
                                }

                            }
                            for (Advocate advocate:oldParty.getAdvocate()){
                                List<PartyAdv> partyAdvList1=advocateRepository.getPartyAdv(advocate.getId(),hearingDetailsRequest.getHearing().getCaseId());
                                for (PartyAdv partyAdv1: partyAdvList1){
                                    if (partyAdv1.getAdvocateId().equals(advocate.getId())){
                                        partyAdv1.setStatus(Status.INACTIVE);
                                        partyAdv1.setAuditDetails(caseUtils.getAuditDetails(hearingDetailsRequest.getRequestInfo().getUserInfo().getUuid(), false));
                                        PartyAdvWrapper partyAdvWrapper = PartyAdvWrapper.builder().partyAdv(partyAdv1).build();
                                        producer.push(configuration.getUpdatePartyAdvocateBridgeTopic(), partyAdvWrapper);
                                    }
                                }
                            }

                        }
                    }
                    //checking petitioner details
                    if (party.getPartyType().equals(PartyType.PETITIONER.toString()) && party.getId().equals(oldParty.getId())) {
                        if (!StringUtils.isEmpty(party.getCaseId())) {
                            oldParty.setCaseId(party.getCaseId());
                        }
                        if (!StringUtils.isEmpty(party.getFirstName())) {
                            oldParty.setFirstName(party.getFirstName());
                        }
                        if (!StringUtils.isEmpty(party.getAdvocateId())) {
                            oldParty.setAdvocateId(party.getAdvocateId());
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
                        if (!StringUtils.isEmpty(party.getDepartmentName())) {
                            oldParty.setDepartmentName(party.getDepartmentName());
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
                        //Setting Data For Petitioner Advocate
                        if (!StringUtils.isEmpty(party.getAdvocate())) {
                     //       setAdvocatesForHearing(oldHearingRequest,party,oldParty,hearingDetailsRequest.getHearing().getCaseId(),tenantId,hearingDetailsRequest.getRequestInfo());
                            for (Advocate advocate : party.getAdvocate()) {
                            AdvocateSearchCriteria criteria = AdvocateSearchCriteria.builder().contactNumber(advocate.getContactNumber()).build();
                            AdvocateResponse advocateResponse = null;
                            advocateResponse = advocateRepository.getAdvocateDetails(criteria);
                            if (!advocateResponse.getAdvocate().isEmpty()) {
                                List<Advocate> oldAdvocate1=advocateResponse.getAdvocate();
                                Advocate oldAdvocate=oldAdvocate1.get(0);
                                if (!StringUtils.isEmpty(advocate.getFirstName())) {
                                    oldAdvocate.setFirstName(advocate.getFirstName());
                                }
                                if (!StringUtils.isEmpty(advocate.getLastName())) {
                                    oldAdvocate.setLastName(advocate.getLastName());
                                }
                                if (!StringUtils.isEmpty(advocate.getContactNumber())) {
                                    oldAdvocate.setContactNumber(advocate.getContactNumber());
                                }

                                if (!StringUtils.isEmpty(advocate.getStatus())) {
                                    oldAdvocate.setStatus(advocate.getStatus());
                                }
                                List<PartyAdv> partyAdvList1=advocateRepository.getPartyAdv(oldAdvocate.getId(),hearingDetailsRequest.getHearing().getCaseId());

                                if (partyAdvList1.isEmpty()){

                                    PartyAdv partyAdv1 = new PartyAdv();
                                    partyAdv1.setId(UUID.randomUUID().toString());
                                    partyAdv1.setCaseId(hearingDetailsRequest.getHearing().getCaseId());
                                    partyAdv1.setAdvocateId(oldAdvocate.getId());
                                    partyAdv1.setPartyId(oldParty.getId());
                                    partyAdv1.setPartyType(party.getPartyType());
                                    partyAdv1.setAuditDetails(caseUtils.getAuditDetails(hearingDetailsRequest.getRequestInfo().getUserInfo().getUuid(), true));
                                    partyAdvList.add(partyAdv1);
                                    oldHearingRequest.setPartyAdv(partyAdvList);
                                }
                            }
                            else {
                                AdvocateRequest advocateRequest=new AdvocateRequest();
                                advocate.setTenantId(oldHearingRequest.getTenantId());
                                advocateRequest.setAdvocate(advocate);
                                advocateRequest.setRequestInfo(hearingDetailsRequest.getRequestInfo());
                                Advocate responseAdv= advocateService.create(advocateRequest);
                                PartyAdv partyAdv1 = caseEnrichmentService.createNewPartyAdvocateId(hearingDetailsRequest.getRequestInfo(), oldHearingRequest.getTenantId(),
                                        responseAdv.getId(), hearingDetailsRequest.getHearing().getCaseId(), oldParty.getId(), party.getPartyType());
                                partyAdvList.add(partyAdv1);
                                oldHearingRequest.setPartyAdv(partyAdvList);
                            }

                        }
                        for (Advocate advocate:oldParty.getAdvocate()){
                            List<PartyAdv> partyAdvList1=advocateRepository.getPartyAdv(advocate.getId(),hearingDetailsRequest.getHearing().getCaseId());
                            for (PartyAdv partyAdv1: partyAdvList1){
                                if (partyAdv1.getAdvocateId().equals(advocate.getId())){
                                    partyAdv1.setStatus(Status.INACTIVE);
                                    partyAdv1.setAuditDetails(caseUtils.getAuditDetails(hearingDetailsRequest.getRequestInfo().getUserInfo().getUuid(), false));
                                    PartyAdvWrapper partyAdvWrapper = PartyAdvWrapper.builder().partyAdv(partyAdv1).build();
                                    producer.push(configuration.getUpdatePartyAdvocateBridgeTopic(), partyAdvWrapper);
                                }
                            }
                        }

                    }
                    }
                }
                if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getAdditionalDetails())) {
                    oldHearingRequest.setAdditionalDetails(hearingDetailsRequest.getHearing().getAdditionalDetails());
                }
            }
            if (Objects.nonNull(hearingDetailsRequest.getHearing().getPayment())) {
                if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getPayment().getFineImposedDate())) {
                    oldHearingRequest.getPayment().setFineImposedDate(hearingDetailsRequest.getHearing().getPayment().getFineImposedDate());
                }
                if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getPayment().getFineDueDate())) {
                    oldHearingRequest.getPayment().setFineDueDate(hearingDetailsRequest.getHearing().getPayment().getFineDueDate());
                }
                if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getPayment().getFineAmount())) {
                    oldHearingRequest.getPayment().setFineAmount(hearingDetailsRequest.getHearing().getPayment().getFineAmount());
                }
            }

        updatedRequest.setHearing(oldHearingRequest);
        caseEnrichmentService.enrichmentForHearingUpdateRequest(updatedRequest);
        return updatedRequest;
    }


    public ProcessInstanceRequest getWfForHearingCreate(HearingRequest request, CreationReason creationReasonForWorkflow) {

        Hearing hearing = request.getHearing();
        ProcessInstance wf = null != hearing.getWorkflow() ? hearing.getWorkflow() : new ProcessInstance();
        wf.setBusinessId(hearing.getId());

        switch (creationReasonForWorkflow) {
            case CREATE:
                wf.setBusinessService(configuration.getCreateHearingWfName());
                wf.setModuleName(configuration.getPropertyModuleName());

                wf.setAction("CREATE_HEARING");
                wf.setTenantId(request.getHearing().getTenantId());
                List<User> userList = new ArrayList<>();
                User user = new User();
                user.setUuid(request.getRequestInfo().getUserInfo().getUuid());
                userList.add(user);
                wf.setAssignes(userList);

                break;

            case UPDATE:
                String hearingId = request.getHearing().getId();
                HearingSearchCriteria criteria = HearingSearchCriteria.builder().id(hearingId).build();
                HearingResponse hearingResponse = hearingRepository.getHearingDetails(criteria);
                String tenantId = hearingResponse.getHearingList().get(0).getTenantId();
                wf.setTenantId(tenantId);
                wf.setAssignes(request.getHearing().getWorkflow().getAssignes());
                break;

            default:
                break;
        }
        hearing.setWorkflow(wf);
        return ProcessInstanceRequest.builder().processInstances(Collections.singletonList(wf)).requestInfo(request.getRequestInfo()).build();
    }

    public ProcessInstanceRequest hearingWFUpdate(HearingRequest request, String action) {
        Hearing hearing = request.getHearing();
        ProcessInstance wf = null != hearing.getWorkflow() ? hearing.getWorkflow() : new ProcessInstance();
        wf.setBusinessId(hearing.getId());
        wf.setBusinessService(configuration.getCreateHearingWfName());
        wf.setModuleName(configuration.getPropertyModuleName());
        wf.setAction(action);
        wf.setTenantId(request.getHearing().getTenantId());
        wf.setAssignes(request.getHearing().getWorkflow().getAssignes());
        hearing.setWorkflow(wf);
        return ProcessInstanceRequest.builder().processInstances(Collections.singletonList(wf)).requestInfo(request.getRequestInfo()).build();
    }

//    public void setAdvocatesForHearing(Hearing oldHearingRequest,Party partyRequest,Party oldParty, String caseId,String tenantId, RequestInfo requestInfo){
//        List<PartyAdv> partyAdvList=new ArrayList<>();
//        for (Advocate advocate : partyRequest.getAdvocate()) {
//
//            AdvocateSearchCriteria criteria = AdvocateSearchCriteria.builder().contactNumber(advocate.getContactNumber()).build();
//            AdvocateResponse advocateResponse = null;
//            advocateResponse = advocateRepository.getAdvocateDetails(criteria);
//            if (!advocateResponse.getAdvocate().isEmpty()) {
//                List<Advocate> oldAdvocate1=advocateResponse.getAdvocate();
//                Advocate oldAdvocate=oldAdvocate1.get(0);
//                if (!StringUtils.isEmpty(advocate.getFirstName())) {
//                    oldAdvocate.setFirstName(advocate.getFirstName());
//                }
//                if (!StringUtils.isEmpty(advocate.getLastName())) {
//                    oldAdvocate.setLastName(advocate.getLastName());
//                }
//                if (!StringUtils.isEmpty(advocate.getContactNumber())) {
//                    oldAdvocate.setContactNumber(advocate.getContactNumber());
//                }
//
//                if (!StringUtils.isEmpty(advocate.getStatus())) {
//                    oldAdvocate.setStatus(advocate.getStatus());
//                }
//                List<PartyAdv> partyAdvList1=advocateRepository.getPartyAdv(oldAdvocate.getId(),caseId);
//
//                if (partyAdvList1.isEmpty()){
//
//                    PartyAdv partyAdv1 = new PartyAdv();
//                    partyAdv1.setId(UUID.randomUUID().toString());
//                    partyAdv1.setCaseId(caseId);
//                    partyAdv1.setAdvocateId(oldAdvocate.getId());
//                    partyAdv1.setPartyId(oldParty.getId());
//                    partyAdv1.setPartyType(oldParty.getPartyType());
//                    partyAdv1.setAuditDetails(caseUtils.getAuditDetails(requestInfo.getUserInfo().getUuid(), true));
//                    partyAdvList.add(partyAdv1);
//                    oldHearingRequest.setPartyAdv(partyAdvList);
//                }
//            }
//            else {
//
//                AdvocateRequest advocateRequest=new AdvocateRequest();
//                advocate.setTenantId(tenantId);
//                advocateRequest.setAdvocate(advocate);
//                advocateRequest.setRequestInfo(requestInfo);
//                Advocate responseAdv= advocateService.create(advocateRequest);
//                PartyAdv partyAdv1 = caseEnrichmentService.createNewPartyAdvocateId(requestInfo, tenantId,
//                        responseAdv.getId(), caseId, oldParty.getId(), oldParty.getPartyType());
//                partyAdvList.add(partyAdv1);
//                oldHearingRequest.setPartyAdv(partyAdvList);
//            }
//
//        }
//        for (Advocate advocate:oldParty.getAdvocate()){
//            List<PartyAdv> partyAdvList1=advocateRepository.getPartyAdv(advocate.getId(),caseId);
//            for (PartyAdv partyAdv1: partyAdvList1){
//                if (partyAdv1.getAdvocateId().equals(advocate.getId())){
//                    partyAdv1.setStatus(Status.INACTIVE);
//                    partyAdv1.setAuditDetails(caseUtils.getAuditDetails(requestInfo.getUserInfo().getUuid(), false));
//                    PartyAdvWrapper partyAdvWrapper = PartyAdvWrapper.builder().partyAdv(partyAdv1).build();
//                    producer.push(configuration.getUpdatePartyAdvocateBridgeTopic(), partyAdvWrapper);
//                }
//            }
//        }
//
//    }
}
