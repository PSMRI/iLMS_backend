package org.legal.service;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.legal.configs.LEGALConfiguration;
import org.legal.repository.AdvocateRepository;
import org.legal.repository.CaseRepository;
import org.legal.repository.HearingRepository;
import org.legal.repository.IdGenRepository;
import org.legal.util.CaseUtils;
import org.legal.util.HearingUtils;
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
public class HearingEnrichmentService {
    @Autowired
    private LEGALConfiguration legalConfiguration;

    @Autowired
    private CaseUtils caseUtils;

    @Autowired
    private IdGenRepository idGenRepository;

    @Autowired
    private CaseRepository caseRepository;
    @Autowired
    private HearingRepository hearingDetailsRepository;

    @Autowired
    private AdvocateService advocateService;

    @Autowired
    private AdvocateRepository advocateRepository;

    @Autowired
    private HearingUtils hearingUtils;

    public void enrichHearingCreateRequest(HearingRequest hearingRequest) {

        Hearing hearing = hearingRequest.getHearing();
        setIdgenIds(hearingRequest);
        AuditDetails auditDetails = caseUtils.getAuditDetails(hearingRequest.getRequestInfo().getUserInfo().getUuid(), true);
        hearingRequest.getHearing().setAuditDetails(auditDetails);
        hearing.setAuditDetails(auditDetails);
        for (Party party : hearingRequest.getHearing().getParties()) {
            party.setAuditDetails(auditDetails);
        }
        if (Objects.nonNull(hearingRequest.getHearing().getPayment())) {
            hearingRequest.getHearing().getPayment().setAuditDetails(auditDetails);
            hearing.getPayment().setAuditDetails(auditDetails);
        }
    }

    private void setIdgenIds(HearingRequest request) {
        RequestInfo requestInfo = request.getRequestInfo();
        CaseSearchCriteria criteria = CaseSearchCriteria.builder().id(Collections.singletonList(request.getHearing().getCaseId())).build();
        CaseResponse caseResponse = caseRepository.getLegalCaseData(criteria);
        String tenantId = caseResponse.getCaseList().get(0).getTenantId();
        Hearing hearing = request.getHearing();
        List<String> applicationNumbers = getIdList(requestInfo, tenantId, legalConfiguration.getHearingIdgenName(),
                legalConfiguration.getHearingIdgenFormat(), 1);
        ListIterator<String> itr = applicationNumbers.listIterator();
        List<String> paymentIds = getIdList(requestInfo, tenantId, legalConfiguration.getPaymentIdgenName(), legalConfiguration.getPaymentIdgenFormat(),
                1);
        ListIterator<String> paymentItr = paymentIds.listIterator();
        Map<String, String> errorMap = new HashMap<>();

        if (!errorMap.isEmpty()) {
            throw new CustomException(errorMap);
        }

       // List<Party> partyList = hearingDetailsRepository.getPartyFromPartyQuery(request.getHearing().getCaseId());
        String petId=null;
        String resId=null;
        for (Party oldparty:caseResponse.getCaseList().get(0).getParties()){
            if (oldparty.getPartyType().equals(PartyType.PETITIONER.toString())){
                 petId=oldparty.getId();
            }else {
                resId=oldparty.getId();
            }
        }
        List<PartyAdv> partyAdvList = hearing.getPartyAdv();
        if (partyAdvList == null) {
            partyAdvList = new ArrayList<>();
        }
        hearing.setId(itr.next());
            for (Party party : hearing.getParties()) {
                if (party.getPartyType().equals(PartyType.PETITIONER.toString())) {
                    if (Objects.nonNull(party.getAdvocate())) {
                     //   hearingUtils.setAdvocatesForHearing(hearing,party,,hearing.getCaseId(),tenantId,requestInfo);
                        for (Advocate advocate : party.getAdvocate()) {
                            AdvocateSearchCriteria advCriteria = new AdvocateSearchCriteria();
                            advCriteria.setContactNumber(advocate.getContactNumber());
                            AdvocateResponse petadvocate = advocateService.advocateSearch(advCriteria);
                            // List<Advocate> advocates = new ArrayList<>();
                            if (!petadvocate.getAdvocate().isEmpty()) {
                                String  advocateId=petadvocate.getAdvocate().get(0).getId();
                                List<PartyAdv> partyAdvList1=advocateRepository.getPartyAdv(advocateId,hearing.getCaseId());
                                if (partyAdvList1.isEmpty()) {
                                    Advocate advocate1 = petadvocate.getAdvocate().get(0);
                                    PartyAdv partyAdv1 = new PartyAdv();
                                    partyAdv1.setId(UUID.randomUUID().toString());
                                    partyAdv1.setCaseId(party.getCaseId());
                                    partyAdv1.setAdvocateId(advocate1.getId());
                                    partyAdv1.setPartyId(petId);
                                    partyAdv1.setPartyType(party.getPartyType());
                                    partyAdv1.setAuditDetails(caseUtils.getAuditDetails(requestInfo.getUserInfo().getUuid(), true));
                                    partyAdvList.add(partyAdv1);
                                    party.setAdvocate(null);
                                }
                            } else {
                                List<String> padvocateId = getIdList(requestInfo, tenantId, legalConfiguration.getPetitionerAdvocateIdgenName(),
                                        legalConfiguration.getPetitionerAdvocateIdgenFormat(), 1);
                                ListIterator<String> padvocateItr = padvocateId.listIterator();
                                advocate.setId(padvocateItr.next());
                                PartyAdv partyAdv1 = new PartyAdv();
                                partyAdv1.setId(UUID.randomUUID().toString());
                                partyAdv1.setCaseId(hearing.getCaseId());
                                partyAdv1.setAdvocateId(advocate.getId());
                                partyAdv1.setPartyId(petId);
                                partyAdv1.setPartyType(party.getPartyType());
                                partyAdv1.setAuditDetails(caseUtils.getAuditDetails(requestInfo.getUserInfo().getUuid(), true));
                                partyAdvList.add(partyAdv1);
                            }

                            hearing.setPartyAdv(partyAdvList);
                        }
                    }
                } else {
                    if (Objects.nonNull(party.getAdvocate())) {
                        for (Advocate advocate : party.getAdvocate()) {
                            AdvocateSearchCriteria advCriteria = new AdvocateSearchCriteria();
                            advCriteria.setContactNumber(advocate.getContactNumber());
                            AdvocateResponse resadvocate = advocateService.advocateSearch(advCriteria);
                            //                        List<Advocate> advocates = new ArrayList<>();
                            if (!resadvocate.getAdvocate().isEmpty()) {
                                String advocateId = resadvocate.getAdvocate().get(0).getId();
                                List<PartyAdv> partyAdvList1 = advocateRepository.getPartyAdv(advocateId, hearing.getCaseId());
                                if (partyAdvList1.isEmpty()) {
                                    Advocate advocate1 = resadvocate.getAdvocate().get(0);
                                    PartyAdv partyAdv1 = new PartyAdv();
                                    partyAdv1.setId(UUID.randomUUID().toString());
                                    partyAdv1.setCaseId(party.getCaseId());
                                    partyAdv1.setAdvocateId(advocate1.getId());
                                    partyAdv1.setPartyId(resId);
                                    partyAdv1.setPartyType(party.getPartyType());
                                    partyAdvList.add(partyAdv1);
                                    partyAdv1.setAuditDetails(caseUtils.getAuditDetails(requestInfo.getUserInfo().getUuid(), true));
                                    party.setAdvocate(null);
                                } else
                                {
                                    List<String> radvocateId = getIdList(requestInfo, tenantId, legalConfiguration.getRespondentAdvocateIdgenName(),
                                            legalConfiguration.getRespondentAdvocateIdgenFormat(), 1);
                                    ListIterator<String> radvocateItr = radvocateId.listIterator();
                                    advocate.setId(radvocateItr.next());
                                    PartyAdv partyAdv1 = new PartyAdv();
                                    partyAdv1.setId(UUID.randomUUID().toString());
                                    partyAdv1.setCaseId(hearing.getCaseId());
                                    partyAdv1.setAdvocateId(advocate.getId());
                                    partyAdv1.setPartyId(resId);
                                    partyAdv1.setPartyType(party.getPartyType());
                                    partyAdv1.setAuditDetails(caseUtils.getAuditDetails(requestInfo.getUserInfo().getUuid(), true));
                                    partyAdvList.add(partyAdv1);
                                }
                                hearing.setPartyAdv(partyAdvList);
                            }
                        }
                    }
                }
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
            throw new CustomException(LegalErrorConstants.IDGEN_ERROR, "No ids returned from idgen Service");
        }

        return idResponses.stream().map(IdResponse::getId).collect(Collectors.toList());
    }
}



