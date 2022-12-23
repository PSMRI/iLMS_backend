package org.ilms.service;

import static org.ilms.util.ILMSConstants.ACTION_FOR_ASSESSMENT;
import static org.ilms.util.ILMSConstants.CHANNEL_NAME_EMAIL;
import static org.ilms.util.ILMSConstants.CHANNEL_NAME_SMS;
import static org.ilms.util.ILMSConstants.CREATE_STRING;
import static org.ilms.util.ILMSConstants.NOTIFICATION_CASEID;
import static org.ilms.util.ILMSConstants.PT_BUSINESSSERVICE;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.ilms.configs.ILMSConfiguration;
import org.ilms.repository.CaseRepository;
import org.ilms.repository.ServiceRepository;
import org.ilms.util.ILMSErrorConstants;
import org.ilms.util.NotificationUtil;
import org.ilms.web.model.Case;
import org.ilms.web.model.CaseRequest;
import org.ilms.web.model.CaseResponse;
import org.ilms.web.model.CaseSearchCriteria;
import org.ilms.web.model.EmailRequest;
import org.ilms.web.model.SMSRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class NotificationService {
    @Autowired
    NotificationUtil notificationUtil;

    @Autowired
    ILMSConfiguration ilmsConfiguration;

    @Autowired
    CaseRepository caseRepository;

    @Value ("${notification.url}")
    private String notificationURL;

    @Autowired
    private ServiceRepository restRepo;

    public void process(String topicName, CaseRequest caseRequest) {

        RequestInfo requestInfo = caseRequest.getRequestInfo();
        Case cases = caseRequest.getCases();
        String tenantId;
        if (cases.getTenantId()!=null){
            tenantId = cases.getTenantId();
        } else {
            String caseId = caseRequest.getCases().getId();
            CaseSearchCriteria criteria = CaseSearchCriteria.builder().id(Collections.singletonList(caseId)).build();
            CaseResponse caseResponse = caseRepository.getILMSCaseData(criteria);
            tenantId = caseResponse.getCases().get(0).getTenantId();
        }

        List<String> configuredChannelNamesForCase = notificationUtil.fetchChannelList(new RequestInfo(), tenantId, PT_BUSINESSSERVICE,
                ACTION_FOR_ASSESSMENT);

        List<SMSRequest> smsRequests = enrichSMSRequest(topicName, caseRequest, cases,tenantId);
        if (configuredChannelNamesForCase.contains(CHANNEL_NAME_SMS)) {
            notificationUtil.sendSMS(smsRequests);
        }

//        if (configuredChannelNamesForCase.contains(CHANNEL_NAME_EVENT)) {
//            Boolean isActionReq = false;
//            if (topicName.equalsIgnoreCase(ilmsConfiguration.getCreateCaseTopic()) && cases.getWorkflow() == null)
//                isActionReq = true;
//
//            List<Event> events = notificationUtil.enrichEvent(smsRequests, requestInfo, tenantId, cases, isActionReq);
//            notificationUtil.sendEventNotification(new EventRequest(requestInfo, events));
//        }

        if (configuredChannelNamesForCase.contains(CHANNEL_NAME_EMAIL)) {
            List<EmailRequest> emailRequests = notificationUtil.createEmailRequestFromSMSRequests(requestInfo, smsRequests, tenantId);
            notificationUtil.sendEmail(emailRequests);
        }
    }

    private List<SMSRequest> enrichSMSRequest(String topicName, CaseRequest request, Case cases,String tenantId) {

        String localizationMessages = notificationUtil.getLocalizationMessages(tenantId, request.getRequestInfo());
       String message = getCustomizedMsg(topicName, cases, localizationMessages);
        String officerId=request.getCases().getAssignedOfficerId();
        List<String> ids = new ArrayList<>();
        ids.add(officerId);
        Map<String, String> mobileNumberToOwner =  fetchUsersByOfficerId(ids,tenantId);
        if (message == null)
            return Collections.emptyList();
        return notificationUtil.createSMSRequest(message, mobileNumberToOwner);
    }

    private String getCustomizedMsg(String topicName, Case cases, String localizationMessages) {

        String msgCode = null, messageTemplate = null;
        String action;
        action = cases.getWorkflow().getAction();

            if (topicName.equalsIgnoreCase(ilmsConfiguration.getCreateCaseTopic()))
                msgCode = CREATE_STRING;

            else
                msgCode = action;

            messageTemplate = customize(cases, msgCode, localizationMessages);

        return messageTemplate;
    }

    private String customize(Case cases, String msgCode, String localizationMessages) {

        String messageTemplate = notificationUtil.getMessageTemplate(msgCode, localizationMessages);

            messageTemplate = messageTemplate.replace(NOTIFICATION_CASEID, cases.getId());

        return messageTemplate;
    }

    public Map<String, String> fetchUsersByOfficerId(List<String> officerId, String tenantId) {
        StringBuilder uri = new StringBuilder();
        uri.append(ilmsConfiguration.getUserHost()).append(ilmsConfiguration.getUserSearchEndPoint());
        Map<String, Object> userSearchRequest = new HashMap<>();
        userSearchRequest.put("tenantId", tenantId);
        userSearchRequest.put("uuid", officerId);
        Map<String, String> mobileNumberToUser  = new HashMap<>();
        try {
            Object user = restRepo.fetchUserResult(uri, userSearchRequest);
            if (user != null) {
                String mobileNumber = JsonPath.read(user, "$.user[0].mobileNumber");
                mobileNumberToUser.put("mobileNumber", mobileNumber);
            }
        } catch (Exception e) {
            throw new CustomException(ILMSErrorConstants.UNABLE_TO_FETCH, "Unable to fetch User from system");
        }
        return  mobileNumberToUser;
    }

}
