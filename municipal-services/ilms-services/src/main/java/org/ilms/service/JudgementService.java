package org.ilms.service;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.ilms.configs.ILMSConfiguration;
import org.ilms.producer.Producer;
import org.ilms.repository.HearingRepository;
import org.ilms.repository.JudgementRepository;
import org.ilms.util.ILMSErrorConstants;
import org.ilms.validator.JudgementValidator;
import org.ilms.web.model.HearingResponse;
import org.ilms.web.model.HearingSearchCriteria;
import org.ilms.web.model.Judgement;
import org.ilms.web.model.JudgementRequest;
import org.ilms.web.model.JudgementResponse;
import org.ilms.web.model.JudgementSearchCriteria;
import org.ilms.web.model.enums.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JudgementService {
    @Autowired
    JudgementEnrichmentService judgementEnrichmentService;

    @Autowired
    Producer producer;

    @Autowired
    ILMSConfiguration ilmsConfiguration;

    @Autowired
    JudgementRepository judgementRepository;

    @Autowired
    JudgementValidator judgementValidator;

    @Autowired
    HearingRepository hearingRepository;

    public Judgement create(JudgementRequest judgementRequest) {
        HearingResponse hearingResponse = null;
        HearingSearchCriteria criteria = HearingSearchCriteria.builder()
                                                              .caseId(Collections.singletonList(judgementRequest.getJudgement().getCaseId())).build();
        hearingResponse = hearingRepository.getHearingDetails(criteria);
        if (!hearingResponse.getHearingList().isEmpty()) {
            judgementRequest.getJudgement().setStatus(Status.ACTIVE);
            judgementValidator.createValidator(judgementRequest);
            judgementEnrichmentService.enrichJudgementCreateRequest(judgementRequest);
            producer.push(ilmsConfiguration.getCreateJudgementTopic(), judgementRequest);
        } else {
            throw new CustomException(ILMSErrorConstants.HEARING_NOT_AVAILABLE, "Hearing is not Available for this Judgement");
        }
        return judgementRequest.getJudgement();
    }

    public JudgementResponse JudgementSearch(JudgementSearchCriteria criteria, RequestInfo requestInfo) {
        List<Judgement> judgements = new LinkedList<>();
        JudgementResponse judgementResponse = null;
        judgementResponse = judgementRepository.getJudgementData(criteria);
        judgements = judgementResponse.getJudgementList();
        if (!judgements.isEmpty()) {
            judgementEnrichmentService.enrichJudgementSearch();
        } else {
            throw new CustomException(ILMSErrorConstants.JUDGEMENT_NOT_AVAILABLE, "Judgement is not Available");
        }
        return judgementResponse;
    }

    public Judgement updateJudgement(JudgementRequest judgementRequest) {
        if (judgementRequest.getJudgement().getId() != null) {
            JudgementSearchCriteria criteria = JudgementSearchCriteria.builder()
                                                                      .id(Collections.singletonList(judgementRequest.getJudgement().getId())).build();
            JudgementResponse judgementResponse = judgementRepository.getJudgementData(criteria);
            if (!judgementResponse.getJudgementList().isEmpty()) {
                List<Judgement> judgements = judgementResponse.getJudgementList();
                Judgement oldJudgement = judgements.get(0);
                JudgementRequest finalRequest = judgementRepository.getMappedData(judgementRequest, oldJudgement);
                judgementValidator.updateValidator(finalRequest.getJudgement(), judgementRequest);
                producer.push(ilmsConfiguration.getUpdateJudgementTopic(), finalRequest);
            } else {
                throw new CustomException(ILMSErrorConstants.JUDGEMENT_NOT_AVAILABLE, "Judgement is not Available");
            }
        } else {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "Id is mandatory");
        }
        return judgementRequest.getJudgement();
    }
}
