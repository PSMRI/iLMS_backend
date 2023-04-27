package org.legal.service;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.legal.configs.LEGALConfiguration;
import org.legal.producer.Producer;
import org.legal.repository.HearingRepository;
import org.legal.repository.JudgementRepository;
import org.legal.util.LegalErrorConstants;
import org.legal.validator.JudgementValidator;
import org.legal.web.model.*;
import org.legal.web.model.enums.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Service
public class JudgementService {
    @Autowired
    JudgementEnrichmentService judgementEnrichmentService;

    @Autowired
    Producer producer;

    @Autowired
    LEGALConfiguration ilmsConfiguration;

    @Autowired
    JudgementRepository judgementRepository;

    @Autowired
    JudgementValidator judgementValidator;

    @Autowired
    HearingRepository hearingRepository;

    public judgement create(JudgementRequest judgementRequest) {
        HearingResponse hearingResponse = null;
        HearingSearchCriteria criteria = HearingSearchCriteria.builder()
                .caseId(Collections.singletonList(judgementRequest.getJudgement().getCaseId())).build();
        hearingResponse = hearingRepository.getHearingDetails(criteria);
        String tenantId = hearingResponse.getHearingList().get(0).getTenantId();
        judgementRequest.getJudgement().setTenantId(tenantId);
        if (!hearingResponse.getHearingList().isEmpty()) {
            judgementRequest.getJudgement().setStatus(Status.ACTIVE);
            judgementValidator.createValidator(judgementRequest);
            judgementEnrichmentService.enrichJudgementCreateRequest(judgementRequest);
            producer.push(ilmsConfiguration.getCreateJudgementTopic(), judgementRequest);
        } else {
            throw new CustomException(LegalErrorConstants.HEARING_NOT_AVAILABLE, "Hearing is not Available for this Judgement");
        }
        return judgementRequest.getJudgement();
    }

    public JudgementResponse JudgementSearch(JudgementSearchCriteria criteria, RequestInfo requestInfo) {
        List<judgement> judgements = new LinkedList<>();
        JudgementResponse judgementResponse = null;
        judgementResponse = judgementRepository.getJudgementData(criteria);
        judgements = judgementResponse.getJudgementList();
        if (!judgements.isEmpty()) {
            judgementEnrichmentService.enrichJudgementSearch();
        } else {
            throw new CustomException(LegalErrorConstants.JUDGEMENT_NOT_AVAILABLE, "Judgement is not Available");
        }
        return judgementResponse;
    }

    public judgement updateJudgement(JudgementRequest judgementRequest) {
        if (judgementRequest.getJudgement().getId() != null) {
            JudgementSearchCriteria criteria = JudgementSearchCriteria.builder()
                    .id(Collections.singletonList(judgementRequest.getJudgement().getId())).build();
            JudgementResponse judgementResponse = judgementRepository.getJudgementData(criteria);
            if (!judgementResponse.getJudgementList().isEmpty()) {
                List<judgement> judgements = judgementResponse.getJudgementList();
                judgement oldJudgement = judgements.get(0);
                JudgementRequest finalRequest = judgementRepository.getMappedData(judgementRequest, oldJudgement);
                judgementValidator.updateValidator(finalRequest.getJudgement(), judgementRequest);
                producer.push(ilmsConfiguration.getUpdateJudgementTopic(), finalRequest);
            } else {
                throw new CustomException(LegalErrorConstants.JUDGEMENT_NOT_AVAILABLE, "Judgement is not Available");
            }
        } else {
            throw new CustomException(LegalErrorConstants.INVALID_TYPE_ERROR, "Id is mandatory");
        }
        return judgementRequest.getJudgement();
    }
}
