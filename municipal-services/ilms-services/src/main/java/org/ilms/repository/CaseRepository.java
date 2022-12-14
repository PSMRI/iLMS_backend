package org.ilms.repository;

import java.util.ArrayList;
import java.util.List;
import org.ilms.repository.querybuilder.CaseQueryBuilder;
import org.ilms.repository.rowmapper.CaseRowMapper;
import org.ilms.repository.rowmapper.DocumentMapper;
import org.ilms.repository.rowmapper.PartyRowMapper;
import org.ilms.web.model.Case;
import org.ilms.web.model.CaseResponse;
import org.ilms.web.model.CaseSearchCriteria;
import org.ilms.web.model.Document;
import org.ilms.web.model.Party;
import org.ilms.web.model.enums.PartyType;
import org.ilms.web.model.enums.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import lombok.extern.slf4j.Slf4j;

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

    public CaseResponse getILMSCaseData(CaseSearchCriteria criteria) {
        List<Object> preparedStmtList = new ArrayList<>();
        String query = caseQueryBuilder.getILMSCaseSearchQuery(criteria, preparedStmtList);
        List<Case> aCases = jdbcTemplate.query(query, preparedStmtList.toArray(), caseRowMapper);
        for (Case singleCase : aCases) {
            singleCase.setDocuments(getDocumentList(singleCase.getId()));
            List<Party> partyList = getParty(singleCase.getId());
            for (Party party : partyList) {
                if (party.getPartyType().equals(PartyType.RESPONDENT.toString()) && party.getStatus() == Status.ACTIVE) {
                    singleCase.setRespondent(party);
                } else if (party.getStatus() == Status.ACTIVE) {
                    singleCase.setPetitioner(party);
                }
            }
        }
        CaseResponse caseResponse = CaseResponse.builder().Cases(aCases).totalCount(caseRowMapper.getFullCount()).build();
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
}
