package org.legal.repository;

import lombok.extern.slf4j.Slf4j;
import org.egov.tracer.model.CustomException;
import org.egov.tracer.model.ServiceCallException;
import org.legal.repository.querybuilder.CaseQueryBuilder;
import org.legal.repository.querybuilder.HearingQueryBuilder;
import org.legal.repository.rowmapper.DocumentMapper;
import org.legal.repository.rowmapper.HearingRowMapper;
import org.legal.repository.rowmapper.PartyRowMapper;
import org.legal.util.LegalErrorConstants;
import org.legal.web.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@Repository
@Slf4j
public class HearingRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private HearingQueryBuilder hearingQueryBuilder;

    @Autowired
    private HearingRowMapper hearingRowMapper;

    @Autowired
    private PartyRowMapper partyRowMapper;

    @Autowired
    private CaseQueryBuilder caseQueryBuilder;

    @Autowired
    private CaseRepository caseRepository;

    @Autowired
    private AdvocateRepository advocateRepository;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private DocumentMapper documentMapper;
    @Autowired
    private RestTemplate restTemplate;

    public HearingResponse getHearingDetails(HearingSearchCriteria criteria) {
        List<Object> preparedStmtList = new ArrayList<>();
        String query = hearingQueryBuilder.getHearingSearchQuery(criteria, preparedStmtList);
        List<Hearing> hearingDetails = jdbcTemplate.query(query, preparedStmtList.toArray(), hearingRowMapper);
        for (Hearing singleHearing : hearingDetails) {
            singleHearing.setDocuments(getHearingDocumentList(singleHearing.getId()));
            String respondentAdvocateId = singleHearing.getRespondentAdvocate().getId();
            if (respondentAdvocateId != null) {
                List<Advocate> respondentAdvocateList = caseRepository.getAdvocateById(singleHearing.getRespondentAdvocate().getId());
                if (!respondentAdvocateList.isEmpty()) {
                    Advocate advocate = respondentAdvocateList.get(0);
                    singleHearing.setRespondentAdvocate(advocate);
                } else {
                    throw new CustomException(LegalErrorConstants.ADVOCATE_NOT_AVAILABLE, LegalErrorConstants.ADVOCATE_NOT_AVAILABLE_MSG);
                }
            } else {
                singleHearing.setRespondentAdvocate(null);
            }
            String petitionerAdvocateId = singleHearing.getPetitionerAdvocate().getId();
            if (petitionerAdvocateId != null) {
                List<Advocate> petitionerAdvocateList = caseRepository.getAdvocateById(singleHearing.getPetitionerAdvocate().getId());
                if (!petitionerAdvocateList.isEmpty()) {
                    Advocate petAdvocate = petitionerAdvocateList.get(0);
                    singleHearing.setPetitionerAdvocate(petAdvocate);
                } else {
                    throw new CustomException(LegalErrorConstants.ADVOCATE_NOT_AVAILABLE, LegalErrorConstants.ADVOCATE_NOT_AVAILABLE_MSG);
                }
            } else {
                singleHearing.setPetitionerAdvocate(null);
            }
        }
        HearingResponse hearingResponse = HearingResponse.builder().hearingList(hearingDetails).totalCount(hearingRowMapper.getFullCount()).build();
        return hearingResponse;

    }

    public List<Party> getHearing(String caseId) {
        List<Object> preparedStmtList = new ArrayList<>();
        preparedStmtList.add(caseId);
        List<Party> parties = jdbcTemplate.query(caseQueryBuilder.getPartyQuery(), preparedStmtList.toArray(), partyRowMapper);
        return parties;
    }

    public List<Document> getHearingDocumentList(String hearingId) {
        List<Object> preparedStmtList = new ArrayList<>();
        preparedStmtList.add(hearingId);
        List<Document> documentList = jdbcTemplate.query(hearingQueryBuilder.getDocQuery(), preparedStmtList.toArray(), documentMapper);
        return documentList;
    }

    public String getMaxValueOfHearing(String caseId) {
        int value = 1;
        String finalValue = null;
        List<Object> preparedStmtList = new ArrayList<>();
        preparedStmtList.add(caseId);
        List<String> maxHearingValue = jdbcTemplate.query(hearingQueryBuilder.getMaxHearingQuery(), preparedStmtList.toArray(),
                new SingleColumnRowMapper<>(String.class));
        try {
            if (maxHearingValue != null) {
                value = Integer.parseInt(maxHearingValue.get(0));
                finalValue = Integer.toString(value + 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            finalValue = Integer.toString(value);
        }
        return finalValue;
    }

    public String getTenantIdFromHearing(String id) {

        List<String> preparedStmtList = new ArrayList<>();
        preparedStmtList.add(id);
        List<String> tenantId = jdbcTemplate.query(hearingQueryBuilder.getTenantIdFromHearingQuery(), preparedStmtList.toArray(),
                new SingleColumnRowMapper<>(String.class));
        if (!tenantId.isEmpty()) {
            return tenantId.get(0);
        }
        return null;
    }

    public List<Party> getPartyFromPartyQuery(String caseId) {
        List<Party> partyList = caseRepository.getParty(caseId);
        return partyList;
    }

    public Object fetchResult(StringBuilder uri, Object request) {
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        Object response = null;
        try {
            response = restTemplate.postForObject(uri.toString(), request, Map.class);
        } catch (HttpClientErrorException e) {
            log.error("External Service threw an Exception: ", e);
            throw new ServiceCallException(e.getResponseBodyAsString());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Exception while fetching from searcher: ", e);
        }
        return response;
    }

}
