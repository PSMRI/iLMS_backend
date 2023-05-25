package org.legal.repository.querybuilder;

import java.util.List;
import org.legal.configs.LEGALConfiguration;
import org.legal.web.model.AdvocateSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AdvocateQueryBuilder {
    private static final String Query = "select * from eg_lg_advocate ";

    private final String paginationWrapper = "{} {orderBy} {pagination}";

    private final String partyAdvQuery = "select * from eg_lg_party_advocate_bridge where advocate_id = ? AND case_id = ?";
    private final String partyCaseAdvQuery = "select * FROM eg_lg_party_advocate_bridge ";

    private final String partyCaseAdvQueryWithPartyIdAndCaseId = "select * from eg_lg_party_advocate_bridge where party_id = ? AND case_id = ? ";

    private final String advocatesQuery = "select * from eg_lg_advocate ";

    @Autowired
    private LEGALConfiguration legalConfiguration;

    public String getAdvocateSearchQuery(AdvocateSearchCriteria criteria, List<Object> preparedStmtList) {
        StringBuilder builder = new StringBuilder(Query);
        if (criteria.getId() != null) {
            if (criteria.getId().split("\\.").length == 1) {
                addClauseIfRequired(preparedStmtList, builder);
                builder.append(" eg_lg_advocate.id like ?");
                preparedStmtList.add('%' + criteria.getId() + '%');
            } else {
                addClauseIfRequired(preparedStmtList, builder);
                builder.append(" eg_lg_advocate.id = ?");
                preparedStmtList.add(criteria.getId());
            }
        }

        if (criteria.getContactNumber() != null) {
            addClauseIfRequired(preparedStmtList, builder);
            builder.append(" eg_lg_advocate.contact_number = ?");
            preparedStmtList.add(criteria.getContactNumber());
        }
        return addPaginationWrapper(builder.toString(), preparedStmtList, criteria);
    }

    private String addPaginationWrapper(String query, List<Object> preparedStmtList, AdvocateSearchCriteria criteria) {

        int limit = legalConfiguration.getDefaultLimit();
        int offset = legalConfiguration.getDefaultOffset();
        String finalQuery = paginationWrapper.replace("{}", query);
        if (criteria.getLimit() != null && criteria.getLimit() <= legalConfiguration.getMaxSearchLimit()) {
            limit = criteria.getLimit();
        }
        if (criteria.getLimit() != null && criteria.getLimit() > legalConfiguration.getMaxSearchLimit()) {
            limit = legalConfiguration.getMaxSearchLimit();
        }
        if (criteria.getOffset() != null) {
            offset = criteria.getOffset();
        }
        StringBuilder orderQuery = new StringBuilder();
        addOrderByClause(orderQuery, criteria);
        finalQuery = finalQuery.replace("{orderBy}", orderQuery.toString());
        if (limit == -1) {
            finalQuery = finalQuery.replace("{pagination}", "");
        } else {
            finalQuery = finalQuery.replace("{pagination}", " offset ?  limit ?  ");
            preparedStmtList.add(offset);
            preparedStmtList.add(limit);
        }
        return finalQuery;
    }

    private void addClauseIfRequired(List<Object> values, StringBuilder queryString) {
        if (values.isEmpty()) {
            queryString.append(" WHERE ");
        } else {
            queryString.append(" AND");
        }
    }

    private void addToPreparedStatement(List<Object> preparedStmtList, List<String> ids) {
        ids.forEach(id -> {
            preparedStmtList.add(id);
        });
    }

    private Object createQuery(List<String> ids) {
        StringBuilder builder = new StringBuilder();
        int length = ids.size();
        for (int i = 0; i < length; i++) {
            builder.append(" ?");
            if (i != length - 1) {
                builder.append(",");
            }
        }
        return builder.toString();
    }

    private void addOrderByClause(StringBuilder builder, AdvocateSearchCriteria criteria) {
        if (criteria.getSortBy() == AdvocateSearchCriteria.SortBy.id) {
            builder.append(" ORDER BY eg_lg_hearing.id ");
        }
        if (criteria.getSortOrder() == AdvocateSearchCriteria.SortOrder.ASC) {
            builder.append("ASC");
        } else if (criteria.getSortOrder() == AdvocateSearchCriteria.SortOrder.DESC) {
            builder.append("DESC");
        }
    }

    public String getPartyAdvQuery() {
        return partyAdvQuery;
    }

    public String getAdvocates(List<String> mobileNumbers,List<Object> preparedStmtList) {
        StringBuilder builder = new StringBuilder(advocatesQuery);
        addClauseIfRequired(preparedStmtList,builder);
        builder.append("eg_lg_advocate.contact_number IN (").append(createQuery(mobileNumbers)).append(")");
        addToPreparedStatement(preparedStmtList,mobileNumbers);
        return builder.toString();
    }

    public String getAdvocatesOfParty(String partyId,String caseId,List<String> mobileNumber, List<Object> preparedStmtList) {
        StringBuilder builder = new StringBuilder(partyCaseAdvQuery);
        addClauseIfRequired(preparedStmtList,builder);
        builder.append(" eg_lg_party_advocate_bridge.party_id= ? ");
        preparedStmtList.add(partyId);
        builder.append(" AND eg_lg_party_advocate_bridge.case_id= ?");
        preparedStmtList.add(caseId);
        builder.append(" AND eg_lg_party_advocate_bridge.advocate_contact_number IN (").append(createQuery(mobileNumber)).append(")");
        addToPreparedStatement(preparedStmtList,mobileNumber);
        return builder.toString();
    }

    public String getAdvocatesOfPartyByCaseIdAndPartyId() {
        return partyCaseAdvQueryWithPartyIdAndCaseId;
    }
}
