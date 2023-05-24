package org.legal.validator;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.egov.tracer.model.CustomException;
import org.legal.repository.CaseRepository;
import org.legal.util.CommonUtils;
import org.legal.util.LEGALConstants;
import org.legal.util.LegalErrorConstants;
import org.legal.web.model.CaseResponse;
import org.legal.web.model.CaseSearchCriteria;
import org.legal.web.model.Hearing;
import org.legal.web.model.HearingRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Component
@Slf4j
public class HearingValidator {
    @Autowired
    CommonUtils commonUtils;

    @Autowired
    private CaseRepository caseRepository;

    private static Map<String, String> validateCodes(Hearing hearing, Map<String, List<String>> codes, Map<String, String> errorMap) {

        if (Objects.nonNull(hearing.getBench()) && !codes.get(LEGALConstants.MDMS_LEGAL_BENCH).contains(hearing.getBench())) {
            errorMap.put("Invalid Bench", "The Bench '" + hearing.getBench() + "' does not exists");
        }
        if (Objects.nonNull(hearing.getHearingType()) && !codes.get(LEGALConstants.MDMS_LEGAL_HEARING_TYPE).contains(hearing.getHearingType())) {
            errorMap.put("Invalid HearingType", "The Type '" + hearing.getHearingType() + "' does not exists");
        }

        return errorMap;
    }

    public void createValidator(HearingRequest hearingRequest) {

        if (!StringUtils.isNotBlank(hearingRequest.getHearing().getCaseId())) {
            throw new CustomException(LegalErrorConstants.INVALID_TYPE_ERROR, "CaseId is mandatory");
        }

        if (!StringUtils.isNotBlank(hearingRequest.getHearing().getCaseNumber())) {
            throw new CustomException(LegalErrorConstants.INVALID_TYPE_ERROR, "CaseNumber is mandatory");
        }

        Map<String, String> errorMap = new HashMap<>();
        if (!errorMap.isEmpty()) {
            throw new CustomException(errorMap);
        }
        validateMasterData(hearingRequest.getHearing(), hearingRequest, errorMap);
    }

    public void updateValidator(Hearing hearing, HearingRequest hearingRequest) {
        Map<String, String> errorMap = new HashMap<>();
        if (!errorMap.isEmpty()) {
            throw new CustomException(errorMap);
        }
        validateMasterData(hearing, hearingRequest, errorMap);
    }

    private void validateMasterData(Hearing hearing, HearingRequest request, Map<String, String> errorMap) {

        String caseId = hearing.getCaseId();
        CaseSearchCriteria criteria = CaseSearchCriteria.builder().id(Collections.singletonList(caseId)).build();
        CaseResponse caseResponse = caseRepository.getLegalCaseData(criteria);
        String tenantId = caseResponse.getCaseList().get(0).getTenantId();

        List<String> masterNames = new ArrayList<>(

                Arrays.asList(LEGALConstants.MDMS_LEGAL_COURT_NAME,
                        LEGALConstants.MDMS_LEGAL_BENCH, LEGALConstants.MDMS_LEGAL_HEARING_TYPE));

        Map<String, List<String>> codes = commonUtils.getAttributeValues(tenantId, LEGALConstants.MDMS_LEGAL_MOD_NAME, masterNames, "$.*.code",
                LEGALConstants.JSONPATH_CODES, request.getRequestInfo());

        if (null != codes) {
            validateMDMSData(masterNames, codes);
            validateCodes(hearing, codes, errorMap);
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
