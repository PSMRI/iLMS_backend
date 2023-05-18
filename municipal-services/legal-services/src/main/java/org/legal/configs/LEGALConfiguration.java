package org.legal.configs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.egov.tracer.config.TracerConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.TimeZone;

@Import({TracerConfiguration.class})
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Component
public class LEGALConfiguration {
    @Value("${app.timezone}")
    private String timeZone;

    //    MDMS Config

    @Value("${legal.business.codes}")
    private List<String> businessServiceList;

    @Value("${workflow.host}")
    private String wfHost;

    @Value("${workflow.transition.path}")
    private String wfTransitionPath;

    @Value("${workflow.businessservice.search.path}")
    private String wfBusinessServiceSearchPath;

    @Value("${workflow.processinstance.search.path}")
    private String wfProcessInstanceSearchPath;

    @Value("${workflow.process.statuscount.path}")
    private String processStatusCountPath;

    @Value("${is.workflow.enabled}")
    private Boolean isWorkflowEnabled;

    @Value("${legal.create.case.workflow.name}")
    private String createCaseWfName;

    @Value("${legal.create.hearing.workflow.name}")
    private String createHearingWfName;

    @Value("${legal.create.judgement.workflow.name}")
    private String createJudgementWfName;

    @Value("${legal.legacy.entry.workflow.name}")
    private String legacyIlmsWfName;

    @Value("${legal.update.workflow.name}")
    private String updateIlmsWfName;

    @Value("${is.mutation.workflow.enabled}")
    private Boolean isMutationWorkflowEnabled;

    @Value("${mutation.workflow.name}")
    private String mutationWfName;

    @Value("${mutation.workflow.open.state}")
    private String mutationOpenState;

    @Value("${workflow.status.active}")
    private String wfStatusActive;

    @Value("${legal.module.name}")
    private String propertyModuleName;

    // Idgen Config
    @Value("${egov.idgen.host}")
    private String idGenHost;

    @Value("${egov.idgen.path}")
    private String idGenPath;

    @Value("${egov.legal.idgen.case.id.name}")
    private String caseIdgenName;

    @Value("${egov.legal.idgen.case.id.format}")
    private String caseIdgenFormat;

    @Value("${egov.legal.idgen.act.id.name}")
    private String actIdgenName;

    @Value("${egov.legal.idgen.act.id.format}")
    private String actIdgenFormat;

    @Value("${egov.legal.idgen.respondent.id.name}")
    private String respondentIdgenName;

    @Value("${egov.legal.idgen.respondent.id.format}")
    private String respondentIdgenFormat;

    @Value("${egov.legal.idgen.petitioner.id.name}")
    private String petitionerIdgenName;

    @Value("${egov.legal.idgen.petitioner.id.format}")
    private String petitionerIdgenFormat;

    @Value("${egov.legal.idgen.respondent.advocate.name}")
    private String respondentAdvocateIdgenName;

    @Value("${egov.legal.idgen.hearing.payment.id.name}")
    private String paymentIdgenName;

    @Value("${egov.legal.idgen.hearing.payment.id.format}")
    private String paymentIdgenFormat;

    @Value("${egov.legal.idgen.respondent.advocate.format}")
    private String respondentAdvocateIdgenFormat;

    @Value("${egov.legal.idgen.petitioner.advocate.name}")
    private String petitionerAdvocateIdgenName;

    @Value("${egov.legal.idgen.petitioner.advocate.format}")
    private String petitionerAdvocateIdgenFormat;

    @Value("${egov.legal.idgen.hearing.id.name}")
    private String hearingIdgenName;

    @Value("${egov.legal.idgen.hearing.id.format}")
    private String hearingIdgenFormat;

    @Value("${egov.legal.idgen.hearing.court.id.name}")
    private String courtIdgenName;

    @Value("${egov.legal.idgen.hearing.court.id.format}")
    private String courtIdgenFormat;

    @Value("${persister.save.legal.case.topic}")
    private String createCaseTopic;

    @Value("${persister.update.legal.case.topic}")
    private String updateCaseTopic;

    @Value("${persister.save.legal.advocate.topic}")
    private String createAdvocateTopic;

    @Value("${persister.update.legal.advocate.topic}")
    private String updateAdvocateTopic;

    @Value("${persister.update-child-case.topic}")
    private String updateChildCaseTopic;

    @Value("${persister.save.hearing.details.topic}")
    private String createHearingTopic;

    @Value("${persister.update.hearing.details.topic}")
    private String updateHearingTopic;

    @Value("${egov.legal.idgen.judgement.id.name}")
    private String judgementIdgenName;

    @Value("${egov.legal.idgen.judgement.id.format}")
    private String judgementIdgenFormat;

    @Value("${egov.legal.idgen.document.id.name}")
    private String documentIdgenName;

    @Value("${egov.legal.idgen.document.id.format}")
    private String documentIdgenFormat;

    @Value("${persister.save.judgement.details.topic}")
    private String createJudgementTopic;

    @Value("${persister.save.document.topic}")
    private String createDocumentTopic;

    @Value("${persister.update.document.topic}")
    private String updateDocumentTopic;

    @Value("${persister.update.judgement.details.topic}")
    private String updateJudgementTopic;

    @Value("${egov.legal.default.limit}")
    private Integer defaultLimit;

    @Value("${egov.legal.default.offset}")
    private Integer defaultOffset;

    @Value("${egov.legal.max.limit}")
    private Integer maxSearchLimit;

    // ##### mdms

    @Value("${egov.mdms.host}")
    private String mdmsHost;

    @Value("${egov.mdms.search.endpoint}")
    private String mdmsEndpoint;
    //user

    @Value("${egov.user.search.endpoint}")
    private String userSearchEndPoint;

    @Value("${egov.user.host}")
    private String userHost;

    //NOTIFICATION TOPICS
    @Value("${kafka.topics.notification.sms}")
    private String smsNotifTopic;

    @Value("${kafka.topics.notification.email}")
    private String emailNotifTopic;

    @Value("${kafka.topics.notification.pg.save.txns}")
    private String pgTopic;

    @Value("${egov.localization.statelevel}")
    private Boolean isStateLevel;

    @Value("${notif.sms.enabled}")
    private Boolean isSMSNotificationEnabled;

    @Value("${notif.email.enabled}")
    private Boolean isEmailNotificationEnabled;

    //localization

    @Value("${egov.localization.statelevel}")
    private Boolean isLocalizationStateLevel;

    @Value("${egov.localization.host}")
    private String localizationHost;

    @Value("${egov.localization.context.path}")
    private String localizationContextPath;

    @Value("${egov.localization.search.endpoint}")
    private String localizationSearchEndpoint;

    // url shortner

    @Value("${egov.url.shortner.host}")
    private String urlShortnerHost;

    @Value("${egov.url.shortner.endpoint}")
    private String urlShortnerEndpoint;

    //USER EVENTS

    @Value("${egov.usr.events.create.topic}")
    private String saveUserEventsTopic;

    @Value("${egov.user.event.notification.enabled}")
    private Boolean isUserEventsNotificationEnabled;

    @Value("${egov.notif.subject}")
    private String notifSubject;

    @Value("${legal.statelevel.tenantid}")
    private String tenantId;

    @Value("${pending.days}")
    private Long pendingDays;

    @Value("${sla.days}")
    private Long slaDays;


    @PostConstruct
    public void initialize() {
        TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
    }
}
