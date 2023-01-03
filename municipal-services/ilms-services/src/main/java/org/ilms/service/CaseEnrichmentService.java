package org.ilms.service;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.ilms.configs.ILMSConfiguration;
import org.ilms.repository.IdGenRepository;
import org.ilms.util.CaseUtils;
import org.ilms.util.ILMSErrorConstants;
import org.ilms.web.model.AuditDetails;
import org.ilms.web.model.Case;
import org.ilms.web.model.CaseRequest;
import org.ilms.web.model.Hearing;
import org.ilms.web.model.HearingRequest;
import org.ilms.web.model.enums.Status;
import org.ilms.web.model.idGen.IdResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import lombok.extern.slf4j.Slf4j;

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
        if (request.getHearing().getCourt() != null) {
            request.getHearing().getCourt().setAuditDetails(auditDetails);
            ilmsCase.getCourt().setAuditDetails(auditDetails);
        }
        if (request.getHearing().getRespondent() != null) {
            request.getHearing().getRespondent().setAuditDetails(auditDetails);
            ilmsCase.getRespondent().setAuditDetails(auditDetails);
        }
        if (request.getHearing().getRespondent().getAdvocate() != null) {
            request.getHearing().getRespondent().getAdvocate().setAuditDetails(auditDetails);
            ilmsCase.getRespondent().getAdvocate().setAuditDetails(auditDetails);
        }
        if (request.getHearing().getPetitioner() != null) {
            request.getHearing().getPetitioner().setAuditDetails(auditDetails);
            ilmsCase.getPetitioner().setAuditDetails(auditDetails);
        }
        if (request.getHearing().getPetitioner().getAdvocate() != null) {
            request.getHearing().getPetitioner().getAdvocate().setAuditDetails(auditDetails);
            ilmsCase.getPetitioner().getAdvocate().setAuditDetails(auditDetails);
        }
        if (request.getHearing().getPayment() != null) {
            request.getHearing().getPayment().setAuditDetails(auditDetails);
            ilmsCase.getPayment().setAuditDetails(auditDetails);
        }
    }

    private void setIdgenIds(CaseRequest request) {
        RequestInfo requestInfo = request.getRequestInfo();
        String tenantId = request.getCaseObj().getTenantId();
        Case aCase = request.getCaseObj();
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
        Map<String, String> errorMap = new HashMap<>();
        if (!errorMap.isEmpty()) {
            throw new CustomException(errorMap);
        }
        aCase.setId(caseItr.next());
        aCase.getAct().setId(actItr.next());
        aCase.getRespondent().getAdvocate().setId(radvocateItr.next());
        aCase.getPetitioner().getAdvocate().setId(padvocateItr.next());
        aCase.getPetitioner().setId(petitionerItr.next());
        aCase.getRespondent().setId(respondentItr.next());
        if (Objects.nonNull(aCase.getDocuments())) {
            aCase.getDocuments().forEach((doc -> {
                List<String> docId = getIdList(requestInfo, tenantId, ilmsConfiguration.getDocumentIdgenName(),
                        ilmsConfiguration.getDocumentIdgenFormat(), 1);
                doc.setId(docId.get(0));
            }));
        }
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
        AuditDetails auditDetails = caseUtils.getAuditDetails(caseRequest.getCaseObj().getId(), true);
        caseRequest.getCaseObj().setAuditDetails(auditDetails);
        aCase.setAuditDetails(auditDetails);
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
}
