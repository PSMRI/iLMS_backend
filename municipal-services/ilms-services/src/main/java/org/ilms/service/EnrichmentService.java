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
import org.ilms.web.model.Hearing;
import org.ilms.web.model.HearingRequest;
import org.ilms.web.model.ILMSCase;
import org.ilms.web.model.ILMSCaseRequest;
import org.ilms.web.model.enums.Status;
import org.ilms.web.model.idGen.IdResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EnrichmentService {
    @Autowired
    private ILMSConfiguration config;

    @Autowired
    private CaseUtils caseUtils;

    @Autowired
    private IdGenRepository idGenRepository;

    public void enrichCaseCreateRequest(ILMSCaseRequest ilmsCaseRequest) {
        RequestInfo requestInfo = ilmsCaseRequest.getRequestInfo();
        ILMSCase ilmsCase = ilmsCaseRequest.getIlmsCase();
        setIdgenIds(ilmsCaseRequest);
        AuditDetails auditDetails = caseUtils.getAuditDetails(requestInfo.getUserInfo().getUserName(), true);
        ilmsCaseRequest.getIlmsCase().setAuditDetails(auditDetails);
        ilmsCase.setAuditDetails(auditDetails);
        if (ilmsCaseRequest.getIlmsCase().getRespondent() != null) {
            ilmsCaseRequest.getIlmsCase().getRespondent().setAuditDetails(auditDetails);
            ilmsCase.getRespondent().setAuditDetails(auditDetails);
        }
        if (ilmsCaseRequest.getIlmsCase().getRespondent().getAdvocate() != null) {
            ilmsCaseRequest.getIlmsCase().getRespondent().getAdvocate().setAuditDetails(auditDetails);
            ilmsCase.getRespondent().getAdvocate().setAuditDetails(auditDetails);
        }
        if (ilmsCaseRequest.getIlmsCase().getPetitioner() != null) {
            ilmsCaseRequest.getIlmsCase().getPetitioner().setAuditDetails(auditDetails);
            ilmsCase.getPetitioner().setAuditDetails(auditDetails);
        }
        if (ilmsCaseRequest.getIlmsCase().getPetitioner().getAdvocate() != null) {
            ilmsCaseRequest.getIlmsCase().getPetitioner().getAdvocate().setAuditDetails(auditDetails);
            ilmsCase.getPetitioner().getAdvocate().setAuditDetails(auditDetails);
        }
        if (!CollectionUtils.isEmpty(ilmsCase.getDocuments())) {
            ilmsCase.getDocuments().forEach(doc -> {
                doc.setAuditDetails(auditDetails);
                doc.setStatus(Status.ACTIVE);
            });
        }
        if (ilmsCaseRequest.getIlmsCase().getAct() != null) {
            ilmsCaseRequest.getIlmsCase().getAct().setAuditDetails(auditDetails);
            ilmsCase.getAct().setAuditDetails(auditDetails);
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

    private void setIdgenIds(ILMSCaseRequest request) {
        RequestInfo requestInfo = request.getRequestInfo();
        String tenantId = request.getIlmsCase().getTenantId();
        ILMSCase ilmsCase = request.getIlmsCase();
        List<String> caseId = getIdList(requestInfo, tenantId, config.getCaseIdgenName(), config.getCaseIdgenFormat(), 1);
        ListIterator<String> caseItr = caseId.listIterator();
        List<String> actId = getIdList(requestInfo, tenantId, config.getActIdgenName(), config.getActIdgenFormat(), 1);
        ListIterator<String> actItr = actId.listIterator();
        List<String> padvocateId = getIdList(requestInfo, tenantId, config.getPetitionerAdvocateIdgenName(),
                config.getPetitionerAdvocateIdgenFormat(), 1);
        ListIterator<String> padvocateItr = padvocateId.listIterator();
        List<String> radvocateId = getIdList(requestInfo, tenantId, config.getRespondentAdvocateIdgenName(),
                config.getRespondentAdvocateIdgenFormat(), 1);
        ListIterator<String> radvocateItr = radvocateId.listIterator();
        List<String> petitionerId = getIdList(requestInfo, tenantId, config.getPetitionerIdgenName(), config.getPetitionerIdgenFormat(), 1);
        ListIterator<String> petitionerItr = petitionerId.listIterator();
        List<String> respondentId = getIdList(requestInfo, tenantId, config.getRespondentIdgenName(), config.getRespondentIdgenFormat(), 1);
        ListIterator<String> respondentItr = respondentId.listIterator();
        Map<String, String> errorMap = new HashMap<>();
        if (!errorMap.isEmpty()) {
            throw new CustomException(errorMap);
        }
        ilmsCase.setId(caseItr.next());
        ilmsCase.getAct().setId(actItr.next());
        ilmsCase.getRespondent().getAdvocate().setId(radvocateItr.next());
        ilmsCase.getPetitioner().getAdvocate().setId(padvocateItr.next());
        ilmsCase.getPetitioner().setId(petitionerItr.next());
        ilmsCase.getRespondent().setId(respondentItr.next());
        if (Objects.nonNull(ilmsCase.getDocuments())) {
            ilmsCase.getDocuments().forEach((doc -> {
                List<String> docId = getIdList(requestInfo, tenantId, config.getDocumentIdGenName(), config.getDocumentIdGenFormat(), 1);
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

    public void enrichCaseUpdateRequest(ILMSCaseRequest ilmsCaseRequest) {
        RequestInfo requestInfo = ilmsCaseRequest.getRequestInfo();
        ILMSCase ilmsCase = ilmsCaseRequest.getIlmsCase();
        AuditDetails auditDetails = caseUtils.getAuditDetails(ilmsCaseRequest.getIlmsCase().getId(), true);
        ilmsCaseRequest.getIlmsCase().setAuditDetails(auditDetails);
        ilmsCase.setAuditDetails(auditDetails);
        if (ilmsCaseRequest.getIlmsCase().getRespondent() != null) {
            ilmsCaseRequest.getIlmsCase().getRespondent().setAuditDetails(auditDetails);
            ilmsCase.getRespondent().setAuditDetails(auditDetails);
        }
        if (ilmsCaseRequest.getIlmsCase().getRespondent().getAdvocate() != null) {
            ilmsCaseRequest.getIlmsCase().getRespondent().getAdvocate().setAuditDetails(auditDetails);
            ilmsCase.getRespondent().getAdvocate().setAuditDetails(auditDetails);
        }

        if (ilmsCaseRequest.getIlmsCase().getPetitioner() != null) {
            ilmsCaseRequest.getIlmsCase().getPetitioner().setAuditDetails(auditDetails);
            ilmsCase.getPetitioner().setAuditDetails(auditDetails);
        }
        if (ilmsCaseRequest.getIlmsCase().getPetitioner().getAdvocate() != null) {
            ilmsCaseRequest.getIlmsCase().getPetitioner().getAdvocate().setAuditDetails(auditDetails);
            ilmsCase.getPetitioner().getAdvocate().setAuditDetails(auditDetails);
        }
        if (ilmsCaseRequest.getIlmsCase().getAct() != null) {
            ilmsCaseRequest.getIlmsCase().getAct().setAuditDetails(auditDetails);
            ilmsCase.getAct().setAuditDetails(auditDetails);
        }
        if (!CollectionUtils.isEmpty(ilmsCase.getDocuments())) {
            ilmsCase.getDocuments().forEach(doc -> {
                doc.setAuditDetails(auditDetails);
                doc.setStatus(Status.ACTIVE);
            });
        }
    }
}
