package org.legal.validator;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.egov.tracer.model.CustomException;
import org.legal.repository.CaseRepository;
import org.legal.util.CommonUtils;
import org.legal.util.LEGALConstants;
import org.legal.util.LegalErrorConstants;
import org.legal.web.model.Case;
import org.legal.web.model.CaseRequest;
import org.legal.web.model.CaseSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Component
@Slf4j
public class CaseValidator {
    @Autowired
    CommonUtils commonUtils;

    @Autowired
    CaseRepository caseRepository;

    private static Map<String, String> validateCodes(Case cases, Map<String, List<String>> codes, Map<String, String> errorMap) {
        if (Objects.nonNull(cases.getCourt())) {
            if (Objects.nonNull(cases.getCourt().getCourtName()) && !codes.get(LEGALConstants.MDMS_LEGAL_COURT_NAME).contains(cases.getCourt().getCourtName())) {
                errorMap.put("Invalid CourtName", "The CourtName '" + cases.getCourt().getCourtName() + "' does not exists");
            }
        }
        if (Objects.nonNull(cases.getType()) && !codes.get(LEGALConstants.MDMS_LEGAL_CASE_TYPE).contains(cases.getType())) {
            errorMap.put("Invalid CASE TYPE", "The CaseType '" + cases.getType() + "' does not exists");
        }
        if (Objects.nonNull(cases.getCaseStatus()) && !codes.get(LEGALConstants.MDMS_LEGAL_CASE_STATUS).contains(cases.getCaseStatus())) {
            errorMap.put("Invalid CASE Status", "The CaseStatus '" + cases.getCaseStatus() + "' does not exists");
        }
        if (Objects.nonNull(cases.getCategory()) && !codes.get(LEGALConstants.MDMS_LEGAL_CASE_CATEGORY).contains(cases.getCategory())) {
            errorMap.put("Invalid CaseCategory", "The CaseCategory '" + cases.getCategory() + "' does not exists");
        }
//        if (Objects.nonNull(cases.getApplicationStatus()) && !codes.get(LEGALConstants.MDMS_ILMS_CASE_STAGE).contains(cases.getApplicationStatus())) {
//            errorMap.put("Invalid CaseStage", "The CaseStage '" + cases.getApplicationStatus() + "' does not exists");
//        }
//        if (Objects.nonNull(cases.getSubStage()) && !codes.get(LEGALConstants.MDMS_ILMS_SUB_STAGE).contains(cases.getSubStage())) {
//            errorMap.put("Invalid CaseSubStage", "The CaseSubStage '" + cases.getSubStage() + "' does not exists");
//        }
        if (Objects.nonNull(cases.getRecommendOIC()) && !codes.get(LEGALConstants.MDMS_LEGAL_DEPARTMENT_IOC).contains(cases.getRecommendOIC())) {
            errorMap.put("Invalid RecommendOIC", "The RecommendOIC '" + cases.getRecommendOIC() + "' does not exists");
        }
        if (Objects.nonNull(cases.getPriority()) && !codes.get(LEGALConstants.CASE_FLAG).contains(cases.getPriority())) {
            errorMap.put("Invalid CaseFlag", "The CaseFlag '" + cases.getPriority() + "' does not exists");
        }
        if (Objects.nonNull(cases.getDocuments())) {
            cases.getDocuments().forEach(document -> {
                if (Objects.nonNull(document.getDocumentType()) && !codes.get(LEGALConstants.MDMS_LEGAL_DOCUMENT_CATEGORY)
                        .contains(document.getDocumentType())) {
                    errorMap.put("Invalid DocumentCategory", "The DocumentCategory '" + document.getDocumentType() + "' does not exists");
                }
            });
        }
        return errorMap;

    }

    public void validateCreate(CaseRequest caseRequest) {

        if (!StringUtils.isNotBlank(caseRequest.getCaseObj().getTenantId())) {
            throw new CustomException(LegalErrorConstants.INVALID_TYPE_ERROR,
                    "TenantId is mandatory [ " + caseRequest.getCaseObj().getTenantId() + " ]");
        }
        if (!StringUtils.isNotBlank(caseRequest.getCaseObj().getNumber())) {
            throw new CustomException(LegalErrorConstants.INVALID_TYPE_ERROR,
                    "caseNumber is mandatory [ " + caseRequest.getCaseObj().getNumber() + " ]");
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
                Arrays.asList(LEGALConstants.MDMS_LEGAL_CASE_TYPE, LEGALConstants.MDMS_LEGAL_CASE_STATUS, LEGALConstants.MDMS_LEGAL_CASE_CATEGORY,
                        LEGALConstants.MDMS_LEGAL_GENDER_TYPE, LEGALConstants.MDMS_LEGAL_CASE_STAGE,
                        LEGALConstants.MDMS_LEGAL_PETITIONER_TYPE, LEGALConstants.MDMS_LEGAL_DEPARTMENT_NAME, LEGALConstants.MDMS_LEGAL_DOCUMENT_CATEGORY,
                        LEGALConstants.MDMS_LEGAL_DEPARTMENT_IOC, LEGALConstants.CASE_FLAG, LEGALConstants.MDMS_LEGAL_COURT_NAME));

        Map<String, List<String>> codes = commonUtils.getAttributeValues(tenantId, LEGALConstants.MDMS_LEGAL_MOD_NAME, masterNames, "$.*.code",
                LEGALConstants.JSONPATH_CODES, request.getRequestInfo());

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
        CaseSearchCriteria criteria = CaseSearchCriteria.builder().number(Collections.singletonList(caseRequest.getCaseObj().getNumber()))
                .build();
        Integer count = caseRepository.getCaseCount(criteria);
        if (count >= 1) {
            throw new CustomException(LegalErrorConstants.DUPLICATE_VALUE_ERROR,
                    "Already Exists In System, case number should be unique [ " + caseRequest.getCaseObj().getNumber() + " ]");
        }
    }

}
