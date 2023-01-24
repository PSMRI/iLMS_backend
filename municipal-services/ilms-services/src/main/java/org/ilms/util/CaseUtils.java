package org.ilms.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.egov.common.contract.request.User;
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
            return AuditDetails.builder().createdBy(by).createdTime(time).build();
        } else {
            return AuditDetails.builder().lastModifiedBy(by).lastModifiedTime(time).build();
        }
    }

    public CaseRequest prepareObjectMapperForUpdate(Case oldData, CaseRequest caseRequest) {
        final CaseRequest request = new CaseRequest();

        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getTenantId())) {
            oldData.setTenantId(caseRequest.getCaseObj().getTenantId());
        }
        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getCaseNumber())) {
            oldData.setCaseNumber(caseRequest.getCaseObj().getCaseNumber());
        }
        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getCnrNumber())) {
            oldData.setCnrNumber(caseRequest.getCaseObj().getCnrNumber());
        }
        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getParentCaseId())) {
            oldData.setParentCaseId(caseRequest.getCaseObj().getParentCaseId());
        }
        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getCaseHierarchy())) {
            oldData.setCaseHierarchy(caseRequest.getCaseObj().getCaseHierarchy());
        }
        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getCaseType())) {
            oldData.setCaseType(caseRequest.getCaseObj().getCaseType());
        }
        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getCaseCategory())) {
            oldData.setCaseCategory(caseRequest.getCaseObj().getCaseCategory());
        }
        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getCaseYear())) {
            oldData.setCaseYear(caseRequest.getCaseObj().getCaseYear());
        }
        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getFilingNumber())) {
            oldData.setFilingNumber(caseRequest.getCaseObj().getFilingNumber());
        }
        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getFilingDate())) {
            oldData.setFilingDate(caseRequest.getCaseObj().getFilingDate());
        }
        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getRegistrationDate())) {
            oldData.setRegistrationDate(caseRequest.getCaseObj().getRegistrationDate());
        }
        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getCaseSummary())) {
            oldData.setCaseSummary(caseRequest.getCaseObj().getCaseSummary());
        }
        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getArisingDetails())) {
            oldData.setArisingDetails(caseRequest.getCaseObj().getArisingDetails());
        }
        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getPolicyOrNonPolicyMatter())) {
            oldData.setPolicyOrNonPolicyMatter(caseRequest.getCaseObj().getPolicyOrNonPolicyMatter());
        }
        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getApplicationNumber())) {
            oldData.setApplicationNumber(caseRequest.getCaseObj().getApplicationNumber());
        }
        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getIsCaseNumberCorrect())) {
            oldData.setIsCaseNumberCorrect(caseRequest.getCaseObj().getIsCaseNumberCorrect());
        }
        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getCaseStatus())) {
            oldData.setCaseStatus(caseRequest.getCaseObj().getCaseStatus());
        }
        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getFirstHearingDate())) {
            oldData.setFirstHearingDate(caseRequest.getCaseObj().getFirstHearingDate());
        }
        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getPreviousHearingDate())) {
            oldData.setPreviousHearingDate(caseRequest.getCaseObj().getPreviousHearingDate());
        }
        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getNextHearingDate())) {
            oldData.setNextHearingDate(caseRequest.getCaseObj().getPreviousHearingDate());
        }
        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getCaseStage())) {
            oldData.setCaseStage(caseRequest.getCaseObj().getCaseStage());
        }
        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getCaseSubStage())) {
            oldData.setCaseSubStage(caseRequest.getCaseObj().getCaseSubStage());
        }
        if (Objects.nonNull(caseRequest.getCaseObj().getCaseFlag())) {
            if (!StringUtils.isEmpty(caseRequest.getCaseObj().getCaseFlag())) {
                List<String> uuids = new ArrayList<>();
                uuids.add(caseRequest.getRequestInfo().getUserInfo().getUuid());
                if (commonUtils.isUserMO(uuids, caseRequest.getCaseObj().getTenantId(), "caseFlag")) {
                    oldData.setCaseFlag(caseRequest.getCaseObj().getCaseFlag());
                }
            }
        }
        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getDepartmentName())) {
            oldData.setDepartmentName(caseRequest.getCaseObj().getDepartmentName());
        }
        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getRecommendOIC())) {
            oldData.setRecommendOIC(caseRequest.getCaseObj().getRecommendOIC());
        }
        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getRemarks())) {
            oldData.setRemarks(caseRequest.getCaseObj().getRemarks());
        }
        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getAssignedOfficerId())) {
            List<String> uuids = new ArrayList<>();
            uuids.add(caseRequest.getCaseObj().getAssignedOfficerId());
            if (commonUtils.isUserExists(uuids, caseRequest.getCaseObj().getTenantId())) {
                oldData.setAssignedOfficerId(caseRequest.getCaseObj().getAssignedOfficerId());
            }
        }
        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getAdditionalDetails())) {
            oldData.setAdditionalDetails(caseRequest.getCaseObj().getAdditionalDetails());
        }
        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getStatus())) {
            oldData.setStatus(caseRequest.getCaseObj().getStatus());
        }
        //        Setting Petitioner Details
        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getPetitioner())) {
            if (!StringUtils.isEmpty(caseRequest.getCaseObj().getPetitioner().getCaseId())) {
                oldData.getPetitioner().setCaseId(caseRequest.getCaseObj().getPetitioner().getCaseId());
            }
            if (!StringUtils.isEmpty(caseRequest.getCaseObj().getPetitioner().getFirstName())) {
                oldData.getPetitioner().setFirstName(caseRequest.getCaseObj().getPetitioner().getFirstName());
            }
            if (!StringUtils.isEmpty(caseRequest.getCaseObj().getPetitioner().getLastName())) {
                oldData.getPetitioner().setLastName(caseRequest.getCaseObj().getPetitioner().getLastName());
            }
            if (!StringUtils.isEmpty(caseRequest.getCaseObj().getPetitioner().getGender())) {
                oldData.getPetitioner().setGender(caseRequest.getCaseObj().getPetitioner().getGender());
            }
            if (!StringUtils.isEmpty(caseRequest.getCaseObj().getPetitioner().getPetitionerType())) {
                oldData.getPetitioner().setPetitionerType(caseRequest.getCaseObj().getPetitioner().getPetitionerType());
            }
            if (!StringUtils.isEmpty(caseRequest.getCaseObj().getPetitioner().getAddress())) {
                oldData.getPetitioner().setAddress(caseRequest.getCaseObj().getPetitioner().getAddress());
            }
            if (!StringUtils.isEmpty(caseRequest.getCaseObj().getPetitioner().getDepartmentName())) {
                oldData.getPetitioner().setDepartmentName(caseRequest.getCaseObj().getPetitioner().getDepartmentName());
            }
            if (!StringUtils.isEmpty(caseRequest.getCaseObj().getPetitioner().getContactNumber())) {
                oldData.getPetitioner().setContactNumber(caseRequest.getCaseObj().getPetitioner().getContactNumber());
            }
            if (!StringUtils.isEmpty(caseRequest.getCaseObj().getPetitioner().getPartyType())) {
                oldData.getPetitioner().setPartyType(caseRequest.getCaseObj().getPetitioner().getPartyType());
            }
            if (!StringUtils.isEmpty(caseRequest.getCaseObj().getPetitioner().getStatus())) {
                oldData.getPetitioner().setStatus(caseRequest.getCaseObj().getPetitioner().getStatus());
            }
            //Setting Data For Petitioner Advocate
            if (!StringUtils.isEmpty(caseRequest.getCaseObj().getPetitioner().getAdvocate())) {
                if (!StringUtils.isEmpty(caseRequest.getCaseObj().getPetitioner().getAdvocate().getPartyId())) {
                    oldData.getPetitioner().getAdvocate().setPartyId(caseRequest.getCaseObj().getPetitioner().getAdvocate().getPartyId());
                }
                if (!StringUtils.isEmpty(caseRequest.getCaseObj().getPetitioner().getAdvocate().getHearingId())) {
                    oldData.getPetitioner().getAdvocate().setHearingId(caseRequest.getCaseObj().getPetitioner().getAdvocate().getHearingId());
                }
                if (!StringUtils.isEmpty(caseRequest.getCaseObj().getPetitioner().getAdvocate().getFirstName())) {
                    oldData.getPetitioner().getAdvocate().setFirstName(caseRequest.getCaseObj().getPetitioner().getAdvocate().getFirstName());
                }
                if (!StringUtils.isEmpty(caseRequest.getCaseObj().getPetitioner().getAdvocate().getLastName())) {
                    oldData.getPetitioner().getAdvocate().setLastName(caseRequest.getCaseObj().getPetitioner().getAdvocate().getLastName());
                }
                if (!StringUtils.isEmpty(caseRequest.getCaseObj().getPetitioner().getAdvocate().getContactNumber())) {
                    oldData.getPetitioner().getAdvocate().setContactNumber(caseRequest.getCaseObj().getPetitioner().getAdvocate().getContactNumber());
                }
                if (!StringUtils.isEmpty(caseRequest.getCaseObj().getPetitioner().getAdvocate().getPartyType())) {
                    oldData.getPetitioner().getAdvocate().setPartyType(caseRequest.getCaseObj().getPetitioner().getAdvocate().getPartyType());
                }
                if (!StringUtils.isEmpty(caseRequest.getCaseObj().getPetitioner().getAdvocate().getStatus())) {
                    oldData.getPetitioner().getAdvocate().setStatus(caseRequest.getCaseObj().getPetitioner().getAdvocate().getStatus());
                }
            }
        }
        //       Setting Respondent details
        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getRespondent())) {
            if (!StringUtils.isEmpty(caseRequest.getCaseObj().getRespondent().getCaseId())) {
                oldData.getRespondent().setCaseId(caseRequest.getCaseObj().getRespondent().getCaseId());
            }
            if (!StringUtils.isEmpty(caseRequest.getCaseObj().getRespondent().getFirstName())) {
                oldData.getRespondent().setFirstName(caseRequest.getCaseObj().getRespondent().getFirstName());
            }
            if (!StringUtils.isEmpty(caseRequest.getCaseObj().getRespondent().getLastName())) {
                oldData.getRespondent().setLastName(caseRequest.getCaseObj().getRespondent().getLastName());
            }
            if (!StringUtils.isEmpty(caseRequest.getCaseObj().getRespondent().getGender())) {
                oldData.getRespondent().setGender(caseRequest.getCaseObj().getRespondent().getGender());
            }
            if (!StringUtils.isEmpty(caseRequest.getCaseObj().getRespondent().getPetitionerType())) {
                oldData.getRespondent().setPetitionerType(caseRequest.getCaseObj().getRespondent().getPetitionerType());
            }
            if (!StringUtils.isEmpty(caseRequest.getCaseObj().getRespondent().getAddress())) {
                oldData.getRespondent().setAddress(caseRequest.getCaseObj().getRespondent().getAddress());
            }
            if (!StringUtils.isEmpty(caseRequest.getCaseObj().getRespondent().getDepartmentName())) {
                oldData.getRespondent().setDepartmentName(caseRequest.getCaseObj().getRespondent().getDepartmentName());
            }
            if (!StringUtils.isEmpty(caseRequest.getCaseObj().getRespondent().getContactNumber())) {
                oldData.getRespondent().setContactNumber(caseRequest.getCaseObj().getRespondent().getContactNumber());
            }
            if (!StringUtils.isEmpty(caseRequest.getCaseObj().getRespondent().getPartyType())) {
                oldData.getRespondent().setPartyType(caseRequest.getCaseObj().getRespondent().getPartyType());
            }
            if (!StringUtils.isEmpty(caseRequest.getCaseObj().getRespondent().getStatus())) {
                oldData.getRespondent().setStatus(caseRequest.getCaseObj().getRespondent().getStatus());
            }
            //Setting Data For Respondent Advocate
            if (!StringUtils.isEmpty(caseRequest.getCaseObj().getRespondent().getAdvocate())) {
                if (!StringUtils.isEmpty(caseRequest.getCaseObj().getPetitioner().getAdvocate().getPartyId())) {
                    oldData.getRespondent().getAdvocate().setPartyId(caseRequest.getCaseObj().getRespondent().getAdvocate().getPartyId());
                }
                if (!StringUtils.isEmpty(caseRequest.getCaseObj().getRespondent().getAdvocate().getHearingId())) {
                    oldData.getRespondent().getAdvocate().setHearingId(caseRequest.getCaseObj().getRespondent().getAdvocate().getHearingId());
                }
                if (!StringUtils.isEmpty(caseRequest.getCaseObj().getRespondent().getAdvocate().getFirstName())) {
                    oldData.getRespondent().getAdvocate().setFirstName(caseRequest.getCaseObj().getRespondent().getAdvocate().getFirstName());
                }
                if (!StringUtils.isEmpty(caseRequest.getCaseObj().getRespondent().getAdvocate().getLastName())) {
                    oldData.getRespondent().getAdvocate().setLastName(caseRequest.getCaseObj().getRespondent().getAdvocate().getLastName());
                }

                if (!StringUtils.isEmpty(caseRequest.getCaseObj().getRespondent().getAdvocate().getContactNumber())) {
                    oldData.getRespondent().getAdvocate().setContactNumber(caseRequest.getCaseObj().getRespondent().getAdvocate().getContactNumber());
                }
                if (!StringUtils.isEmpty(caseRequest.getCaseObj().getRespondent().getAdvocate().getPartyType())) {
                    oldData.getRespondent().getAdvocate().setPartyType(caseRequest.getCaseObj().getRespondent().getAdvocate().getPartyType());
                }
                if (!StringUtils.isEmpty(caseRequest.getCaseObj().getRespondent().getAdvocate().getStatus())) {
                    oldData.getRespondent().getAdvocate().setStatus(caseRequest.getCaseObj().getRespondent().getAdvocate().getStatus());
                }
            }
        }
        //setting act details
        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getAct())) {
            if (!StringUtils.isEmpty(caseRequest.getCaseObj().getAct().getCaseId())) {
                oldData.getAct().setCaseId(caseRequest.getCaseObj().getAct().getCaseId());
            }
            if (!StringUtils.isEmpty(caseRequest.getCaseObj().getAct().getActName())) {
                oldData.getAct().setActName(caseRequest.getCaseObj().getAct().getActName());
            }
            if (!StringUtils.isEmpty(caseRequest.getCaseObj().getAct().getSectionNumber())) {
                oldData.getAct().setSectionNumber(caseRequest.getCaseObj().getAct().getSectionNumber());
            }
            if (!StringUtils.isEmpty(caseRequest.getCaseObj().getAct().getStatus())) {
                oldData.getAct().setStatus(caseRequest.getCaseObj().getAct().getStatus());
            }
        }
        //setting documents details
        if (!StringUtils.isEmpty(caseRequest.getCaseObj().getDocuments())) {
            List<Document> documentList = caseRequest.getCaseObj().getDocuments();
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
        request.setCaseObj(oldData);
        caseEnrichmentService.enrichCaseUpdateRequest(request);
        return request;
    }

    public ProcessInstanceRequest getWfForCaseCreate(CaseRequest request, CreationReason creationReasonForWorkflow) {

        Case aCase = request.getCaseObj();
        ProcessInstance wf = null != aCase.getWorkflow() ? aCase.getWorkflow() : new ProcessInstance();
        wf.setBusinessId(aCase.getId());

        switch (creationReasonForWorkflow) {
            case CREATE:
                wf.setBusinessService(ilmsConfiguration.getCreatePTWfName());
                wf.setModuleName(ilmsConfiguration.getPropertyModuleName());

                wf.setAction("CREATE");
                wf.setTenantId(request.getCaseObj().getTenantId());
                List<User> userList = new ArrayList<>();
                User user = new User();
                user.setUuid(request.getRequestInfo().getUserInfo().getUuid());
                userList.add(user);
                wf.setAssignes(userList);

                break;

            case UPDATE:
                String caseId = request.getCaseObj().getId();
                CaseSearchCriteria criteria = CaseSearchCriteria.builder().id(Collections.singletonList(caseId)).build();
                CaseResponse caseResponse = caseRepository.getILMSCaseData(criteria);
                String tenantId = caseResponse.getCaseList().get(0).getTenantId();
                wf.setTenantId(tenantId);
                wf.setAssignes(request.getCaseObj().getWorkflow().getAssignes());
                break;

            default:
                break;
        }
        aCase.setWorkflow(wf);
        return ProcessInstanceRequest.builder().processInstances(Arrays.asList(wf)).requestInfo(request.getRequestInfo()).build();
    }
}
