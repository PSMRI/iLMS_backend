package org.ilms.service;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.ilms.configs.ILMSConfiguration;
import org.ilms.repository.IdGenRepository;
import org.ilms.util.CaseUtils;
import org.ilms.util.ILMSErrorConstants;
import org.ilms.web.model.*;
import org.ilms.web.model.enums.PartyType;
import org.ilms.web.model.enums.Status;
import org.ilms.web.model.idGen.IdResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CaseEnrichmentService {
    @Autowired
    private ILMSConfiguration ilmsConfiguration;

    @Autowired
    private CaseUtils caseUtils;

    @Autowired
    private IdGenRepository idGenRepository;

    public void enrichCaseCreateRequest(CaseRequest caseRequest) {
        RequestInfo requestInfo = caseRequest.getRequestInfo();
        Case aCase = caseRequest.getCaseObj();
        setIdgenIds(caseRequest);
        AuditDetails auditDetails = caseUtils.getAuditDetails(requestInfo.getUserInfo().getUserName(), true);
        caseRequest.getCaseObj().setAuditDetails(auditDetails);
        aCase.setAuditDetails(auditDetails);
        if (Objects.nonNull(caseRequest.getCaseObj().getCourt())) {
            caseRequest.getCaseObj().getCourt().setAuditDetails(auditDetails);
            aCase.getCourt().setAuditDetails(auditDetails);
        }
        if (caseRequest.getCaseObj().getRespondent() != null) {
            caseRequest.getCaseObj().getRespondent().setAuditDetails(auditDetails);
            aCase.getRespondent().setAuditDetails(auditDetails);
        }
        if (caseRequest.getCaseObj().getRespondent().getAdvocate() != null) {
            caseRequest.getCaseObj().getRespondent().getAdvocate().setAuditDetails(auditDetails);
            aCase.getRespondent().getAdvocate().setAuditDetails(auditDetails);
        }
        if (caseRequest.getCaseObj().getPetitioner() != null) {
            caseRequest.getCaseObj().getPetitioner().setAuditDetails(auditDetails);
            aCase.getPetitioner().setAuditDetails(auditDetails);
        }
        if (caseRequest.getCaseObj().getPetitioner().getAdvocate() != null) {
            caseRequest.getCaseObj().getPetitioner().getAdvocate().setAuditDetails(auditDetails);
            aCase.getPetitioner().getAdvocate().setAuditDetails(auditDetails);
        }
        if (!CollectionUtils.isEmpty(aCase.getDocuments())) {
            aCase.getDocuments().forEach(doc -> {
                doc.setAuditDetails(auditDetails);
                doc.setStatus(Status.ACTIVE);
            });
        }
        if (caseRequest.getCaseObj().getAct() != null) {
            caseRequest.getCaseObj().getAct().setAuditDetails(auditDetails);
            aCase.getAct().setAuditDetails(auditDetails);
        }
    }

    public void enrichmentForHearingUpdateRequest(HearingRequest request) {
        RequestInfo requestInfo = request.getRequestInfo();
        Hearing ilmsCase = request.getHearing();
        AuditDetails auditDetails = caseUtils.getAuditDetails(request.getHearing().getId(), false);
        request.getHearing().setAuditDetails(auditDetails);
        ilmsCase.setAuditDetails(auditDetails);
        if (request.getHearing().getRespondent() != null) {
            request.getHearing().getRespondent().setAuditDetails(auditDetails);
            ilmsCase.getRespondent().setAuditDetails(auditDetails);
            if (request.getHearing().getRespondent().getAdvocate() != null) {
                request.getHearing().getRespondent().getAdvocate().setAuditDetails(auditDetails);
                ilmsCase.getRespondent().getAdvocate().setAuditDetails(auditDetails);
            }
        }

        if (request.getHearing().getPetitioner() != null) {
            request.getHearing().getPetitioner().setAuditDetails(auditDetails);
            ilmsCase.getPetitioner().setAuditDetails(auditDetails);
            if (request.getHearing().getPetitioner().getAdvocate() != null) {
                request.getHearing().getPetitioner().getAdvocate().setAuditDetails(auditDetails);
                ilmsCase.getPetitioner().getAdvocate().setAuditDetails(auditDetails);
            }
        }

        if (request.getHearing().getPayment() != null) {
            request.getHearing().getPayment().setAuditDetails(auditDetails);
            ilmsCase.getPayment().setAuditDetails(auditDetails);
        }
    }

    private void setIdgenIds(CaseRequest request) {
        RequestInfo requestInfo = request.getRequestInfo();
        String tenantId = request.getCaseObj().getTenantId();
        Case caseObj = request.getCaseObj();
        List<String> caseId = getIdList(requestInfo, tenantId, ilmsConfiguration.getCaseIdgenName(), ilmsConfiguration.getCaseIdgenFormat(), 1);
        ListIterator<String> caseItr = caseId.listIterator();
        List<String> actId = getIdList(requestInfo, tenantId, ilmsConfiguration.getActIdgenName(), ilmsConfiguration.getActIdgenFormat(), 1);
        ListIterator<String> actItr = actId.listIterator();
        List<String> padvocateId = getIdList(requestInfo, tenantId, ilmsConfiguration.getPetitionerAdvocateIdgenName(),
                ilmsConfiguration.getPetitionerAdvocateIdgenFormat(), 1);
        ListIterator<String> padvocateItr = padvocateId.listIterator();
        List<String> radvocateId = getIdList(requestInfo, tenantId, ilmsConfiguration.getRespondentAdvocateIdgenName(),
                ilmsConfiguration.getRespondentAdvocateIdgenFormat(), 1);
        ListIterator<String> radvocateItr = radvocateId.listIterator();
        List<String> petitionerId = getIdList(requestInfo, tenantId, ilmsConfiguration.getPetitionerIdgenName(),
                ilmsConfiguration.getPetitionerIdgenFormat(), 1);
        ListIterator<String> petitionerItr = petitionerId.listIterator();
        List<String> respondentId = getIdList(requestInfo, tenantId, ilmsConfiguration.getRespondentIdgenName(),
                ilmsConfiguration.getRespondentIdgenFormat(), 1);
        ListIterator<String> respondentItr = respondentId.listIterator();
        List<String> courtId = getIdList(requestInfo, tenantId, ilmsConfiguration.getCourtIdgenName(),
                ilmsConfiguration.getCourtIdgenFormat(), 1);
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
            caseObj.getAct().setId(actItr.next());
        } else {
            Act act = new Act();
            act.setId(actItr.next());
            act.setStatus(Status.DRAFTED);
            caseObj.setAct(act);
        }
        if (Objects.nonNull(caseObj.getPetitioner())) {
            caseObj.getPetitioner().setId(petitionerItr.next());
        } else {
            Party party = new Party();
            party.setId(petitionerItr.next());
            party.setStatus(Status.DRAFTED);
            party.setPartyType(PartyType.PETITIONER.toString());
            caseObj.setPetitioner(party);
        }
        if (Objects.nonNull(caseObj.getRespondent())) {
            caseObj.getRespondent().setId(respondentItr.next());
        } else {
            Party party = new Party();
            party.setId(respondentItr.next());
            party.setStatus(Status.DRAFTED);
            party.setPartyType(PartyType.RESPONDENT.toString());
            caseObj.setRespondent(party);
        }
        if (Objects.nonNull(caseObj.getRespondent().getAdvocate())) {
            caseObj.getRespondent().getAdvocate().setId(radvocateItr.next());
        } else {
            Advocate advocate = new Advocate();
            advocate.setId(radvocateItr.next());
            advocate.setStatus(Status.DRAFTED);
            advocate.setPartyType(PartyType.RESPONDENT);
            caseObj.getRespondent().setAdvocate(advocate);
        }
        if (Objects.nonNull(caseObj.getPetitioner().getAdvocate())) {
            caseObj.getPetitioner().getAdvocate().setId(padvocateItr.next());
        } else {
            Advocate advocate = new Advocate();
            advocate.setId(padvocateItr.next());
            advocate.setStatus(Status.DRAFTED);
            advocate.setPartyType(PartyType.PETITIONER);
            caseObj.getPetitioner().setAdvocate(advocate);
        }
        if (Objects.nonNull(caseObj.getDocuments())) {
            caseObj.getDocuments().forEach((doc -> {
                List<String> docId = getIdList(requestInfo, tenantId, ilmsConfiguration.getDocumentIdgenName(),
                        ilmsConfiguration.getDocumentIdgenFormat(), 1);
                doc.setId(docId.get(0));
            }));
        }
        //        else {
        //            Document document = new Document();
        //            document.setStatus(Status.DRAFTED);
        //        }
    }

    private List<String> getIdList(RequestInfo requestInfo, String tenantId, String idName, String idformat, int count) {
        List<IdResponse> idResponses = idGenRepository.getId(requestInfo, tenantId, idName, idformat, count).getIdResponses();
        if (CollectionUtils.isEmpty(idResponses)) {
            throw new CustomException(ILMSErrorConstants.IDGEN_ERROR, "No ids returned from idgen Service");
        }
        return idResponses.stream().map(IdResponse::getId).collect(Collectors.toList());
    }

    public void enrichCaseUpdateRequest(CaseRequest caseRequest) {
        RequestInfo requestInfo = caseRequest.getRequestInfo();
        Case aCase = caseRequest.getCaseObj();
        AuditDetails auditDetails = caseUtils.getAuditDetails(requestInfo.getUserInfo().getUserName(), false);
        caseRequest.getCaseObj().setAuditDetails(auditDetails);
        aCase.setAuditDetails(auditDetails);
        if (caseRequest.getCaseObj().getCourt() != null) {
            caseRequest.getCaseObj().getCourt().setAuditDetails(auditDetails);
            aCase.getCourt().setAuditDetails(auditDetails);
        }
        if (caseRequest.getCaseObj().getRespondent() != null) {
            caseRequest.getCaseObj().getRespondent().setAuditDetails(auditDetails);
            aCase.getRespondent().setAuditDetails(auditDetails);
        }
        if (caseRequest.getCaseObj().getRespondent().getAdvocate() != null) {
            caseRequest.getCaseObj().getRespondent().getAdvocate().setAuditDetails(auditDetails);
            aCase.getRespondent().getAdvocate().setAuditDetails(auditDetails);
        }

        if (caseRequest.getCaseObj().getPetitioner() != null) {
            caseRequest.getCaseObj().getPetitioner().setAuditDetails(auditDetails);
            aCase.getPetitioner().setAuditDetails(auditDetails);
        }
        if (caseRequest.getCaseObj().getPetitioner().getAdvocate() != null) {
            caseRequest.getCaseObj().getPetitioner().getAdvocate().setAuditDetails(auditDetails);
            aCase.getPetitioner().getAdvocate().setAuditDetails(auditDetails);
        }
        if (caseRequest.getCaseObj().getAct() != null) {
            caseRequest.getCaseObj().getAct().setAuditDetails(auditDetails);
            aCase.getAct().setAuditDetails(auditDetails);
        }
        if (!CollectionUtils.isEmpty(aCase.getDocuments())) {
            aCase.getDocuments().forEach(doc -> {
                doc.setAuditDetails(auditDetails);
                doc.setStatus(Status.ACTIVE);
            });
        }
    }

    public void enrichDocumentUpdateRequest(DocumentRequest request) {
        RequestInfo requestInfo = request.getRequestInfo();
        List<Document> document = request.getDocument();
        for (Document documents : document) {
            AuditDetails auditDetails = caseUtils.getAuditDetails(requestInfo.getUserInfo().getUserName(), false);
            documents.setAuditDetails(auditDetails);
            documents.setAuditDetails(auditDetails);

        }
    }
}
