package org.ilms.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.ilms.configs.ILMSConfiguration;
import org.ilms.producer.Producer;
import org.ilms.repository.HearingRepository;
import org.ilms.repository.ILMSCaseRepository;
import org.ilms.util.HearingUtils;
import org.ilms.util.ILMSErrorConstants;
import org.ilms.validator.HearingValidator;
import org.ilms.web.model.Hearing;
import org.ilms.web.model.HearingRequest;
import org.ilms.web.model.HearingResponse;
import org.ilms.web.model.HearingSearchCriteria;
import org.ilms.web.model.ILMSCaseResponse;
import org.ilms.web.model.ILMSCaseSearchCriteria;
import org.ilms.web.model.ILMSParty;
import org.ilms.web.model.enums.PartyType;
import org.ilms.web.model.enums.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private ILMSCaseRepository ilmsCaseRepository;

    public Hearing create(HearingRequest hearingRequest) {
        String petitionerId = null;
        String respondentId = null;
        ILMSCaseResponse ilmsCaseResponse = null;
        ILMSCaseSearchCriteria criteria = ILMSCaseSearchCriteria.builder().id(Collections.singletonList(hearingRequest.getHearing().getCaseId()))
                                                                .build();
        ilmsCaseResponse = ilmsCaseRepository.getILMSCaseData(criteria);
        if (Objects.nonNull(ilmsCaseResponse.getIlmsCases())) {
            if (ilmsCaseResponse.getIlmsCases().get(0).getCaseNumber().equals(hearingRequest.getHearing().getCaseNumber())) {
                hearingRequest.getHearing().setStatus(Status.ACTIVE);
                hearingRequest.getHearing().getCourt().setStatus(Status.ACTIVE);
                hearingRequest.getHearing().getPetitioner().setStatus(Status.ACTIVE);
                hearingRequest.getHearing().getRespondent().setStatus(Status.ACTIVE);
                hearingRequest.getHearing().getRespondent().getAdvocate().setStatus(Status.ACTIVE);
                hearingRequest.getHearing().getPetitioner().getAdvocate().setStatus(Status.ACTIVE);
                hearingRequest.getHearing().getPetitioner().setPartyType(PartyType.PETITIONER.toString());
                hearingRequest.getHearing().getRespondent().setPartyType(PartyType.RESPONDENT.toString());
                hearingRequest.getHearing().getPetitioner().getAdvocate().setPartyType(PartyType.PETITIONER);
                hearingRequest.getHearing().getRespondent().getAdvocate().setPartyType(PartyType.RESPONDENT);
                hearingRequest.getHearing().getPetitioner().setCaseId(hearingRequest.getHearing().getCaseId());
                hearingRequest.getHearing().getRespondent().setCaseId(hearingRequest.getHearing().getCaseId());
                hearingRequest.getHearing().setHearingNumber(hearingDetailsRepository.getMaxValueOfHearing(hearingRequest.getHearing().getCaseId()));
                hearingRequest.getHearing().getPayment().setStatus(Status.ACTIVE);
                List<ILMSParty> partyList = hearingDetailsRepository.getGetFromPartyQuery(hearingRequest.getHearing().getCaseId());
                for (ILMSParty party : partyList) {
                    if (party.getPartyType().equals(PartyType.RESPONDENT.toString())) {
                        respondentId = party.getId();
                    } else {
                        petitionerId = party.getId();
                    }
                }
                hearingRequest.getHearing().getPetitioner().getAdvocate().setPartyId(petitionerId);
                hearingRequest.getHearing().getRespondent().getAdvocate().setPartyId(respondentId);
                hearingDetailsValidator.createValidator(hearingRequest);
                hearingEnrichmentService.enrichHearingCreateRequest(hearingRequest);
                producer.push(ilmsConfiguration.getCreateHearingDetailsTopic(), hearingRequest);
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
        if (!hearingResponse.getHearingDetails().isEmpty()) {
            ilmsHearingList = hearingResponse.getHearingDetails();
        } else {
            throw new CustomException(ILMSErrorConstants.HEARING_NOT_AVAILABLE, "Hearing is not Available");
        }
        return hearingResponse;
    }

    public Hearing update(HearingRequest hearingDetailsRequest) {
        if (hearingDetailsRequest.getHearing().getId() != null) {
            HearingSearchCriteria criteria = HearingSearchCriteria.builder().id((hearingDetailsRequest.getHearing().getId())).build();
            HearingResponse hearingDetailsResponse = hearingDetailsRepository.getHearingDetails(criteria);
            if (!hearingDetailsResponse.getHearingDetails().isEmpty()) {
                List<Hearing> hearingList = hearingDetailsResponse.getHearingDetails();
                Hearing oldHearing = hearingList.get(0);
                HearingRequest updatedRequest = hearingUtils.prepareHearingDetailsModalForUpdate(hearingDetailsRequest, oldHearing);
                hearingDetailsValidator.updateValidator(updatedRequest.getHearing(), hearingDetailsRequest);
                producer.push(ilmsConfiguration.getUpdateHearingDetailsTopic(), updatedRequest);
            } else {
                throw new CustomException(ILMSErrorConstants.HEARING_NOT_AVAILABLE, "Hearing is not Available");
            }
        } else {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "Id is mandatory");
        }
        return hearingDetailsRequest.getHearing();
    }

}


