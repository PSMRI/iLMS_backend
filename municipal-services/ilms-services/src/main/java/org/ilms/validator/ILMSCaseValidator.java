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
import org.ilms.repository.ILMSCaseRepository;
import org.ilms.util.CommonUtils;
import org.ilms.util.ILMSConstants;
import org.ilms.util.ILMSErrorConstants;
import org.ilms.web.model.Document;
import org.ilms.web.model.ILMSCase;
import org.ilms.web.model.ILMSCaseRequest;
import org.ilms.web.model.ILMSCaseResponse;
import org.ilms.web.model.ILMSCaseSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ILMSCaseValidator {
    @Autowired
    CommonUtils commonUtils;

    @Autowired
    ILMSCaseRepository ilmsCaseRepository;

    private static Map<String, String> validateCodes(ILMSCase ilmsCase, Map<String, List<String>> codes, Map<String, String> errorMap) {

        if (ilmsCase.getCaseType() != null && !codes.get(ILMSConstants.MDMS_ILMS_CASE_TYPE).contains(ilmsCase.getCaseType())) {
            errorMap.put("Invalid CASE TYPE", "The CaseType '" + ilmsCase.getCaseType() + "' does not exists");
        }
        if (ilmsCase.getCaseStatus() != null && !codes.get(ILMSConstants.MDMS_ILMS_CASE_STATUS).contains(ilmsCase.getCaseStatus())) {
            errorMap.put("Invalid CASE Status", "The CaseStatus '" + ilmsCase.getCaseStatus() + "' does not exists");
        }
        if (ilmsCase.getCaseCategory() != null && !codes.get(ILMSConstants.MDMS_ILMS_CASE_CATEGORY).contains(ilmsCase.getCaseCategory())) {
            errorMap.put("Invalid CaseCategory", "The CaseCategory '" + ilmsCase.getCaseCategory() + "' does not exists");
        }
        if (ilmsCase.getCaseStage() != null && !codes.get(ILMSConstants.MDMS_ILMS_CASE_STAGE).contains(ilmsCase.getCaseStage())) {
            errorMap.put("Invalid CaseStage", "The CaseStage '" + ilmsCase.getCaseStage() + "' does not exists");
        }
        if (ilmsCase.getCaseSubStage() != null && !codes.get(ILMSConstants.MDMS_ILMS_SUB_STAGE).contains(ilmsCase.getCaseSubStage())) {
            errorMap.put("Invalid CaseSubStage", "The CaseSubStage '" + ilmsCase.getCaseSubStage() + "' does not exists");
        }
        if (ilmsCase.getPetitioner().getGender() != null && !codes.get(ILMSConstants.MDMS_ILMS_GENDER_TYPE)
                                                                  .contains(ilmsCase.getPetitioner().getGender())) {
            errorMap.put("Invalid Gender", "The Gender '" + ilmsCase.getPetitioner().getGender() + "' does not exists");
        }
        if (ilmsCase.getRespondent().getGender() != null && !codes.get(ILMSConstants.MDMS_ILMS_GENDER_TYPE)
                                                                  .contains(ilmsCase.getRespondent().getGender())) {
            errorMap.put("Invalid Gender", "The Gender '" + ilmsCase.getRespondent().getGender() + "' does not exists");
        }
        if (ilmsCase.getPetitioner().getPetitionerType() != null && !codes.get(ILMSConstants.MDMS_ILMS_PETITIONER_TYPE)
                                                                          .contains(ilmsCase.getPetitioner().getPetitionerType())) {
            errorMap.put("Invalid PetitionerType", "The PetitionerType '" + ilmsCase.getPetitioner().getPetitionerType() + "' does not exists");
        }
        if (ilmsCase.getDepartmentName() != null && !codes.get(ILMSConstants.MDMS_ILMS_DEPARTMENT_NAME).contains(ilmsCase.getDepartmentName())) {
            errorMap.put("Invalid DepartmentName", "The DepartmentName '" + ilmsCase.getDepartmentName() + "' does not exists");
        }
        if (ilmsCase.getRecommendOIC() != null && !codes.get(ILMSConstants.MDMS_ILMS_DEPARTMENT_IOC).contains(ilmsCase.getRecommendOIC())) {
            errorMap.put("Invalid RecommendOIC", "The RecommendOIC '" + ilmsCase.getRecommendOIC() + "' does not exists");
        }
        if (Objects.nonNull(ilmsCase.getDocuments())){
            ilmsCase.getDocuments().forEach(document->{
                if (document.getDocumentType() != null && !codes.get(ILMSConstants.MDMS_ILMS_DOCUMENT_CATEGORY)
                                                                                      .contains(document.getDocumentType())) {
                    errorMap.put("Invalid DocumentCategory",
                            "The DocumentCategory '" + document.getDocumentType() + "' does not exists");
                }
            });
        }

        return errorMap;

    }

    public void validateCreate(ILMSCaseRequest ilmsCaseRequest) {

        if (!StringUtils.isNotBlank(ilmsCaseRequest.getIlmsCase().getTenantId())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                    "TenantId is mandatory [ " + ilmsCaseRequest.getIlmsCase().getTenantId() + " ]");
        }
        if (!StringUtils.isNotBlank(ilmsCaseRequest.getIlmsCase().getCaseNumber())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                    "caseNumber is mandatory [ " + ilmsCaseRequest.getIlmsCase().getCaseNumber() + " ]");
        }
        if (!StringUtils.isNotBlank(ilmsCaseRequest.getIlmsCase().getCnrNumber())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                    "CNRNumber is mandatory [ " + ilmsCaseRequest.getIlmsCase().getCnrNumber() + " ]");
        }
        if (!StringUtils.isNotBlank(ilmsCaseRequest.getIlmsCase().getCaseType())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                    "caseType is mandatory [ " + ilmsCaseRequest.getIlmsCase().getCaseType() + " ]");
        }
        if (!StringUtils.isNotBlank(ilmsCaseRequest.getIlmsCase().getCaseCategory())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                    "caseCategory is mandatory [ " + ilmsCaseRequest.getIlmsCase().getCaseCategory() + " ]");
        }
        if (!StringUtils.isNotBlank(ilmsCaseRequest.getIlmsCase().getCaseYear().toString())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                    "caseYear is mandatory [ " + ilmsCaseRequest.getIlmsCase().getCaseYear() + " ]");
        }
        if (!StringUtils.isNotBlank(ilmsCaseRequest.getIlmsCase().getFilingDate().toString())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                    "filingDate is mandatory [ " + ilmsCaseRequest.getIlmsCase().getFilingDate() + " ]");
        } else {
            if (commonUtils.isCorrectDate(ilmsCaseRequest.getIlmsCase().getFilingDate()))
                ;
        }
        if (!StringUtils.isNotBlank(ilmsCaseRequest.getIlmsCase().getRegistrationDate().toString())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                    "registrationDate is mandatory [ " + ilmsCaseRequest.getIlmsCase().getRegistrationDate() + " ]");
        } else {
            if (commonUtils.isCorrectDate(ilmsCaseRequest.getIlmsCase().getRegistrationDate()))
                ;
        }
        if (!StringUtils.isNotBlank(ilmsCaseRequest.getIlmsCase().getCaseSummary())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                    "caseSummary is mandatory [ " + ilmsCaseRequest.getIlmsCase().getCaseSummary() + " ]");
        }
        if (!StringUtils.isNotBlank(ilmsCaseRequest.getIlmsCase().getPolicyOrNonPolicyMatter())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                    "policyOrNonPolicyMatters is mandatory [ " + ilmsCaseRequest.getIlmsCase().getPolicyOrNonPolicyMatter() + " ]");
        }
        if (!StringUtils.isNotBlank(ilmsCaseRequest.getIlmsCase().getIsCaseNumberCorrect().toString())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                    "isCaseNumberCorrect is mandatory [ " + ilmsCaseRequest.getIlmsCase().getIsCaseNumberCorrect() + " ]");
        }
        if (!StringUtils.isNotBlank(ilmsCaseRequest.getIlmsCase().getCaseStatus())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                    "caseStatus is mandatory [ " + ilmsCaseRequest.getIlmsCase().getCaseStatus() + " ]");
        }
        if (!StringUtils.isNotBlank(ilmsCaseRequest.getIlmsCase().getFirstHearingDate().toString())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                    "firstHearingDate is mandatory [ " + ilmsCaseRequest.getIlmsCase().getFirstHearingDate() + " ]");
        } else {
            if (commonUtils.isCorrectDate(ilmsCaseRequest.getIlmsCase().getFirstHearingDate()))
                ;
        }
        if (!StringUtils.isNotBlank(ilmsCaseRequest.getIlmsCase().getNextHearingDate().toString())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                    "nextHearingDate is mandatory [ " + ilmsCaseRequest.getIlmsCase().getNextHearingDate() + " ]");
        } else {
            if (commonUtils.isCorrectDate(ilmsCaseRequest.getIlmsCase().getNextHearingDate()))
                ;
        }
        if (!StringUtils.isNotBlank(ilmsCaseRequest.getIlmsCase().getCaseStage())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                    "caseStage is mandatory [ " + ilmsCaseRequest.getIlmsCase().getCaseStage() + " ]");
        }
        if (!StringUtils.isNotBlank(ilmsCaseRequest.getIlmsCase().getCaseSubStage())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                    "caseSubStage is mandatory [ " + ilmsCaseRequest.getIlmsCase().getCaseSubStage() + " ]");
        }
        if (!StringUtils.isNotBlank(ilmsCaseRequest.getIlmsCase().getDepartmentName())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                    "DepartmentName is mandatory [ " + ilmsCaseRequest.getIlmsCase().getDepartmentName() + " ]");
        }
        if (!StringUtils.isNotBlank(ilmsCaseRequest.getIlmsCase().getRecommendOIC())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                    "recommendedOIC is mandatory [ " + ilmsCaseRequest.getIlmsCase().getRecommendOIC() + " ]");
        }
        //        Setting Petitioner Details
        if (Objects.nonNull(ilmsCaseRequest.getIlmsCase().getPetitioner())) {
            if (!StringUtils.isNotBlank(ilmsCaseRequest.getIlmsCase().getPetitioner().getFirstName())) {
                throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                        "firstName for Petitioner is mandatory [ " + ilmsCaseRequest.getIlmsCase().getPetitioner().getFirstName() + " ]");
            }
            if (!StringUtils.isNotBlank(ilmsCaseRequest.getIlmsCase().getPetitioner().getLastName())) {
                throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                        "lastName for Petitioner is mandatory [ " + ilmsCaseRequest.getIlmsCase().getPetitioner().getLastName() + " ]");
            }
            if (!StringUtils.isNotBlank(ilmsCaseRequest.getIlmsCase().getPetitioner().getGender())) {
                throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                        "gender for Petitioner is mandatory [ " + ilmsCaseRequest.getIlmsCase().getPetitioner().getGender() + " ]");
            }
            if (!StringUtils.isNotBlank(ilmsCaseRequest.getIlmsCase().getPetitioner().getPetitionerType())) {
                throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                        "petitionerType for Petitioner is mandatory [ " + ilmsCaseRequest.getIlmsCase().getPetitioner().getPetitionerType() + " ]");
            }
            if (!StringUtils.isNotBlank(ilmsCaseRequest.getIlmsCase().getPetitioner().getAddress())) {
                throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                        "address for Petitioner is mandatory [ " + ilmsCaseRequest.getIlmsCase().getPetitioner().getAddress() + " ]");
            }
            if (!StringUtils.isNotBlank(ilmsCaseRequest.getIlmsCase().getPetitioner().getContactNumber())) {
                throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                        "contactNumber for Petitioner is mandatory [ " + ilmsCaseRequest.getIlmsCase().getPetitioner().getContactNumber() + " ]");
            }
            //Setting Data For Petitioner Advocate
            if (Objects.nonNull(ilmsCaseRequest.getIlmsCase().getPetitioner().getAdvocate())) {
                if (!StringUtils.isNotBlank(ilmsCaseRequest.getIlmsCase().getPetitioner().getAdvocate().getFirstName())) {
                    throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                            "firstName for Advocate in Petitioner is mandatory [ " + ilmsCaseRequest.getIlmsCase().getPetitioner().getAdvocate()
                                                                                                    .getFirstName() + " ]");
                }
                if (!StringUtils.isNotBlank(ilmsCaseRequest.getIlmsCase().getPetitioner().getAdvocate().getLastName())) {
                    throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                            "lastName for Advocate in Petitioner is mandatory [ " + ilmsCaseRequest.getIlmsCase().getPetitioner().getAdvocate()
                                                                                                   .getLastName() + " ]");
                }
                if (!StringUtils.isNotBlank(ilmsCaseRequest.getIlmsCase().getPetitioner().getAdvocate().getContactNumber())) {
                    throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                            "contactNumber for Advocate in Petitioner is mandatory [ " + ilmsCaseRequest.getIlmsCase().getPetitioner().getAdvocate()
                                                                                                        .getContactNumber() + " ]");
                }
            } else {
                throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                        "Advocate details are mandatory [ " + ilmsCaseRequest.getIlmsCase().getPetitioner().getAdvocate() + " ]");
            }
        } else {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                    "petitioner details are mandatory [ " + ilmsCaseRequest.getIlmsCase().getPetitioner() + " ]");
        }
        //       Setting Respondent details
        if (Objects.nonNull(ilmsCaseRequest.getIlmsCase().getRespondent())) {
            if (!StringUtils.isNotBlank(ilmsCaseRequest.getIlmsCase().getRespondent().getFirstName())) {
                throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                        "firstName for Respondent is mandatory [ " + ilmsCaseRequest.getIlmsCase().getRespondent().getFirstName() + " ]");
            }
            if (!StringUtils.isNotBlank(ilmsCaseRequest.getIlmsCase().getRespondent().getLastName())) {
                throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                        "lastName for Respondent is mandatory [ " + ilmsCaseRequest.getIlmsCase().getRespondent().getLastName() + " ]");
            }
            if (!StringUtils.isNotBlank(ilmsCaseRequest.getIlmsCase().getRespondent().getGender())) {
                throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                        "gender for Respondent is mandatory [ " + ilmsCaseRequest.getIlmsCase().getRespondent().getGender() + " ]");
            }
            if (!StringUtils.isNotBlank(ilmsCaseRequest.getIlmsCase().getRespondent().getAddress())) {
                throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                        "address for Respondent is mandatory [ " + ilmsCaseRequest.getIlmsCase().getRespondent().getAddress() + " ]");
            }
            if (!StringUtils.isNotBlank(ilmsCaseRequest.getIlmsCase().getRespondent().getDepartmentName())) {
                throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                        "departmentName for Respondent is mandatory [ " + ilmsCaseRequest.getIlmsCase().getRespondent().getDepartmentName() + " ]");
            }
            if (!StringUtils.isNotBlank(ilmsCaseRequest.getIlmsCase().getRespondent().getContactNumber())) {
                throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                        "contactNumber for Respondent is mandatory [ " + ilmsCaseRequest.getIlmsCase().getRespondent().getContactNumber() + " ]");
            }
            //Setting Data For getRespondent Advocate
            if (Objects.nonNull(ilmsCaseRequest.getIlmsCase().getRespondent().getAdvocate())) {
                if (!StringUtils.isNotBlank(ilmsCaseRequest.getIlmsCase().getRespondent().getAdvocate().getFirstName())) {
                    throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                            "firstName for Advocate in Respondent is mandatory [ " + ilmsCaseRequest.getIlmsCase().getRespondent().getAdvocate()
                                                                                                    .getFirstName() + " ]");
                }
                if (!StringUtils.isNotBlank(ilmsCaseRequest.getIlmsCase().getRespondent().getAdvocate().getLastName())) {
                    throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                            "lastName for Advocate in Respondent is mandatory [ " + ilmsCaseRequest.getIlmsCase().getRespondent().getAdvocate()
                                                                                                   .getLastName() + " ]");
                }
                if (!StringUtils.isNotBlank(ilmsCaseRequest.getIlmsCase().getRespondent().getAdvocate().getContactNumber())) {
                    throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                            "contactNumber for Advocate in Respondent is mandatory [ " + ilmsCaseRequest.getIlmsCase().getRespondent().getAdvocate()
                                                                                                        .getContactNumber() + " ]");
                }
            } else {
                throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                        "Advocate details for Respondent are mandatory [ " + ilmsCaseRequest.getIlmsCase().getRespondent().getAdvocate() + " ]");
            }
        } else {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                    "Respondent details are mandatory [ " + ilmsCaseRequest.getIlmsCase().getRespondent() + " ]");
        }
        //setting act details
        if (Objects.nonNull(ilmsCaseRequest.getIlmsCase().getAct())) {
            if (!StringUtils.isNotBlank(ilmsCaseRequest.getIlmsCase().getAct().getActName())) {
                throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                        "actName for act details is mandatory [ " + ilmsCaseRequest.getIlmsCase().getAct().getActName() + " ]");
            }
            if (!StringUtils.isNotBlank(ilmsCaseRequest.getIlmsCase().getAct().getSectionNumber())) {
                throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                        "sectionNumber for act details is mandatory [ " + ilmsCaseRequest.getIlmsCase().getAct().getSectionNumber() + " ]");
            }
        } else {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                    "act Details are mandatory [ " + ilmsCaseRequest.getIlmsCase().getAct() + " ]");
        }
        Map<String, String> errorMap = new HashMap<>();
        if (!errorMap.isEmpty()) {
            throw new CustomException(errorMap);
        }
        validateMasterData(ilmsCaseRequest.getIlmsCase(), ilmsCaseRequest, errorMap);
    }

    public void validateUpdate(ILMSCase ilmsCase, ILMSCaseRequest ilmsCaseRequest) {
        Map<String, String> errorMap = new HashMap<>();
        if (!errorMap.isEmpty()) {
            throw new CustomException(errorMap);
        }
        validateMasterData(ilmsCase, ilmsCaseRequest, errorMap);
    }

    private void validateMasterData(ILMSCase ilmsCase, ILMSCaseRequest request, Map<String, String> errorMap) {

        String tenantId = ilmsCase.getTenantId();

        List<String> masterNames = new ArrayList<>(
                Arrays.asList(ILMSConstants.MDMS_ILMS_CASE_TYPE, ILMSConstants.MDMS_ILMS_CASE_STATUS, ILMSConstants.MDMS_ILMS_CASE_CATEGORY,
                        ILMSConstants.MDMS_ILMS_CASE_STAGE, ILMSConstants.MDMS_ILMS_SUB_STAGE, ILMSConstants.MDMS_ILMS_GENDER_TYPE,
                        ILMSConstants.MDMS_ILMS_PETITIONER_TYPE, ILMSConstants.MDMS_ILMS_DEPARTMENT_NAME, ILMSConstants.MDMS_ILMS_DOCUMENT_CATEGORY,
                        ILMSConstants.MDMS_ILMS_DEPARTMENT_IOC));

        Map<String, List<String>> codes = commonUtils.getAttributeValues(tenantId, ILMSConstants.MDMS_ILMS_MOD_NAME, masterNames, "$.*.code",
                ILMSConstants.JSONPATH_CODES, request.getRequestInfo());

        if (null != codes) {
            validateMDMSData(masterNames, codes);
            validateCodes(ilmsCase, codes, errorMap);
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

    public void caseNumberDuplicacyCheck(ILMSCaseRequest ilmsCaseRequest) {
        ILMSCaseSearchCriteria criteria = ILMSCaseSearchCriteria.builder()
                                                                .caseNumber(Collections.singletonList(ilmsCaseRequest.getIlmsCase().getCaseNumber()))
                                                                .build();
        ILMSCaseResponse response = ilmsCaseRepository.getILMSCaseData(criteria);
        if (response.getIlmsCases().size() >= 1) {
            throw new CustomException(ILMSErrorConstants.DUPLICATE_VALUE_ERROR,
                    "Already Exists In System, case number should be unique [ " + ilmsCaseRequest.getIlmsCase().getCaseNumber() + " ]");
        }
    }

    public void cnrDuplicacyCheck(ILMSCaseRequest ilmsCaseRequest) {
        ILMSCaseSearchCriteria criteria = ILMSCaseSearchCriteria.builder().cnrNumber(ilmsCaseRequest.getIlmsCase().getCnrNumber()).build();
        ILMSCaseResponse response = ilmsCaseRepository.getILMSCaseData(criteria);
        if (response.getIlmsCases().size() >= 1) {
            throw new CustomException(ILMSErrorConstants.DUPLICATE_VALUE_ERROR,
                    "Already Exists In System, CNR number should be unique [ " + ilmsCaseRequest.getIlmsCase().getCnrNumber() + " ]");
        }
    }
}
