package org.legal.repository;

import lombok.extern.slf4j.Slf4j;
import org.legal.repository.querybuilder.AdvocateQueryBuilder;
import org.legal.repository.querybuilder.HearingQueryBuilder;
import org.legal.repository.rowmapper.AdvocateMapper;
import org.legal.repository.rowmapper.HearingRowMapper;
import org.legal.repository.rowmapper.PartyAdvRowMapper;
import org.legal.web.model.Advocate;
import org.legal.web.model.AdvocateResponse;
import org.legal.web.model.AdvocateSearchCriteria;
import org.legal.web.model.PartyAdv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.stereotype.Repository;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class AdvocateRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private HearingQueryBuilder hearingQueryBuilder;

    @Autowired
    private HearingRowMapper hearingRowMapper;

    @Autowired
    private AdvocateMapper advocateMapper;

    @Autowired
    private AdvocateQueryBuilder advocateQueryBuilder;

    @Autowired
    private CaseRepository caseRepository;

    @Autowired
    private PartyAdvRowMapper partyAdvRowMapper;

    public AdvocateResponse getAdvocateDetails(AdvocateSearchCriteria criteria) {
        List<Object> preparedStmtList = new ArrayList<>();
        String query = advocateQueryBuilder.getAdvocateSearchQuery(criteria, preparedStmtList);
        List<Advocate> advocateDetails = jdbcTemplate.query(query, preparedStmtList.toArray(), advocateMapper);
        AdvocateResponse advocateResponse = AdvocateResponse.builder().advocate(advocateDetails).totalCount(advocateMapper.getFullCount()).build();
        return advocateResponse;
    }

    public List<PartyAdv> getPartyAdv(String advocateId,String caseId) {
        List<Object> preparedStmtList = new ArrayList<>();
        preparedStmtList.add(advocateId);
        preparedStmtList.add(caseId);
        List<PartyAdv> parties = jdbcTemplate.query(advocateQueryBuilder.getPartyAdvQuery(), preparedStmtList.toArray(), partyAdvRowMapper);
        return parties;
    }

    public List<PartyAdv> getPartyCaseAdv(String partyId,String caseId,List<String > mobileNumber) {
        List<Object> preparedStmtList = new ArrayList<>();
//        preparedStmtList.add(partyId);
//        preparedStmtList.add(caseId);
//        preparedStmtList.add(mobileNumber);
        String query = advocateQueryBuilder.getAdvocatesOfParty(partyId,caseId,mobileNumber, preparedStmtList);
        List<PartyAdv> parties = jdbcTemplate.query(query, preparedStmtList.toArray(), partyAdvRowMapper);
        return parties;
    }

    public List<Advocate> getAdvocatesById(List<String> Ids){
        List<Object> preparedStmtList = new ArrayList<>();
        String query = advocateQueryBuilder.getAdvocatesById(Ids, preparedStmtList);
        List<Advocate> advocateDetails = jdbcTemplate.query(query, preparedStmtList.toArray(), advocateMapper);

        return advocateDetails;

    }

//    public List<Advocate> getAdvocates(List<String> mobileNumbers) {
//        String sql = advocateQueryBuilder.getAdvocates();
//        PreparedStatementSetter pss = preparedStatement -> {
//            Array mobileNumbersArray = preparedStatement.getConnection().createArrayOf("character_varying", mobileNumbers.toArray());
//            preparedStatement.setArray(1, mobileNumbersArray);
//        };
//        return jdbcTemplate.query(sql.replace("?", "(?)"), pss, advocateMapper);
//    }

    public List<PartyAdv> getPartyAdvByCaseIdAndPartyId(String partyId,String caseId) {
        List<Object> preparedStmtList = new ArrayList<>();
        preparedStmtList.add(partyId);
        preparedStmtList.add(caseId);
        List<PartyAdv> parties = jdbcTemplate.query(advocateQueryBuilder.getAdvocatesOfPartyByCaseIdAndPartyId(), preparedStmtList.toArray(), partyAdvRowMapper);
        return parties;
    }

}

