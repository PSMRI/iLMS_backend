package org.ilms.configs;

import java.util.TimeZone;
import java.util.List;
import javax.annotation.PostConstruct;
import org.egov.tracer.config.TracerConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Import ({ TracerConfiguration.class })
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Component
public class ILMSConfiguration {
    @Value ("${app.timezone}")
    private String timeZone;

    //    MDMS Config

    @Value("${ilms.business.codes}")
    private List<String> businessServiceList;

    @Value("${workflow.host}")
    private String wfHost;

    @Value("${workflow.transition.path}")
    private String wfTransitionPath;

    @Value("${workflow.businessservice.search.path}")
    private String wfBusinessServiceSearchPath;

    @Value("${workflow.processinstance.search.path}")
    private String wfProcessInstanceSearchPath;

    @Value("${is.workflow.enabled}")
    private Boolean isWorkflowEnabled;

    @Value("${ilms.create.workflow.name}")
    private String createPTWfName;

    @Value("${ilms.legacy.entry.workflow.name}")
    private String LegacyPTWfName;

    @Value("${ilms.update.workflow.name}")
    private String updatePTWfName;

    @Value("${is.mutation.workflow.enabled}")
    private Boolean isMutationWorkflowEnabled;

    @Value("${mutation.workflow.name}")
    private String mutationWfName;

    @Value("${mutation.workflow.open.state}")
    private String mutationOpenState;

    @Value("${workflow.status.active}")
    private String wfStatusActive;

    @Value("${ilms.module.name}")
    private String propertyModuleName;

    // Idgen Config
    @Value ("${egov.idgen.host}")
    private String idGenHost;

    @Value ("${egov.idgen.path}")
    private String idGenPath;

    @Value ("${egov.ilms.idgen.case.id.name}")
    private String caseIdgenName;

    @Value ("${egov.ilms.idgen.case.id.format}")
    private String caseIdgenFormat;

    @Value ("${egov.ilms.idgen.act.id.name}")
    private String actIdgenName;

    @Value ("${egov.ilms.idgen.act.id.format}")
    private String actIdgenFormat;

    @Value ("${egov.ilms.idgen.respondent.id.name}")
    private String respondentIdgenName;

    @Value ("${egov.ilms.idgen.respondent.id.format}")
    private String respondentIdgenFormat;

    @Value ("${egov.ilms.idgen.petitioner.id.name}")
    private String petitionerIdgenName;

    @Value ("${egov.ilms.idgen.petitioner.id.format}")
    private String petitionerIdgenFormat;

    @Value ("${egov.ilms.idgen.respondent.advocate.name}")
    private String respondentAdvocateIdgenName;

    @Value ("${egov.ilms.idgen.hearing.payment.id.name}")
    private String paymentIdgenName;

    @Value ("${egov.ilms.idgen.hearing.payment.id.format}")
    private String paymentIdgenFormat;

    @Value ("${egov.ilms.idgen.respondent.advocate.format}")
    private String respondentAdvocateIdgenFormat;

    @Value ("${egov.ilms.idgen.petitioner.advocate.name}")
    private String petitionerAdvocateIdgenName;

    @Value ("${egov.ilms.idgen.petitioner.advocate.format}")
    private String petitionerAdvocateIdgenFormat;

    @Value ("${egov.ilms.idgen.hearing.id.name}")
    private String hearingIdgenName;

    @Value ("${egov.ilms.idgen.hearing.id.format}")
    private String hearingIdgenFormat;

    @Value ("${egov.ilms.idgen.hearing.court.id.name}")
    private String courtIdGenName;

    @Value ("${egov.ilms.idgen.hearing.court.id.format}")
    private String courtIdGenFormat;

    @Value ("${persister.save.ilms.case.topic}")
    private String createCaseTopic;

    @Value ("${persister.update.ilms.case.topic}")
    private String updateCaseTopic;

    @Value ("${persister.update-child-case.topic}")
    private String updateChildCaseTopic;

    @Value ("${persister.save.hearing.details.topic}")
    private String createHearingDetailsTopic;

    @Value ("${persister.update.hearing.details.topic}")
    private String UpdateHearingDetailsTopic;

    @Value ("${egov.ilms.idgen.judgement.id.name}")
    private String judgeIdGenName;

    @Value ("${egov.ilms.idgen.judgement.id.format}")
    private String judgeIdGenFormat;

    @Value ("${egov.ilms.idgen.document.id.name}")
    private String documentIdGenName;

    @Value ("${egov.ilms.idgen.document.id.format}")
    private String documentIdGenFormat;

    @Value ("${persister.save.judgement.details.topic}")
    private String CreateJudgementTopic;

    @Value ("${persister.save.document.topic}")
    private String createDocumentTopic;

    @Value ("${egov.ilms.idgen.judgement.update}")
    private String UpdateJudgement;

    @Value ("${egov.ilms.default.limit}")
    private Integer defaultLimit;

    @Value ("${egov.ilms.default.offset}")
    private Integer defaultOffset;

    @Value ("${egov.ilms.max.limit}")
    private Integer maxSearchLimit;

    // ##### mdms

    @Value ("${egov.mdms.host}")
    private String mdmsHost;

    @Value ("${egov.mdms.search.endpoint}")
    private String mdmsEndpoint;

    @PostConstruct
    public void initialize() {
        TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
    }
}
