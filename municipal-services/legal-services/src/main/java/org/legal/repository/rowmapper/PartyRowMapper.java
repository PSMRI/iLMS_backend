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
import java.util.*;

@Repository
public class PartyRowMapper implements ResultSetExtractor<List<Party>> {
    @Override
    public List<Party> extractData(ResultSet rs) throws SQLException, DataAccessException {
        List<Party> parties = new ArrayList<>();
        Map<String, Party> partyMap = new HashMap<>();

        while (rs.next()) {
            String partyId = rs.getString("id");
            Party party;

            if (partyMap.containsKey(partyId)) {
                // Retrieve existing party from the map
                party = partyMap.get(partyId);
            } else {
                // Create new party if not found in the map
                AuditDetails partyAuditDetails = AuditDetails.builder()
                        .createdTime(rs.getLong("createdtime"))
                        .createdBy(rs.getString("createdby"))
                        .lastModifiedBy(rs.getString("lastmodifiedby"))
                        .lastModifiedTime(rs.getLong("lastmodifiedtime"))
                        .build();

                party = Party.builder()
                        .id(partyId)
                        .departmentName(rs.getString("department_name"))
                        .firstName(rs.getString("first_name"))
                        .lastName(rs.getString("last_name"))
                        .gender(rs.getString("gender"))
                        .petitionerType(rs.getString("petitioner_type"))
                        .partyType(rs.getString("party_type"))
                        .address(rs.getString("address"))
                        .contactNumber(rs.getString("contact_number"))
                        .caseId(rs.getString("case_id"))
                        .status(Status.valueOf(rs.getString("status")))
                        .auditDetails(partyAuditDetails)
                        .advocate(new ArrayList<>())
                        .build();

                partyMap.put(partyId, party);
                parties.add(party);
            }

            // Create advocateAuditDetails and advocate object for current row
            AuditDetails advocateAuditDetails = AuditDetails.builder()
                    .createdTime(rs.getLong("createdTime"))
                    .createdBy(rs.getString("createdBy"))
                    .lastModifiedBy(rs.getString("lastModifiedBy"))
                    .lastModifiedTime(rs.getLong("lastModifiedTime"))
                    .build();

            Advocate advocate = Advocate.builder()
                    .id(rs.getString("adv_id"))
                    .contactNumber(rs.getString("adv_contact_number"))
                    .firstName(rs.getString("adv_first_name"))
                    .lastName(rs.getString("adv_last_name"))
                    .status(Status.valueOf(rs.getString("adv_status")))
                    .auditDetails(advocateAuditDetails)
                    .build();

            // Add advocate to the advocate list of the party
            party.getAdvocate().add(advocate);
        }

        return parties;

    }
}
