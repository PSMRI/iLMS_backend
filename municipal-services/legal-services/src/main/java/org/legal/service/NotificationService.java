package org.legal.service;

import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.json.JSONObject;
import org.legal.configs.LEGALConfiguration;
import org.legal.repository.CaseRepository;
import org.legal.repository.ServiceRepository;
import org.legal.util.LegalErrorConstants;
import org.legal.util.NotificationUtil;
import org.legal.web.model.Case;
import org.legal.web.model.CaseRequest;
import org.legal.web.model.CaseResponse;
import org.legal.web.model.CaseSearchCriteria;
import org.legal.web.model.Recepient;
import org.legal.web.model.enums.Source;
import org.legal.web.model.notification.Action;
import org.legal.web.model.notification.Email;
import org.legal.web.model.notification.EmailRequest;
import org.legal.web.model.notification.Event;
import org.legal.web.model.notification.EventRequest;
import org.legal.web.model.notification.SMSRequest;
import org.legal.web.model.user.UserSearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.apache.commons.lang3.ClassUtils.getName;
import static org.legal.util.LEGALConstants.*;

@Slf4j
@Service
public class NotificationService {
    @Autowired
    NotificationUtil notificationUtil;

    @Autowired
    LEGALConfiguration configs;

    @Autowired
    CaseRepository caseRepository;

    @Value("${notification.url}")
    private String notificationURL;

    @Autowired
    private ServiceRepository restRepo;

    public void process(String topicName, CaseRequest caseRequest) {

        RequestInfo requestInfo = caseRequest.getRequestInfo();
        String assignee;
        if (!caseRequest.getWorkflow().getAssignes().get(0).isEmpty()) {
            assignee = caseRequest.getWorkflow().getAssignes().get(0);
        } else {
            assignee = caseRequest.getRequestInfo().getUserInfo().getUuid();
        }
        Case cases = caseRequest.getCaseObj();
        String moduleName = configs.getModuleName();
        String action = caseRequest.getWorkflow().getAction();
        String tenantId;
        if (cases.getTenantId() != null) {
            tenantId = cases.getTenantId();
        } else {
            String caseId = caseRequest.getCaseObj().getId();
            CaseSearchCriteria criteria = CaseSearchCriteria.builder().id(Collections.singletonList(caseId)).build();
            CaseResponse caseResponse = caseRepository.getLegalCaseData(criteria);
            tenantId = caseResponse.getCaseList().get(0).getTenantId();
        }

        List<String> configuredChannelNamesForCase = notificationUtil.fetchChannelList(new RequestInfo(), tenantId, moduleName,
                action);

        List<SMSRequest> smsRequests = enrichSMSRequest(topicName, caseRequest, tenantId);
        if (configuredChannelNamesForCase.contains(CHANNEL_NAME_SMS)) {
            notificationUtil.sendSMS(smsRequests);
        }

        if (configuredChannelNamesForCase.contains(CHANNEL_NAME_EVENT)) {
            List<Event> events = notificationUtil.enrichEvent(smsRequests, requestInfo, assignee, tenantId);
            notificationUtil.sendEventNotification(new EventRequest(requestInfo, events));
        }

        if (configuredChannelNamesForCase.contains(CHANNEL_NAME_EMAIL)) {
            List<EmailRequest> emailRequests = notificationUtil.createEmailRequestFromSMSRequests(requestInfo, smsRequests, tenantId);
            notificationUtil.sendEmail(emailRequests);
        }
    }

    private List<SMSRequest> enrichSMSRequest(String topicName, CaseRequest request, String tenantId) {

        String finalMessage = getFinalMessage(request, topicName);
        String officerId;
        List<String> ids = new ArrayList<>();
        if (!(request.getWorkflow().getAssignes()).isEmpty()) {
            officerId = request.getWorkflow().getAssignes().get(0);
            ids.add(officerId);
        } else {
            officerId = request.getRequestInfo().getUserInfo().getUuid();
            ids.add(officerId);
        }
        Map<String, String> mobileNumberToOwner = fetchUsersByOfficerId(ids, tenantId);
        return notificationUtil.createSMSRequest(finalMessage, mobileNumberToOwner);
    }

    private String getFinalMessage(CaseRequest request, String topic) {
        String tenantId = request.getCaseObj().getTenantId();
        String action = request.getWorkflow().getAction();
        String localizationMessages = notificationUtil.getLocalizationMessages(tenantId, request.getRequestInfo());

        String message =getCustomizedMsg(action, localizationMessages);
        if (message == null) {
            log.info("No message Found For Topic : " + topic);
            return message;
        }
        String finalMessage = getMessageForMobileNumber(message,request);
        return finalMessage;
    }
    public String getMessageForMobileNumber(String message, CaseRequest request){
        String messageToReplace = message;

        if (messageToReplace.contains("{id}"))
            messageToReplace = messageToReplace.replace("{id}",request.getCaseObj().getId());
        return messageToReplace;
    }

        //    private String getCustomizedMsg(String topicName, Case cases, String action, String localizationMessages) {
//
//        String msgCode = null, messageTemplate = null;
//        msgCode = action;
//
//        messageTemplate = customize(cases, msgCode, localizationMessages);
//
//        return messageTemplate;
//    }
//
//    private String customize(Case cases, String msgCode, String localizationMessages) {
//
//        String messageTemplate = notificationUtil.getMessageTemplate(msgCode, localizationMessages);
//
//        messageTemplate = messageTemplate.replace(NOTIFICATION_CASEID, cases.getId());
//
//        return messageTemplate;
//    }
public String getCustomizedMsg(String action, String localizationMessage) {
    StringBuilder notificationCode = new StringBuilder();
    notificationCode.append("LEGAL_").append(action.toUpperCase()).append("_SMS_MESSAGE");
    String path = "$..messages[?(@.code==\"{}\")].message";
    path = path.replace("{}", notificationCode);
    String message = null;
    try {
        ArrayList<String> messageObj = (ArrayList<String>) JsonPath.parse(localizationMessage).read(path);
        if(messageObj != null && messageObj.size() > 0) {
            message = messageObj.get(0);
        }
    } catch (Exception e) {
        log.warn("Fetching from localization failed", e);
    }
    return message;
}

    public Map<String, String> fetchUsersByOfficerId(List<String> officerId, String tenantId) {
        StringBuilder uri = new StringBuilder();
        uri.append(configs.getUserHost()).append(configs.getUserSearchEndPoint());
        Map<String, Object> userSearchRequest = new HashMap<>();
        userSearchRequest.put("tenantId", tenantId);
        userSearchRequest.put("uuid", officerId);
        Map<String, String> mobileNumberToUser = new HashMap<>();
        try {
            Object user = restRepo.fetchUserResult(uri, userSearchRequest);
            if (user != null) {
                String mobileNumber = JsonPath.read(user, "$.user[0].mobileNumber");
                mobileNumberToUser.put("mobileNumber", mobileNumber);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException(LegalErrorConstants.UNABLE_TO_FETCH, "Unable to fetch User from system");
        }
        return mobileNumberToUser;
    }

    public void schedulerMsg(RequestInfo requestInfo, String uuid, String action) {
        List<String> ids = new ArrayList<>();
        ids.add(uuid);
        String tenantId = configs.getTenantId();
        String localizationMessages = notificationUtil.getLocalizationMessages(tenantId, requestInfo);
        String message = getCustomizedMsgForSchedular(localizationMessages, action, uuid);
        if (configs.getIsUserEventsNotificationEnabled() != null && configs.getIsUserEventsNotificationEnabled()) {
            EventRequest eventRequest = enrichEventRequestForScheduler(requestInfo, message, uuid);
            if (eventRequest != null) {
                notificationUtil.sendEventNotification(eventRequest);
            }
        }
        if (configs.getIsSMSNotificationEnabled() != null && configs.getIsSMSNotificationEnabled()) {
            List<SMSRequest> smsRequests = new ArrayList<>();
//            String locale=requestInfo.getMsgId()!=null ? requestInfo.getMsgId().split("\\|")[1]:"en_IN";
//            log.info("locale is "+locale );
            Map<String, String> mobileNumbers = fetchUsersByOfficerId(ids, tenantId);
            smsRequests = enrichSmsRequestForEmployee(mobileNumbers, message);
            if (!CollectionUtils.isEmpty(smsRequests)) {
                notificationUtil.sendSMS(smsRequests);
            }
        }
        if (configs.getIsEmailNotificationEnabled() != null && configs.getIsEmailNotificationEnabled()) {
            List<EmailRequest> emailRequests = new ArrayList<>();
            emailRequests = enrichEmailRequestForEmployee(requestInfo, uuid, message, tenantId);
            if (emailRequests != null) {
                notificationUtil.sendEmail(emailRequests);
            }
        }

    }

    private String getCustomizedMsgForSchedular(String localizationMessages, String action, String uuid) {
        String msgCode = null, messageTemplate = null;
        msgCode = action;
        messageTemplate = customizeForSchedular(msgCode, localizationMessages, uuid);
        return messageTemplate;
    }

    private String customizeForSchedular(String msgCode, String localizationMessages, String uuid) {
        String messageTemplate = getMessageTemplate(msgCode, localizationMessages, uuid);
        return messageTemplate;
    }

    public String getMessageTemplate(String notificationCode, String localizationMessage, String uuid) {
        String path = "$..messages[?(@.code==\"{}\")].message";
        path = path.replace("{}", notificationCode);
        String message = "";
        try {
            Object messageObj = JsonPath.parse(localizationMessage).read(path);
            message = ((ArrayList<String>) messageObj).get(0);
        } catch (Exception e) {
            e.printStackTrace();
            log.warn("Fetching from localization failed", e);
        }
        return message;
    }

    private EventRequest enrichEventRequestForScheduler(RequestInfo requestInfo, String finalMessage, String uuid) {
        String tenantId = configs.getTenantId();
        List<Event> events = new ArrayList<>();
        List<String> toUsers = new ArrayList<>();
        toUsers.add(uuid);
        Action action = null;
        Recepient recepient = Recepient.builder().toUsers(toUsers).toRoles(null).build();
        events.add(Event.builder().tenantId(tenantId).description(finalMessage).eventType(USREVENTS_EVENT_TYPE)
                .name(USREVENTS_EVENT_NAME).postedBy(USREVENTS_EVENT_POSTEDBY)
                .source(Source.WEBAPP).recepient(recepient).actions(action).eventDetails(null).build());

        if (!CollectionUtils.isEmpty(events)) {
            return EventRequest.builder().requestInfo(requestInfo).events(events).build();
        } else {
            return null;
        }
    }

    private List<SMSRequest> enrichSmsRequestForEmployee(Map<String, String> mobileNumber, String finalMessage) {
        List<SMSRequest> smsRequest = new LinkedList<>();
        for (Map.Entry<String, String> entryset : mobileNumber.entrySet()) {
            smsRequest.add(new SMSRequest(entryset.getValue(), finalMessage));
        }
        return smsRequest;
    }

    private List<EmailRequest> enrichEmailRequestForEmployee(RequestInfo requestInfo, String uuid, String finalMessage, String tenantId) {
        List<String> uuids = new ArrayList<>();
        uuids.add(uuid);
        Map<String, String> mobileNumberToEmailId = fetchUserEmail(uuids);
        if (CollectionUtils.isEmpty(mobileNumberToEmailId.keySet())) {
            log.error("Email Ids Not found for Mobilenumbers");
        }
        List<EmailRequest> emailRequest = new LinkedList<>();
        for (Map.Entry<String, String> entryset : mobileNumberToEmailId.entrySet()) {
            if (finalMessage.contains("##")) {
                finalMessage = finalMessage.split("##")[0];
            }
            String message = finalMessage;
            String subject = configs.getNotifSubject();
            String body = message;
            Email emailobj = Email.builder().emailTo(Collections.singleton(entryset.getValue())).body(body).subject(subject).build();
            EmailRequest email = new EmailRequest(requestInfo, emailobj);
            emailRequest.add(email);
        }
        return emailRequest;
    }

    public Map<String, String> fetchUserEmail(List<String> uuid) {
        Map<String, String> mapOfPhoneNoAndEmails = new HashMap<>();
        StringBuilder uri = new StringBuilder();
        uri.append(configs.getUserHost()).append(configs.getUserSearchEndPoint());
        UserSearchRequest userSearchRequest = new UserSearchRequest();
        userSearchRequest.setUuid(uuid);
        try {
            Object user = restRepo.fetchResult(uri, userSearchRequest);
            if (null != user) {
                String emailId = JsonPath.read(user, "$.user[0].emailId");
                mapOfPhoneNoAndEmails.put(uuid.get(0), emailId);
            } else {
                log.error("Service returned null while fetching user for username - " + uuid);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Exception while fetching user for username - " + uuid);
            log.error("Exception trace: ", e);
        }
        return mapOfPhoneNoAndEmails;
    }
}
