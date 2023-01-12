package org.ilms.service;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.ilms.configs.ILMSConfiguration;
import org.ilms.repository.CaseRepository;
import org.ilms.repository.HearingRepository;
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
public class HearingEnrichmentService {
    @Autowired
    private ILMSConfiguration ilmsConfiguration;

    @Autowired
    private CaseUtils caseUtils;

    @Autowired
    private IdGenRepository idGenRepository;

    @Autowired
    private CaseRepository caseRepository;
    @Autowired
    private HearingRepository hearingDetailsRepository;

    public void enrichHearingCreateRequest(HearingRequest hearingRequest) {

        RequestInfo requestInfo = hearingRequest.getRequestInfo();
        Hearing hearing = hearingRequest.getHearing();
        setIdgenIds(hearingRequest);
        AuditDetails auditDetails = caseUtils.getAuditDetails(hearingRequest.getRequestInfo().getUserInfo().getUserName(), true);
        hearingRequest.getHearing().setAuditDetails(auditDetails);
        hearing.setAuditDetails(auditDetails);
        if (Objects.nonNull(hearingRequest.getHearing().getCourt())) {
            hearingRequest.getHearing().getCourt().setAuditDetails(auditDetails);
            hearing.getCourt().setAuditDetails(auditDetails);
        }
        if (Objects.nonNull(hearingRequest.getHearing().getRespondent())) {
            hearingRequest.getHearing().getRespondent().setAuditDetails(auditDetails);
            hearing.getRespondent().setAuditDetails(auditDetails);
        }
        if (Objects.nonNull(hearingRequest.getHearing().getRespondent())) {
            if (Objects.nonNull(hearingRequest.getHearing().getRespondent().getAdvocate())) {
                hearingRequest.getHearing().getRespondent().getAdvocate().setAuditDetails(auditDetails);
                hearing.getRespondent().getAdvocate().setAuditDetails(auditDetails);
            }
        }
        if (Objects.nonNull(hearingRequest.getHearing().getPetitioner())) {
            hearingRequest.getHearing().getPetitioner().setAuditDetails(auditDetails);
            hearing.getPetitioner().setAuditDetails(auditDetails);
        }
        if (Objects.nonNull(hearingRequest.getHearing().getPetitioner())) {
            if (Objects.nonNull(hearingRequest.getHearing().getPetitioner().getAdvocate())) {
                hearingRequest.getHearing().getPetitioner().getAdvocate().setAuditDetails(auditDetails);
                hearing.getPetitioner().getAdvocate().setAuditDetails(auditDetails);
            }
        }
        if (Objects.nonNull(hearingRequest.getHearing().getPayment())) {
            hearingRequest.getHearing().getPayment().setAuditDetails(auditDetails);
            hearing.getPayment().setAuditDetails(auditDetails);
        }
    }

    private void setIdgenIds(HearingRequest request) {
        String petitionerId = null;
        String respondentId = null;
        RequestInfo requestInfo = request.getRequestInfo();
        CaseSearchCriteria criteria = CaseSearchCriteria.builder().id(Collections.singletonList(request.getHearing().getCaseId())).build();
        CaseResponse caseResponse = caseRepository.getILMSCaseData(criteria);
        String tenantId = caseResponse.getCaseList().get(0).getTenantId();
        Hearing hearing = request.getHearing();
        List<String> applicationNumbers = getIdList(requestInfo, tenantId, ilmsConfiguration.getHearingIdgenName(),
                ilmsConfiguration.getHearingIdgenFormat(), 1);
        ListIterator<String> itr = applicationNumbers.listIterator();
        List<String> courtId = getIdList(requestInfo, tenantId, ilmsConfiguration.getCourtIdgenName(), ilmsConfiguration.getCourtIdgenFormat(), 1);
        ListIterator<String> courtItr = courtId.listIterator();
        List<String> padvocateId = getIdList(requestInfo, tenantId, ilmsConfiguration.getPetitionerAdvocateIdgenName(),
                ilmsConfiguration.getPetitionerAdvocateIdgenFormat(), 1);
        ListIterator<String> padvocateItr = padvocateId.listIterator();

        List<String> radvocateId = getIdList(requestInfo, tenantId, ilmsConfiguration.getRespondentAdvocateIdgenName(),
                ilmsConfiguration.getRespondentAdvocateIdgenFormat(), 1);
        ListIterator<String> radvocateItr = radvocateId.listIterator();
        List<String> paymentIds = getIdList(requestInfo, tenantId, ilmsConfiguration.getPaymentIdgenName(), ilmsConfiguration.getPaymentIdgenFormat(),
                1);
        ListIterator<String> paymentItr = paymentIds.listIterator();

        Map<String, String> errorMap = new HashMap<>();

        if (!errorMap.isEmpty()) {
            throw new CustomException(errorMap);
        }
        List<Party> partyList = hearingDetailsRepository.getGetFromPartyQuery(request.getHearing().getCaseId());
        for (Party party : partyList) {
            if (party.getPartyType().equals(PartyType.RESPONDENT.toString())) {
                respondentId = party.getId();
            } else {
                petitionerId = party.getId();
            }
        }

        hearing.setId(itr.next());
        if (Objects.nonNull(hearing.getCourt())) {
            hearing.getCourt().setId(courtItr.next());
        } else {
            Court court = new Court();
            court.setStatus(Status.ACTIVE);
            court.setId(courtItr.next());
            hearing.setCourt(court);
        }
        if (Objects.nonNull(hearing.getRespondent())) {
            if (Objects.nonNull(hearing.getRespondent().getAdvocate())) {
                hearing.getRespondent().getAdvocate().setId(radvocateItr.next());
            }
        } else {
            Party party = new Party();
            Advocate advocate = new Advocate();
            advocate.setId(radvocateItr.next());
            advocate.setStatus(Status.ACTIVE);
            advocate.setPartyType(PartyType.RESPONDENT);
            advocate.setPartyId(respondentId);
            party.setAdvocate(advocate);
            hearing.setRespondent(party);
        }
        if (Objects.nonNull(hearing.getPetitioner())) {
            if (Objects.nonNull(hearing.getPetitioner().getAdvocate())) {
                hearing.getPetitioner().getAdvocate().setId(padvocateItr.next());
            }
        } else {
            Party party = new Party();
            Advocate advocate = new Advocate();
            advocate.setId(padvocateItr.next());
            advocate.setStatus(Status.ACTIVE);
            advocate.setPartyType(PartyType.PETITIONER);
            advocate.setPartyId(petitionerId);
            party.setAdvocate(advocate);
            hearing.setPetitioner(party);
        }
        if (Objects.nonNull(hearing.getPayment())) {
            hearing.getPayment().setId(paymentItr.next());
        } else {
            Payment payment = new Payment();
            payment.setId(paymentItr.next());
            payment.setStatus(Status.ACTIVE);
            hearing.setPayment(payment);
        }

    }

    private List<String> getIdList(RequestInfo requestInfo, String tenantId, String idName, String idformat, int count) {
        List<IdResponse> idResponses = idGenRepository.getId(requestInfo, tenantId, idName, idformat, count).getIdResponses();

        if (CollectionUtils.isEmpty(idResponses)) {
            throw new CustomException(ILMSErrorConstants.IDGEN_ERROR, "No ids returned from idgen Service");
        }

        return idResponses.stream().map(IdResponse::getId).collect(Collectors.toList());
    }
}



