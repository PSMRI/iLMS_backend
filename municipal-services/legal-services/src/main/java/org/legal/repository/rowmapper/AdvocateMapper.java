package org.legal.repository.rowmapper;

import org.legal.web.model.Advocate;
import org.legal.web.model.AuditDetails;
import org.legal.web.model.Party;
import org.legal.web.model.enums.Status;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AdvocateMapper implements ResultSetExtractor<List<Advocate>> {

    private int fullCount = 0;

    public int getFullCount() {
        return fullCount;
    }

    public void setFullCount(int full_count) {
        this.fullCount = full_count;
    }
    @Override
    public List<Advocate> extractData(ResultSet rs) throws SQLException, DataAccessException {
        List<Advocate> advocates = new ArrayList<Advocate>();
        while (rs.next()) {

            AuditDetails advocateAuditDetails = AuditDetails.builder().createdTime(rs.getLong("createdtime"))
                    .createdBy(rs.getString("createdby"))
                    .lastModifiedBy(rs.getString("lastmodifiedby"))
                    .lastModifiedTime(rs.getLong("lastmodifiedtime")).build();

            Advocate advocate = new Advocate();
            advocate = Advocate.builder().id(rs.getString("id")).contactNumber(rs.getString("contact_number"))
                    .firstName(rs.getString("first_name")).lastName(rs.getString("last_name"))
                    .status(Status.valueOf(rs.getString("status")))
                    .auditDetails(advocateAuditDetails).build();
advocates.add(advocate);
        }
        return advocates;
    }
}
