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
        if (Objects.nonNull(hearingRequest.getHearing().getPayment())) {
            hearingRequest.getHearing().getPayment().setAuditDetails(auditDetails);
            hearingRequest.getHearing().getPayment().setStatus(Status.ACTIVE);
            hearingRequest.getHearing().getPayment().setCaseId(hearing.getCaseId());
            hearingRequest.getHearing().getPayment().setHearingId(hearing.getId());
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
        hearing.setId(itr.next());
        if (Objects.nonNull(hearing.getPayment())) {
            hearing.getPayment().setId(paymentItr.next());
            hearing.getPayment().setHearingId(hearing.getId());
        } else {
            Payment payment = new Payment();
            payment.setId(paymentItr.next());
            payment.setStatus(Status.ACTIVE);
            hearing.setPayment(payment);
        }
        if (Objects.nonNull(hearing.getDocuments())) {
            List<Document> documents = new ArrayList<>();
            for (Document document : hearing.getDocuments()) {
                hearing.getDocuments().forEach((doc -> {
                    List<String> docId = getIdList(requestInfo, tenantId, legalConfiguration.getDocumentIdgenName(),
                            legalConfiguration.getDocumentIdgenFormat(), 1);
                    doc.setId(docId.get(0));
                    doc.setHearingId(hearing.getId());
                    doc.setCaseId(hearing.getCaseId());
                    doc.setStatus(Status.ACTIVE);
                    documents.add(document);

                }));
            }
        }
    }

    public List<String> getIdList(RequestInfo requestInfo, String tenantId, String idName, String idformat, int count) {
        List<IdResponse> idResponses = idGenRepository.getId(requestInfo, tenantId, idName, idformat, count).getIdResponses();

        if (CollectionUtils.isEmpty(idResponses)) {
            throw new CustomException(LegalErrorConstants.IDGEN_ERROR, "No ids returned from idgen Service");
        }

        return idResponses.stream().map(IdResponse::getId).collect(Collectors.toList());
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

    }
}



