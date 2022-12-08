package org.ilms.repository.querybuilder;

import org.ilms.configs.ILMSConfiguration;
import org.ilms.web.model.ILMSCaseSearchCriteria;
import org.ilms.web.model.JudgementSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JudgementQueryBuilder {

    @Autowired
    private ILMSConfiguration config;


    private static final String Query = "SELECT id, order_type, order_date, decision_status, compliance_date, revised_compliance_date, order_no_override, case_id, revised_complaince_reason, compliance_status,"
            + "remarks, additional_details, status, createdby, createdtime, lastmodifiedby, lastmodifiedtime"
            + " FROM ilms_judgement";

    private final String paginationWrapper = "{} {orderBy} {pagination}";

    public String getFSMSearchQuery(JudgementSearchCriteria criteria, List<Object> preparedStmtList) {
        StringBuilder builder = new StringBuilder(Query);
        if (criteria.getId() != null) {
            addClauseIfRequired(preparedStmtList, builder);
            builder.append("ilms_judgement.id = ?");
            addToPreparedStatement(preparedStmtList, criteria.getId());
        }
        if (criteria.getCaseId() != null) {
            addClauseIfRequired(preparedStmtList, builder);
            builder.append("ilms_judgement.case_id = ?");
            addToPreparedStatement(preparedStmtList, criteria.getCaseId());
        }
        return addPaginationWrapper(builder.toString(), preparedStmtList, criteria);
    }

    /**
     * @param query            prepared Query
     * @param preparedStmtList values to be replased on the query
     * @param criteria         judgement search criteria
     * @return the query by replacing the placeholders with preparedStmtList
     */
    private String addPaginationWrapper(String query, List<Object> preparedStmtList, JudgementSearchCriteria criteria) {

        int limit = config.getDefaultLimit();
        int offset = config.getDefaultOffset();
        String finalQuery = paginationWrapper.replace("{}", query);

        if (criteria.getLimit() != null && criteria.getLimit() <= config.getMaxSearchLimit())
            limit = criteria.getLimit();

        if (criteria.getLimit() != null && criteria.getLimit() > config.getMaxSearchLimit()) {
            limit = config.getMaxSearchLimit();
        }

        if (criteria.getOffset() != null)
            offset = criteria.getOffset();

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

    private void addToPreparedStatement(List<Object> preparedStmtList, List<String> ids) {
        ids.forEach(id -> {
            preparedStmtList.add(id);
        });
    }

    private static void addClauseIfRequired(List<Object> values,StringBuilder queryString) {
        if (values.isEmpty())
            queryString.append(" WHERE ");
        else {
            queryString.append(" AND ");
        }
    }

    /**
     * @param builder
     * @param criteria
     */
    private void addOrderByClause(StringBuilder builder, JudgementSearchCriteria criteria) {
        if (criteria.getSortBy() == ILMSCaseSearchCriteria.SortBy.caseNumber)
            builder.append(" ORDER BY ilms_judgement.id ");

        else if (criteria.getSortBy() == ILMSCaseSearchCriteria.SortBy.cnrNumber)
            builder.append(" ORDER BY ilms_judgement.case_id ");

        if (criteria.getSortOrder() == ILMSCaseSearchCriteria.SortOrder.ASC)
            builder.append("ASC");
        else if (criteria.getSortOrder() == ILMSCaseSearchCriteria.SortOrder.DESC)
            builder.append("DESC");
    }
}
