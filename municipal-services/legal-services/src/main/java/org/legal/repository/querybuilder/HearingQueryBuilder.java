package org.legal.repository.querybuilder;

import org.legal.configs.LEGALConfiguration;
import org.legal.web.model.HearingSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Component
public class HearingQueryBuilder {

    private static final String getTenantIdQuery = "SELECT tenant_id FROM ilms_case WHERE ID=(select case_id FROM eg_lg_hearing WHERE id=?)";

    private static final String maxValueQuery = "select count(*) from eg_lg_hearing where case_id = ?";

    private static final String Query = "select count(*) OVER() AS full_count,eg_lg_hearing.id as hearing_id, eg_lg_hearing.hearing_number as hearing_number," + "eg_lg_hearing.tenant_id as tenant_id," + " eg_lg_hearing.court_number as hearing_court_number, eg_lg_hearing.bench as hearing_bench, eg_lg_hearing.case_id as hearing_case_id, " + " eg_lg_hearing.judge_name as hearing_judge_name, eg_lg_hearing.hearing_date as hearing_date," + " eg_lg_hearing.business_date as hearing_business_date, eg_lg_hearing.hearing_purpose as hearing_purpose, " + " eg_lg_hearing.required_officer as hearing_required_officer, eg_lg_hearing.affidavit_filing_date as affidavit_filing_date, " + " eg_lg_hearing.affidavit_filing_due_date as affidavit_filing_due_date, eg_lg_hearing.case_number as hearing_case_number, " + " eg_lg_hearing.oath_number as hearing_oath_number,  " + " eg_lg_hearing.additional_details as hearing_additionalDetails," + " eg_lg_hearing.next_hearing_date as next_hearing_date,eg_lg_hearing.first_hearing_date as first_hearing_date, eg_lg_hearing.is_presence_required as hearing_is_presence_required, " + " eg_lg_hearing.hearing_type as hearing_type, eg_lg_hearing.department_officer as hearing_department_officer," + " eg_lg_hearing.remarks as hearing_remarks,eg_lg_hearing.status as hearing_status,eg_lg_hearing.createdby as hearing_createdby," + " eg_lg_hearing.createdtime as hearing_createdtime,eg_lg_hearing.lastmodifiedby as hearing_lastmodifiedby," + " eg_lg_hearing.lastmodifiedtime as hearing_lastmodifiedtime, " + " eg_lg_payment.id as payment_id, eg_lg_payment.case_id as payment_case_id, eg_lg_payment.hearing_id as payment_hearing_id, " + " eg_lg_payment.fine_imposed_date as payment_fine_imposed_date, eg_lg_payment.fine_due_date as payment_fine_due_date," + " eg_lg_payment.fine_amount as payment_fine_amount,  eg_lg_payment.status as payment_status ," + " eg_lg_payment.createdby as payment_createdby,eg_lg_payment.createdtime as payment_createdtime," + " eg_lg_payment.lastmodifiedby as payment_lastmodifiedby,eg_lg_payment.lastmodifiedtime as payment_lastmodifiedtime " + " FROM eg_lg_hearing " + " LEFT OUTER JOIN eg_lg_payment on eg_lg_payment.hearing_id = eg_lg_hearing.id";
    private final String paginationWrapper = "{} {orderBy} {pagination}";

    @Autowired
    private LEGALConfiguration legalConfiguration;

    public String getHearingSearchQuery(HearingSearchCriteria criteria, List<Object> preparedStmtList) {
        StringBuilder builder = new StringBuilder(Query);
        if (criteria.getId() != null) {
            if (criteria.getId().split("\\.").length == 1) {
                addClauseIfRequired(preparedStmtList, builder);
                builder.append(" eg_lg_hearing.id like ?");
                preparedStmtList.add('%' + criteria.getId() + '%');
            } else {
                addClauseIfRequired(preparedStmtList, builder);
                builder.append(" eg_lg_hearing.id = ?");
                preparedStmtList.add('%' + criteria.getId() + '%');
            }
        }

        List<String> caseId = criteria.getCaseId();
        try {
            if (!CollectionUtils.isEmpty(caseId)) {
                addClauseIfRequired(preparedStmtList, builder);
                builder.append(" eg_lg_hearing.case_id IN (").append(createQuery(caseId)).append(")");
                addToPreparedStatement(preparedStmtList, caseId);
            }
        } catch (NullPointerException e) {
            preparedStmtList.add("");
        }
        return addPaginationWrapper(builder.toString(), preparedStmtList, criteria);
    }

    private String addPaginationWrapper(String query, List<Object> preparedStmtList, HearingSearchCriteria criteria) {

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

    private void addOrderByClause(StringBuilder builder, HearingSearchCriteria criteria) {
        if (criteria.getSortBy() == HearingSearchCriteria.SortBy.id) {
            builder.append(" ORDER BY eg_lg_hearing.id ");
        } else if (criteria.getSortBy() == HearingSearchCriteria.SortBy.caseId) {
            builder.append(" ORDER BY eg_lg_hearing.case_id ");
        }
        if (criteria.getSortOrder() == HearingSearchCriteria.SortOrder.ASC) {
            builder.append("ASC");
        } else if (criteria.getSortOrder() == HearingSearchCriteria.SortOrder.DESC) {
            builder.append("DESC");
        }
    }

    public String getMaxHearingQuery() {
        return maxValueQuery;
    }

    public String getTenantIdFromHearingQuery() {
        return getTenantIdQuery;
    }

}
