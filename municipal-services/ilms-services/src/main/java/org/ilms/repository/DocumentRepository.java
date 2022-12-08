package org.ilms.repository;

import lombok.extern.slf4j.Slf4j;
import org.ilms.repository.querybuilder.DocumentQueryBuilder;
import org.ilms.repository.rowmapper.DocumentMapper;
import org.ilms.web.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
@Repository
@Slf4j
public class DocumentRepository {

    @Autowired
    private DocumentQueryBuilder documentQueryBuilder;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DocumentMapper documentMapper;

    public DocumentResponse getDocumentsData(DocumentSearchCriteria criteria) {
        List<Object> preparedStmtList = new ArrayList<>();
        String query = documentQueryBuilder.getDocumentSearchQuery(criteria, preparedStmtList);
        List<Document> documentList = jdbcTemplate.query(query, preparedStmtList.toArray(), documentMapper);
        DocumentResponse documentResponse = DocumentResponse.builder().documents(documentList).totalCount(documentMapper.getFullCount()).build();
        return documentResponse;
    }
}
