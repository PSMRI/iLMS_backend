package org.ilms.validator;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.egov.tracer.model.CustomException;
import org.ilms.repository.ILMSCaseRepository;
import org.ilms.util.CommonUtils;
import org.ilms.util.ILMSConstants;
import org.ilms.util.ILMSErrorConstants;
import org.ilms.web.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Component
@Slf4j
public class DocumentValidator {
    @Autowired
    CommonUtils commonUtils;

    @Autowired
    private ILMSCaseRepository ilmsCaseRepository;

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

    public void createDocumentValidator(IlmsDocumentRequest request) {
        request.getDocument().forEach(document -> {
        if (!StringUtils.isNotBlank(document.getCaseId())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "caseId is mandatory [ " + document.getCaseId() + " ]");
        }
        if (!StringUtils.isNotBlank(document.getDocumentType())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                    "documentType is mandatory [ " + document.getDocumentType() + " ]");
        }
        if (!StringUtils.isNotBlank(document.getFileStoreId())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                    "fileStoreId is mandatory [ " + document.getFileStoreId() + " ]");
        }
        });

        // todo mdms validation for document create
        Map<String, String> errorMap = new HashMap<>();
        if (!errorMap.isEmpty()) {
            throw new CustomException(errorMap);
        }
        validateMasterData(request, errorMap);
    }

    private void validateMasterData(IlmsDocumentRequest request, Map<String, String> errorMap) {
        request.getDocument().forEach(document -> {
        ILMSCaseSearchCriteria criteria = ILMSCaseSearchCriteria.builder().id(Collections.singletonList(document.getCaseId())).build();
            ILMSCaseResponse ilmsCaseResponse = ilmsCaseRepository.getILMSCaseData(criteria);
        if (ilmsCaseResponse.getIlmsCases().size() <= 0){
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                    "caseDetails Not Found [ " + ilmsCaseResponse.getIlmsCases()+ " ]");
        }
        String tenantId = ilmsCaseResponse.getIlmsCases().get(0).getTenantId();
        List<String> masterNames = new ArrayList<>(Collections.singletonList(ILMSConstants.MDMS_ILMS_DOCUMENT_CATEGORY
        ));
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
