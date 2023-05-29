package org.legal.service;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.legal.configs.LEGALConfiguration;
import org.legal.repository.CaseRepository;
import org.legal.repository.HearingRepository;
import org.legal.repository.JudgementRepository;
import org.legal.web.model.Case;
import org.legal.web.model.CaseResponse;
import org.legal.web.model.CaseSearchCriteria;
import org.legal.web.model.Hearing;
import org.legal.web.model.HearingResponse;
import org.legal.web.model.HearingSearchCriteria;
import org.legal.web.model.Judgement;
import org.legal.web.model.JudgementResponse;
import org.legal.web.model.JudgementSearchCriteria;
import org.legal.web.model.workflow.ProcessInstance;
import org.legal.web.model.workflow.ProcessInstanceResponse;
import org.legal.web.model.workflow.ProcessInstanceSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EscalationService {

    @Autowired
    private LEGALConfiguration config;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private CaseService caseService;

    @Autowired
    private CaseRepository caseRepository;

    @Autowired
    private HearingRepository hearingRepository;

    @Autowired
    private JudgementRepository judgementRepository;

    @Autowired
    private WorkflowService workflowService;


    public void fetchSLAs(RequestInfo requestInfo) {
        try {
            CaseSearchCriteria criteria = new CaseSearchCriteria();
            criteria.setLimit(-1);
            List<Case> caseList = new ArrayList<>();
            CaseResponse caseResponse = null;
            caseResponse = caseRepository.getLegalCaseData(criteria);
            caseResponse.getCaseList().forEach(caseObj -> {
                caseList.add(caseObj);
            });

            for (Case cases : caseList) {
                log.info("For loop for service defs");
                String caseId = cases.getId();

                Long lastModifiedTime = null;
                if (cases.getAuditDetails().getLastModifiedTime() == 0) {
                    lastModifiedTime = cases.getAuditDetails().getCreatedTime();
                } else {
                    lastModifiedTime = cases.getAuditDetails().getLastModifiedTime();
                }
                Date lastModifiedDate = new Date(lastModifiedTime);

                ProcessInstanceResponse processInstanceResponse = workflowService.getWorkflow(requestInfo, cases.getTenantId(), caseId);
                String assigneeUUid = processInstanceResponse.getProcessInstances().get(0).getAssignes().get(0).getUuid();
                Long slaDay = config.getSlaDays();// 20
                Long pendingSLADay = config.getPendingDays();

                Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
                Date currentDate = new Date(timeStamp.getTime());
                long remainingDays = currentDate.getDate() - lastModifiedDate.getDate();
                Long slaRemaining = slaDay - remainingDays;
                //     String uuid = cases.getWorkflow().getAssignes().get(0).getUuid();
                if (slaRemaining <= (pendingSLADay)) {
                    String action = "SCHEDULAR_ACTION_CURRENT_PROCESS";
                    log.info("Sending notification");
                    notificationService.schedulerMsg(requestInfo, assigneeUUid, action);
                    log.info("Sent the notification");
                    System.out.println("Notification sent successfully!");
                }
            }

            HearingSearchCriteria hearingCriteria = new HearingSearchCriteria();
            criteria.setLimit(-1);
            List<Hearing> hearingList = new ArrayList<>();
            HearingResponse hearingResponse = null;
            hearingResponse = hearingRepository.getHearingDetails(hearingCriteria);
            hearingResponse.getHearingList().forEach(hearingObj -> {
                hearingList.add(hearingObj);
            });
            for (Hearing hearing : hearingList) {
                log.info("For loop for service defs");
                String hearingId = hearing.getId();

                Long lastModifiedTime = null;
                if (hearing.getAuditDetails().getLastModifiedTime() == 0) {
                    lastModifiedTime = hearing.getAuditDetails().getCreatedTime();
                } else {
                    lastModifiedTime = hearing.getAuditDetails().getLastModifiedTime();
                }
                Date lastModifiedDate = new Date(lastModifiedTime);

                ProcessInstanceResponse processInstanceResponse = workflowService.getWorkflow(requestInfo, hearing.getTenantId(), hearingId);
                String assigneeUUid = processInstanceResponse.getProcessInstances().get(0).getAssignes().get(0).getUuid();
                Long slaDay = config.getSlaDays();// 20
                Long pendingSLADay = config.getPendingDays();

                Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
                Date currentDate = new Date(timeStamp.getTime());
                long remainingDays = currentDate.getDate() - lastModifiedDate.getDate();
                Long slaRemaining = slaDay - remainingDays;
                //     String uuid = cases.getWorkflow().getAssignes().get(0).getUuid();
                if (slaRemaining <= (pendingSLADay)) {
                    String action = "SCHEDULAR_ACTION_CURRENT_PROCESS";
                    log.info("Sending notification");
                    notificationService.schedulerMsg(requestInfo, assigneeUUid, action);
                    log.info("Sent the notification");
                    System.out.println("Notification sent successfully!");
                }
            }

            JudgementSearchCriteria judgementSearchCriteria = new JudgementSearchCriteria();
            criteria.setLimit(-1);
            List<Judgement> judgementList = new ArrayList<>();
            JudgementResponse judgementResponse = null;
            judgementResponse = judgementRepository.getJudgementData(judgementSearchCriteria);
            judgementResponse.getJudgementList().forEach(judgementObj -> {
                judgementList.add(judgementObj);
            });
            for (Judgement judgement : judgementList) {
                String judgementId = judgement.getId();

                Long lastModifiedTime = null;
                if (judgement.getAuditDetails().getLastModifiedTime() == 0) {
                    lastModifiedTime = judgement.getAuditDetails().getCreatedTime();
                } else {
                    lastModifiedTime = judgement.getAuditDetails().getLastModifiedTime();
                }
                Date lastModifiedDate = new Date(lastModifiedTime);

                ProcessInstanceResponse processInstanceResponse = workflowService.getWorkflow(requestInfo, judgement.getTenantId(), judgementId);
                String assigneeUUid = processInstanceResponse.getProcessInstances().get(0).getAssignes().get(0).getUuid();
                Long slaDay = config.getSlaDays();// 20
                Long pendingSLADay = config.getPendingDays();

                Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
                Date currentDate = new Date(timeStamp.getTime());
                long remainingDays = currentDate.getDate() - lastModifiedDate.getDate();
                Long slaRemaining = slaDay - remainingDays;
                //     String uuid = cases.getWorkflow().getAssignes().get(0).getUuid();
                if (slaRemaining <= (pendingSLADay)) {
                    String action = "SCHEDULAR_ACTION_CURRENT_PROCESS";
                    log.info("Sending notification");
                    notificationService.schedulerMsg(requestInfo, assigneeUUid, action);
                    log.info("Sent the notification");
                    System.out.println("Notification sent successfully!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("SLA Notification Failed: ", e);
        }
    }
}
