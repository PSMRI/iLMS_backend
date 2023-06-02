package org.legal.util;

import org.egov.common.contract.request.User;
import org.legal.configs.LEGALConfiguration;
import org.legal.producer.Producer;
import org.legal.repository.AdvocateRepository;
import org.legal.repository.HearingRepository;
import org.legal.service.AdvocateService;
import org.legal.service.HearingEnrichmentService;
import org.legal.web.model.*;
import org.legal.web.model.workflow.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class HearingUtils {
    @Autowired
    private HearingEnrichmentService hearingEnrichmentService;

    @Autowired
    private CommonUtils commonUtils;

    @Autowired
    private HearingRepository hearingRepository;

    @Autowired
    private LEGALConfiguration configuration;

    @Autowired
    private AdvocateService advocateService;

    @Autowired
    private AdvocateRepository advocateRepository;

    @Autowired
    private CaseUtils caseUtils;

    @Autowired
    private Producer producer;

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
        if (Objects.nonNull(hearingDetailsRequest.getHearing().getFirstHearingDate())) {
            oldHearingRequest.setFirstHearingDate(hearingDetailsRequest.getHearing().getFirstHearingDate());
        }
        if (Objects.nonNull(hearingDetailsRequest.getHearing().getJudgeName())) {
            oldHearingRequest.setJudgeName(hearingDetailsRequest.getHearing().getJudgeName());
        }
        if (Objects.nonNull(hearingDetailsRequest.getHearing().getHearingDate())) {
            oldHearingRequest.setHearingDate(hearingDetailsRequest.getHearing().getHearingDate());
        }
        if (Objects.nonNull(hearingDetailsRequest.getHearing().getBusinessDate())) {
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
        if (Objects.nonNull(hearingDetailsRequest.getHearing().getAffidavitFilingDate())) {
            List<String> uuids = new ArrayList<>();
            uuids.add(hearingDetailsRequest.getRequestInfo().getUserInfo().getUuid());

            if (!hearingDetailsRequest.getHearing().getAffidavitFilingDate().equals(oldHearingRequest.getAffidavitFilingDate()) && commonUtils.isUserOIC(uuids, tenantId, Constants.AffidavitFilingDate)) {
                oldHearingRequest.setAffidavitFilingDate(hearingDetailsRequest.getHearing().getAffidavitFilingDate());
            }
        }
        if (Objects.nonNull(hearingDetailsRequest.getHearing().getAffidavitFilingDueDate())) {
            oldHearingRequest.setAffidavitFilingDueDate(hearingDetailsRequest.getHearing().getAffidavitFilingDueDate());
        }
        if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getCaseNumber())) {
            oldHearingRequest.setCaseNumber(hearingDetailsRequest.getHearing().getCaseNumber());
        }
        if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getOathNumber())) {
            List<String> uuids = new ArrayList<>();
            uuids.add(hearingDetailsRequest.getRequestInfo().getUserInfo().getUuid());
            if (!hearingDetailsRequest.getHearing().getOathNumber().equals(oldHearingRequest.getOathNumber()) && commonUtils.isUserOIC(uuids, tenantId, Constants.OathNumber)) {
                oldHearingRequest.setOathNumber(hearingDetailsRequest.getHearing().getOathNumber());
            }
        }
        if (Objects.nonNull(hearingDetailsRequest.getHearing().getNextHearingDate())) {
            oldHearingRequest.setNextHearingDate(hearingDetailsRequest.getHearing().getNextHearingDate());

        }
        if (Objects.nonNull(hearingDetailsRequest.getHearing().getIsPresenceRequired())) {
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
        if (Objects.nonNull(hearingDetailsRequest.getHearing().getPetitionerAdvocate())) {
            oldHearingRequest.setPetitionerAdvocate(hearingDetailsRequest.getHearing().getPetitionerAdvocate());
        }
        if (Objects.nonNull(hearingDetailsRequest.getHearing().getRespondentAdvocate())) {
            oldHearingRequest.setRespondentAdvocate(hearingDetailsRequest.getHearing().getRespondentAdvocate());
        }
        if (Objects.nonNull(hearingDetailsRequest.getHearing().getPayment())) {
            if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getPayment().getFineImposedDate())) {
                oldHearingRequest.getPayment().setFineImposedDate(hearingDetailsRequest.getHearing().getPayment().getFineImposedDate());
            }
            if (Objects.nonNull(hearingDetailsRequest.getHearing().getPayment().getFineDueDate())) {
                oldHearingRequest.getPayment().setFineDueDate(hearingDetailsRequest.getHearing().getPayment().getFineDueDate());
            }
            if (!StringUtils.isEmpty(hearingDetailsRequest.getHearing().getPayment().getFineAmount())) {
                oldHearingRequest.getPayment().setFineAmount(hearingDetailsRequest.getHearing().getPayment().getFineAmount());
            }

        }
        if (Objects.nonNull(hearingDetailsRequest.getHearing().getDocuments())) {
            List<Document> documentList = hearingDetailsRequest.getHearing().getDocuments();
            for (Document document : documentList) {
                for (Document oldDocData : oldHearingRequest.getDocuments()) {
                    if (oldDocData.getId().equalsIgnoreCase(document.getId())) {

                        if (!StringUtils.isEmpty(document.getRemarks())) {
                            oldDocData.setRemarks(document.getRemarks());
                        }

                        if (!StringUtils.isEmpty(document.getDocumentType())) {
                            oldDocData.setDocumentType(document.getDocumentType());
                        }
                        if (!StringUtils.isEmpty(document.getFileStoreId())) {
                            oldDocData.setFileStoreId(document.getFileStoreId());
                        }
                        if (!StringUtils.isEmpty(document.getStatus())) {
                            oldDocData.setStatus(document.getStatus());
                        }
                    }
                }
            }
        }

        updatedRequest.setHearing(oldHearingRequest);
        hearingEnrichmentService.enrichmentForHearingUpdateRequest(updatedRequest);
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
