package org.legal.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.legal.web.model.Advocate;
import org.legal.web.model.AuditDetails;
import org.legal.web.model.Party;
import org.legal.web.model.PartyAdv;
import org.legal.web.model.enums.Status;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

@Repository
public class PartyAdvRowMapper implements ResultSetExtractor<List<PartyAdv>> {
    @Override
    public List<PartyAdv> extractData(ResultSet rs) throws SQLException, DataAccessException {
        List<PartyAdv> parties = new ArrayList<PartyAdv>();
        while (rs.next()) {

            AuditDetails partyAuditDetails = AuditDetails.builder().createdTime(rs.getLong("createdtime")).createdBy(rs.getString("createdby"))
                                                         .lastModifiedBy(rs.getString("lastmodifiedby"))
                                                         .lastModifiedTime(rs.getLong("lastmodifiedtime")).build();



            PartyAdv party = PartyAdv.builder().id(rs.getString("id")).partyId(rs.getString("party_id")).partyType(rs.getString("party_type"))
                    .caseId(rs.getString("case_id")).advocateId(rs.getString("advocate_id")).advocateContactNumber(rs.getString("advocate_contact_number"))
                               .status(Status.valueOf(rs.getString("status"))).auditDetails(partyAuditDetails)
                               .build();
            parties.add(party);
        }
        return parties;
    }
}
