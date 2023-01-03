package org.ilms.validator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.egov.tracer.model.CustomException;
import org.ilms.repository.CaseRepository;
import org.ilms.util.CommonUtils;
import org.ilms.util.ILMSConstants;
import org.ilms.util.ILMSErrorConstants;
import org.ilms.web.model.Case;
import org.ilms.web.model.CaseRequest;
import org.ilms.web.model.CaseResponse;
import org.ilms.web.model.CaseSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CaseValidator {
    @Autowired
    CommonUtils commonUtils;

    @Autowired
    CaseRepository caseRepository;

    private static Map<String, String> validateCodes(Case cases, Map<String, List<String>> codes, Map<String, String> errorMap) {

        if (cases.getCaseType() != null && !codes.get(ILMSConstants.MDMS_ILMS_CASE_TYPE).contains(cases.getCaseType())) {
            errorMap.put("Invalid CASE TYPE", "The CaseType '" + cases.getCaseType() + "' does not exists");
        }
        if (cases.getCaseStatus() != null && !codes.get(ILMSConstants.MDMS_ILMS_CASE_STATUS).contains(cases.getCaseStatus())) {
            errorMap.put("Invalid CASE Status", "The CaseStatus '" + cases.getCaseStatus() + "' does not exists");
        }
        if (cases.getCaseCategory() != null && !codes.get(ILMSConstants.MDMS_ILMS_CASE_CATEGORY).contains(cases.getCaseCategory())) {
            errorMap.put("Invalid CaseCategory", "The CaseCategory '" + cases.getCaseCategory() + "' does not exists");
        }
        if (cases.getCaseStage() != null && !codes.get(ILMSConstants.MDMS_ILMS_CASE_STAGE).contains(cases.getCaseStage())) {
            errorMap.put("Invalid CaseStage", "The CaseStage '" + cases.getCaseStage() + "' does not exists");
        }
        if (cases.getCaseSubStage() != null && !codes.get(ILMSConstants.MDMS_ILMS_SUB_STAGE).contains(cases.getCaseSubStage())) {
            errorMap.put("Invalid CaseSubStage", "The CaseSubStage '" + cases.getCaseSubStage() + "' does not exists");
        }
        if (cases.getPetitioner().getGender() != null && !codes.get(ILMSConstants.MDMS_ILMS_GENDER_TYPE)
                                                               .contains(cases.getPetitioner().getGender())) {
            errorMap.put("Invalid Gender", "The Gender '" + cases.getPetitioner().getGender() + "' does not exists");
        }
        if (cases.getRespondent().getGender() != null && !codes.get(ILMSConstants.MDMS_ILMS_GENDER_TYPE)
                                                               .contains(cases.getRespondent().getGender())) {
            errorMap.put("Invalid Gender", "The Gender '" + cases.getRespondent().getGender() + "' does not exists");
        }
        if (cases.getPetitioner().getPetitionerType() != null && !codes.get(ILMSConstants.MDMS_ILMS_PETITIONER_TYPE)
                                                                       .contains(cases.getPetitioner().getPetitionerType())) {
            errorMap.put("Invalid PetitionerType", "The PetitionerType '" + cases.getPetitioner().getPetitionerType() + "' does not exists");
        }
        if (cases.getDepartmentName() != null && !codes.get(ILMSConstants.MDMS_ILMS_DEPARTMENT_NAME).contains(cases.getDepartmentName())) {
            errorMap.put("Invalid DepartmentName", "The DepartmentName '" + cases.getDepartmentName() + "' does not exists");
        }
        if (cases.getRecommendOIC() != null && !codes.get(ILMSConstants.MDMS_ILMS_DEPARTMENT_IOC).contains(cases.getRecommendOIC())) {
            errorMap.put("Invalid RecommendOIC", "The RecommendOIC '" + cases.getRecommendOIC() + "' does not exists");
        }
        if (cases.getCaseFlag() != null && !codes.get(ILMSConstants.CASE_FLAG).contains(cases.getCaseFlag())) {
            errorMap.put("Invalid CaseFlag", "The CaseFlag '" + cases.getCaseFlag() + "' does not exists");
        }
        if (Objects.nonNull(cases.getDocuments())) {
            cases.getDocuments().forEach(document -> {
                if (document.getDocumentType() != null && !codes.get(ILMSConstants.MDMS_ILMS_DOCUMENT_CATEGORY)
                                                                .contains(document.getDocumentType())) {
                    errorMap.put("Invalid DocumentCategory", "The DocumentCategory '" + document.getDocumentType() + "' does not exists");
                }
            });
        }

        return errorMap;

    }

    public void validateCreate(CaseRequest caseRequest) {

        if (!StringUtils.isNotBlank(caseRequest.getCases().getTenantId())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                    "TenantId is mandatory [ " + caseRequest.getCases().getTenantId() + " ]");
        }
        if (!StringUtils.isNotBlank(caseRequest.getCases().getCaseNumber())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                    "caseNumber is mandatory [ " + caseRequest.getCases().getCaseNumber() + " ]");
        }
        if (!StringUtils.isNotBlank(caseRequest.getCases().getCnrNumber())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                    "CNRNumber is mandatory [ " + caseRequest.getCases().getCnrNumber() + " ]");
        }
        if (!StringUtils.isNotBlank(caseRequest.getCases().getCaseType())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                    "caseType is mandatory [ " + caseRequest.getCases().getCaseType() + " ]");
        }
        if (!StringUtils.isNotBlank(caseRequest.getCases().getCaseCategory())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                    "caseCategory is mandatory [ " + caseRequest.getCases().getCaseCategory() + " ]");
        }
        if (!StringUtils.isNotBlank(caseRequest.getCases().getCaseYear().toString())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                    "caseYear is mandatory [ " + caseRequest.getCases().getCaseYear() + " ]");
        }
        if (!StringUtils.isNotBlank(caseRequest.getCases().getFilingDate().toString())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                    "filingDate is mandatory [ " + caseRequest.getCases().getFilingDate() + " ]");
        }
        if (!StringUtils.isNotBlank(caseRequest.getCases().getRegistrationDate().toString())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                    "registrationDate is mandatory [ " + caseRequest.getCases().getRegistrationDate() + " ]");
        }
        if (!StringUtils.isNotBlank(caseRequest.getCases().getCaseSummary())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                    "caseSummary is mandatory [ " + caseRequest.getCases().getCaseSummary() + " ]");
        }
        if (!StringUtils.isNotBlank(caseRequest.getCases().getPolicyOrNonPolicyMatter())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                    "policyOrNonPolicyMatters is mandatory [ " + caseRequest.getCases().getPolicyOrNonPolicyMatter() + " ]");
        }
        if (!StringUtils.isNotBlank(caseRequest.getCases().getIsCaseNumberCorrect().toString())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                    "isCaseNumberCorrect is mandatory [ " + caseRequest.getCases().getIsCaseNumberCorrect() + " ]");
        }
        if (!StringUtils.isNotBlank(caseRequest.getCases().getCaseStatus())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                    "caseStatus is mandatory [ " + caseRequest.getCases().getCaseStatus() + " ]");
        }
        if (!StringUtils.isNotBlank(caseRequest.getCases().getFirstHearingDate().toString())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                    "firstHearingDate is mandatory [ " + caseRequest.getCases().getFirstHearingDate() + " ]");
        }
        if (!StringUtils.isNotBlank(caseRequest.getCases().getNextHearingDate().toString())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                    "nextHearingDate is mandatory [ " + caseRequest.getCases().getNextHearingDate() + " ]");
        }
        if (!StringUtils.isNotBlank(caseRequest.getCases().getCaseStage())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                    "caseStage is mandatory [ " + caseRequest.getCases().getCaseStage() + " ]");
        }
        if (!StringUtils.isNotBlank(caseRequest.getCases().getCaseSubStage())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                    "caseSubStage is mandatory [ " + caseRequest.getCases().getCaseSubStage() + " ]");
        }
        if (StringUtils.isNotBlank(caseRequest.getCases().getCaseFlag())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                    "caseFlag is not mandatory while creating the case [ " + caseRequest.getCases().getCaseFlag() + " ]");
        }
        if (!StringUtils.isNotBlank(caseRequest.getCases().getDepartmentName())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                    "DepartmentName is mandatory [ " + caseRequest.getCases().getDepartmentName() + " ]");
        }
        if (!StringUtils.isNotBlank(caseRequest.getCases().getRecommendOIC())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                    "recommendedOIC is mandatory [ " + caseRequest.getCases().getRecommendOIC() + " ]");
        }
        if (StringUtils.isNotBlank(caseRequest.getCases().getAssignedOfficerId())) {
            List<String> uuids = new ArrayList<>();
            uuids.add(caseRequest.getCases().getAssignedOfficerId());
            if (commonUtils.isUserDEC(uuids, caseRequest.getCases().getTenantId(), "AssignedOfficerId")) ;
        }
        //        Setting Petitioner Details
        if (Objects.nonNull(caseRequest.getCases().getPetitioner())) {
            if (!StringUtils.isNotBlank(caseRequest.getCases().getPetitioner().getFirstName())) {
                throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                        "firstName for Petitioner is mandatory [ " + caseRequest.getCases().getPetitioner().getFirstName() + " ]");
            }
            if (!StringUtils.isNotBlank(caseRequest.getCases().getPetitioner().getLastName())) {
                throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                        "lastName for Petitioner is mandatory [ " + caseRequest.getCases().getPetitioner().getLastName() + " ]");
            }
            if (!StringUtils.isNotBlank(caseRequest.getCases().getPetitioner().getGender())) {
                throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                        "gender for Petitioner is mandatory [ " + caseRequest.getCases().getPetitioner().getGender() + " ]");
            }
            if (!StringUtils.isNotBlank(caseRequest.getCases().getPetitioner().getPetitionerType())) {
                throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                        "petitionerType for Petitioner is mandatory [ " + caseRequest.getCases().getPetitioner().getPetitionerType() + " ]");
            }
            if (!StringUtils.isNotBlank(caseRequest.getCases().getPetitioner().getAddress())) {
                throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                        "address for Petitioner is mandatory [ " + caseRequest.getCases().getPetitioner().getAddress() + " ]");
            }
            if (!StringUtils.isNotBlank(caseRequest.getCases().getPetitioner().getContactNumber())) {
                throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                        "contactNumber for Petitioner is mandatory [ " + caseRequest.getCases().getPetitioner().getContactNumber() + " ]");
            }
            //Setting Data For Petitioner Advocate
            if (Objects.nonNull(caseRequest.getCases().getPetitioner().getAdvocate())) {
                if (!StringUtils.isNotBlank(caseRequest.getCases().getPetitioner().getAdvocate().getFirstName())) {
                    throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                            "firstName for Advocate in Petitioner is mandatory [ " + caseRequest.getCases().getPetitioner().getAdvocate()
                                                                                                .getFirstName() + " ]");
                }
                if (!StringUtils.isNotBlank(caseRequest.getCases().getPetitioner().getAdvocate().getLastName())) {
                    throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                            "lastName for Advocate in Petitioner is mandatory [ " + caseRequest.getCases().getPetitioner().getAdvocate()
                                                                                               .getLastName() + " ]");
                }
                if (!StringUtils.isNotBlank(caseRequest.getCases().getPetitioner().getAdvocate().getContactNumber())) {
                    throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                            "contactNumber for Advocate in Petitioner is mandatory [ " + caseRequest.getCases().getPetitioner().getAdvocate()
                                                                                                    .getContactNumber() + " ]");
                }
            } else {
                throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                        "Advocate details are mandatory [ " + caseRequest.getCases().getPetitioner().getAdvocate() + " ]");
            }
        } else {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                    "petitioner details are mandatory [ " + caseRequest.getCases().getPetitioner() + " ]");
        }
        //       Setting Respondent details
        if (Objects.nonNull(caseRequest.getCases().getRespondent())) {
            if (!StringUtils.isNotBlank(caseRequest.getCases().getRespondent().getFirstName())) {
                throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                        "firstName for Respondent is mandatory [ " + caseRequest.getCases().getRespondent().getFirstName() + " ]");
            }
            if (!StringUtils.isNotBlank(caseRequest.getCases().getRespondent().getLastName())) {
                throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                        "lastName for Respondent is mandatory [ " + caseRequest.getCases().getRespondent().getLastName() + " ]");
            }
            if (!StringUtils.isNotBlank(caseRequest.getCases().getRespondent().getGender())) {
                throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                        "gender for Respondent is mandatory [ " + caseRequest.getCases().getRespondent().getGender() + " ]");
            }
            if (!StringUtils.isNotBlank(caseRequest.getCases().getRespondent().getAddress())) {
                throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                        "address for Respondent is mandatory [ " + caseRequest.getCases().getRespondent().getAddress() + " ]");
            }
            if (!StringUtils.isNotBlank(caseRequest.getCases().getRespondent().getDepartmentName())) {
                throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                        "departmentName for Respondent is mandatory [ " + caseRequest.getCases().getRespondent().getDepartmentName() + " ]");
            }
            if (!StringUtils.isNotBlank(caseRequest.getCases().getRespondent().getContactNumber())) {
                throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                        "contactNumber for Respondent is mandatory [ " + caseRequest.getCases().getRespondent().getContactNumber() + " ]");
            }
            //Setting Data For getRespondent Advocate
            if (Objects.nonNull(caseRequest.getCases().getRespondent().getAdvocate())) {
                if (!StringUtils.isNotBlank(caseRequest.getCases().getRespondent().getAdvocate().getFirstName())) {
                    throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                            "firstName for Advocate in Respondent is mandatory [ " + caseRequest.getCases().getRespondent().getAdvocate()
                                                                                                .getFirstName() + " ]");
                }
                if (!StringUtils.isNotBlank(caseRequest.getCases().getRespondent().getAdvocate().getLastName())) {
                    throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                            "lastName for Advocate in Respondent is mandatory [ " + caseRequest.getCases().getRespondent().getAdvocate()
                                                                                               .getLastName() + " ]");
                }
                if (!StringUtils.isNotBlank(caseRequest.getCases().getRespondent().getAdvocate().getContactNumber())) {
                    throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                            "contactNumber for Advocate in Respondent is mandatory [ " + caseRequest.getCases().getRespondent().getAdvocate()
                                                                                                    .getContactNumber() + " ]");
                }
            } else {
                throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                        "Advocate details for Respondent are mandatory [ " + caseRequest.getCases().getRespondent().getAdvocate() + " ]");
            }
        } else {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                    "Respondent details are mandatory [ " + caseRequest.getCases().getRespondent() + " ]");
        }
        //setting act details
        if (Objects.nonNull(caseRequest.getCases().getAct())) {
            if (!StringUtils.isNotBlank(caseRequest.getCases().getAct().getActName())) {
                throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                        "actName for act details is mandatory [ " + caseRequest.getCases().getAct().getActName() + " ]");
            }
            if (!StringUtils.isNotBlank(caseRequest.getCases().getAct().getSectionNumber())) {
                throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                        "sectionNumber for act details is mandatory [ " + caseRequest.getCases().getAct().getSectionNumber() + " ]");
            }
        } else {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "act Details are mandatory [ " + caseRequest.getCases().getAct() + " ]");
        }
        Map<String, String> errorMap = new HashMap<>();
        if (!errorMap.isEmpty()) {
            throw new CustomException(errorMap);
        }
        validateMasterData(caseRequest.getCases(), caseRequest, errorMap);
    }

    public void validateUpdate(Case aCase, CaseRequest caseRequest) {
        Map<String, String> errorMap = new HashMap<>();
        if (!errorMap.isEmpty()) {
            throw new CustomException(errorMap);
        }
        validateMasterData(aCase, caseRequest, errorMap);
    }

    private void validateMasterData(Case aCase, CaseRequest request, Map<String, String> errorMap) {

        String tenantId = aCase.getTenantId();

        List<String> masterNames = new ArrayList<>(
                Arrays.asList(ILMSConstants.MDMS_ILMS_CASE_TYPE, ILMSConstants.MDMS_ILMS_CASE_STATUS, ILMSConstants.MDMS_ILMS_CASE_CATEGORY,
                        ILMSConstants.MDMS_ILMS_CASE_STAGE, ILMSConstants.MDMS_ILMS_SUB_STAGE, ILMSConstants.MDMS_ILMS_GENDER_TYPE,
                        ILMSConstants.MDMS_ILMS_PETITIONER_TYPE, ILMSConstants.MDMS_ILMS_DEPARTMENT_NAME, ILMSConstants.MDMS_ILMS_DOCUMENT_CATEGORY,
                        ILMSConstants.MDMS_ILMS_DEPARTMENT_IOC, ILMSConstants.CASE_FLAG));

        Map<String, List<String>> codes = commonUtils.getAttributeValues(tenantId, ILMSConstants.MDMS_ILMS_MOD_NAME, masterNames, "$.*.code",
                ILMSConstants.JSONPATH_CODES, request.getRequestInfo());

        if (null != codes) {
            validateMDMSData(masterNames, codes);
            validateCodes(aCase, codes, errorMap);
        } else {
            errorMap.put("MASTER_FETCH_FAILED", "Couldn't fetch master data for validation");
        }

        if (!errorMap.isEmpty()) {
            throw new CustomException(errorMap);
        }
    }

    private void validateMDMSData(List<String> masterNames, Map<String, List<String>> codes) {

        Map<String, String> errorMap = new HashMap<>();
        for (String masterName : masterNames) {
            if (CollectionUtils.isEmpty(codes.get(masterName))) {
                errorMap.put("MDMS DATA ERROR ", "Unable to fetch " + masterName + " codes from MDMS");
            }
        }
        if (!errorMap.isEmpty()) {
            throw new CustomException(errorMap);
        }
    }

    public void caseNumberDuplicacyCheck(CaseRequest caseRequest) {
        CaseSearchCriteria criteria = CaseSearchCriteria.builder().caseNumber(Collections.singletonList(caseRequest.getCases().getCaseNumber()))
                                                        .build();
        CaseResponse response = caseRepository.getILMSCaseData(criteria);
        if (response.getCases().size() >= 1) {
            throw new CustomException(ILMSErrorConstants.DUPLICATE_VALUE_ERROR,
                    "Already Exists In System, case number should be unique [ " + caseRequest.getCases().getCaseNumber() + " ]");
        }
    }

    public void cnrDuplicacyCheck(CaseRequest caseRequest) {
        CaseSearchCriteria criteria = CaseSearchCriteria.builder().cnrNumber(caseRequest.getCases().getCnrNumber()).build();
        CaseResponse response = caseRepository.getILMSCaseData(criteria);
        if (response.getCases().size() >= 1) {
            throw new CustomException(ILMSErrorConstants.DUPLICATE_VALUE_ERROR,
                    "Already Exists In System, CNR number should be unique [ " + caseRequest.getCases().getCnrNumber() + " ]");
        }
    }
}
