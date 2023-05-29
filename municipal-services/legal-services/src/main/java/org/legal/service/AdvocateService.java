package org.legal.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.legal.configs.LEGALConfiguration;
import org.legal.producer.Producer;
import org.legal.repository.AdvocateRepository;
import org.legal.repository.CaseRepository;
import org.legal.repository.rowmapper.AdvocateMapper;
import org.legal.util.AdvocateUtils;
import org.legal.util.CaseUtils;
import org.legal.util.LegalErrorConstants;
import org.legal.web.model.Advocate;
import org.legal.web.model.AdvocateRequest;
import org.legal.web.model.AdvocateResponse;
import org.legal.web.model.AdvocateSearchCriteria;
import org.legal.web.model.Case;
import org.legal.web.model.CaseRequest;
import org.legal.web.model.CaseResponse;
import org.legal.web.model.CaseSearchCriteria;
import org.legal.web.model.Hearing;
import org.legal.web.model.HearingResponse;
import org.legal.web.model.HearingSearchCriteria;
import org.legal.web.model.enums.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AdvocateService {

    @Autowired
    private Producer producer;

    @Autowired
    private AdvocateRepository advocateRepository;

    @Autowired
    private CaseRepository caseRepository;

    @Autowired
    private AdvocateEnrichmentService advocateEnrichmentService;
    @Autowired
    private LEGALConfiguration legalConfiguration;

    @Autowired
    private AdvocateUtils advocateUtils;

    @Autowired
    private AdvocateMapper advocateMapper;

    public Advocate create(AdvocateRequest request) {
        try {
            AdvocateSearchCriteria criteria = new AdvocateSearchCriteria();
            criteria.setContactNumber(request.getAdvocate().getContactNumber());
            AdvocateResponse advocateResponse = advocateRepository.getAdvocateDetails(criteria);
            if (!advocateResponse.getAdvocate().isEmpty()) {
                return advocateResponse.getAdvocate().get(0);
            } else {
                advocateEnrichmentService.advocateEnrichmentRequest(request);
                request.getAdvocate().setStatus(Status.ACTIVE);
                producer.push(legalConfiguration.getCreateAdvocateTopic(), request);
            }
            return request.getAdvocate();
        } catch (Exception e) {
            e.printStackTrace();
            log.error(LegalErrorConstants.ADVOCATE_CREATE_FAILED_MSG, e.getMessage());
            throw new CustomException(LegalErrorConstants.ADVOCATE_CREATE_FAILED, LegalErrorConstants.ADVOCATE_CREATE_FAILED);
        }
    }

    public Advocate update(AdvocateRequest advocateRequest) {
        try {

            if (advocateRequest.getAdvocate().getId() != null) {
                AdvocateSearchCriteria criteria = AdvocateSearchCriteria.builder().id(advocateRequest.getAdvocate().getId()).build();
                List<Advocate> advocateList = caseRepository.getAdvocateById(criteria.getId());
                if (!advocateList.isEmpty()) {
                    AdvocateRequest updatedAdvocateRequest = advocateUtils.prepareObjectMapperForUpdate(advocateList.get(0), advocateRequest);
                    producer.push(legalConfiguration.getUpdateAdvocateTopic(), updatedAdvocateRequest);
                    //                todo : notification has send to all the officers who has worked on this case.
                    advocateRequest.setAdvocate(updatedAdvocateRequest.getAdvocate());
                } else {
                    throw new CustomException(LegalErrorConstants.ADVOCATE_NOT_AVAILABLE, "Advocate is not Available");
                }
            } else {
                throw new CustomException(LegalErrorConstants.ADVOCATE_NOT_AVAILABLE, "id is mandatory");
            }
            return advocateRequest.getAdvocate();
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            log.error(LegalErrorConstants.ADVOCATE_UPDATE_FAILED_MSG, e.getMessage());
            throw new CustomException(LegalErrorConstants.ADVOCATE_UPDATE_FAILED, LegalErrorConstants.ADVOCATE_UPDATE_FAILED_MSG);
        }
    }

    public AdvocateResponse advocateSearch(AdvocateSearchCriteria criteria) {
        try {
            AdvocateResponse advocateResponse = null;
            advocateResponse = advocateRepository.getAdvocateDetails(criteria);
            if (!advocateResponse.getAdvocate().isEmpty()) {
                return advocateResponse;
            } else {
                throw new CustomException(LegalErrorConstants.ADVOCATE_NOT_AVAILABLE, LegalErrorConstants.ADVOCATE_NOT_AVAILABLE_MSG);
            }
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            log.error(LegalErrorConstants.ADVOCATE_SEARCH_FAILED, e.getMessage());
            throw new CustomException(LegalErrorConstants.ADVOCATE_SEARCH_FAILED, LegalErrorConstants.ADVOCATE_SEARCH_FAILED_MSG);
        }
    }
}
