package org.legal.service;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.egov.common.contract.request.RequestInfo;
import org.legal.configs.LEGALConfiguration;
import org.legal.repository.CaseRepository;
import org.legal.web.model.Case;
import org.legal.web.model.CaseResponse;
import org.legal.web.model.CaseSearchCriteria;
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





        public void fetchSLAs(RequestInfo requestInfo) {
            try {
                ProcessInstanceSearchCriteria processInstanceSearchCriteria = null;
                ObjectMapper mapper = new ObjectMapper();
                Long slaHours = null;
                Long pendingSLA = null;
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
                    // for (ServiceDefs serviceDefs1 : serviceDefsList) {
                    // if (serviceWrapper.getService().getServiceCode().equalsIgnoreCase(serviceDefs1.getServiceCode())) {
                    //                            slaDa = serviceDefs1.getSlaHours();
                    //                            pendingSLA = serviceDefs1.getPendingSLA();
                    Long slaDay = config.getSlaDays();// 20
                    Long pendingSLADay = config.getPendingDays(); //5

                    Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
                    Date currentDate = new Date(timeStamp.getTime()); //13may
                    if (cases.getWorkflow().getNotificationAction().contains("Pending_Ro")) {
                        Long createdTime = cases.getAuditDetails().getCreatedTime();
                        Timestamp timeStamp1 = new Timestamp(createdTime);
                        Date createdDate = new Date(timeStamp1.getTime()); //25Apr
                        long remainingDays = currentDate.getDate() - createdDate.getDate();

                        Long slaRemaining = slaDay - remainingDays;

                        String uuid = cases.getWorkflow().getAssignes().get(0).getUuid();
                        if (slaRemaining <= (pendingSLADay)) {
                            String action = "SCHEDULAR_ACTION_CURRENT";
                            log.info("Sending notification");
                            notificationService.schedulerMsg(requestInfo, uuid, action);
                            log.info("Sent the notification");
                            System.out.println("Notification sent successfully!");
                        }
                        // }
                        //  }
                        // }
                    }
                }
            }
            catch (Exception e) {
                log.error("SLA Notification Failed: ", e);
            }
    }

}
