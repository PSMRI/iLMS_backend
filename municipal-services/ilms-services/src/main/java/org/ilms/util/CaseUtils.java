package org.ilms.util;

import java.util.List;
import org.ilms.service.EnrichmentService;
import org.ilms.web.model.AuditDetails;
import org.ilms.web.model.Document;
import org.ilms.web.model.ILMSCase;
import org.ilms.web.model.ILMSCaseRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class CaseUtils {
    @Autowired
    private EnrichmentService enrichmentService;

    public AuditDetails getAuditDetails(String by, Boolean isCreate) {
        Long time = System.currentTimeMillis();
        if (isCreate) {
            return AuditDetails.builder().createdBy(by).lastModifiedBy(by).createdTime(time).lastModifiedTime(time).build();
        } else {
            return AuditDetails.builder().lastModifiedBy(by).lastModifiedTime(time).build();
        }
    }

    public ILMSCaseRequest prepareObjectMapperForUpdate(ILMSCase oldData, ILMSCaseRequest ilmsCaseRequest) {
        final ILMSCaseRequest request = new ILMSCaseRequest();

        if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getTenantId())) {
            oldData.setTenantId(ilmsCaseRequest.getIlmsCase().getTenantId());
        }
        if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getCaseNumber())) {
            oldData.setCaseNumber(ilmsCaseRequest.getIlmsCase().getCaseNumber());
        }
        if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getCnrNumber())) {
            oldData.setCnrNumber(ilmsCaseRequest.getIlmsCase().getCnrNumber());
        }
        if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getParentCaseId())) {
            oldData.setParentCaseId(ilmsCaseRequest.getIlmsCase().getParentCaseId());
        }
        if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getCaseHierarchy())) {
            oldData.setCaseHierarchy(ilmsCaseRequest.getIlmsCase().getCaseHierarchy());
        }
        if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getCaseType())) {
            oldData.setCaseType(ilmsCaseRequest.getIlmsCase().getCaseType());
        }
        if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getCaseCategory())) {
            oldData.setCaseCategory(ilmsCaseRequest.getIlmsCase().getCaseCategory());
        }
        if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getCaseYear())) {
            oldData.setCaseYear(ilmsCaseRequest.getIlmsCase().getCaseYear());
        }
        if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getFilingNumber())) {
            oldData.setFilingNumber(ilmsCaseRequest.getIlmsCase().getFilingNumber());
        }
        if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getFilingDate())) {
            oldData.setFilingDate(ilmsCaseRequest.getIlmsCase().getFilingDate());
        }
        if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getRegistrationDate())) {
            oldData.setRegistrationDate(ilmsCaseRequest.getIlmsCase().getRegistrationDate());
        }
        if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getCaseSummary())) {
            oldData.setCaseSummary(ilmsCaseRequest.getIlmsCase().getCaseSummary());
        }
        if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getArisingDetails())) {
            oldData.setArisingDetails(ilmsCaseRequest.getIlmsCase().getArisingDetails());
        }
        if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getPolicyOrNonPolicyMatter())) {
            oldData.setPolicyOrNonPolicyMatter(ilmsCaseRequest.getIlmsCase().getPolicyOrNonPolicyMatter());
        }
        if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getApplicationNumber())) {
            oldData.setApplicationNumber(ilmsCaseRequest.getIlmsCase().getApplicationNumber());
        }
        if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getIsCaseNumberCorrect())) {
            oldData.setIsCaseNumberCorrect(ilmsCaseRequest.getIlmsCase().getIsCaseNumberCorrect());
        }
        if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getCaseStatus())) {
            oldData.setCaseStatus(ilmsCaseRequest.getIlmsCase().getCaseStatus());
        }
        if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getFirstHearingDate())) {
            oldData.setFirstHearingDate(ilmsCaseRequest.getIlmsCase().getFirstHearingDate());
        }
        if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getPreviousHearingDate())) {
            oldData.setPreviousHearingDate(ilmsCaseRequest.getIlmsCase().getPreviousHearingDate());
        }
        if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getNextHearingDate())) {
            oldData.setNextHearingDate(ilmsCaseRequest.getIlmsCase().getPreviousHearingDate());
        }
        if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getCaseStage())) {
            oldData.setCaseStage(ilmsCaseRequest.getIlmsCase().getCaseStage());
        }
        if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getCaseSubStage())) {
            oldData.setCaseStage(ilmsCaseRequest.getIlmsCase().getCaseSubStage());
        }
        if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getCaseFlag())) {
            oldData.setCaseStage(ilmsCaseRequest.getIlmsCase().getCaseFlag());
        }
        if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getDepartmentName())) {
            oldData.setDepartmentName(ilmsCaseRequest.getIlmsCase().getDepartmentName());
        }
        if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getRecommendOIC())) {
            oldData.setRecommendOIC(ilmsCaseRequest.getIlmsCase().getRecommendOIC());
        }
        if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getRemarks())) {
            oldData.setRemarks(ilmsCaseRequest.getIlmsCase().getRemarks());
        }
        if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getAssignedOfficerId())) {
            oldData.setAssignedOfficerId(ilmsCaseRequest.getIlmsCase().getAssignedOfficerId());
        }
        if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getAdditionalDetails())) {
            oldData.setAdditionalDetails(ilmsCaseRequest.getIlmsCase().getAdditionalDetails());
        }
        if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getStatus())) {
            oldData.setStatus(ilmsCaseRequest.getIlmsCase().getStatus());
        }
        //        Setting Petitioner Details
        if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getPetitioner())) {
            if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getPetitioner().getCaseId())) {
                oldData.getPetitioner().setCaseId(ilmsCaseRequest.getIlmsCase().getPetitioner().getCaseId());
            }
            if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getPetitioner().getFirstName())) {
                oldData.getPetitioner().setFirstName(ilmsCaseRequest.getIlmsCase().getPetitioner().getFirstName());
            }
            if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getPetitioner().getLastName())) {
                oldData.getPetitioner().setLastName(ilmsCaseRequest.getIlmsCase().getPetitioner().getLastName());
            }
            if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getPetitioner().getGender())) {
                oldData.getPetitioner().setGender(ilmsCaseRequest.getIlmsCase().getPetitioner().getGender());
            }
            if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getPetitioner().getPetitionerType())) {
                oldData.getPetitioner().setPetitionerType(ilmsCaseRequest.getIlmsCase().getPetitioner().getPetitionerType());
            }
            if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getPetitioner().getAddress())) {
                oldData.getPetitioner().setAddress(ilmsCaseRequest.getIlmsCase().getPetitioner().getAddress());
            }
            if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getPetitioner().getDepartmentName())) {
                oldData.getPetitioner().setDepartmentName(ilmsCaseRequest.getIlmsCase().getPetitioner().getDepartmentName());
            }
            if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getPetitioner().getContactNumber())) {
                oldData.getPetitioner().setContactNumber(ilmsCaseRequest.getIlmsCase().getPetitioner().getContactNumber());
            }
            if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getPetitioner().getPartyType())) {
                oldData.getPetitioner().setPartyType(ilmsCaseRequest.getIlmsCase().getPetitioner().getPartyType());
            }
            if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getPetitioner().getStatus())) {
                oldData.getPetitioner().setStatus(ilmsCaseRequest.getIlmsCase().getPetitioner().getStatus());
            }
            //Setting Data For Petitioner Advocate
            if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getPetitioner().getAdvocate())) {
                if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getPetitioner().getAdvocate().getPartyId())) {
                    oldData.getPetitioner().getAdvocate().setPartyId(ilmsCaseRequest.getIlmsCase().getPetitioner().getAdvocate().getPartyId());
                }
                if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getPetitioner().getAdvocate().getHearingId())) {
                    oldData.getPetitioner().getAdvocate().setHearingId(ilmsCaseRequest.getIlmsCase().getPetitioner().getAdvocate().getHearingId());
                }
                if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getPetitioner().getAdvocate().getFirstName())) {
                    oldData.getPetitioner().getAdvocate().setFirstName(ilmsCaseRequest.getIlmsCase().getPetitioner().getAdvocate().getFirstName());
                }
                if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getPetitioner().getAdvocate().getLastName())) {
                    oldData.getPetitioner().getAdvocate().setLastName(ilmsCaseRequest.getIlmsCase().getPetitioner().getAdvocate().getLastName());
                }
                if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getPetitioner().getAdvocate().getContactNumber())) {
                    oldData.getPetitioner().getAdvocate()
                           .setContactNumber(ilmsCaseRequest.getIlmsCase().getPetitioner().getAdvocate().getContactNumber());
                }
                if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getPetitioner().getAdvocate().getPartyType())) {
                    oldData.getPetitioner().getAdvocate().setPartyType(ilmsCaseRequest.getIlmsCase().getPetitioner().getAdvocate().getPartyType());
                }
                if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getPetitioner().getAdvocate().getStatus())) {
                    oldData.getPetitioner().getAdvocate().setStatus(ilmsCaseRequest.getIlmsCase().getPetitioner().getAdvocate().getStatus());
                }
            }
        }
        //       Setting Respondent details
        if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getRespondent())) {
            if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getRespondent().getCaseId())) {
                oldData.getRespondent().setCaseId(ilmsCaseRequest.getIlmsCase().getRespondent().getCaseId());
            }
            if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getRespondent().getFirstName())) {
                oldData.getRespondent().setFirstName(ilmsCaseRequest.getIlmsCase().getRespondent().getFirstName());
            }
            if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getRespondent().getLastName())) {
                oldData.getRespondent().setLastName(ilmsCaseRequest.getIlmsCase().getRespondent().getLastName());
            }
            if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getRespondent().getGender())) {
                oldData.getRespondent().setGender(ilmsCaseRequest.getIlmsCase().getRespondent().getGender());
            }
            if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getRespondent().getPetitionerType())) {
                oldData.getRespondent().setPetitionerType(ilmsCaseRequest.getIlmsCase().getRespondent().getPetitionerType());
            }
            if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getRespondent().getAddress())) {
                oldData.getRespondent().setAddress(ilmsCaseRequest.getIlmsCase().getRespondent().getAddress());
            }
            if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getRespondent().getDepartmentName())) {
                oldData.getRespondent().setDepartmentName(ilmsCaseRequest.getIlmsCase().getRespondent().getDepartmentName());
            }
            if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getRespondent().getContactNumber())) {
                oldData.getRespondent().setContactNumber(ilmsCaseRequest.getIlmsCase().getRespondent().getContactNumber());
            }
            if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getRespondent().getPartyType())) {
                oldData.getRespondent().setPartyType(ilmsCaseRequest.getIlmsCase().getRespondent().getPartyType());
            }
            if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getRespondent().getStatus())) {
                oldData.getRespondent().setStatus(ilmsCaseRequest.getIlmsCase().getRespondent().getStatus());
            }
            //Setting Data For Respondent Advocate
            if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getRespondent().getAdvocate())) {
                if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getPetitioner().getAdvocate().getPartyId())) {
                    oldData.getRespondent().getAdvocate().setPartyId(ilmsCaseRequest.getIlmsCase().getRespondent().getAdvocate().getPartyId());
                }
                if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getRespondent().getAdvocate().getHearingId())) {
                    oldData.getRespondent().getAdvocate().setHearingId(ilmsCaseRequest.getIlmsCase().getRespondent().getAdvocate().getHearingId());
                }
                if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getRespondent().getAdvocate().getFirstName())) {
                    oldData.getRespondent().getAdvocate().setFirstName(ilmsCaseRequest.getIlmsCase().getRespondent().getAdvocate().getFirstName());
                }
                if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getRespondent().getAdvocate().getLastName())) {
                    oldData.getRespondent().getAdvocate().setLastName(ilmsCaseRequest.getIlmsCase().getRespondent().getAdvocate().getLastName());
                }

                if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getRespondent().getAdvocate().getContactNumber())) {
                    oldData.getRespondent().getAdvocate()
                           .setContactNumber(ilmsCaseRequest.getIlmsCase().getRespondent().getAdvocate().getContactNumber());
                }
                if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getRespondent().getAdvocate().getPartyType())) {
                    oldData.getRespondent().getAdvocate().setPartyType(ilmsCaseRequest.getIlmsCase().getRespondent().getAdvocate().getPartyType());
                }
                if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getRespondent().getAdvocate().getStatus())) {
                    oldData.getRespondent().getAdvocate().setStatus(ilmsCaseRequest.getIlmsCase().getRespondent().getAdvocate().getStatus());
                }
            }
        }
        //setting act details
        if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getAct())) {
            if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getAct().getCaseId())) {
                oldData.getAct().setCaseId(ilmsCaseRequest.getIlmsCase().getAct().getCaseId());
            }
            if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getAct().getActName())) {
                oldData.getAct().setActName(ilmsCaseRequest.getIlmsCase().getAct().getActName());
            }
            if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getAct().getSectionNumber())) {
                oldData.getAct().setSectionNumber(ilmsCaseRequest.getIlmsCase().getAct().getSectionNumber());
            }
            if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getAct().getStatus())) {
                oldData.getAct().setStatus(ilmsCaseRequest.getIlmsCase().getAct().getStatus());
            }
        }
        //setting documents details
        if (!StringUtils.isEmpty(ilmsCaseRequest.getIlmsCase().getDocuments())) {
            List<Document> documentList = ilmsCaseRequest.getIlmsCase().getDocuments();
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
        request.setIlmsCase(oldData);
        enrichmentService.enrichCaseUpdateRequest(request);
        return request;
    }
}
