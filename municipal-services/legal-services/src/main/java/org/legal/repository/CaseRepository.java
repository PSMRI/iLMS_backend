package org.legal.repository;

import lombok.extern.slf4j.Slf4j;
import org.legal.repository.querybuilder.CaseQueryBuilder;
import org.legal.repository.querybuilder.CountQueryBuilder;
import org.legal.repository.rowmapper.*;
import org.legal.web.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Repository
@Slf4j
public class CaseRepository {
    @Autowired
    private CaseQueryBuilder caseQueryBuilder;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CaseRowMapper caseRowMapper;

    @Autowired
    private PartyRowMapper partyRowMapper;

    @Autowired
    private CountQueryBuilder countQueryBuilder;

    @Autowired
    private DocumentMapper documentMapper;

    @Autowired
    private AdvocateMapper advocateMapper;

    @Autowired
    private ActRowMapper actRowMapper;

    @Autowired
    private CourtRowMapper courtRowMapper;

    public CaseResponse getLegalCaseData(CaseSearchCriteria criteria) {
        List<Object> preparedStmtList = new ArrayList<>();
        List<String> ids = getUUID(criteria);
        if (Objects.nonNull(criteria.getId())) {
            for (String id : ids) {
                if (id.equals(criteria.getId())) {
                    criteria = CaseSearchCriteria.builder().id(Collections.singletonList(id)).caseNumber(criteria.getCaseNumber()).build();
                }
            }
        } else {
            criteria = CaseSearchCriteria.builder().id(ids).uuid(criteria.getUuid()).caseNumber(criteria.getCaseNumber()).applicationStatus(criteria.getApplicationStatus()).limit(criteria.getLimit()).offset(criteria.getOffset()).sortBy(criteria.getSortBy()).type(criteria.getType()).category(criteria.getCategory()).sortOrder(criteria.getSortOrder()).build();
        }
        String query = caseQueryBuilder.getLegalCaseSearchQuery(criteria, preparedStmtList);
        List<Case> caseList = jdbcTemplate.query(query, preparedStmtList.toArray(), caseRowMapper);
        for (Case singleCase : caseList) {
            singleCase.setDocuments(getDocumentList(singleCase.getId()));
            List<Party> partyList = getParty(singleCase.getId());
            List<Party> party1 = new ArrayList<>();
            for (Party party : partyList) {
                party1.add(party);
                singleCase.setParties(party1);
            }
            singleCase.setAct(getAct(singleCase.getId()));
            singleCase.setCourt(getcourt(singleCase.getId()));
        }
        CaseResponse caseResponse = CaseResponse.builder().caseList(caseList).totalCount(caseRowMapper.getFullCount()).build();
        return caseResponse;
    }


    public List<Party> getParty(String caseId) {
        List<Object> preparedStmtList = new ArrayList<>();
        preparedStmtList.add(caseId);
        List<Party> parties = jdbcTemplate.query(caseQueryBuilder.getPartyQuery(), preparedStmtList.toArray(), partyRowMapper);
        return parties;
    }

    public List<Act> getAct(String caseId) {
        List<Object> preparedStmtList = new ArrayList<>();
        preparedStmtList.add(caseId);
        List<Act> acts = jdbcTemplate.query(caseQueryBuilder.getActQuery(), preparedStmtList.toArray(), actRowMapper);
        return acts;
    }

    public Court getcourt(String caseId) {
        List<Object> preparedStmtList = new ArrayList<>();
        preparedStmtList.add(caseId);
        Court court = jdbcTemplate.query(caseQueryBuilder.getCourtQuery(), preparedStmtList.toArray(), courtRowMapper);
        return court;
    }

    public Integer getCaseCount(CaseSearchCriteria criteria) {
        CountRequest query = caseQueryBuilder.getTotalCount(criteria);
        String count = this.jdbcTemplate.queryForObject(query.getQuery(), query.getPreparedStatement().toArray(), String.class);
        return Integer.parseInt(count);
    }

    public Integer getCountOfUser(String user) {
        List<Object> preparedStmtList = new ArrayList<>();
        preparedStmtList.add(user);
        String count = jdbcTemplate.queryForObject(countQueryBuilder.getCountQuery(), preparedStmtList.toArray(), String.class);
        return Integer.parseInt(count);
    }

    public List<String> getUUID(CaseSearchCriteria criteria) {
        String uuid = criteria.getUuid();
        List<Object> preparedStmtList = new ArrayList<>();
        preparedStmtList.add(uuid);
        String query = caseQueryBuilder.getAssignedCases(uuid);
        List<String> ids = jdbcTemplate.queryForList(query, String.class);
        return ids;
    }

    public List<Document> getDocumentList(String caseId) {
        List<Object> preparedStmtList = new ArrayList<>();
        preparedStmtList.add(caseId);
        List<Document> documentList = jdbcTemplate.query(caseQueryBuilder.getDocQuery(), preparedStmtList.toArray(), documentMapper);
        return documentList;
    }


    public List<Advocate> getAdvocateById(String id) {
        List<Advocate> advocateList = jdbcTemplate.query(caseQueryBuilder.getAdvocateQuery(id), advocateMapper);
        return advocateList;
    }

    public Integer getCount(CaseSearchCriteria criteria) {
        List<Object> preparedStmtList = new ArrayList<>();
        String query = caseQueryBuilder.getCountQuery(criteria, preparedStmtList);
        Integer count = jdbcTemplate.queryForObject(query, preparedStmtList.toArray(), Integer.class);
        return count;
    }
}

