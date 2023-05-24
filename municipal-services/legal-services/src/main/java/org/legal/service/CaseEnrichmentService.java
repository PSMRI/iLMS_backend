package org.legal.service;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.legal.configs.LEGALConfiguration;
import org.legal.repository.CaseRepository;
import org.legal.repository.IdGenRepository;
import org.legal.util.CaseUtils;
import org.legal.util.LegalErrorConstants;
import org.legal.web.model.*;
import org.legal.web.model.enums.PartyType;
import org.legal.web.model.enums.Status;
import org.legal.web.model.idGen.IdResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CaseEnrichmentService {
    @Autowired
    private LEGALConfiguration legalConfiguration;

    @Autowired
    private CaseUtils caseUtils;

    @Autowired
    private IdGenRepository idGenRepository;

    @Autowired
    private CaseRepository caseRepository;

    @Autowired
    private AdvocateService advocateService;


    public void enrichCaseCreateRequest(CaseRequest caseRequest) {
        RequestInfo requestInfo = caseRequest.getRequestInfo();
        Case aCase = caseRequest.getCaseObj();
        setIdgenIds(caseRequest);
        AuditDetails auditDetails = caseUtils.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);
        caseRequest.getCaseObj().setAuditDetails(auditDetails);
        aCase.setAuditDetails(auditDetails);
        if (Objects.nonNull(caseRequest.getCaseObj().getCourt())) {
            caseRequest.getCaseObj().getCourt().setAuditDetails(auditDetails);
            aCase.getCourt().setAuditDetails(auditDetails);
        }
        for (Party party : aCase.getParties()) {
            party.setAuditDetails(auditDetails);
            if (Objects.nonNull(party.getAdvocate()))
                for (Advocate advocate : party.getAdvocate()) {
                    advocate.setAuditDetails(auditDetails);
                }
        }
        if (!CollectionUtils.isEmpty(aCase.getDocuments())) {
            aCase.getDocuments().forEach(doc -> {
                doc.setAuditDetails(auditDetails);
                doc.setStatus(Status.ACTIVE);
            });
        }
//        if (caseRequest.getCaseObj().getAct() != null) {
//            caseRequest.getCaseObj().getAct().setAuditDetails(auditDetails);
//            aCase.getAct().setAuditDetails(auditDetails);
//        }
        if (Objects.nonNull(caseRequest.getCaseObj().getAct())) {
            for (Act act : aCase.getAct()) {
                act.setAuditDetails(auditDetails);
            }
        }
    }

    public void enrichmentForHearingUpdateRequest(HearingRequest request) {
        RequestInfo requestInfo = request.getRequestInfo();
        Hearing legalCase = request.getHearing();
        AuditDetails auditDetails = caseUtils.getAuditDetails(request.getRequestInfo().getUserInfo().getUuid(), false);
        request.getHearing().setAuditDetails(auditDetails);
        legalCase.setAuditDetails(auditDetails);
        for (Party party : legalCase.getParties()) {
            party.setAuditDetails(auditDetails);
            for (Advocate advocate : party.getAdvocate()) {
                advocate.setAuditDetails(auditDetails);
            }
        }
        if (request.getHearing().getPayment() != null) {
            request.getHearing().getPayment().setAuditDetails(auditDetails);
            legalCase.getPayment().setAuditDetails(auditDetails);
        }
    }

    private void setIdgenIds(CaseRequest request) {
        PartyAdv partyAdv = new PartyAdv();
        RequestInfo requestInfo = request.getRequestInfo();
        String tenantId = request.getCaseObj().getTenantId();
        Case caseObj = request.getCaseObj();
        List<String> caseId = getIdList(requestInfo, tenantId, legalConfiguration.getCaseIdgenName(), legalConfiguration.getCaseIdgenFormat(), 1);
        ListIterator<String> caseItr = caseId.listIterator();
        List<String> courtId = getIdList(requestInfo, tenantId, legalConfiguration.getCourtIdgenName(),
                legalConfiguration.getCourtIdgenFormat(), 1);
        ListIterator<String> courtItr = courtId.listIterator();
        Map<String, String> errorMap = new HashMap<>();

        if (!errorMap.isEmpty()) {
            throw new CustomException(errorMap);
        }
        caseObj.setId(caseItr.next());
        if (Objects.nonNull(caseObj.getCourt())) {
            caseObj.getCourt().setId(courtItr.next());
        }
        if (Objects.nonNull(caseObj.getAct())) {
            for (Act act : caseObj.getAct()) {
                List<String> actId = getIdList(requestInfo, tenantId, legalConfiguration.getActIdgenName(), legalConfiguration.getActIdgenFormat(), 1);
                ListIterator<String> actItr = actId.listIterator();
                act.setId(actItr.next());
            }
        }
        for (Party party : caseObj.getParties()) {
            if (party.getPartyType().equals(PartyType.PETITIONER.toString())) {
                List<String> petitionerId = getIdList(requestInfo, tenantId, legalConfiguration.getPetitionerIdgenName(),
                        legalConfiguration.getPetitionerIdgenFormat(), 1);
                ListIterator<String> petitionerItr = petitionerId.listIterator();
                party.setId(petitionerItr.next());
                if (Objects.nonNull(party.getAdvocate())) {
                    List<PartyAdv> partyAdvList = caseObj.getPartyAdv();
                    if (partyAdvList == null) {
                        partyAdvList = new ArrayList<>();
                    }
                    for (Advocate advocate : party.getAdvocate()) {
                        AdvocateSearchCriteria criteria = new AdvocateSearchCriteria();
                        criteria.setContactNumber(advocate.getContactNumber());
                        AdvocateResponse petadvocate = advocateService.advocateSearch(criteria);
                        // List<Advocate> advocates = new ArrayList<>();
                        if (!petadvocate.getAdvocate().isEmpty()) {
                            Advocate advocate1 = petadvocate.getAdvocate().get(0);
                            PartyAdv partyAdv1 = new PartyAdv();
                            partyAdv1.setId(UUID.randomUUID().toString());
                            partyAdv1.setCaseId(party.getCaseId());
                            partyAdv1.setAdvocateId(advocate1.getId());
                            partyAdv1.setPartyId(party.getId());
                            partyAdv1.setPartyType(party.getPartyType());
                            partyAdv1.setAuditDetails(caseUtils.getAuditDetails(requestInfo.getUserInfo().getUuid(), true));
                            partyAdvList.add(partyAdv1);
                            party.setAdvocate(null);
                        } else {
                            List<String> padvocateId = getIdList(requestInfo, tenantId, legalConfiguration.getPetitionerAdvocateIdgenName(),
                                    legalConfiguration.getPetitionerAdvocateIdgenFormat(), 1);
                            ListIterator<String> padvocateItr = padvocateId.listIterator();
                            advocate.setId(padvocateItr.next());
                            PartyAdv partyAdv1 = new PartyAdv();
                            partyAdv1.setId(UUID.randomUUID().toString());
                            partyAdv1.setCaseId(caseId.toString());
                            partyAdv1.setAdvocateId(advocate.getId());
                            partyAdv1.setPartyId(party.getId());
                            partyAdv1.setPartyType(party.getPartyType());
                            partyAdv1.setAuditDetails(caseUtils.getAuditDetails(requestInfo.getUserInfo().getUuid(), true));
                            partyAdvList.add(partyAdv1);
                        }

                        caseObj.setPartyAdv(partyAdvList);
                    }

                }

            } else {
                List<String> respondentId = getIdList(requestInfo, tenantId, legalConfiguration.getRespondentIdgenName(),
                        legalConfiguration.getRespondentIdgenFormat(), 1);
                ListIterator<String> respondentItr = respondentId.listIterator();
                party.setId(respondentItr.next());
                if (Objects.nonNull(party.getAdvocate())) {
                    List<PartyAdv> partyAdvList = caseObj.getPartyAdv();
                    if (partyAdvList == null) {
                        partyAdvList = new ArrayList<>();
                    }
                    for (Advocate advocate : party.getAdvocate()) {
                        AdvocateSearchCriteria criteria = new AdvocateSearchCriteria();
                        criteria.setContactNumber(advocate.getContactNumber());
                        AdvocateResponse resadvocate = advocateService.advocateSearch(criteria);
//                        List<Advocate> advocates = new ArrayList<>();
                        if (!resadvocate.getAdvocate().isEmpty()) {
                            Advocate advocate1 = resadvocate.getAdvocate().get(0);
                            PartyAdv partyAdv1 = new PartyAdv();
                            partyAdv1.setId(UUID.randomUUID().toString());
                            partyAdv1.setCaseId(party.getCaseId());
                            partyAdv1.setAdvocateId(advocate1.getId());
                            partyAdv1.setPartyId(party.getId());
                            partyAdv1.setPartyType(party.getPartyType());
                            partyAdvList.add(partyAdv1);
                            partyAdv1.setAuditDetails(caseUtils.getAuditDetails(requestInfo.getUserInfo().getUuid(), true));
                            party.setAdvocate(null);
                        } else
//                        for (Advocate radvocate : advocates) {
                        {
                            List<String> radvocateId = getIdList(requestInfo, tenantId, legalConfiguration.getRespondentAdvocateIdgenName(),
                                    legalConfiguration.getRespondentAdvocateIdgenFormat(), 1);
                            ListIterator<String> radvocateItr = radvocateId.listIterator();
                            advocate.setId(radvocateItr.next());
                            PartyAdv partyAdv1 = new PartyAdv();
                            partyAdv1.setId(UUID.randomUUID().toString());
                            partyAdv1.setCaseId(caseId.toString());
                            partyAdv1.setAdvocateId(advocate.getId());
                            partyAdv1.setPartyId(party.getId());
                            partyAdv1.setPartyType(party.getPartyType());
                            partyAdv1.setAuditDetails(caseUtils.getAuditDetails(requestInfo.getUserInfo().getUuid(), true));
                            partyAdvList.add(partyAdv1);
                        }
                        caseObj.setPartyAdv(partyAdvList);
                    }

                }

            }
        }
        if (Objects.nonNull(caseObj.getDocuments())) {
            caseObj.getDocuments().forEach((doc -> {
                List<String> docId = getIdList(requestInfo, tenantId, legalConfiguration.getDocumentIdgenName(),
                        legalConfiguration.getDocumentIdgenFormat(), 1);
                doc.setId(docId.get(0));
            }));
        }
    }

    private List<String> getIdList(RequestInfo requestInfo, String tenantId, String idName, String idformat, int count) {
        List<IdResponse> idResponses = idGenRepository.getId(requestInfo, tenantId, idName, idformat, count).getIdResponses();
        if (CollectionUtils.isEmpty(idResponses)) {
            throw new CustomException(LegalErrorConstants.IDGEN_ERROR, "No ids returned from idgen Service");
        }
        return idResponses.stream().map(IdResponse::getId).collect(Collectors.toList());
    }

    public void enrichCaseUpdateRequest(CaseRequest caseRequest) {
        RequestInfo requestInfo = caseRequest.getRequestInfo();
        Case aCase = caseRequest.getCaseObj();
        AuditDetails auditDetails = caseUtils.getAuditDetails(requestInfo.getUserInfo().getUuid(), false);
        caseRequest.getCaseObj().setAuditDetails(auditDetails);
        aCase.setAuditDetails(auditDetails);
        if (caseRequest.getCaseObj().getCourt() != null) {
            caseRequest.getCaseObj().getCourt().setAuditDetails(auditDetails);
            aCase.getCourt().setAuditDetails(auditDetails);
        }
        for (Party party : aCase.getParties()) {
            party.setAuditDetails(auditDetails);
            for (Advocate advocate : party.getAdvocate()) {
                advocate.setAuditDetails(auditDetails);
            }
        }
//        if (caseRequest.getCaseObj().getAct() != null) {
//            caseRequest.getCaseObj().getAct().setAuditDetails(auditDetails);
//            aCase.getAct().setAuditDetails(auditDetails);
//        }
        if (Objects.nonNull(aCase.getAct())) {
            for (Act act : aCase.getAct()) {
                act.setAuditDetails(auditDetails);
            }
        }
        if (!CollectionUtils.isEmpty(aCase.getDocuments())) {
            aCase.getDocuments().forEach(doc -> {
                doc.setAuditDetails(auditDetails);
                doc.setStatus(Status.ACTIVE);
            });
        }
    }
public PartyAdv createNewPetAdvocateId(RequestInfo requestInfo, String tenantId,Advocate advocate,String casId,String partyId, String partyType){
    List<String> petAdvocateId = getIdList(requestInfo, tenantId, legalConfiguration.getPetitionerAdvocateIdgenName(),
            legalConfiguration.getPetitionerAdvocateIdgenFormat(), 1);
    ListIterator<String> petAdvocateItr = petAdvocateId.listIterator();
                                        advocate.setId(petAdvocateItr.next());
    PartyAdv partyAdv1 = new PartyAdv();
    partyAdv1.setId(UUID.randomUUID().toString());
    partyAdv1.setCaseId(casId);
    partyAdv1.setAdvocateId(advocate.getId());
    partyAdv1.setPartyId(partyId);
    partyAdv1.setPartyType(partyType);
    partyAdv1.setAuditDetails(caseUtils.getAuditDetails(requestInfo.getUserInfo().getUuid(), true));
    return partyAdv1;
    }

    public PartyAdv createNewPartyAdvocateId(RequestInfo requestInfo, String tenantId,String advId,String casId,String partyId, String partyType){
//        List<String> petAdvocateId = getIdList(requestInfo, tenantId, ilmsConfiguration.getRespondentAdvocateIdgenName(),
//                ilmsConfiguration.getRespondentAdvocateIdgenFormat(), 1);
//        ListIterator<String> petAdvocateItr = petAdvocateId.listIterator();
//        advocate.setId(petAdvocateItr.next());
        PartyAdv partyAdv1 = new PartyAdv();
        partyAdv1.setId(UUID.randomUUID().toString());
        partyAdv1.setCaseId(casId);
        partyAdv1.setAdvocateId(advId);
        partyAdv1.setPartyId(partyId);
        partyAdv1.setPartyType(partyType);
        partyAdv1.setAuditDetails(caseUtils.getAuditDetails(requestInfo.getUserInfo().getUuid(), true));
        return partyAdv1;
    }

}
