package org.legal.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.legal.web.model.Act;
import org.legal.web.model.AuditDetails;
import org.legal.web.model.enums.Status;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;
@Repository
public class ActRowMapper implements ResultSetExtractor<List<Act>> {
    @Override
    public List<Act> extractData(ResultSet rs) throws SQLException, DataAccessException {
        List<Act> acts = new ArrayList<Act>();
        while (rs.next()) {

            AuditDetails actAuditDetails = AuditDetails.builder().createdTime(rs.getLong("createdtime"))
                                                            .createdBy(rs.getString("createdby"))
                                                            .lastModifiedBy(rs.getString("lastmodifiedby"))
                                                            .lastModifiedTime(rs.getLong("lastmodifiedtime")).build();
            Act act=new Act();
           act = Act.builder().id(rs.getString("id")).actName(rs.getString("name")).status(Status.valueOf(rs.getString("status")))
                    .sectionNumber(Arrays.asList(rs.getString("section_number"))).caseId(rs.getString("case_id")).auditDetails(actAuditDetails).build();
            acts.add(act);
        }
        return acts;
    }
}