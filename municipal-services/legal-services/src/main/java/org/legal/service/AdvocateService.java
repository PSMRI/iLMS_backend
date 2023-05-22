package org.legal.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.legal.configs.LEGALConfiguration;
import org.legal.producer.Producer;
import org.legal.repository.AdvocateRepository;
import org.legal.repository.CaseRepository;
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

    public Advocate create(AdvocateRequest request){
        advocateEnrichmentService.advocateEnrichmentRequest(request);
        request.getAdvocate().setStatus(Status.ACTIVE);
        producer.push(legalConfiguration.getCreateAdvocateTopic(),request);
        return request.getAdvocate();
    }

    public Advocate update(AdvocateRequest advocateRequest) {
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
    }

    public AdvocateResponse advocateSearch(AdvocateSearchCriteria criteria) {
        List<Advocate> advocateList = new ArrayList<>();
        AdvocateResponse advocateResponse = null;
        advocateResponse = advocateRepository.getAdvocateDetails(criteria);
        if (!advocateResponse.getAdvocate().isEmpty()) {
            advocateList = advocateResponse.getAdvocate();
        } else {
            throw new CustomException(LegalErrorConstants.ADVOCATE_NOT_AVAILABLE, "Advocate is not Available");
        }
        return advocateResponse;
    }

}
