package org.ilms.validator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.egov.tracer.model.CustomException;
import org.ilms.repository.ILMSCaseRepository;
import org.ilms.util.CommonUtils;
import org.ilms.util.ILMSConstants;
import org.ilms.util.ILMSErrorConstants;
import org.ilms.web.model.ILMSCaseResponse;
import org.ilms.web.model.ILMSCaseSearchCriteria;
import org.ilms.web.model.Judgement;
import org.ilms.web.model.JudgementRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JudgementValidator {
    @Autowired
    CommonUtils commonUtils;

    @Autowired
    private ILMSCaseRepository ilmsCaseRepository;

    private static Map<String, String> validateCodes(Judgement judgement, Map<String, List<String>> codes, Map<String, String> errorMap) {

        if (judgement.getComplianceStatus() != null && !codes.get(ILMSConstants.MDMS_ILMS_STATUS_OF_COMPLIANCE)
                                                             .contains(judgement.getComplianceStatus())) {
            errorMap.put("Invalid ComplianceStatus", "The ComplianceStatus '" + judgement.getComplianceStatus() + "' does not exists");
        }
        if (judgement.getOrderType() != null && !codes.get(ILMSConstants.MDMS_ILMS_ORDER_TYPE).contains(judgement.getOrderType())) {
            errorMap.put("Invalid OrderType", "The OrderType '" + judgement.getOrderType() + "' does not exists");
        }
        return errorMap;
    }

    public void createValidator(JudgementRequest request) {

        if (!StringUtils.isNotBlank(request.getJudgement().getCaseId())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "caseId is mandatory");
        }
        if (!StringUtils.isNotBlank(request.getJudgement().getOrderType())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "orderType is mandatory");
        }
        if (!StringUtils.isNotBlank(request.getJudgement().getOrderDate().toString())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "orderDate is mandatory");
        } else {
            if (commonUtils.isCorrectDate(request.getJudgement().getOrderDate()))
                ;
        }
        if (!StringUtils.isNotBlank(request.getJudgement().getDecisionStatus())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "decisionStatus is mandatory");
        }
        if (!StringUtils.isNotBlank(request.getJudgement().getComplianceDate().toString())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "complianceDate is mandatory");
        } else {
            if (commonUtils.isCorrectDate(request.getJudgement().getComplianceDate()))
                ;
        }
        if (!StringUtils.isNotBlank(request.getJudgement().getRevisedComplianceDate().toString())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "revisedComplianceDate is mandatory");
        } else {
            if (commonUtils.isCorrectDate(request.getJudgement().getRevisedComplianceDate()))
                ;
        }
        if (!StringUtils.isNotBlank(request.getJudgement().getOrderNoOverride())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "orderNoOverride is mandatory");
        }
        if (!StringUtils.isNotBlank(request.getJudgement().getRevisedComplainceReason())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "revisedComplianceReason is mandatory");
        }
        if (!StringUtils.isNotBlank(request.getJudgement().getComplianceStatus())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "complianceStatus is mandatory");
        }
        if (!StringUtils.isNotBlank(request.getJudgement().getRemarks())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "remarks is mandatory");
        }
        Map<String, String> errorMap = new HashMap<>();
        if (!errorMap.isEmpty()) {
            throw new CustomException(errorMap);
        }
        validateMasterData(request.getJudgement(), request, errorMap);
    }

    public void updateValidator(Judgement judgement, JudgementRequest request) {
        Map<String, String> errorMap = new HashMap<>();
        if (!errorMap.isEmpty()) {
            throw new CustomException(errorMap);
        }
        validateMasterData(judgement, request, errorMap);
    }

    private void validateMasterData(Judgement judgement, JudgementRequest request, Map<String, String> errorMap) {

        String caseId = judgement.getCaseId();
        ILMSCaseSearchCriteria criteria = ILMSCaseSearchCriteria.builder().id(Collections.singletonList(caseId)).build();
        ILMSCaseResponse ilmsCaseResponse = ilmsCaseRepository.getILMSCaseData(criteria);
        String tenantId = ilmsCaseResponse.getIlmsCases().get(0).getTenantId();
        List<String> masterNames = new ArrayList<>(Arrays.asList(ILMSConstants.MDMS_ILMS_STATUS_OF_COMPLIANCE, ILMSConstants.MDMS_ILMS_ORDER_TYPE));

        Map<String, List<String>> codes = commonUtils.getAttributeValues(tenantId, ILMSConstants.MDMS_ILMS_MOD_NAME, masterNames, "$.*.code",
                ILMSConstants.JSONPATH_CODES, request.getRequestInfo());

        if (null != codes) {
            validateMDMSData(masterNames, codes);
            validateCodes(judgement, codes, errorMap);
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
}
