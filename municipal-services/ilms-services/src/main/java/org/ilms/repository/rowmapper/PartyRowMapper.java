package org.ilms.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.ilms.web.model.Advocate;
import org.ilms.web.model.AuditDetails;
import org.ilms.web.model.Party;
import org.ilms.web.model.enums.PartyType;
import org.ilms.web.model.enums.Status;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

@Repository
public class PartyRowMapper implements ResultSetExtractor<List<Party>> {
    @Override
    public List<Party> extractData(ResultSet rs) throws SQLException, DataAccessException {
        List<Party> parties = new ArrayList<Party>();
        while (rs.next()) {

            AuditDetails partyAuditDetails = AuditDetails.builder().createdTime(rs.getLong("createdtime")).createdBy(rs.getString("createdby"))
                                                         .lastModifiedBy(rs.getString("lastmodifiedby"))
                                                         .lastModifiedTime(rs.getLong("lastmodifiedtime")).build();

            AuditDetails advocateAuditDetails = AuditDetails.builder().createdTime(rs.getLong("advocate_createdTime"))
                                                            .createdBy(rs.getString("advocate_createdBy"))
                                                            .lastModifiedBy(rs.getString("advocate_lastModifiedBy"))
                                                            .lastModifiedTime(rs.getLong("advocate_lastModifiedTime")).build();

            Advocate advocate = new Advocate();
            advocate = Advocate.builder().id(rs.getString("advocate_id")).contactNumber(rs.getString("advocate_contactNumber"))
                               .firstName(rs.getString("advocate_firstName")).lastName(rs.getString("advocate_lastName"))
                               .partyType(PartyType.valueOf(rs.getString("advocate_partyType"))).partyId(rs.getString("advocate_partyId"))
                               .hearingId(rs.getString("advocate_hearingId")).status(Status.valueOf(rs.getString("advocate_status")))
                               .auditDetails(advocateAuditDetails).build();

            Party party = Party.builder().id(rs.getString("id")).departmentName(rs.getString("department_name")).firstName(rs.getString("first_name"))
                               .lastName(rs.getString("last_name")).gender(rs.getString("gender")).petitionerType(rs.getString("petitioner_type"))
                               .partyType(rs.getString("party_type")).address(rs.getString("address")).contactNumber(rs.getString("contact_number"))
                               .caseId(rs.getString("case_id")).status(Status.valueOf(rs.getString("status"))).auditDetails(partyAuditDetails)
                               .advocate(advocate).build();
            parties.add(party);
        }
        return parties;
    }
}
