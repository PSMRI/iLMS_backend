package org.ilms.repository;

import lombok.extern.slf4j.Slf4j;
import org.ilms.repository.querybuilder.CaseQueryBuilder;
import org.ilms.repository.querybuilder.CountQueryBuilder;
import org.ilms.repository.rowmapper.CaseRowMapper;
import org.ilms.repository.rowmapper.DocumentMapper;
import org.ilms.repository.rowmapper.PartyRowMapper;
import org.ilms.web.model.*;
import org.ilms.web.model.enums.PartyType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

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
    private DocumentMapper documentMapper;

    @Autowired
    private PartyRowMapper partyRowMapper;

    @Autowired
    private CountQueryBuilder countQueryBuilder;

    public CaseResponse getILMSCaseData(CaseSearchCriteria criteria) {
        List<Object> preparedStmtList = new ArrayList<>();
        List<String> ids = getUUID(criteria);
        criteria = CaseSearchCriteria.builder().id(ids).build();
        String query = caseQueryBuilder.getILMSCaseSearchQuery(criteria, preparedStmtList);
        List<Case> caseList = jdbcTemplate.query(query, preparedStmtList.toArray(), caseRowMapper);
        for (Case singleCase : caseList) {
            singleCase.setDocuments(getDocumentList(singleCase.getId()));
            List<Party> partyList = getParty(singleCase.getId());
            for (Party party : partyList) {
                if (party.getPartyType().equals(PartyType.RESPONDENT.toString())) {
                    singleCase.setRespondent(party);
                } else {
                    singleCase.setPetitioner(party);
                }
            }
        }
        CaseResponse caseResponse = CaseResponse.builder().caseList(caseList).totalCount(caseRowMapper.getFullCount()).build();
        return caseResponse;
    }

    public List<Document> getDocumentList(String caseId) {
        List<Object> preparedStmtList = new ArrayList<>();
        preparedStmtList.add(caseId);
        List<Document> documentList = jdbcTemplate.query(caseQueryBuilder.getDocQuery(), preparedStmtList.toArray(), documentMapper);
        return documentList;
    }

    public List<Party> getParty(String caseId) {
        List<Object> preparedStmtList = new ArrayList<>();
        preparedStmtList.add(caseId);
        List<Party> parties = jdbcTemplate.query(caseQueryBuilder.getPartyQuery(), preparedStmtList.toArray(), partyRowMapper);
        return parties;
    }

    public List<String> getCaseIdsByParentCaseId(String parentCaseId) {
        List<Object> preparedStmtList = new ArrayList<>();
        preparedStmtList.add(parentCaseId);
        preparedStmtList.add(parentCaseId);
        String query = caseQueryBuilder.getChildCaseIds(parentCaseId, preparedStmtList);
        List<String> caseIdsList = jdbcTemplate.queryForList(query, preparedStmtList.toArray(), String.class);
        return caseIdsList;
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
}
