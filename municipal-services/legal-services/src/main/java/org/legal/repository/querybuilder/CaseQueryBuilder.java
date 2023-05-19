package org.legal.repository.querybuilder;

import org.apache.commons.lang3.StringUtils;
import org.legal.configs.LEGALConfiguration;
import org.legal.web.model.CaseSearchCriteria;
import org.legal.web.model.CountRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class CaseQueryBuilder {
    private static final String docQuery = "select count(*) OVER() AS document_full_count,* from eg_lg_document where case_id = ?";

    private static final String partyQuery = "select party.*,advocate.id as adv_id, advocate.first_name as adv_first_name, advocate.last_name as adv_last_name,advocate.contact_number as adv_contact_number,advocate.status as adv_status,advocate.createdby,advocate.createdtime,advocate.lastmodifiedby,advocate.lastmodifiedtime from eg_lg_case_party party inner join eg_lg_party_advocate_bridge bridge ON bridge.party_id = party.id inner join eg_lg_advocate advocate ON advocate.id = bridge.advocate_id where party.case_id=?";

    private static final String actQuery = "select * from eg_lg_act where case_id = ?";

    private static final String Query = "select count(*) OVER() AS full_count,eg_lg_case.id as ilmsCase_id, eg_lg_case.case_number as ilms_caseNumber, eg_lg_case.cnr_number as ilms_cnrNumber, eg_lg_case.tenant_id as ilms_tenantId, eg_lg_case.parent_case_id as ilms_parentCaseId, eg_lg_case.linked_cases as ilms_linkedCases, eg_lg_case.case_type as ilms_caseType, eg_lg_case.case_category as ilms_caseCategory, eg_lg_case.filing_number as ilms_filingNumber, eg_lg_case.filing_date as ilms_filingDate, eg_lg_case.case_summary as ilms_caseSummary, eg_lg_case.arising_details as ilms_arisingDetails, eg_lg_case.policy_or_nonpolicy_matter as ilms_matter, eg_lg_case.case_status as ilms_caseStatus, eg_lg_case.application_status as ilms_applicationStatus, eg_lg_case.priority as ilms_priority, eg_lg_case.recommend_oic as ilms_recommendOic, eg_lg_case.remarks as ilms_remarks, eg_lg_case.additional_details as ilms_additionalDetails, eg_lg_case.status as ilms_status, eg_lg_case.createdby as ilms_createdBy, eg_lg_case.createdtime as ilms_createdTime, eg_lg_case.lastmodifiedby as ilms_lastModifiedBy, eg_lg_case.lastmodifiedtime as ilms_lastModifiedTime,eg_lg_court.id as court_id, eg_lg_court.case_id as court_caseId, eg_lg_court.court_name as court_name, eg_lg_court.district as court_district, eg_lg_court.state as court_state, eg_lg_court.division as court_division,eg_lg_court.status as court_status, eg_lg_court.createdby as court_createdby,eg_lg_court.createdtime as court_createdtime,eg_lg_court.lastmodifiedby as court_lastmodifiedby,eg_lg_court.lastmodifiedtime as court_lastmodifiedtime FROM eg_lg_case LEFT OUTER JOIN eg_lg_court on eg_lg_court.case_id = eg_lg_case.id ";

    private static final String ChildCaseQuery = "SELECT id FROM eg_lg_case where id= ? or parent_case_id= ? ";

    private static final String TOTALCOUNTQUERY = "select count(*) from eg_lg_case ";

    private static final String CaseQuery1 = "select DISTINCT(cases.id) from eg_lg_case as cases INNER JOIN eg_wf_processinstance_v2 pi ON pi.businessid = cases.id LEFT JOIN eg_wf_assignee_v2 assg ON pi.id = assg.processinstanceid ";
    private static final String CaseQuery2 = " AND pi.createdtime IN (select max(createdtime) from eg_wf_processinstance_v2 wf where wf.businessid = cases.id GROUP BY wf.businessid)";

    private static final String advocateQuery = "select * from eg_lg_advocate ";
    private final String paginationWrapper = "{} {orderBy} {pagination}";


    @Autowired
    private LEGALConfiguration ilmsConfiguration;

    public String getLegalCaseSearchQuery(CaseSearchCriteria criteria, List<Object> preparedStmtList) {

        StringBuilder builder = new StringBuilder(Query);
        if (criteria.getCnrNumber() != null) {
            if (criteria.getCnrNumber().split("\\.").length == 1) {
                addClauseIfRequired(preparedStmtList, builder);
                builder.append(" eg_lg_case.cnr_number like ?");
                preparedStmtList.add('%' + criteria.getCnrNumber() + '%');
            } else {
                addClauseIfRequired(preparedStmtList, builder);
                builder.append(" eg_lg_case.cnr_number = ?");
                preparedStmtList.add('%' + criteria.getCnrNumber() + '%');
            }
        }

        List<String> caseNumber = criteria.getNumber();
        try {
            if (!CollectionUtils.isEmpty(caseNumber)) {
                addClauseIfRequired(preparedStmtList, builder);
                builder.append(" eg_lg_case.case_number IN (").append(createQuery(caseNumber)).append(")");
                addToPreparedStatement(preparedStmtList, caseNumber);
            }
        } catch (NullPointerException e) {
            preparedStmtList.add("");
        }
        List<String> caseId = criteria.getId();
        try {
            if (!CollectionUtils.isEmpty(caseId)) {
                addClauseIfRequired(preparedStmtList, builder);
                builder.append(" eg_lg_case.id IN (").append(createQuery(caseId)).append(")");
                addToPreparedStatement(preparedStmtList, caseId);
            }
        } catch (NullPointerException e) {
            preparedStmtList.add("");
        }
        List<String> parentCaseId = criteria.getParentCaseId();
        try {
            if (!CollectionUtils.isEmpty(parentCaseId)) {
                addClauseIfRequired(preparedStmtList, builder);
                builder.append(" eg_lg_case.parent_case_id IN (").append(createQuery(parentCaseId)).append(")");
                addToPreparedStatement(preparedStmtList, parentCaseId);
            }
        } catch (NullPointerException e) {
            preparedStmtList.add("");
        }
        return addPaginationWrapper(builder.toString(), preparedStmtList, criteria);
    }

    /**
     * @param query            prepared Query
     * @param preparedStmtList values to be replased on the query
     * @param criteria         ilms case search criteria
     * @return the query by replacing the placeholders with preparedStmtList
     */
    private String addPaginationWrapper(String query, List<Object> preparedStmtList, CaseSearchCriteria criteria) {

        int limit = ilmsConfiguration.getDefaultLimit();
        int offset = ilmsConfiguration.getDefaultOffset();
        String finalQuery = paginationWrapper.replace("{}", query);
        if (criteria.getLimit() != null && criteria.getLimit() <= ilmsConfiguration.getMaxSearchLimit()) {
            limit = criteria.getLimit();
        }
        if (criteria.getLimit() != null && criteria.getLimit() > ilmsConfiguration.getMaxSearchLimit()) {
            limit = ilmsConfiguration.getMaxSearchLimit();
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

    /**
     *
     */
    private void addOrderByClause(StringBuilder builder, CaseSearchCriteria criteria) {
        if (criteria.getSortBy() == CaseSearchCriteria.SortBy.caseNumber) {
            builder.append(" ORDER BY eg_lg_case.case_number ");
        } else if (criteria.getSortBy() == CaseSearchCriteria.SortBy.cnrNumber) {
            builder.append(" ORDER BY eg_lg_case.cnr_number ");
        }
        if (criteria.getSortOrder() == CaseSearchCriteria.SortOrder.ASC) {
            builder.append("ASC");
        } else if (criteria.getSortOrder() == CaseSearchCriteria.SortOrder.DESC) {
            builder.append("DESC");
        }
    }

    public String getDocQuery() {
        return docQuery;
    }

    public String getPartyQuery() {
        return partyQuery;
    }

    public String getActQuery() {
        return actQuery;
    }

    public String getChildCaseIds(String parentCaseId, List<Object> preparedStmtList) {
        return ChildCaseQuery;
    }

    public CountRequest getTotalCount(CaseSearchCriteria criteria) {
        CountRequest finalRequest = new CountRequest();
        List<Object> preparedStmtList = new ArrayList<>();
        StringBuilder builder = new StringBuilder(TOTALCOUNTQUERY);
        //        if (!CollectionUtils.isEmpty(Collections.singleton(criteria.getCnrNumber()))) {
        if (StringUtils.isNotBlank(criteria.getCnrNumber())) {
            addClauseIfRequired(preparedStmtList, builder);
            builder.append("cnr_number= ?");
            preparedStmtList.add(criteria.getCnrNumber());
        } else if (Objects.nonNull(criteria.getNumber())) {
            addClauseIfRequired(preparedStmtList, builder);
            builder.append("case_number= ?");
            preparedStmtList.add(criteria.getNumber().get(0));
        }
        addClauseIfRequired(preparedStmtList, builder);
        builder.append(" status = 'ACTIVE' ");
        finalRequest.setQuery(builder.toString());
        finalRequest.setPreparedStatement(preparedStmtList);
        return finalRequest;
    }


    public String getAssignedCases(String uuid) {
        return CaseQuery1 + "where " + "assg.assignee = " + "'" + uuid + "'" + CaseQuery2;
    }

    public String getAdvocateQuery(String id) {
        return advocateQuery + "where id ='" + id + "';";
    }
}
