package org.ilms.validator;

import java.util.ArrayList;
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
import org.ilms.web.model.CaseResponse;
import org.ilms.web.model.CaseSearchCriteria;
import org.ilms.web.model.Document;
import org.ilms.web.model.DocumentRequest;
import org.ilms.web.model.DocumentSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DocumentValidator {
    @Autowired
    CommonUtils commonUtils;

    @Autowired
    private CaseRepository caseRepository;

    private static Map<String, String> validateCodes(Document document, Map<String, List<String>> codes, Map<String, String> errorMap) {

        if (document.getDocumentType() != null && !codes.get(ILMSConstants.MDMS_ILMS_DOCUMENT_CATEGORY).contains(document.getDocumentType())) {
            errorMap.put("Invalid DocumentType", "The DocumentType '" + document.getDocumentType() + "' does not exists");
        }
        return errorMap;
    }

    public void validDocSearch(DocumentSearchCriteria criteria) {
        int validate = 0;
        if (Objects.nonNull(criteria.getId())) {
            validate = 1;
        }
        if (Objects.nonNull(criteria.getDocumentType())) {
            validate = 1;
        }
        if (Objects.nonNull(criteria.getCaseId())) {
            validate = 1;
        }
        if (Objects.nonNull(criteria.getFileStoreId())) {
            validate = 1;
        }
        if (validate == 0) {
            throw new CustomException(ILMSErrorConstants.NO_SEARCH_PARAMETERS, "Some search parameter must be provided");
        }
    }

    public void createDocumentValidator(DocumentRequest request) {
        request.getDocument().forEach(document -> {
            if (!StringUtils.isNotBlank(document.getCaseId())) {
                throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "caseId is mandatory [ " + document.getCaseId() + " ]");
            }
            if (!StringUtils.isNotBlank(document.getDocumentType())) {
                throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "documentType is mandatory [ " + document.getDocumentType() + " ]");
            }
            if (!StringUtils.isNotBlank(document.getFileStoreId())) {
                throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "fileStoreId is mandatory [ " + document.getFileStoreId() + " ]");
            }
        });

        // todo mdms validation for document create
        Map<String, String> errorMap = new HashMap<>();
        if (!errorMap.isEmpty()) {
            throw new CustomException(errorMap);
        }
        validateMasterData(request, errorMap);
    }

    private void validateMasterData(DocumentRequest request, Map<String, String> errorMap) {
        request.getDocument().forEach(document -> {
            CaseSearchCriteria criteria = CaseSearchCriteria.builder().id(Collections.singletonList(document.getCaseId())).build();
            CaseResponse caseResponse = caseRepository.getILMSCaseData(criteria);
            if (caseResponse.getCaseList().size() <= 0) {
                throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "caseDetails Not Found [ " + caseResponse.getCaseList() + " ]");
            }
            String tenantId = caseResponse.getCaseList().get(0).getTenantId();
            List<String> masterNames = new ArrayList<>(Collections.singletonList(ILMSConstants.MDMS_ILMS_DOCUMENT_CATEGORY));
            Map<String, List<String>> codes = commonUtils.getAttributeValues(tenantId, ILMSConstants.MDMS_ILMS_MOD_NAME, masterNames, "$.*.code",
                    ILMSConstants.JSONPATH_CODES, request.getRequestInfo());
            if (null != codes) {
                validateMDMSData(masterNames, codes);
                validateCodes(document, codes, errorMap);
            } else {
                errorMap.put("MASTER_FETCH_FAILED", "Couldn't fetch master data for validation");
            }
            if (!errorMap.isEmpty()) {
                throw new CustomException(errorMap);
            }
        });
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
