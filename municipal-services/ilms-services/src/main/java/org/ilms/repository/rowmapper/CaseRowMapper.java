package org.ilms.repository.rowmapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.tracer.model.CustomException;
import org.ilms.web.model.Act;
import org.ilms.web.model.AuditDetails;
import org.ilms.web.model.Case;
import org.ilms.web.model.enums.Status;
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

        Map<String, Case> ilmsCaseMap = new LinkedHashMap<String, Case>();
        this.setFullCount(0);
        while (rs.next()) {
            String duplicacyCheck = "";
            Case currentCase = new Case();
            // TODO fill the ILMSCase object with data in the result set record
            if (!duplicacyCheck.equals(rs.getString("ilmsCase_id"))) {
                String id = rs.getString("ilmsCase_id");
                duplicacyCheck = id;
                String cnrNumber = rs.getString("ilms_cnrNumber");
                String caseType = rs.getString("ilms_caseType");
                currentCase = ilmsCaseMap.get(id);
                String tenantId = rs.getString("ilms_tenandId");
                String parentCaseId = rs.getString("ilms_parentCaseId");
                String caseCategory = rs.getString("ilms_caseCategory");
                String caseNumber = rs.getString("ilms_caseNumber");
                String state = rs.getString("ilms_state");
                String district = rs.getString("ilms_district");
                String division = rs.getString("ilms_division");
                String courtName = rs.getString("ilms_courtName");
                String filingNumber = rs.getString("ilms_filingNumber");
                Long filingDate = rs.getLong("ilms_filingDate");
                Long registrationDate = rs.getLong("ilms_registrationDate");
                String caseSummary = rs.getString("ilms_caseSummary");
                String arisingDetails = rs.getString("ilms_arisingDetails");
                String matter = rs.getString("ilms_matter");
                this.setFullCount((rs.getInt("full_count")));
                String caseStatus = rs.getString("ilms_caseStatus");
                String caseStage = rs.getString("ilms_caseStage");
                String caseSubStage = rs.getString("ilms_caseSubStage");
                String priority = rs.getString("ilms_priority");
                String recommendOic = rs.getString("ilms_recommendOic");
                String remarks = rs.getString("ilms_remarks");
                JsonNode additionalDetails = getAdditionalDetail("ilms_additionalDetails", rs);
                String status = rs.getString("ilms_status");

                AuditDetails auditDetails = AuditDetails.builder().createdTime(rs.getLong("ilms_createdTime"))
                        .createdBy(rs.getString("ilms_createdBy"))
                        .lastModifiedTime(rs.getLong("ilms_lastModifiedTime"))
                        .lastModifiedBy(rs.getString("ilms_lastModifiedBy")).build();

                if (currentCase == null) {
                    currentCase = Case.builder().id(id).cnrNumber(cnrNumber).tenantId(tenantId).additionalDetails(additionalDetails)
                            .caseType(caseType).caseCategory(caseCategory)
                            .parentCaseId(parentCaseId).caseNumber(caseNumber).filingNumber(filingNumber)
                            .remarks(remarks).filingDate(filingDate).registrationDate(registrationDate)
                            .caseSummary(caseSummary).status(Status.valueOf(status)).arisingDetails(arisingDetails)
                            .policyOrNonPolicyMatter(matter).priority(priority)
                            .caseStatus(caseStatus).state(state).courtName(courtName)
                            .caseStage(caseStage).caseSubStage(caseSubStage).district(district).division(division)
                            .recommendOIC(recommendOic).auditDetails(auditDetails).build();

                    ilmsCaseMap.put(id, currentCase);
                }
            }
            addChildrenToProperty(rs, currentCase);
        }
        return new ArrayList<>(ilmsCaseMap.values());
    }

    @SuppressWarnings("unused")
    private void addChildrenToProperty(ResultSet rs, Case aCase) throws SQLException {
        // TODO add all the child data petitioner, respondant, act, advocate
        AuditDetails auditDetails = AuditDetails.builder().createdBy(rs.getString("act_createdBy")).createdTime(rs.getLong("act_createdTime"))
                .lastModifiedBy(rs.getString("act_lastModifiedBy"))
                .lastModifiedTime(rs.getLong("act_lastModifiedTime")).build();

        Act act = Act.builder().id(rs.getString("actId")).actName(rs.getString("actName")).status(Status.valueOf(rs.getString("actStatus")))
                .sectionNumber(rs.getString("actSectionNumber")).caseId(rs.getString("actCaseId")).auditDetails(auditDetails).build();
        aCase.setAct(act);
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

}
