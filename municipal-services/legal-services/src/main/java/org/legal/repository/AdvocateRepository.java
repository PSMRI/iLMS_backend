package org.legal.repository;

import lombok.extern.slf4j.Slf4j;
import org.legal.repository.querybuilder.AdvocateQueryBuilder;
import org.legal.repository.querybuilder.CaseQueryBuilder;
import org.legal.repository.querybuilder.HearingQueryBuilder;
import org.legal.repository.rowmapper.AdvocateMapper;
import org.legal.repository.rowmapper.HearingRowMapper;
import org.legal.repository.rowmapper.PartyRowMapper;
import org.legal.web.model.Advocate;
import org.legal.web.model.AdvocateResponse;
import org.legal.web.model.AdvocateSearchCriteria;
import org.legal.web.model.Hearing;
import org.legal.web.model.HearingResponse;
import org.legal.web.model.HearingSearchCriteria;
import org.legal.web.model.Party;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Repository;

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

    public AdvocateResponse getAdvocateDetails(AdvocateSearchCriteria criteria) {
        List<Object> preparedStmtList = new ArrayList<>();
        String query = advocateQueryBuilder.getAdvocateSearchQuery(criteria, preparedStmtList);
        List<Advocate> advocateDetails = jdbcTemplate.query(query, preparedStmtList.toArray(), advocateMapper);
        AdvocateResponse advocateResponse = AdvocateResponse.builder().advocate(advocateDetails).totalCount(advocateMapper.getFullCount()).build();
        return advocateResponse;
    }
}

