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
import org.ilms.web.model.CaseResponse;
import org.ilms.web.model.CaseSearchCriteria;
import org.ilms.web.model.Hearing;
import org.ilms.web.model.HearingRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class HearingValidator {
    @Autowired
    CommonUtils commonUtils;

    @Autowired
    private CaseRepository caseRepository;

    private static Map<String, String> validateCodes(Hearing hearing, Map<String, List<String>> codes, Map<String, String> errorMap) {

        if (hearing.getCourt().getCourtName() != null && !codes.get(ILMSConstants.MDMS_ILMS_COURT_NAME).contains(hearing.getCourt().getCourtName())) {
            errorMap.put("Invalid CourtName", "The CourtName '" + hearing.getCourt().getCourtName() + "' does not exists");
        }
        if (hearing.getCourt().getDistrict() != null && !codes.get(ILMSConstants.MDMS_ILMS_DISTRICT).contains(hearing.getCourt().getDistrict())) {
            errorMap.put("Invalid District", "The District '" + hearing.getCourt().getDistrict() + "' does not exists");
        }
        if (hearing.getCourt().getState() != null && !codes.get(ILMSConstants.MDMS_ILMS_STATE).contains(hearing.getCourt().getState())) {
            errorMap.put("Invalid State", "The State '" + hearing.getCourt().getState() + "' does not exists");
        }
        if (hearing.getCourt().getBench() != null && !codes.get(ILMSConstants.MDMS_ILMS_BENCH).contains(hearing.getCourt().getBench())) {
            errorMap.put("Invalid Bench", "The Bench '" + hearing.getCourt().getBench() + "' does not exists");
        }
        if (hearing.getCourt().getDivision() != null && !codes.get(ILMSConstants.MDMS_ILMS_DIVISION).contains(hearing.getCourt().getDivision())) {
            errorMap.put("Invalid Division", "The Division '" + hearing.getCourt().getDivision() + "' does not exists");
        }

        return errorMap;
    }

    public void createValidator(HearingRequest hearingDetailsRequest) {

        if (!StringUtils.isNotBlank(hearingDetailsRequest.getHearing().getCaseId())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "CaseId is mandatory");
        }
        if (!StringUtils.isNotBlank(hearingDetailsRequest.getHearing().getJudgeName())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "judgeName is mandatory");
        }
        if (!StringUtils.isNotBlank(hearingDetailsRequest.getHearing().getHearingDate().toString())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "hearingDate is mandatory");
        }
        if (!StringUtils.isNotBlank(hearingDetailsRequest.getHearing().getBusinessDate().toString())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "businessDate is mandatory");
        }
        if (!StringUtils.isNotBlank(hearingDetailsRequest.getHearing().getHearingPurpose())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "hearingPurpose is mandatory");
        }
        if (!StringUtils.isNotBlank(hearingDetailsRequest.getHearing().getRequiredOfficer())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "requiredOfficer is mandatory");
        }
        if (Objects.nonNull(hearingDetailsRequest.getHearing().getAffidavitFilingDate())) {
            if (StringUtils.isNotBlank(hearingDetailsRequest.getHearing().getAffidavitFilingDate().toString())) {
                throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "affidavitFilingDate is not allowed while creating hearing ");
            }
        }
        if (!StringUtils.isNotBlank(hearingDetailsRequest.getHearing().getAffidavitFilingDueDate().toString())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "affidavitFilingDueDate is mandatory");
        }
        if (!StringUtils.isNotBlank(hearingDetailsRequest.getHearing().getCaseNumber())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "CaseNumber is mandatory");
        }
        if (StringUtils.isNotBlank(hearingDetailsRequest.getHearing().getOathNumber())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "OathNumber is not allowed while creating hearing");
        }
        if (!StringUtils.isNotBlank(hearingDetailsRequest.getHearing().getNextHearingDate().toString())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "nextHearingDate is mandatory");
        }
        if (!StringUtils.isNotBlank(hearingDetailsRequest.getHearing().getFirstHearingDate().toString())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "FirstHearingDate is mandatory");
        }

        if (!StringUtils.isNotBlank(hearingDetailsRequest.getHearing().getIsPresenceRequired().toString())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "isPresenceRequired is mandatory");
        }
        if (!StringUtils.isNotBlank(hearingDetailsRequest.getHearing().getHearingType())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "hearingType is mandatory");
        }
        if (!StringUtils.isNotBlank(hearingDetailsRequest.getHearing().getDepartmentOfficer())) {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "departmentOfficer is mandatory");
        }
        //checking respondent details
        if (Objects.nonNull(hearingDetailsRequest.getHearing().getRespondent())) {
            //Setting Data For Respondent Advocate
            if (Objects.nonNull(hearingDetailsRequest.getHearing().getRespondent().getAdvocate())) {
                if (!StringUtils.isNotBlank(hearingDetailsRequest.getHearing().getRespondent().getAdvocate().getFirstName())) {
                    throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "firstName for Advocate in Respondent is mandatory");
                }
                if (!StringUtils.isNotBlank(hearingDetailsRequest.getHearing().getRespondent().getAdvocate().getLastName())) {
                    throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "lastName for Advocate in Respondent is mandatory");
                }
                if (!StringUtils.isNotBlank(hearingDetailsRequest.getHearing().getRespondent().getAdvocate().getContactNumber())) {
                    throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "contactNumber for Advocate in Respondent is mandatory");
                }
            } else {
                throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "Advocate in Respondent is mandatory");
            }
        } else {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "Respondent is mandatory");
        }
        //checking petitioner details
        if (Objects.nonNull(hearingDetailsRequest.getHearing().getPetitioner())) {
            //Setting Data For Petitioner Advocate
            if (Objects.nonNull(hearingDetailsRequest.getHearing().getPetitioner().getAdvocate())) {
                if (!StringUtils.isNotBlank(hearingDetailsRequest.getHearing().getPetitioner().getAdvocate().getFirstName())) {
                    throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "firstName for Advocate in Petitioner is mandatory");
                }
                if (!StringUtils.isNotBlank(hearingDetailsRequest.getHearing().getPetitioner().getAdvocate().getLastName())) {
                    throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "lastName for Advocate in Petitioner is mandatory");
                }
                if (!StringUtils.isNotBlank(hearingDetailsRequest.getHearing().getPetitioner().getAdvocate().getContactNumber())) {
                    throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "contactNumber for Advocate in Petitioner is mandatory");
                }
            } else {
                throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "Advocate in Petitioner is mandatory");
            }
        } else {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "Petitioner is mandatory");
        }
        if (Objects.nonNull(hearingDetailsRequest.getHearing().getCourt())) {
            if (!StringUtils.isNotBlank(hearingDetailsRequest.getHearing().getCourt().getCourtName())) {
                throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "courtName is mandatory");
            }
            if (!StringUtils.isNotBlank(hearingDetailsRequest.getHearing().getCourt().getCourtNumber())) {
                throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "courtNumber is mandatory");
            }
            if (!StringUtils.isNotBlank(hearingDetailsRequest.getHearing().getCourt().getDistrict())) {
                throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "District for Court is mandatory");
            }
            if (!StringUtils.isNotBlank(hearingDetailsRequest.getHearing().getCourt().getState())) {
                throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "state for court is mandatory");
            }
            if (!StringUtils.isNotBlank(hearingDetailsRequest.getHearing().getCourt().getDivision())) {
                throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "division for court is mandatory");
            }
        }
        //checking payment request
        if (Objects.nonNull(hearingDetailsRequest.getHearing().getPayment())) {
            if (!StringUtils.isNotBlank(hearingDetailsRequest.getHearing().getPayment().getFineImposedDate().toString())) {
                throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "FineImposedDate is mandatory");
            }
            if (!StringUtils.isNotBlank(hearingDetailsRequest.getHearing().getPayment().getFineDueDate().toString())) {
                throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "fineDueDate is mandatory");
            }
            if (!StringUtils.isNotBlank(hearingDetailsRequest.getHearing().getPayment().getFineAmount())) {
                throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "fineAmount is mandatory");
            }
        } else {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "Payment is mandatory");
        }

        Map<String, String> errorMap = new HashMap<>();
        if (!errorMap.isEmpty()) {
            throw new CustomException(errorMap);
        }
        validateMasterData(hearingDetailsRequest.getHearing(), hearingDetailsRequest, errorMap);
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
        CaseResponse caseResponse = caseRepository.getILMSCaseData(criteria);
        String tenantId = caseResponse.getCases().get(0).getTenantId();

        List<String> masterNames = new ArrayList<>(
                Arrays.asList(ILMSConstants.MDMS_ILMS_COURT_NAME, ILMSConstants.MDMS_ILMS_DISTRICT, ILMSConstants.MDMS_ILMS_STATE,
                        ILMSConstants.MDMS_ILMS_BENCH, ILMSConstants.MDMS_ILMS_DIVISION));

        Map<String, List<String>> codes = commonUtils.getAttributeValues(tenantId, ILMSConstants.MDMS_ILMS_MOD_NAME, masterNames, "$.*.code",
                ILMSConstants.JSONPATH_CODES, request.getRequestInfo());

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
