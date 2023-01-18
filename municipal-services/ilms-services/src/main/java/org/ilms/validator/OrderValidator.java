package org.ilms.validator;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.egov.tracer.model.CustomException;
import org.ilms.repository.CaseRepository;
import org.ilms.util.CommonUtils;
import org.ilms.util.ILMSConstants;
import org.ilms.util.ILMSErrorConstants;
import org.ilms.web.model.CaseSearchCriteria;
import org.ilms.web.model.CaseSearchResponse;
import org.ilms.web.model.Order;
import org.ilms.web.model.OrderRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Component
@Slf4j
public class OrderValidator {
    @Autowired
    CommonUtils commonUtils;

    @Autowired
    private CaseRepository caseRepository;

    private static Map<String, String> validateCode(Order order, Map<String, List<String>> codes, Map<String, String> errorMap) {
        if (order.getOrderType() != null && !codes.get(ILMSConstants.MDMS_ILMS_ORDER_TYPE).contains(order.getOrderType())) {
            errorMap.put("Invalid OrderType", "The OrderType '" + order.getOrderType() + "' does not exists");
        }
        return errorMap;
    }

    private static Map<String, String> validateCodesForUpdate(Order order, Map<String, List<String>> codes, Map<String, String> errorMap) {

        if (order.getComplianceStatus() != null && !codes.get(ILMSConstants.MDMS_ILMS_STATUS_OF_COMPLIANCE)
                .contains(order.getComplianceStatus())) {
            errorMap.put("Invalid ComplianceStatus", "The ComplianceStatus '" + order.getComplianceStatus() + "' does not exists");
        }
        if (order.getOrderType() != null && !codes.get(ILMSConstants.MDMS_ILMS_ORDER_TYPE).contains(order.getOrderType())) {
            errorMap.put("Invalid OrderType", "The OrderType '" + order.getOrderType() + "' does not exists");
        }
        return errorMap;
    }

    public void createValidator(OrderRequest request) {

        if (!StringUtils.isNotBlank(request.getOrder().getCaseId())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "caseId is mandatory");
        }
        if (!StringUtils.isNotBlank(request.getOrder().getOrderType())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "orderType is mandatory");
        }
        if (!StringUtils.isNotBlank(request.getOrder().getOrderDate().toString())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "orderDate is mandatory");
        }
        if (StringUtils.isNotBlank(request.getOrder().getDecisionStatus())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "decisionStatus is not allowed while creating judgement");
        }
        if (!StringUtils.isNotBlank(request.getOrder().getOrderNoOverride())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "orderNoOverride is mandatory");
        }
        if (!StringUtils.isNotBlank(request.getOrder().getRevisedComplainceReason())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "revisedComplianceReason is mandatory");
        }
        if (StringUtils.isNotBlank(request.getOrder().getComplianceStatus())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "complianceStatus is not allowed while creating judgement");
        }
        if (!StringUtils.isNotBlank(request.getOrder().getRemarks())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "remarks is mandatory");
        }
        Map<String, String> errorMap = new HashMap<>();
        if (!errorMap.isEmpty()) {
            throw new CustomException(errorMap);
        }
        validateMasterData(request.getOrder(), request, errorMap);
    }

    public void updateValidator(Order order, OrderRequest request) {
        Map<String, String> errorMap = new HashMap<>();
        if (!errorMap.isEmpty()) {
            throw new CustomException(errorMap);
        }
        validateMasterDataForUpdate(order, request, errorMap);
    }

    private void validateMasterData(Order order, OrderRequest request, Map<String, String> errorMap) {

        String caseId = order.getCaseId();
        CaseSearchCriteria criteria = CaseSearchCriteria.builder().id(Collections.singletonList(caseId)).build();
        CaseSearchResponse caseResponse = caseRepository.getILMSCaseData(criteria);
        String tenantId = caseResponse.getCaseList().get(0).getTenantId();
        List<String> masterNames = new ArrayList<>(Collections.singletonList(ILMSConstants.MDMS_ILMS_ORDER_TYPE));

        Map<String, List<String>> codes = commonUtils.getAttributeValues(tenantId, ILMSConstants.MDMS_ILMS_MOD_NAME, masterNames, "$.*.code",
                ILMSConstants.JSONPATH_CODES, request.getRequestInfo());

        if (null != codes) {
            validateMDMSData(masterNames, codes);
            validateCode(order, codes, errorMap);
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

    private void validateMasterDataForUpdate(Order order, OrderRequest request, Map<String, String> errorMap) {

        String caseId = order.getCaseId();
        CaseSearchCriteria criteria = CaseSearchCriteria.builder().id(Collections.singletonList(caseId)).build();
        CaseSearchResponse caseResponse = caseRepository.getILMSCaseData(criteria);
        String tenantId = caseResponse.getCaseList().get(0).getTenantId();
        List<String> masterNames = new ArrayList<>(Arrays.asList(ILMSConstants.MDMS_ILMS_STATUS_OF_COMPLIANCE, ILMSConstants.MDMS_ILMS_ORDER_TYPE));

        Map<String, List<String>> codes = commonUtils.getAttributeValues(tenantId, ILMSConstants.MDMS_ILMS_MOD_NAME, masterNames, "$.*.code",
                ILMSConstants.JSONPATH_CODES, request.getRequestInfo());

        if (null != codes) {
            validateMDMSData(masterNames, codes);
            validateCodesForUpdate(order, codes, errorMap);
        } else {
            errorMap.put("MASTER_FETCH_FAILED", "Couldn't fetch master data for validation");
        }

        if (!errorMap.isEmpty()) {
            throw new CustomException(errorMap);
        }
    }
}
