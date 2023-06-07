package org.legal.repository.rowmapper;

import org.legal.web.model.Act;
import org.legal.web.model.AuditDetails;
import org.legal.web.model.Court;
import org.legal.web.model.enums.Status;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Repository
public class CourtRowMapper implements ResultSetExtractor<Court> {
    @Override
    public Court extractData(ResultSet rs) throws SQLException, DataAccessException {
        Court court = new Court();
        while (rs.next()) {
            AuditDetails auditDetails = AuditDetails.builder()
                    .createdBy(rs.getString("createdby"))
                    .createdTime(rs.getLong("createdtime"))
                    .lastModifiedBy(rs.getString("lastmodifiedby"))
                    .lastModifiedTime(rs.getLong("lastmodifiedtime")).build();

            court = Court.builder().id(rs.getString("id")).caseId(rs.getString("case_id")).courtName(rs.getString("court_name"))
                    .district(rs.getString("district")).state(rs.getString("state")).division(rs.getString("division"))
                    .status(Status.valueOf(rs.getString("status"))).auditDetails(auditDetails).build();

        }
        return court;
    }

}
