package org.ilms.util;

import org.ilms.repository.HearingRepository;
import org.ilms.service.CaseEnrichmentService;
import org.ilms.web.model.Hearing;
import org.ilms.web.model.HearingRequest;
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
        if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getJudgeName())) {
            oldHearingRequest.setJudgeName(hearingDetailsRequest.getHearing().getJudgeName());
        }
        if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getHearingDate())) {
            oldHearingRequest.setHearingDate(hearingDetailsRequest.getHearing().getHearingDate());
        }
        if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getBusinessDate())) {
            oldHearingRequest.setBusinessDate(hearingDetailsRequest.getHearing().getBusinessDate());
        }
        if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getCourtNumber())) {
            oldHearingRequest.setCourtNumber(hearingDetailsRequest.getHearing().getCourtNumber());
        }
        if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getBench())) {
            oldHearingRequest.setBench(hearingDetailsRequest.getHearing().getBench());
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
        if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getOathNumber())) {
            List<String> uuids = new ArrayList<>();
            uuids.add(hearingDetailsRequest.getRequestInfo().getUserInfo().getUuid());
            if (commonUtils.isUserOIC(uuids, tenantId, "OathNumber")) {
                oldHearingRequest.setOathNumber(hearingDetailsRequest.getHearing().getOathNumber());
            }
        }
        if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getNextHearingDate())) {
            oldHearingRequest.setNextHearingDate(hearingDetailsRequest.getHearing().getNextHearingDate());

        }
        if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getIsPresenceRequired())) {
            oldHearingRequest.setIsPresenceRequired(hearingDetailsRequest.getHearing().getIsPresenceRequired());
        }

        if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getHearingType())) {
            oldHearingRequest.setHearingType(hearingDetailsRequest.getHearing().getHearingType());
        }
        if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getDepartmentOfficer())) {
//            List<String> uuids = new ArrayList<>();
//            uuids.add(hearingDetailsRequest.getRequestInfo().getUserInfo().getUuid());
//            if (commonUtils.isUserOIC(uuids, tenantId, "DepartmentOfficer")) {
            oldHearingRequest.setDepartmentOfficer(hearingDetailsRequest.getHearing().getDepartmentOfficer());
//            }
        }
        if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getRemarks())) {
            oldHearingRequest.setRemarks(hearingDetailsRequest.getHearing().getRemarks());
        }
        if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getStatus())) {
            oldHearingRequest.setStatus(hearingDetailsRequest.getHearing().getStatus());
        }
        //checking respondent details
        if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getRespondent())) {
            if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getRespondent().getCaseId())) {
                oldHearingRequest.getRespondent().setCaseId(hearingDetailsRequest.getHearing().getRespondent().getCaseId());
            }
            if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getRespondent().getFirstName())) {
                oldHearingRequest.getRespondent().setFirstName(hearingDetailsRequest.getHearing().getRespondent().getFirstName());
            }
            if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getRespondent().getLastName())) {
                oldHearingRequest.getRespondent().setLastName(hearingDetailsRequest.getHearing().getRespondent().getLastName());
            }
            if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getRespondent().getGender())) {
                oldHearingRequest.getRespondent().setGender(hearingDetailsRequest.getHearing().getRespondent().getGender());
            }
            if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getRespondent().getPetitionerType())) {
                oldHearingRequest.getRespondent().setPetitionerType(hearingDetailsRequest.getHearing().getRespondent().getPetitionerType());
            }
            if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getRespondent().getAddress())) {
                oldHearingRequest.getRespondent().setAddress(hearingDetailsRequest.getHearing().getRespondent().getAddress());
            }
            if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getRespondent().getDepartmentName())) {
                oldHearingRequest.getRespondent().setDepartmentName(hearingDetailsRequest.getHearing().getRespondent().getDepartmentName());
            }
            if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getRespondent().getContactNumber())) {
                oldHearingRequest.getRespondent().setContactNumber(hearingDetailsRequest.getHearing().getRespondent().getContactNumber());
            }
            if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getRespondent().getPartyType())) {
                oldHearingRequest.getRespondent().setPartyType(hearingDetailsRequest.getHearing().getRespondent().getPartyType());
            }
            if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getRespondent().getStatus())) {
                oldHearingRequest.getRespondent().setStatus(hearingDetailsRequest.getHearing().getRespondent().getStatus());
            }
            //Setting Data For Respondent Advocate
            if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getRespondent().getAdvocate())) {
                if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getRespondent().getAdvocate().getPartyId())) {
                    oldHearingRequest.getRespondent().getAdvocate()
                            .setPartyId(hearingDetailsRequest.getHearing().getRespondent().getAdvocate().getPartyId());
                }
                if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getRespondent().getAdvocate().getHearingId())) {
                    oldHearingRequest.getRespondent().getAdvocate()
                            .setHearingId(hearingDetailsRequest.getHearing().getRespondent().getAdvocate().getHearingId());
                }
                if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getRespondent().getAdvocate().getFirstName())) {
                    oldHearingRequest.getRespondent().getAdvocate()
                            .setFirstName(hearingDetailsRequest.getHearing().getRespondent().getAdvocate().getFirstName());
                }
                if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getRespondent().getAdvocate().getLastName())) {
                    oldHearingRequest.getRespondent().getAdvocate()
                            .setLastName(hearingDetailsRequest.getHearing().getRespondent().getAdvocate().getLastName());
                }
                if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getRespondent().getAdvocate().getContactNumber())) {
                    oldHearingRequest.getRespondent().getAdvocate()
                            .setContactNumber(hearingDetailsRequest.getHearing().getRespondent().getAdvocate().getContactNumber());
                }
                if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getRespondent().getAdvocate().getPartyType())) {
                    oldHearingRequest.getRespondent().getAdvocate()
                            .setPartyType(hearingDetailsRequest.getHearing().getRespondent().getAdvocate().getPartyType());
                }
                if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getRespondent().getAdvocate().getStatus())) {
                    oldHearingRequest.getRespondent().getAdvocate()
                            .setStatus(hearingDetailsRequest.getHearing().getRespondent().getAdvocate().getStatus());
                }
            }
        }
        //checking petitioner details
        if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getPetitioner())) {
            if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getPetitioner().getCaseId())) {
                oldHearingRequest.getPetitioner().setCaseId(hearingDetailsRequest.getHearing().getPetitioner().getCaseId());
            }
            if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getPetitioner().getFirstName())) {
                oldHearingRequest.getPetitioner().setFirstName(hearingDetailsRequest.getHearing().getPetitioner().getFirstName());
            }
            if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getPetitioner().getLastName())) {
                oldHearingRequest.getPetitioner().setLastName(hearingDetailsRequest.getHearing().getPetitioner().getLastName());
            }
            if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getPetitioner().getGender())) {
                oldHearingRequest.getPetitioner().setGender(hearingDetailsRequest.getHearing().getPetitioner().getGender());
            }
            if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getPetitioner().getPetitionerType())) {
                oldHearingRequest.getPetitioner().setPetitionerType(hearingDetailsRequest.getHearing().getPetitioner().getPetitionerType());
            }
            if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getPetitioner().getAddress())) {
                oldHearingRequest.getPetitioner().setAddress(hearingDetailsRequest.getHearing().getPetitioner().getAddress());
            }
            if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getPetitioner().getDepartmentName())) {
                oldHearingRequest.getPetitioner().setDepartmentName(hearingDetailsRequest.getHearing().getPetitioner().getDepartmentName());
            }
            if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getPetitioner().getContactNumber())) {
                oldHearingRequest.getPetitioner().setContactNumber(hearingDetailsRequest.getHearing().getPetitioner().getContactNumber());
            }
            if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getPetitioner().getPartyType())) {
                oldHearingRequest.getPetitioner().setPartyType(hearingDetailsRequest.getHearing().getPetitioner().getPartyType());
            }
            if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getPetitioner().getStatus())) {
                oldHearingRequest.getPetitioner().setStatus(hearingDetailsRequest.getHearing().getPetitioner().getStatus());
            }
            //Setting Data For Petitioner Advocate
            if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getPetitioner().getAdvocate())) {
                if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getPetitioner().getAdvocate().getPartyId())) {
                    oldHearingRequest.getPetitioner().getAdvocate()
                            .setPartyId(hearingDetailsRequest.getHearing().getPetitioner().getAdvocate().getPartyId());
                }
                if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getPetitioner().getAdvocate().getHearingId())) {
                    oldHearingRequest.getPetitioner().getAdvocate()
                            .setHearingId(hearingDetailsRequest.getHearing().getPetitioner().getAdvocate().getHearingId());
                }
                if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getPetitioner().getAdvocate().getFirstName())) {
                    oldHearingRequest.getPetitioner().getAdvocate()
                            .setFirstName(hearingDetailsRequest.getHearing().getPetitioner().getAdvocate().getFirstName());
                }
                if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getPetitioner().getAdvocate().getLastName())) {
                    oldHearingRequest.getPetitioner().getAdvocate()
                            .setLastName(hearingDetailsRequest.getHearing().getPetitioner().getAdvocate().getLastName());
                }
                if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getPetitioner().getAdvocate().getContactNumber())) {
                    oldHearingRequest.getPetitioner().getAdvocate()
                            .setContactNumber(hearingDetailsRequest.getHearing().getPetitioner().getAdvocate().getContactNumber());
                }
                if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getPetitioner().getAdvocate().getPartyType())) {
                    oldHearingRequest.getPetitioner().getAdvocate()
                            .setPartyType(hearingDetailsRequest.getHearing().getPetitioner().getAdvocate().getPartyType());
                }
                if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getPetitioner().getAdvocate().getStatus())) {
                    oldHearingRequest.getPetitioner().getAdvocate()
                            .setStatus(hearingDetailsRequest.getHearing().getPetitioner().getAdvocate().getStatus());
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
