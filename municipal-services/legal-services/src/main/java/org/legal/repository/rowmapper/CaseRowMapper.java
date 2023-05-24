package org.legal.repository.rowmapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.tracer.model.CustomException;
import org.legal.web.model.AuditDetails;
import org.legal.web.model.Case;
import org.legal.web.model.Court;
import org.legal.web.model.enums.Status;
import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public class CaseRowMapper implements ResultSetExtractor<List<Case>> {
    @Autowired
    private ObjectMapper mapper;

    private int fullCount = 0;

    public int getFullCount() {
        return fullCount;
    }

    public void setFullCount(int full_count) {
        this.fullCount = full_count;
    }

    @Override
    public List<Case> extractData(ResultSet rs) throws SQLException, DataAccessException {

        Map<String, Case> legalCaseMap = new LinkedHashMap<String, Case>();
        this.setFullCount(0);
        while (rs.next()) {
            String duplicacyCheck = "";
            Case currentCase = new Case();
            // TODO fill the ILMSCase object with data in the result set record
            if (!duplicacyCheck.equals(rs.getString("legalCase_id"))) {
                String id = rs.getString("legalCase_id");
                duplicacyCheck = id;
                String cnrNumber = rs.getString("legal_cnrNumber");
                String caseType = rs.getString("legal_caseType");
                currentCase = legalCaseMap.get(id);
                String tenantId = rs.getString("legal_tenantId");
                String parentCaseId = rs.getString("legal_parentCaseId");
                JsonNode linkedCases = getLinkedCases("legal_linkedCases", rs);
                String caseCategory = rs.getString("legal_caseCategory");
                String caseNumber = rs.getString("legal_caseNumber");
                String filingNumber = rs.getString("legal_filingNumber");
                Long filingDate = rs.getLong("legal_filingDate");
                // Long registrationDate = rs.getLong("ilms_registrationDate");
                String caseSummary = rs.getString("legal_caseSummary");
                String arisingDetails = rs.getString("legal_arisingDetails");
                String matter = rs.getString("legal_matter");
                this.setFullCount((rs.getInt("full_count")));
                String caseStatus = rs.getString("legal_caseStatus");
                String applicationStatus = rs.getString("legal_applicationStatus");
//                String caseSubStage = rs.getString("ilms_caseSubStage");
                String priority = rs.getString("legal_priority");
                String recommendOic = rs.getString("legal_recommendOic");
                String remarks = rs.getString("legal_remarks");
                JsonNode additionalDetails = getAdditionalDetail("legal_additionalDetails", rs);
                String status = rs.getString("legal_status");

                AuditDetails auditDetails = AuditDetails.builder().createdTime(rs.getLong("legal_createdTime"))
                        .createdBy(rs.getString("legal_createdBy"))
                        .lastModifiedTime(rs.getLong("legal_lastModifiedTime"))
                        .lastModifiedBy(rs.getString("legal_lastModifiedBy")).build();

                if (currentCase == null) {
                    currentCase = Case.builder().id(id).cnrNumber(cnrNumber).tenantId(tenantId).additionalDetails(additionalDetails)
                            .type(caseType).category(caseCategory)
                            .parentCaseId(parentCaseId).caseNumber(caseNumber).filingNumber(filingNumber)
                            .remarks(remarks).filingDate(filingDate)
                            .summary(caseSummary).status(Status.valueOf(status)).arisingDetails(arisingDetails)
                            .policyOrNonPolicyMatter(matter).priority(priority)
                            .caseStatus(caseStatus).applicationStatus(applicationStatus)
                            .recommendOIC(recommendOic).auditDetails(auditDetails).linkedCases(linkedCases).build();

                    legalCaseMap.put(id, currentCase);
                }
            }
            addChildrenToProperty(rs, currentCase);
        }
        return new ArrayList<>(legalCaseMap.values());
    }

    @SuppressWarnings("unused")
    private void addChildrenToProperty(ResultSet rs, Case aCase) throws SQLException {
        // TODO add all the child data petitioner, respondant, act, advocate
//        if (Status.valueOf(rs.getString("actStatus")) == Status.ACTIVE) {
//            AuditDetails auditDetails = AuditDetails.builder().createdBy(rs.getString("act_createdBy")).createdTime(rs.getLong("act_createdTime"))
//                    .lastModifiedBy(rs.getString("act_lastModifiedBy"))
//                    .lastModifiedTime(rs.getLong("act_lastModifiedTime")).build();
//
//            Act act = Act.builder().id(rs.getString("actId")).actName(rs.getString("actName")).status(Status.valueOf(rs.getString("actStatus")))
//                    .sectionNumber(rs.getString("actSectionNumber")).caseId(rs.getString("actCaseId")).auditDetails(auditDetails).build();
//            aCase.setAct(act);
//        }
        if (Status.valueOf(rs.getString("court_status")) == Status.ACTIVE) {
            AuditDetails auditDetails = AuditDetails.builder().createdTime(rs.getLong("court_createdtime")).createdBy(rs.getString("court_createdby"))
                    .lastModifiedBy(rs.getString("court_lastmodifiedby"))
                    .lastModifiedTime(rs.getLong("court_lastmodifiedtime")).build();

            Court court = Court.builder().id(rs.getString("court_id")).caseId(rs.getString("court_caseId")).courtName(rs.getString("court_name"))
                    .district(rs.getString("court_district")).state(rs.getString("court_state")).division(rs.getString("court_division"))
                    .status(Status.valueOf(rs.getString("court_status"))).auditDetails(auditDetails).build();
            aCase.setCourt(court);
        }
    }

    private JsonNode getAdditionalDetail(String columnName, ResultSet rs) {

        JsonNode additionalDetail = null;
        try {
            PGobject pgObj = (PGobject) rs.getObject(columnName);
            if (pgObj != null) {
                additionalDetail = mapper.readTree(pgObj.getValue());
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            throw new CustomException("PARSING_ERROR", "Failed to parse additionalDetail object");
        }
        return additionalDetail;
    }

    private JsonNode getLinkedCases(String columnName, ResultSet rs) {

        JsonNode linkedCases = null;
        try {
            PGobject pgObj = (PGobject) rs.getObject(columnName);
            if (pgObj != null) {
                linkedCases = mapper.readTree(pgObj.getValue());
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            throw new CustomException("PARSING_ERROR", "Failed to parse additionalDetail object");
        }
        return linkedCases;
    }

}
