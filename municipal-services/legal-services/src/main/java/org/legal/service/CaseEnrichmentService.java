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
            aCase.getCourt().setCaseId(caseRequest.getCaseObj().getId());
            caseRequest.getCaseObj().getCourt().setStatus(Status.ACTIVE);
        }
        for (Party party : aCase.getParties()) {
            party.setAuditDetails(auditDetails);
            party.setCaseId(caseRequest.getCaseObj().getId());
            party.setStatus(Status.ACTIVE);
            if (Objects.nonNull(party.getAdvocate()))
                for (Advocate advocate : party.getAdvocate()) {
                    advocate.setAuditDetails(auditDetails);
                    advocate.setStatus(Status.ACTIVE);

                }
        }
        if (!CollectionUtils.isEmpty(aCase.getDocuments())) {
            aCase.getDocuments().forEach(doc -> {
                doc.setCaseId(aCase.getId());
                doc.setAuditDetails(auditDetails);
                doc.setStatus(Status.ACTIVE);
            });
        }

        if (Objects.nonNull(caseRequest.getCaseObj().getAct())) {
            for (Act act : aCase.getAct()) {
                act.setAuditDetails(auditDetails);
                act.setCaseId(caseRequest.getCaseObj().getId());
                act.setStatus(Status.ACTIVE);
            }
        }
    }

    public void enrichmentForHearingUpdateRequest(HearingRequest request) {
        RequestInfo requestInfo = request.getRequestInfo();
        Hearing hearing = request.getHearing();
        AuditDetails auditDetails = caseUtils.getAuditDetails(requestInfo.getUserInfo().getUuid(), false);
        request.getHearing().setAuditDetails(auditDetails);
        hearing.setAuditDetails(auditDetails);
        if (request.getHearing().getPayment() != null) {
            request.getHearing().getPayment().setAuditDetails(auditDetails);
            hearing.getPayment().setAuditDetails(auditDetails);
        }
        if (!CollectionUtils.isEmpty(hearing.getDocuments())) {
            hearing.getDocuments().forEach(doc -> {
                doc.setAuditDetails(auditDetails);
                doc.setStatus(Status.ACTIVE);
            });
        }
    }

    private void setIdgenIds(CaseRequest request) {


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

            } else {
                List<String> respondentId = getIdList(requestInfo, tenantId, legalConfiguration.getRespondentIdgenName(),
                        legalConfiguration.getRespondentIdgenFormat(), 1);
                ListIterator<String> respondentItr = respondentId.listIterator();
                party.setId(respondentItr.next());

            }
        }


        List<PartyAdv> partyAdvList = caseUtils.updatePartyAdvocates(request);
        caseObj.setPartyAdv(partyAdvList);
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

    public PartyAdv createNewPartyAdvocateId(RequestInfo requestInfo, String advId, String casId, String partyId, String partyType) {
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
