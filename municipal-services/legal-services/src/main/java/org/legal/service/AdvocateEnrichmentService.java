package org.legal.service;

import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.legal.configs.LEGALConfiguration;
import org.legal.repository.IdGenRepository;
import org.legal.util.AdvocateUtils;
import org.legal.util.LegalErrorConstants;
import org.legal.web.model.Advocate;
import org.legal.web.model.AdvocateRequest;
import org.legal.web.model.AuditDetails;
import org.legal.web.model.idGen.IdResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AdvocateEnrichmentService {
    @Autowired
    private LEGALConfiguration legalConfiguration;

    @Autowired
    private AdvocateUtils advocateUtils;

    @Autowired
    private IdGenRepository idGenRepository;


    public void advocateEnrichmentRequest(AdvocateRequest request) {
        RequestInfo requestInfo = request.getRequestInfo();
        Advocate advocate = request.getAdvocate();
        setIdgenIds(request);
        AuditDetails auditDetails = advocateUtils.getAuditDetails(request.getRequestInfo().getUserInfo().getUuid(), true);
        request.getAdvocate().setAuditDetails(auditDetails);
        advocate.setAuditDetails(auditDetails);
    }

    private void setIdgenIds(AdvocateRequest request) {
        RequestInfo requestInfo = request.getRequestInfo();
        String tenantId = request.getAdvocate().getTenantId();
        Advocate advocate = request.getAdvocate();
        List<String> advocateId = getIdList(requestInfo, tenantId, legalConfiguration.getPetitionerAdvocateIdgenName(), legalConfiguration.getPetitionerAdvocateIdgenFormat(), 1);
        ListIterator<String> advItr = advocateId.listIterator();
        advocate.setId(advItr.next());
    }


    private List<String> getIdList(RequestInfo requestInfo, String tenantId, String idName, String idformat, int count) {
        List<IdResponse> idResponses = idGenRepository.getId(requestInfo, tenantId, idName, idformat, count).getIdResponses();
        if (CollectionUtils.isEmpty(idResponses)) {
            throw new CustomException(LegalErrorConstants.IDGEN_ERROR, "No ids returned from idgen Service");
        }
        return idResponses.stream().map(IdResponse::getId).collect(Collectors.toList());
    }

    public void enrichAdvocateUpdateRequest(AdvocateRequest advocateRequest) {
        RequestInfo requestInfo = advocateRequest.getRequestInfo();
        Advocate advocate = advocateRequest.getAdvocate();
        AuditDetails auditDetails = advocateUtils.getAuditDetails(requestInfo.getUserInfo().getUuid(), false);
        advocateRequest.getAdvocate().setAuditDetails(auditDetails);
        advocate.setAuditDetails(auditDetails);
    }

}
