package org.ilms.service;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.ilms.configs.ILMSConfiguration;
import org.ilms.producer.Producer;
import org.ilms.repository.CaseRepository;
import org.ilms.repository.HearingRepository;
import org.ilms.util.HearingUtils;
import org.ilms.util.ILMSErrorConstants;
import org.ilms.validator.HearingValidator;
import org.ilms.web.model.*;
import org.ilms.web.model.enums.PartyType;
import org.ilms.web.model.enums.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class HearingService {
    @Autowired
    HearingRepository hearingRepository;

    @Autowired
    private Producer producer;

    @Autowired
    private HearingEnrichmentService hearingEnrichmentService;

    @Autowired
    private ILMSConfiguration ilmsConfiguration;

    @Autowired
    private HearingRepository hearingDetailsRepository;

    @Autowired
    private HearingValidator hearingDetailsValidator;

    @Autowired
    private HearingUtils hearingUtils;

    @Autowired
    private CaseRepository caseRepository;

    public Hearing create(HearingRequest hearingRequest) {
        String petitionerId = null;
        String respondentId = null;
        CaseResponse caseResponse = null;
        CaseSearchCriteria criteria = CaseSearchCriteria.builder().id(Collections.singletonList(hearingRequest.getHearing().getCaseId())).build();
        caseResponse = caseRepository.getILMSCaseData(criteria);
        List<Party> partyList = hearingDetailsRepository.getGetFromPartyQuery(hearingRequest.getHearing().getCaseId());
        for (Party party : partyList) {
            if (party.getPartyType().equals(PartyType.RESPONDENT.toString())) {
                respondentId = party.getId();
            } else {
                petitionerId = party.getId();
            }
        }
        if (Objects.nonNull(caseResponse.getCaseList())) {
            if (caseResponse.getCaseList().get(0).getNumber().equals(hearingRequest.getHearing().getCaseNumber())) {
                hearingRequest.getHearing().setStatus(Status.ACTIVE);
                if (Objects.nonNull(hearingRequest.getHearing().getPetitioner())) {
                    hearingRequest.getHearing().getPetitioner().setStatus(Status.ACTIVE);
                    hearingRequest.getHearing().getPetitioner().setPartyType(PartyType.PETITIONER.toString());
                    hearingRequest.getHearing().getPetitioner().setCaseId(hearingRequest.getHearing().getCaseId());
                    if (Objects.nonNull(hearingRequest.getHearing().getPetitioner().getAdvocate())) {
                        hearingRequest.getHearing().getPetitioner().getAdvocate().setStatus(Status.ACTIVE);
                        hearingRequest.getHearing().getPetitioner().getAdvocate().setPartyType(PartyType.PETITIONER);
                        hearingRequest.getHearing().getPetitioner().getAdvocate().setPartyId(petitionerId);
                    }
                }
                if (Objects.nonNull(hearingRequest.getHearing().getRespondent())) {
                    hearingRequest.getHearing().getRespondent().setStatus(Status.ACTIVE);
                    hearingRequest.getHearing().getRespondent().setPartyType(PartyType.RESPONDENT.toString());
                    hearingRequest.getHearing().getRespondent().setCaseId(hearingRequest.getHearing().getCaseId());
                    if (Objects.nonNull(hearingRequest.getHearing().getRespondent().getAdvocate())) {
                        hearingRequest.getHearing().getRespondent().getAdvocate().setStatus(Status.ACTIVE);
                        hearingRequest.getHearing().getRespondent().getAdvocate().setPartyType(PartyType.RESPONDENT);
                        hearingRequest.getHearing().getRespondent().getAdvocate().setPartyId(respondentId);
                    }
                }
                if (Objects.nonNull(hearingRequest.getHearing().getPayment())) {
                    hearingRequest.getHearing().getPayment().setStatus(Status.ACTIVE);
                }

                hearingRequest.getHearing().setHearingNumber(hearingDetailsRepository.getMaxValueOfHearing(hearingRequest.getHearing().getCaseId()));
                hearingDetailsValidator.createValidator(hearingRequest);
                hearingEnrichmentService.enrichHearingCreateRequest(hearingRequest);
                producer.push(ilmsConfiguration.getCreateHearingTopic(), hearingRequest);
            } else {
                throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "CaseNumber Invalid");
            }
        } else {
            throw new CustomException(ILMSErrorConstants.CASE_NOT_AVAILABLE, "Case is not Available for this Hearing");
        }
        return hearingRequest.getHearing();
    }

    public HearingResponse hearingSearch(HearingSearchCriteria criteria, RequestInfo requestInfo) {
        List<Hearing> ilmsHearingList = new ArrayList<>();
        HearingResponse hearingResponse = null;
        hearingResponse = hearingDetailsRepository.getHearingDetails(criteria);
        if (!hearingResponse.getHearingList().isEmpty()) {
            ilmsHearingList = hearingResponse.getHearingList();
        } else {
            throw new CustomException(ILMSErrorConstants.HEARING_NOT_AVAILABLE, "Hearing is not Available");
        }
        return hearingResponse;
    }

    public Hearing update(HearingRequest hearingDetailsRequest) {
        if (hearingDetailsRequest.getHearing().getId() != null) {
            HearingSearchCriteria criteria = HearingSearchCriteria.builder().id((hearingDetailsRequest.getHearing().getId())).build();
            HearingResponse hearingDetailsResponse = hearingDetailsRepository.getHearingDetails(criteria);
            if (!hearingDetailsResponse.getHearingList().isEmpty()) {
                List<Hearing> hearingList = hearingDetailsResponse.getHearingList();
                Hearing oldHearing = hearingList.get(0);
                HearingRequest updatedRequest = hearingUtils.prepareHearingDetailsModalForUpdate(hearingDetailsRequest, oldHearing);
                hearingDetailsValidator.updateValidator(updatedRequest.getHearing(), hearingDetailsRequest);
                producer.push(ilmsConfiguration.getUpdateHearingTopic(), updatedRequest);
            } else {
                throw new CustomException(ILMSErrorConstants.HEARING_NOT_AVAILABLE, "Hearing is not Available");
            }
        } else {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "Id is mandatory");
        }
        return hearingDetailsRequest.getHearing();
    }
}


