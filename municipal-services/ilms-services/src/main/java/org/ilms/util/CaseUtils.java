package org.ilms.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.ilms.configs.ILMSConfiguration;
import org.ilms.repository.CaseRepository;
import org.ilms.service.CaseEnrichmentService;
import org.ilms.web.model.AuditDetails;
import org.ilms.web.model.Case;
import org.ilms.web.model.CaseRequest;
import org.ilms.web.model.CaseResponse;
import org.ilms.web.model.CaseSearchCriteria;
import org.ilms.web.model.Document;
import org.ilms.web.model.enums.CreationReason;
import org.ilms.web.model.workflow.ProcessInstance;
import org.ilms.web.model.workflow.ProcessInstanceRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class CaseUtils {
    @Autowired
    private CaseEnrichmentService caseEnrichmentService;

    @Autowired
    private ILMSConfiguration ilmsConfiguration;

    @Autowired
    private CaseRepository caseRepository;

    @Autowired
    private CommonUtils commonUtils;

    public AuditDetails getAuditDetails(String by, Boolean isCreate) {
        Long time = System.currentTimeMillis();
        if (isCreate) {
            return AuditDetails.builder().createdBy(by).lastModifiedBy(by).createdTime(time).lastModifiedTime(time).build();
        } else {
            return AuditDetails.builder().lastModifiedBy(by).lastModifiedTime(time).build();
        }
    }

    public CaseRequest prepareObjectMapperForUpdate(Case oldData, CaseRequest caseRequest) {
        final CaseRequest request = new CaseRequest();

        if (!StringUtils.isEmpty(caseRequest.getCases().getTenantId())) {
            oldData.setTenantId(caseRequest.getCases().getTenantId());
        }
        if (!StringUtils.isEmpty(caseRequest.getCases().getCaseNumber())) {
            oldData.setCaseNumber(caseRequest.getCases().getCaseNumber());
        }
        if (!StringUtils.isEmpty(caseRequest.getCases().getCnrNumber())) {
            oldData.setCnrNumber(caseRequest.getCases().getCnrNumber());
        }
        if (!StringUtils.isEmpty(caseRequest.getCases().getParentCaseId())) {
            oldData.setParentCaseId(caseRequest.getCases().getParentCaseId());
        }
        if (!StringUtils.isEmpty(caseRequest.getCases().getCaseHierarchy())) {
            oldData.setCaseHierarchy(caseRequest.getCases().getCaseHierarchy());
        }
        if (!StringUtils.isEmpty(caseRequest.getCases().getCaseType())) {
            oldData.setCaseType(caseRequest.getCases().getCaseType());
        }
        if (!StringUtils.isEmpty(caseRequest.getCases().getCaseCategory())) {
            oldData.setCaseCategory(caseRequest.getCases().getCaseCategory());
        }
        if (!StringUtils.isEmpty(caseRequest.getCases().getCaseYear())) {
            oldData.setCaseYear(caseRequest.getCases().getCaseYear());
        }
        if (!StringUtils.isEmpty(caseRequest.getCases().getFilingNumber())) {
            oldData.setFilingNumber(caseRequest.getCases().getFilingNumber());
        }
        if (!StringUtils.isEmpty(caseRequest.getCases().getFilingDate())) {
            oldData.setFilingDate(caseRequest.getCases().getFilingDate());
        }
        if (!StringUtils.isEmpty(caseRequest.getCases().getRegistrationDate())) {
            oldData.setRegistrationDate(caseRequest.getCases().getRegistrationDate());
        }
        if (!StringUtils.isEmpty(caseRequest.getCases().getCaseSummary())) {
            oldData.setCaseSummary(caseRequest.getCases().getCaseSummary());
        }
        if (!StringUtils.isEmpty(caseRequest.getCases().getArisingDetails())) {
            oldData.setArisingDetails(caseRequest.getCases().getArisingDetails());
        }
        if (!StringUtils.isEmpty(caseRequest.getCases().getPolicyOrNonPolicyMatter())) {
            oldData.setPolicyOrNonPolicyMatter(caseRequest.getCases().getPolicyOrNonPolicyMatter());
        }
        if (!StringUtils.isEmpty(caseRequest.getCases().getApplicationNumber())) {
            oldData.setApplicationNumber(caseRequest.getCases().getApplicationNumber());
        }
        if (!StringUtils.isEmpty(caseRequest.getCases().getIsCaseNumberCorrect())) {
            oldData.setIsCaseNumberCorrect(caseRequest.getCases().getIsCaseNumberCorrect());
        }
        if (!StringUtils.isEmpty(caseRequest.getCases().getCaseStatus())) {
            oldData.setCaseStatus(caseRequest.getCases().getCaseStatus());
        }
        if (!StringUtils.isEmpty(caseRequest.getCases().getFirstHearingDate())) {
            oldData.setFirstHearingDate(caseRequest.getCases().getFirstHearingDate());
        }
        if (!StringUtils.isEmpty(caseRequest.getCases().getPreviousHearingDate())) {
            oldData.setPreviousHearingDate(caseRequest.getCases().getPreviousHearingDate());
        }
        if (!StringUtils.isEmpty(caseRequest.getCases().getNextHearingDate())) {
            oldData.setNextHearingDate(caseRequest.getCases().getPreviousHearingDate());
        }
        if (!StringUtils.isEmpty(caseRequest.getCases().getCaseStage())) {
            oldData.setCaseStage(caseRequest.getCases().getCaseStage());
        }
        if (!StringUtils.isEmpty(caseRequest.getCases().getCaseSubStage())) {
            oldData.setCaseSubStage(caseRequest.getCases().getCaseSubStage());
        }
        if (Objects.nonNull(caseRequest.getCases().getCaseFlag())) {
            if (!StringUtils.isEmpty(caseRequest.getCases().getCaseFlag())) {
                List<String> uuids = new ArrayList<>();
                uuids.add(caseRequest.getRequestInfo().getUserInfo().getUuid());
                if (commonUtils.isUserMO(uuids, caseRequest.getCases().getTenantId(), "caseFlag")) {
                    oldData.setCaseFlag(caseRequest.getCases().getCaseFlag());
                }
            }
        }
        if (!StringUtils.isEmpty(caseRequest.getCases().getDepartmentName())) {
            oldData.setDepartmentName(caseRequest.getCases().getDepartmentName());
        }
        if (!StringUtils.isEmpty(caseRequest.getCases().getRecommendOIC())) {
            oldData.setRecommendOIC(caseRequest.getCases().getRecommendOIC());
        }
        if (!StringUtils.isEmpty(caseRequest.getCases().getRemarks())) {
            oldData.setRemarks(caseRequest.getCases().getRemarks());
        }
        if (!StringUtils.isEmpty(caseRequest.getCases().getAssignedOfficerId())) {
            oldData.setAssignedOfficerId(caseRequest.getCases().getAssignedOfficerId());
        }
        if (!StringUtils.isEmpty(caseRequest.getCases().getAdditionalDetails())) {
            oldData.setAdditionalDetails(caseRequest.getCases().getAdditionalDetails());
        }
        if (!StringUtils.isEmpty(caseRequest.getCases().getStatus())) {
            oldData.setStatus(caseRequest.getCases().getStatus());
        }
        //        Setting Petitioner Details
        if (!StringUtils.isEmpty(caseRequest.getCases().getPetitioner())) {
            if (!StringUtils.isEmpty(caseRequest.getCases().getPetitioner().getCaseId())) {
                oldData.getPetitioner().setCaseId(caseRequest.getCases().getPetitioner().getCaseId());
            }
            if (!StringUtils.isEmpty(caseRequest.getCases().getPetitioner().getFirstName())) {
                oldData.getPetitioner().setFirstName(caseRequest.getCases().getPetitioner().getFirstName());
            }
            if (!StringUtils.isEmpty(caseRequest.getCases().getPetitioner().getLastName())) {
                oldData.getPetitioner().setLastName(caseRequest.getCases().getPetitioner().getLastName());
            }
            if (!StringUtils.isEmpty(caseRequest.getCases().getPetitioner().getGender())) {
                oldData.getPetitioner().setGender(caseRequest.getCases().getPetitioner().getGender());
            }
            if (!StringUtils.isEmpty(caseRequest.getCases().getPetitioner().getPetitionerType())) {
                oldData.getPetitioner().setPetitionerType(caseRequest.getCases().getPetitioner().getPetitionerType());
            }
            if (!StringUtils.isEmpty(caseRequest.getCases().getPetitioner().getAddress())) {
                oldData.getPetitioner().setAddress(caseRequest.getCases().getPetitioner().getAddress());
            }
            if (!StringUtils.isEmpty(caseRequest.getCases().getPetitioner().getDepartmentName())) {
                oldData.getPetitioner().setDepartmentName(caseRequest.getCases().getPetitioner().getDepartmentName());
            }
            if (!StringUtils.isEmpty(caseRequest.getCases().getPetitioner().getContactNumber())) {
                oldData.getPetitioner().setContactNumber(caseRequest.getCases().getPetitioner().getContactNumber());
            }
            if (!StringUtils.isEmpty(caseRequest.getCases().getPetitioner().getPartyType())) {
                oldData.getPetitioner().setPartyType(caseRequest.getCases().getPetitioner().getPartyType());
            }
            if (!StringUtils.isEmpty(caseRequest.getCases().getPetitioner().getStatus())) {
                oldData.getPetitioner().setStatus(caseRequest.getCases().getPetitioner().getStatus());
            }
            //Setting Data For Petitioner Advocate
            if (!StringUtils.isEmpty(caseRequest.getCases().getPetitioner().getAdvocate())) {
                if (!StringUtils.isEmpty(caseRequest.getCases().getPetitioner().getAdvocate().getPartyId())) {
                    oldData.getPetitioner().getAdvocate().setPartyId(caseRequest.getCases().getPetitioner().getAdvocate().getPartyId());
                }
                if (!StringUtils.isEmpty(caseRequest.getCases().getPetitioner().getAdvocate().getHearingId())) {
                    oldData.getPetitioner().getAdvocate().setHearingId(caseRequest.getCases().getPetitioner().getAdvocate().getHearingId());
                }
                if (!StringUtils.isEmpty(caseRequest.getCases().getPetitioner().getAdvocate().getFirstName())) {
                    oldData.getPetitioner().getAdvocate().setFirstName(caseRequest.getCases().getPetitioner().getAdvocate().getFirstName());
                }
                if (!StringUtils.isEmpty(caseRequest.getCases().getPetitioner().getAdvocate().getLastName())) {
                    oldData.getPetitioner().getAdvocate().setLastName(caseRequest.getCases().getPetitioner().getAdvocate().getLastName());
                }
                if (!StringUtils.isEmpty(caseRequest.getCases().getPetitioner().getAdvocate().getContactNumber())) {
                    oldData.getPetitioner().getAdvocate().setContactNumber(caseRequest.getCases().getPetitioner().getAdvocate().getContactNumber());
                }
                if (!StringUtils.isEmpty(caseRequest.getCases().getPetitioner().getAdvocate().getPartyType())) {
                    oldData.getPetitioner().getAdvocate().setPartyType(caseRequest.getCases().getPetitioner().getAdvocate().getPartyType());
                }
                if (!StringUtils.isEmpty(caseRequest.getCases().getPetitioner().getAdvocate().getStatus())) {
                    oldData.getPetitioner().getAdvocate().setStatus(caseRequest.getCases().getPetitioner().getAdvocate().getStatus());
                }
            }
        }
        //       Setting Respondent details
        if (!StringUtils.isEmpty(caseRequest.getCases().getRespondent())) {
            if (!StringUtils.isEmpty(caseRequest.getCases().getRespondent().getCaseId())) {
                oldData.getRespondent().setCaseId(caseRequest.getCases().getRespondent().getCaseId());
            }
            if (!StringUtils.isEmpty(caseRequest.getCases().getRespondent().getFirstName())) {
                oldData.getRespondent().setFirstName(caseRequest.getCases().getRespondent().getFirstName());
            }
            if (!StringUtils.isEmpty(caseRequest.getCases().getRespondent().getLastName())) {
                oldData.getRespondent().setLastName(caseRequest.getCases().getRespondent().getLastName());
            }
            if (!StringUtils.isEmpty(caseRequest.getCases().getRespondent().getGender())) {
                oldData.getRespondent().setGender(caseRequest.getCases().getRespondent().getGender());
            }
            if (!StringUtils.isEmpty(caseRequest.getCases().getRespondent().getPetitionerType())) {
                oldData.getRespondent().setPetitionerType(caseRequest.getCases().getRespondent().getPetitionerType());
            }
            if (!StringUtils.isEmpty(caseRequest.getCases().getRespondent().getAddress())) {
                oldData.getRespondent().setAddress(caseRequest.getCases().getRespondent().getAddress());
            }
            if (!StringUtils.isEmpty(caseRequest.getCases().getRespondent().getDepartmentName())) {
                oldData.getRespondent().setDepartmentName(caseRequest.getCases().getRespondent().getDepartmentName());
            }
            if (!StringUtils.isEmpty(caseRequest.getCases().getRespondent().getContactNumber())) {
                oldData.getRespondent().setContactNumber(caseRequest.getCases().getRespondent().getContactNumber());
            }
            if (!StringUtils.isEmpty(caseRequest.getCases().getRespondent().getPartyType())) {
                oldData.getRespondent().setPartyType(caseRequest.getCases().getRespondent().getPartyType());
            }
            if (!StringUtils.isEmpty(caseRequest.getCases().getRespondent().getStatus())) {
                oldData.getRespondent().setStatus(caseRequest.getCases().getRespondent().getStatus());
            }
            //Setting Data For Respondent Advocate
            if (!StringUtils.isEmpty(caseRequest.getCases().getRespondent().getAdvocate())) {
                if (!StringUtils.isEmpty(caseRequest.getCases().getPetitioner().getAdvocate().getPartyId())) {
                    oldData.getRespondent().getAdvocate().setPartyId(caseRequest.getCases().getRespondent().getAdvocate().getPartyId());
                }
                if (!StringUtils.isEmpty(caseRequest.getCases().getRespondent().getAdvocate().getHearingId())) {
                    oldData.getRespondent().getAdvocate().setHearingId(caseRequest.getCases().getRespondent().getAdvocate().getHearingId());
                }
                if (!StringUtils.isEmpty(caseRequest.getCases().getRespondent().getAdvocate().getFirstName())) {
                    oldData.getRespondent().getAdvocate().setFirstName(caseRequest.getCases().getRespondent().getAdvocate().getFirstName());
                }
                if (!StringUtils.isEmpty(caseRequest.getCases().getRespondent().getAdvocate().getLastName())) {
                    oldData.getRespondent().getAdvocate().setLastName(caseRequest.getCases().getRespondent().getAdvocate().getLastName());
                }

                if (!StringUtils.isEmpty(caseRequest.getCases().getRespondent().getAdvocate().getContactNumber())) {
                    oldData.getRespondent().getAdvocate().setContactNumber(caseRequest.getCases().getRespondent().getAdvocate().getContactNumber());
                }
                if (!StringUtils.isEmpty(caseRequest.getCases().getRespondent().getAdvocate().getPartyType())) {
                    oldData.getRespondent().getAdvocate().setPartyType(caseRequest.getCases().getRespondent().getAdvocate().getPartyType());
                }
                if (!StringUtils.isEmpty(caseRequest.getCases().getRespondent().getAdvocate().getStatus())) {
                    oldData.getRespondent().getAdvocate().setStatus(caseRequest.getCases().getRespondent().getAdvocate().getStatus());
                }
            }
        }
        //setting act details
        if (!StringUtils.isEmpty(caseRequest.getCases().getAct())) {
            if (!StringUtils.isEmpty(caseRequest.getCases().getAct().getCaseId())) {
                oldData.getAct().setCaseId(caseRequest.getCases().getAct().getCaseId());
            }
            if (!StringUtils.isEmpty(caseRequest.getCases().getAct().getActName())) {
                oldData.getAct().setActName(caseRequest.getCases().getAct().getActName());
            }
            if (!StringUtils.isEmpty(caseRequest.getCases().getAct().getSectionNumber())) {
                oldData.getAct().setSectionNumber(caseRequest.getCases().getAct().getSectionNumber());
            }
            if (!StringUtils.isEmpty(caseRequest.getCases().getAct().getStatus())) {
                oldData.getAct().setStatus(caseRequest.getCases().getAct().getStatus());
            }
        }
        //setting documents details
        if (!StringUtils.isEmpty(caseRequest.getCases().getDocuments())) {
            List<Document> documentList = caseRequest.getCases().getDocuments();
            for (Document document : documentList) {
                for (Document oldDocData : oldData.getDocuments()) {
                    //                    oldData.getDocuments().forEach(oldDocData -> {
                    if (oldDocData.getId().equalsIgnoreCase(document.getId())) {
                        if (!StringUtils.isEmpty(document.getCaseId())) {
                            oldDocData.setCaseId(document.getCaseId());
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
        request.setCases(oldData);
        caseEnrichmentService.enrichCaseUpdateRequest(request);
        return request;
    }

    public ProcessInstanceRequest getWfForCaseCreate(CaseRequest request, CreationReason creationReasonForWorkflow) {

        Case aCase = request.getCases();
        ProcessInstance wf = null != aCase.getWorkflow() ? aCase.getWorkflow() : new ProcessInstance();
        wf.setBusinessId(aCase.getId());
        switch (creationReasonForWorkflow) {
            case CREATE:
                wf.setBusinessService(ilmsConfiguration.getCreatePTWfName());
                wf.setModuleName(ilmsConfiguration.getPropertyModuleName());
                wf.setAction("Create");
                wf.setTenantId(request.getCases().getTenantId());
                break;

            case UPDATE:
                String caseId = request.getCases().getId();
                CaseSearchCriteria criteria = CaseSearchCriteria.builder().id(Collections.singletonList(caseId)).build();
                CaseResponse caseResponse = caseRepository.getILMSCaseData(criteria);
                String tenantId = caseResponse.getCases().get(0).getTenantId();
                wf.setTenantId(tenantId);
                break;

            default:
                break;
        }
        aCase.setWorkflow(wf);
        return ProcessInstanceRequest.builder().processInstances(Arrays.asList(wf)).requestInfo(request.getRequestInfo()).build();
    }
}
