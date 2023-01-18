package org.ilms.util;

import org.ilms.repository.HearingRepository;
import org.ilms.service.CaseEnrichmentService;
import org.ilms.web.model.Hearing;
import org.ilms.web.model.HearingRequest;
import org.ilms.web.model.Party;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class HearingUtils {
    @Autowired
    private CaseEnrichmentService caseEnrichmentService;

    @Autowired
    private CommonUtils commonUtils;

    @Autowired
    private HearingRepository hearingRepository;

    public HearingRequest prepareHearingDetailsModalForUpdate(HearingRequest hearingDetailsRequest, Hearing oldHearingRequest) {
        HearingRequest updatedRequest = new HearingRequest();
        final String tenantId = hearingRepository.getTenantIdFromHearing(hearingDetailsRequest.getHearing().getId());
        updatedRequest.setRequestInfo(hearingDetailsRequest.getRequestInfo());
        if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getCaseId())) {
            oldHearingRequest.setCaseId(hearingDetailsRequest.getHearing().getCaseId());
        }
        if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getHearingNumber())) {
            oldHearingRequest.setHearingNumber(hearingDetailsRequest.getHearing().getHearingNumber());
        }
        if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getCourtId())) {
            oldHearingRequest.setCourtId(hearingDetailsRequest.getHearing().getCourtId());
        }
        if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getJudgeName())) {
            oldHearingRequest.setJudgeName(hearingDetailsRequest.getHearing().getJudgeName());
        }
        if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getHearingDate())) {
            oldHearingRequest.setHearingDate(hearingDetailsRequest.getHearing().getHearingDate());
        }
        if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getBusinessDate())) {
            oldHearingRequest.setBusinessDate(hearingDetailsRequest.getHearing().getBusinessDate());
        }
        if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getHearingPurpose())) {
            oldHearingRequest.setHearingPurpose(hearingDetailsRequest.getHearing().getHearingPurpose());
        }
        if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getRequiredOfficer())) {
            oldHearingRequest.setRequiredOfficer(hearingDetailsRequest.getHearing().getRequiredOfficer());
        }
        if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getAffidavitFilingDate())) {
            List<String> uuids = new ArrayList<>();
            uuids.add(hearingDetailsRequest.getRequestInfo().getUserInfo().getUuid());
            if (commonUtils.isUserOIC(uuids, tenantId, "AffidavitFilingDate")) {
                oldHearingRequest.setAffidavitFilingDate(hearingDetailsRequest.getHearing().getAffidavitFilingDate());
            }
        }
        if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getAffidavitFilingDueDate())) {
            oldHearingRequest.setAffidavitFilingDueDate(hearingDetailsRequest.getHearing().getAffidavitFilingDueDate());
        }
        if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getCaseNumber())) {
            oldHearingRequest.setCaseNumber(hearingDetailsRequest.getHearing().getCaseNumber());
        }
        if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getPreviousHearingDate())) {
            List<String> uuids = new ArrayList<>();
            uuids.add(hearingDetailsRequest.getRequestInfo().getUserInfo().getUuid());
            if (commonUtils.isUserOIC(uuids, tenantId, "PreviousHearingDate")) {
                oldHearingRequest.setPreviousHearingDate(hearingDetailsRequest.getHearing().getPreviousHearingDate());
            }
        }
        if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getOathNumber())) {
            List<String> uuids = new ArrayList<>();
            uuids.add(hearingDetailsRequest.getRequestInfo().getUserInfo().getUuid());
            if (commonUtils.isUserOIC(uuids, tenantId, "OathNumber")) {
                oldHearingRequest.setOathNumber(hearingDetailsRequest.getHearing().getOathNumber());
            }
        }
        if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getNextHearingDate())) {
            List<String> uuids = new ArrayList<>();
            uuids.add(hearingDetailsRequest.getRequestInfo().getUserInfo().getUuid());
            if (commonUtils.isUserOIC(uuids, tenantId, "NextHearingDate")) {
                oldHearingRequest.setNextHearingDate(hearingDetailsRequest.getHearing().getNextHearingDate());

            }
        }
        if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getFirstHearingDate())) {
            List<String> uuids = new ArrayList<>();
            uuids.add(hearingDetailsRequest.getRequestInfo().getUserInfo().getUuid());
            if (commonUtils.isUserOIC(uuids, tenantId, "FirstHearingDate")) {
                oldHearingRequest.setFirstHearingDate(hearingDetailsRequest.getHearing().getFirstHearingDate());
            }
        }
        if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getIsPresenceRequired())) {
            List<String> uuids = new ArrayList<>();
            uuids.add(hearingDetailsRequest.getRequestInfo().getUserInfo().getUuid());
            if (commonUtils.isUserOIC(uuids, tenantId, "IsPresenceRequired")) {
                oldHearingRequest.setIsPresenceRequired(hearingDetailsRequest.getHearing().getIsPresenceRequired());
            }
        }

        if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getHearingType())) {
            oldHearingRequest.setHearingType(hearingDetailsRequest.getHearing().getHearingType());
        }
        if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getDepartmentOfficer())) {
            List<String> uuids = new ArrayList<>();
            uuids.add(hearingDetailsRequest.getRequestInfo().getUserInfo().getUuid());
            if (commonUtils.isUserOIC(uuids, tenantId, "DepartmentOfficer")) {
                oldHearingRequest.setDepartmentOfficer(hearingDetailsRequest.getHearing().getDepartmentOfficer());
            }
        }
        if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getRemarks())) {
            oldHearingRequest.setRemarks(hearingDetailsRequest.getHearing().getRemarks());
        }
        if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getStatus())) {
            oldHearingRequest.setStatus(hearingDetailsRequest.getHearing().getStatus());
        }
        //check court details
        if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getCourt())) {
            if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getCourt().getCourtNumber())) {
                oldHearingRequest.getCourt().setCourtNumber(hearingDetailsRequest.getHearing().getCourt().getCourtNumber());
            }
            if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getCourt().getState())) {
                oldHearingRequest.getCourt().setState(hearingDetailsRequest.getHearing().getCourt().getState());
            }
            if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getCourt().getDistrict())) {
                oldHearingRequest.getCourt().setDistrict(hearingDetailsRequest.getHearing().getCourt().getDistrict());
            }
            if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getCourt().getBench())) {
                oldHearingRequest.getCourt().setBench(hearingDetailsRequest.getHearing().getCourt().getBench());
            }
            if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getCourt().getStatus())) {
                oldHearingRequest.getCourt().setStatus(hearingDetailsRequest.getHearing().getCourt().getStatus());
            }
            if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getCourt().getHearingId())) {
                oldHearingRequest.getCourt().setHearingId(hearingDetailsRequest.getHearing().getCourt().getHearingId());
            }
            if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getCourt().getCourtName())) {
                oldHearingRequest.getCourt().setCourtName(hearingDetailsRequest.getHearing().getCourt().getCourtName());
            }
            if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getCourt().getDivision())) {
                oldHearingRequest.getCourt().setDivision(hearingDetailsRequest.getHearing().getCourt().getDivision());
            }
        }
        //checking respondent details
        if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getPartyList())) {
            for (Party oldParty : oldHearingRequest.getPartyList()) {
                for (Party party : hearingDetailsRequest.getHearing().getPartyList()) {
                    if (party.getPartyType().equals(oldParty.getPartyType())) {


                        if (!StringUtils.isEmpty(party.getCaseId())) {
                            oldParty.setCaseId(party.getCaseId());
                        }
                        if (!StringUtils.isEmpty(party.getFirstName())) {
                            oldParty.setFirstName(party.getFirstName());
                        }
                        if (!StringUtils.isEmpty(party.getLastName())) {
                            oldParty.setLastName(party.getLastName());
                        }
                        if (!StringUtils.isEmpty(party.getGender())) {
                            oldParty.setGender(party.getGender());
                        }
                        if (!StringUtils.isEmpty(party.getPetitionerType())) {
                            oldParty.setPetitionerType(party.getPetitionerType());
                        }
                        if (!StringUtils.isEmpty(party.getAddress())) {
                            oldParty.setAddress(party.getAddress());
                        }
                        if (!StringUtils.isEmpty(party.getDepartmentName())) {
                            oldParty.setDepartmentName(party.getDepartmentName());
                        }
                        if (!StringUtils.isEmpty(party.getContactNumber())) {
                            oldParty.setContactNumber(party.getContactNumber());
                        }
                        if (!StringUtils.isEmpty(party.getPartyType())) {
                            oldParty.setPartyType(party.getPartyType());
                        }
                        if (!StringUtils.isEmpty(party.getStatus())) {
                            oldParty.setStatus(party.getStatus());
                        }
                        //Setting Data For Respondent Advocate
                        if (!StringUtils.isEmpty(party.getAdvocate())) {
                            if (!StringUtils.isEmpty(party.getAdvocate().getPartyId())) {
                                oldParty.getAdvocate()
                                        .setPartyId(party.getAdvocate().getPartyId());
                            }
                            if (!StringUtils.isEmpty(party.getAdvocate().getHearingId())) {
                                oldParty.getAdvocate()
                                        .setHearingId(party.getAdvocate().getHearingId());
                            }
                            if (!StringUtils.isEmpty(party.getAdvocate().getFirstName())) {
                                oldParty.getAdvocate()
                                        .setFirstName(party.getAdvocate().getFirstName());
                            }
                            if (!StringUtils.isEmpty(party.getAdvocate().getLastName())) {
                                oldParty.getAdvocate()
                                        .setLastName(party.getAdvocate().getLastName());
                            }
                            if (!StringUtils.isEmpty(party.getAdvocate().getContactNumber())) {
                                oldParty.getAdvocate()
                                        .setContactNumber(party.getAdvocate().getContactNumber());
                            }
                            if (!StringUtils.isEmpty(party.getAdvocate().getPartyType())) {
                                oldParty.getAdvocate()
                                        .setPartyType(party.getAdvocate().getPartyType());
                            }
                            if (!StringUtils.isEmpty(party.getAdvocate().getStatus())) {
                                oldParty.getAdvocate()
                                        .setStatus(party.getAdvocate().getStatus());
                            }
                        }
                    }
                }
            }
            if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getAdditionalDetails())) {
                oldHearingRequest.setAdditionalDetails(hearingDetailsRequest.getHearing().getAdditionalDetails());
            }
        }
        if (Objects.nonNull(hearingDetailsRequest.getHearing().getPayment())) {
            if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getPayment().getFineImposedDate())) {
                oldHearingRequest.getPayment().setFineImposedDate(hearingDetailsRequest.getHearing().getPayment().getFineImposedDate());
            }
            if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getPayment().getFineDueDate())) {
                oldHearingRequest.getPayment().setFineDueDate(hearingDetailsRequest.getHearing().getPayment().getFineDueDate());
            }
            if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getPayment().getFineAmount())) {
                oldHearingRequest.getPayment().setFineAmount(hearingDetailsRequest.getHearing().getPayment().getFineAmount());
            }
        }
        updatedRequest.setHearing(oldHearingRequest);
        caseEnrichmentService.enrichmentForHearingUpdateRequest(updatedRequest);
        return updatedRequest;
    }
}
