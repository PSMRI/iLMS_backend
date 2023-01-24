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
        if (Objects.nonNull(cases.getCaseType()) && !codes.get(ILMSConstants.MDMS_ILMS_CASE_TYPE).contains(cases.getCaseType())) {
            errorMap.put("Invalid CASE TYPE", "The CaseType '" + cases.getCaseType() + "' does not exists");
        }
        if (Objects.nonNull(cases.getCaseStatus()) && !codes.get(ILMSConstants.MDMS_ILMS_CASE_STATUS).contains(cases.getCaseStatus())) {
            errorMap.put("Invalid CASE Status", "The CaseStatus '" + cases.getCaseStatus() + "' does not exists");
        }
        if (Objects.nonNull(cases.getCaseCategory()) && !codes.get(ILMSConstants.MDMS_ILMS_CASE_CATEGORY).contains(cases.getCaseCategory())) {
            errorMap.put("Invalid CaseCategory", "The CaseCategory '" + cases.getCaseCategory() + "' does not exists");
        }
        if (Objects.nonNull(cases.getCaseStage()) && !codes.get(ILMSConstants.MDMS_ILMS_CASE_STAGE).contains(cases.getCaseStage())) {
            errorMap.put("Invalid CaseStage", "The CaseStage '" + cases.getCaseStage() + "' does not exists");
        }
        if (Objects.nonNull(cases.getCaseSubStage()) && !codes.get(ILMSConstants.MDMS_ILMS_SUB_STAGE).contains(cases.getCaseSubStage())) {
            errorMap.put("Invalid CaseSubStage", "The CaseSubStage '" + cases.getCaseSubStage() + "' does not exists");
        }
        if (Objects.nonNull(cases.getPetitioner())) {
            if (Objects.nonNull(cases.getPetitioner().getGender()) && !codes.get(ILMSConstants.MDMS_ILMS_GENDER_TYPE)
                                                                            .contains(cases.getPetitioner().getGender())) {
                errorMap.put("Invalid Gender", "The Gender '" + cases.getPetitioner().getGender() + "' does not exists");
            }
            if (cases.getPetitioner().getPetitionerType() != null && !codes.get(ILMSConstants.MDMS_ILMS_PETITIONER_TYPE)
                                                                           .contains(cases.getPetitioner().getPetitionerType())) {
                errorMap.put("Invalid PetitionerType", "The PetitionerType '" + cases.getPetitioner().getPetitionerType() + "' does not exists");
            }
        }
        if (Objects.nonNull(cases.getRespondent())) {
            if (Objects.nonNull(cases.getRespondent().getGender()) && !codes.get(ILMSConstants.MDMS_ILMS_GENDER_TYPE)
                                                                            .contains(cases.getRespondent().getGender())) {
                errorMap.put("Invalid Gender", "The Gender '" + cases.getRespondent().getGender() + "' does not exists");
            }
        }
        if (Objects.nonNull(cases.getDepartmentName()) && !codes.get(ILMSConstants.MDMS_ILMS_DEPARTMENT_NAME).contains(cases.getDepartmentName())) {
            errorMap.put("Invalid DepartmentName", "The DepartmentName '" + cases.getDepartmentName() + "' does not exists");
        }
        if (Objects.nonNull(cases.getRecommendOIC()) && !codes.get(ILMSConstants.MDMS_ILMS_DEPARTMENT_IOC).contains(cases.getRecommendOIC())) {
            errorMap.put("Invalid RecommendOIC", "The RecommendOIC '" + cases.getRecommendOIC() + "' does not exists");
        }
        if (Objects.nonNull(cases.getCaseFlag()) && !codes.get(ILMSConstants.CASE_FLAG).contains(cases.getCaseFlag())) {
            errorMap.put("Invalid CaseFlag", "The CaseFlag '" + cases.getCaseFlag() + "' does not exists");
        }
        if (Objects.nonNull(cases.getDocuments())) {
            cases.getDocuments().forEach(document -> {
                if (Objects.nonNull(document.getDocumentType()) && !codes.get(ILMSConstants.MDMS_ILMS_DOCUMENT_CATEGORY)
                                                                         .contains(document.getDocumentType())) {
                    errorMap.put("Invalid DocumentCategory", "The DocumentCategory '" + document.getDocumentType() + "' does not exists");
                }
            });
        }
        return errorMap;

    }

    public void validateCreate(CaseRequest caseRequest) {

        if (!StringUtils.isNotBlank(caseRequest.getCaseObj().getTenantId())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                    "TenantId is mandatory [ " + caseRequest.getCaseObj().getTenantId() + " ]");
        }
        if (!StringUtils.isNotBlank(caseRequest.getCaseObj().getCaseNumber())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                    "caseNumber is mandatory [ " + caseRequest.getCaseObj().getCaseNumber() + " ]");
        }
        if (!StringUtils.isNotBlank(caseRequest.getCaseObj().getCnrNumber())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                    "CNRNumber is mandatory [ " + caseRequest.getCaseObj().getCnrNumber() + " ]");
        }

        Map<String, String> errorMap = new HashMap<>();
        if (!errorMap.isEmpty()) {
            throw new CustomException(errorMap);
        }
        validateMasterData(caseRequest.getCaseObj(), caseRequest, errorMap);
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
        CaseSearchCriteria criteria = CaseSearchCriteria.builder().caseNumber(Collections.singletonList(caseRequest.getCaseObj().getCaseNumber()))
                                                        .build();
        Integer count = caseRepository.getCaseCount(criteria);
        if (count >= 1) {
            throw new CustomException(ILMSErrorConstants.DUPLICATE_VALUE_ERROR,
                    "Already Exists In System, case number should be unique [ " + caseRequest.getCaseObj().getCaseNumber() + " ]");
        }
    }

    public void cnrDuplicacyCheck(CaseRequest caseRequest) {
        CaseSearchCriteria criteria = CaseSearchCriteria.builder().cnrNumber(caseRequest.getCaseObj().getCnrNumber()).build();
        Integer count = caseRepository.getCaseCount(criteria);
        if (count >= 1) {
            throw new CustomException(ILMSErrorConstants.DUPLICATE_VALUE_ERROR,
                    "Already Exists In System, CNR number should be unique [ " + caseRequest.getCaseObj().getCnrNumber() + " ]");
        }
    }
}
