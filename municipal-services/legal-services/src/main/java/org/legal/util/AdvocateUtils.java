package org.legal.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.egov.common.contract.request.User;
import org.legal.configs.LEGALConfiguration;
import org.legal.repository.AdvocateRepository;
import org.legal.repository.CaseRepository;
import org.legal.service.AdvocateEnrichmentService;
import org.legal.service.CaseEnrichmentService;
import org.legal.web.model.Advocate;
import org.legal.web.model.AdvocateRequest;
import org.legal.web.model.AdvocateResponse;
import org.legal.web.model.AdvocateSearchCriteria;
import org.legal.web.model.AuditDetails;
import org.legal.web.model.Case;
import org.legal.web.model.CaseRequest;
import org.legal.web.model.CaseResponse;
import org.legal.web.model.CaseSearchCriteria;
import org.legal.web.model.Document;
import org.legal.web.model.Party;
import org.legal.web.model.enums.CreationReason;
import org.legal.web.model.enums.PartyType;
import org.legal.web.model.workflow.ProcessInstance;
import org.legal.web.model.workflow.ProcessInstanceRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class AdvocateUtils {
    @Autowired
    private AdvocateEnrichmentService advocateEnrichmentService;

    @Autowired
    private LEGALConfiguration legalConfiguration;

    @Autowired
    private CaseRepository caseRepository;

    @Autowired
    private AdvocateRepository advocateRepository;

    @Autowired
    private CommonUtils commonUtils;

    public AdvocateRequest prepareObjectMapperForUpdate(Advocate oldData, AdvocateRequest advocateRequest) {
        final AdvocateRequest request = new AdvocateRequest();

        if (!StringUtils.isEmpty(advocateRequest.getAdvocate().getTenantId())) {
            oldData.setTenantId(advocateRequest.getAdvocate().getTenantId());
        }
        if (!StringUtils.isEmpty(advocateRequest.getAdvocate().getFirstName())) {
            oldData.setFirstName(advocateRequest.getAdvocate().getFirstName());
        }
        if (!StringUtils.isEmpty(advocateRequest.getAdvocate().getLastName())) {
            oldData.setLastName(advocateRequest.getAdvocate().getLastName());
        }
        if (!StringUtils.isEmpty(advocateRequest.getAdvocate().getContactNumber())) {
            oldData.setContactNumber(advocateRequest.getAdvocate().getContactNumber());
        }
        if (!StringUtils.isEmpty(advocateRequest.getAdvocate().getStatus())) {
            oldData.setStatus(advocateRequest.getAdvocate().getStatus());
        }
        request.setAdvocate(oldData);
        request.setRequestInfo(advocateRequest.getRequestInfo());
        advocateEnrichmentService.enrichAdvocateUpdateRequest(request);
        return request;
    }
    public CaseRequest setPartyDetailsInResponse(CaseRequest caseRequest){
        caseRequest.getCaseObj().getParties().forEach(party -> {
            if (Objects.nonNull(party.getAdvocate()) && (party.getPartyType().equals(PartyType.PETITIONER.toString()) || party.getPartyType().equals(PartyType.RESPONDENT.toString()))) {
                List<String> advocatesIdsReq = party.getAdvocate().stream().map(Advocate::getContactNumber).collect(Collectors.toList());
                AdvocateSearchCriteria criteria = new AdvocateSearchCriteria();
                List<Advocate> advocateList = new ArrayList<>();
                for (String advContact : advocatesIdsReq) {
                    criteria.setContactNumber(advContact);
                    AdvocateResponse advocatesPresentInDB = advocateRepository.getAdvocateDetails(criteria);
                    if (!advocatesPresentInDB.getAdvocate().isEmpty()) {
                        advocateList.add(advocatesPresentInDB.getAdvocate().get(0));
                    }
                }
                party.setAdvocate(advocateList);
            }
        });
        caseRequest.getCaseObj().setParties(caseRequest.getCaseObj().getParties());
        return caseRequest;
    }
}
