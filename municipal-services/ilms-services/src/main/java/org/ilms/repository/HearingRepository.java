package org.ilms.repository;

import java.util.ArrayList;
import java.util.List;
import org.ilms.repository.querybuilder.HearingQueryBuilder;
import org.ilms.repository.querybuilder.ILMSCaseQueryBuilder;
import org.ilms.repository.rowmapper.HearingRowMapper;
import org.ilms.repository.rowmapper.PartyRowMapper;
import org.ilms.util.ILMSConstants;
import org.ilms.web.model.*;
import org.ilms.web.model.enums.PartyType;
import org.ilms.web.model.enums.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Repository;
import lombok.extern.slf4j.Slf4j;

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
    private ILMSCaseQueryBuilder ilmsCaseQueryBuilder;

    @Autowired
    private ILMSCaseRepository ilmsCaseRepository;

    public HearingResponse getHearingDetails(HearingSearchCriteria criteria) {
            List<Object> preparedStmtList = new ArrayList<>();
            String query = hearingQueryBuilder.getHearingSearchQuery(criteria, preparedStmtList);
            List<Hearing> hearingDetails = jdbcTemplate.query(query, preparedStmtList.toArray(), hearingRowMapper);
            for (Hearing singleHearing : hearingDetails) {
                List<ILMSParty> partyList = getHearing(singleHearing.getCaseId());
                for (ILMSParty party : partyList) {
                    if (party.getPartyType().equals(PartyType.RESPONDENT.toString())  && party.getStatus()== Status.ACTIVE) {
                        singleHearing.setRespondent(party);
                    } else if(party.getStatus()==Status.ACTIVE){
                        singleHearing.setPetitioner(party);
                    }
                }
            }
            HearingResponse hearingResponse = HearingResponse.builder().hearingDetails(hearingDetails).totalCount(hearingRowMapper.getFullCount()).build();
            return hearingResponse;
    }

    public List<ILMSParty> getHearing(String caseId) {
        List<Object> preparedStmtList = new ArrayList<>();
        preparedStmtList.add(caseId);
        List<ILMSParty> parties = jdbcTemplate.query(ilmsCaseQueryBuilder.getPartyQuery(), preparedStmtList.toArray(), partyRowMapper);
        return parties;
    }

    public String getMaxValueOfHearing(String caseId){
        int value = 1;
        String finalValue = null;
        List<Object> preparedStmtList = new ArrayList<>();
        preparedStmtList.add(caseId);
        List<String> maxHearingValue = jdbcTemplate.query(hearingQueryBuilder.getMaxHearingQuery(), preparedStmtList.toArray(), new SingleColumnRowMapper<>(String.class));
        try{
            if (maxHearingValue != null) {
                value = Integer.parseInt(maxHearingValue.get(0));
                finalValue = Integer.toString(value+1);
            }
        }
        catch (Exception e){
            finalValue = Integer.toString(value);
        }
        return finalValue;
    }

    public List<ILMSParty> getGetFromPartyQuery(String caseId){
        List<ILMSParty> partyList = ilmsCaseRepository.getParty(caseId);
        return partyList;
    }

}
