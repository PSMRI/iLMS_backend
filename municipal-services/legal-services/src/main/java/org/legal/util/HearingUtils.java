package org.legal.util;

import org.egov.common.contract.request.User;
import org.legal.configs.LEGALConfiguration;
import org.legal.repository.HearingRepository;
import org.legal.service.CaseEnrichmentService;
import org.legal.web.model.*;
import org.legal.web.model.enums.CreationReason;
import org.legal.web.model.enums.PartyType;
import org.legal.web.model.workflow.ProcessInstance;
import org.legal.web.model.workflow.ProcessInstanceRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
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

    @Autowired
    private LEGALConfiguration configuration;

    public HearingRequest prepareHearingDetailsModalForUpdate(HearingRequest hearingDetailsRequest, Hearing oldHearingRequest) {
        HearingRequest updatedRequest = new HearingRequest();
        String tenantId = hearingRepository.getTenantIdFromHearing(hearingDetailsRequest.getHearing().getId());
        if (tenantId == null) {
            tenantId = hearingDetailsRequest.getHearing().getTenantId();
        }
        updatedRequest.setRequestInfo(hearingDetailsRequest.getRequestInfo());
        if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getCaseId())) {
            oldHearingRequest.setCaseId(hearingDetailsRequest.getHearing().getCaseId());
        }
        if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getTenantId())) {
            oldHearingRequest.setTenantId(hearingDetailsRequest.getHearing().getTenantId());
        }
        if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getHearingNumber())) {
            oldHearingRequest.setHearingNumber(hearingDetailsRequest.getHearing().getHearingNumber());
        }
        if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getFirstHearingDate())) {
            oldHearingRequest.setFirstHearingDate(hearingDetailsRequest.getHearing().getFirstHearingDate());
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
        if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getCourtRoomNumber())) {
            oldHearingRequest.setCourtRoomNumber(hearingDetailsRequest.getHearing().getCourtRoomNumber());
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
            oldHearingRequest.setDepartmentOfficer(hearingDetailsRequest.getHearing().getDepartmentOfficer());
        }
        if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getRemarks())) {
            oldHearingRequest.setRemarks(hearingDetailsRequest.getHearing().getRemarks());
        }
        if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getStatus())) {
            oldHearingRequest.setStatus(hearingDetailsRequest.getHearing().getStatus());
        }
        //checking respondent details
        for (Party party : hearingDetailsRequest.getHearing().getParties()) {
            for (Party oldParty : oldHearingRequest.getParties()) {
                if (party.getPartyType().equals(PartyType.RESPONDENT.toString()) && party.getId().equals(oldParty.getId())) {
                    if (!StringUtils.isEmpty(party.getCaseId())) {
                        oldParty.setCaseId(party.getCaseId());
                    }
                    if (!StringUtils.isEmpty(party.getFirstName())) {
                        oldParty.setFirstName(party.getFirstName());
                    }
                    if (!StringUtils.isEmpty(party.getAdvocateId())) {
                        oldParty.setAdvocateId(party.getAdvocateId());
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

                        for (Advocate advocate : party.getAdvocate()) {
                            for (Advocate oldAdvocate : oldParty.getAdvocate()) {
                                if (!StringUtils.isEmpty(advocate.getFirstName())) {
                                    oldAdvocate
                                            .setFirstName(advocate.getFirstName());
                                }
                                if (!StringUtils.isEmpty(advocate.getLastName())) {
                                    oldAdvocate
                                            .setLastName(advocate.getLastName());
                                }
                                if (!StringUtils.isEmpty(advocate.getContactNumber())) {
                                    oldAdvocate
                                            .setContactNumber(advocate.getContactNumber());
                                }

                                if (!StringUtils.isEmpty(advocate.getStatus())) {
                                    oldAdvocate
                                            .setStatus(advocate.getStatus());
                                }
                            }
                        }
                    }
                    //checking petitioner details
                    if (party.getPartyType().equals(PartyType.PETITIONER.toString()) && party.getId().equals(oldParty.getId())) {
                        if (!StringUtils.isEmpty(party.getCaseId())) {
                            oldParty.setCaseId(party.getCaseId());
                        }
                        if (!StringUtils.isEmpty(party.getFirstName())) {
                            oldParty.setFirstName(party.getFirstName());
                        }
                        if (!StringUtils.isEmpty(party.getAdvocateId())) {
                            oldParty.setAdvocateId(party.getAdvocateId());
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
                        //Setting Data For Petitioner Advocate
                        if (!StringUtils.isEmpty(party.getAdvocate())) {

                            for (Advocate advocate : party.getAdvocate()) {
                                for (Advocate oldAdvocate : oldParty.getAdvocate()) {
                                    if (!StringUtils.isEmpty(advocate.getFirstName())) {
                                        oldAdvocate
                                                .setFirstName(advocate.getFirstName());
                                    }
                                    if (!StringUtils.isEmpty(advocate.getLastName())) {
                                        oldAdvocate
                                                .setLastName(advocate.getLastName());
                                    }
                                    if (!StringUtils.isEmpty(advocate.getContactNumber())) {
                                        oldAdvocate
                                                .setContactNumber(advocate.getContactNumber());
                                    }

                                    if (!StringUtils.isEmpty(advocate.getStatus())) {
                                        oldAdvocate
                                                .setStatus(advocate.getStatus());
                                    }
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
        }
        updatedRequest.setHearing(oldHearingRequest);
        caseEnrichmentService.enrichmentForHearingUpdateRequest(updatedRequest);
        return updatedRequest;
    }


    public ProcessInstance hearingWFUpdate(HearingRequest request, String action) {
        Hearing hearing = request.getHearing();
        Workflow workflow = request.getWorkflow();
        ProcessInstance processInstance = new ProcessInstance();
        processInstance.setBusinessId(hearing.getId());
        processInstance.setAction(action);
        processInstance.setModuleName(configuration.getModuleName());
        processInstance.setTenantId(hearing.getTenantId());
        processInstance.setBusinessService(configuration.getCreateHearingWfName());
        processInstance.setComment(workflow.getComments());
        if (!CollectionUtils.isEmpty(workflow.getAssignes())) {
            List<User> users = new ArrayList<>();
            workflow.getAssignes().forEach(uuid -> {
                User user = new User();
                user.setUuid(uuid);
                users.add(user);
            });
            processInstance.setAssignes(users);
        }
        return processInstance;
    }
}