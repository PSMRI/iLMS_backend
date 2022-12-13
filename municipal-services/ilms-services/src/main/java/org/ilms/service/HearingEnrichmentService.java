package org.ilms.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.stream.Collectors;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.ilms.configs.ILMSConfiguration;
import org.ilms.repository.ILMSCaseRepository;
import org.ilms.repository.IdGenRepository;
import org.ilms.util.CaseUtils;
import org.ilms.util.ILMSErrorConstants;
import org.ilms.web.model.AuditDetails;
import org.ilms.web.model.Hearing;
import org.ilms.web.model.HearingRequest;
import org.ilms.web.model.ILMSCaseResponse;
import org.ilms.web.model.ILMSCaseSearchCriteria;
import org.ilms.web.model.idGen.IdResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class HearingEnrichmentService {
    @Autowired
    private ILMSConfiguration config;

    @Autowired
    private CaseUtils caseUtils;

    @Autowired
    private IdGenRepository idGenRepository;

    @Autowired
    private ILMSCaseRepository ilmsCaseRepository;

    public void enrichHearingCreateRequest(HearingRequest hearingRequest) {

        RequestInfo requestInfo = hearingRequest.getRequestInfo();
        Hearing hearing = hearingRequest.getHearing();
        setIdgenIds(hearingRequest);
        AuditDetails auditDetails = caseUtils.getAuditDetails(hearingRequest.getRequestInfo().getUserInfo().getUserName(), true);
        hearingRequest.getHearing().setAuditDetails(auditDetails);
        hearing.setAuditDetails(auditDetails);
        if (hearingRequest.getHearing().getCourt() != null) {
            hearingRequest.getHearing().getCourt().setAuditDetails(auditDetails);
            hearing.getCourt().setAuditDetails(auditDetails);
        }
        if (hearingRequest.getHearing().getRespondent() != null) {

            hearingRequest.getHearing().getRespondent().setAuditDetails(auditDetails);
            hearing.getRespondent().setAuditDetails(auditDetails);
        }
        if (hearingRequest.getHearing().getRespondent().getAdvocate() != null) {
            hearingRequest.getHearing().getRespondent().getAdvocate().setAuditDetails(auditDetails);
            hearing.getRespondent().getAdvocate().setAuditDetails(auditDetails);
        }
        if (hearingRequest.getHearing().getPetitioner() != null) {
            hearingRequest.getHearing().getPetitioner().setAuditDetails(auditDetails);
            hearing.getPetitioner().setAuditDetails(auditDetails);
        }
        if (hearingRequest.getHearing().getPetitioner().getAdvocate() != null) {
            hearingRequest.getHearing().getPetitioner().getAdvocate().setAuditDetails(auditDetails);
            hearing.getPetitioner().getAdvocate().setAuditDetails(auditDetails);
        }
        if (hearingRequest.getHearing().getPayment() != null) {
            hearingRequest.getHearing().getPayment().setAuditDetails(auditDetails);
            hearing.getPayment().setAuditDetails(auditDetails);
        }
    }

    private void setIdgenIds(HearingRequest request) {
        RequestInfo requestInfo = request.getRequestInfo();
        ILMSCaseSearchCriteria criteria = ILMSCaseSearchCriteria.builder().id(Collections.singletonList(request.getHearing().getCaseId())).build();
        ILMSCaseResponse ilmsCaseResponse = ilmsCaseRepository.getILMSCaseData(criteria);
        String tenantId = ilmsCaseResponse.getIlmsCases().get(0).getTenantId();
        Hearing hearing = request.getHearing();
        List<String> applicationNumbers = getIdList(requestInfo, tenantId, config.getHearingIdgenName(), config.getHearingIdgenFormat(), 1);
        ListIterator<String> itr = applicationNumbers.listIterator();
        List<String> courtId = getIdList(requestInfo, tenantId, config.getCourtIdGenName(), config.getCourtIdGenFormat(), 1);
        ListIterator<String> courtItr = courtId.listIterator();
        List<String> padvocateId = getIdList(requestInfo, tenantId, config.getPetitionerAdvocateIdgenName(),
                config.getPetitionerAdvocateIdgenFormat(), 1);
        ListIterator<String> padvocateItr = padvocateId.listIterator();

        List<String> radvocateId = getIdList(requestInfo, tenantId, config.getRespondentAdvocateIdgenName(),
                config.getRespondentAdvocateIdgenFormat(), 1);
        ListIterator<String> radvocateItr = radvocateId.listIterator();
        List<String> paymentIds = getIdList(requestInfo, tenantId, config.getPaymentIdgenName(), config.getPaymentIdgenFormat(), 1);
        ListIterator<String> paymentItr = paymentIds.listIterator();

        Map<String, String> errorMap = new HashMap<>();

        if (!errorMap.isEmpty()) {
            throw new CustomException(errorMap);
        }

        hearing.setId(itr.next());
        hearing.getCourt().setId(courtItr.next());
        hearing.getRespondent().getAdvocate().setId(radvocateItr.next());
        hearing.getPetitioner().getAdvocate().setId(padvocateItr.next());
        hearing.getPayment().setId(paymentItr.next());

    }

    private List<String> getIdList(RequestInfo requestInfo, String tenantId, String idName, String idformat, int count) {
        List<IdResponse> idResponses = idGenRepository.getId(requestInfo, tenantId, idName, idformat, count).getIdResponses();

        if (CollectionUtils.isEmpty(idResponses)) {
            throw new CustomException(ILMSErrorConstants.IDGEN_ERROR, "No ids returned from idgen Service");
        }

        return idResponses.stream().map(IdResponse::getId).collect(Collectors.toList());
    }
}



