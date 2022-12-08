package org.ilms.repository;

import lombok.extern.slf4j.Slf4j;
import org.ilms.repository.querybuilder.ILMSCaseQueryBuilder;
import org.ilms.repository.rowmapper.DocumentMapper;
import org.ilms.repository.rowmapper.ILMSCaseRowMapper;
import org.ilms.repository.rowmapper.PartyRowMapper;
import org.ilms.web.model.*;
import org.ilms.web.model.enums.PartyType;
import org.ilms.web.model.enums.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class ILMSCaseRepository {

    @Autowired
    private ILMSCaseQueryBuilder ilmsCaseQueryBuilder;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ILMSCaseRowMapper ilmsCaseRowMapper;
    @Autowired
    private DocumentMapper documentMapper;

    @Autowired
    private PartyRowMapper partyRowMapper;

    public ILMSCaseResponse getILMSCaseData(ILMSCaseSearchCriteria criteria) {
        List<Object> preparedStmtList = new ArrayList<>();
        String query = ilmsCaseQueryBuilder.getILMSCaseSearchQuery(criteria, preparedStmtList);
        List<ILMSCase> ilmsCases = jdbcTemplate.query(query, preparedStmtList.toArray(), ilmsCaseRowMapper);
        for (ILMSCase singleCase : ilmsCases) {
            singleCase.setDocuments(getDocumentList(singleCase.getId()));
            List<ILMSParty> partyList = getParty(singleCase.getId());
            for (ILMSParty party : partyList) {
                if (party.getPartyType().equals(PartyType.RESPONDENT.toString())  && party.getStatus()==Status.ACTIVE) {
                    singleCase.setRespondent(party);
                } else if(party.getStatus()==Status.ACTIVE){
                    singleCase.setPetitioner(party);
                }
            }
        }
        ILMSCaseResponse ilmsCaseResponse = ILMSCaseResponse.builder().ilmsCases(ilmsCases).totalCount(ilmsCaseRowMapper.getFullCount()).build();
        return ilmsCaseResponse;
    }

    public List<Document> getDocumentList(String caseId) {
        List<Object> preparedStmtList = new ArrayList<>();
        preparedStmtList.add(caseId);
        List<Document> documentList = jdbcTemplate.query(ilmsCaseQueryBuilder.getDocQuery(), preparedStmtList.toArray(), documentMapper);
        return documentList;
    }

    public List<ILMSParty> getParty(String caseId) {
        List<Object> preparedStmtList = new ArrayList<>();
        preparedStmtList.add(caseId);
        List<ILMSParty> parties = jdbcTemplate.query(ilmsCaseQueryBuilder.getPartyQuery(), preparedStmtList.toArray(), partyRowMapper);
        return parties;
    }
}
