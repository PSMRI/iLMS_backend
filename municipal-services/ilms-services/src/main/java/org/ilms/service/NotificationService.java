package org.ilms.service;

import static org.ilms.util.ILMSConstants.CHANNEL_NAME_SMS;
import static org.ilms.util.ILMSConstants.CORRECTION_PENDING;
import static org.ilms.util.ILMSConstants.CREATED_STRING;
import static org.ilms.util.ILMSConstants.CREATE_STRING;
import static org.ilms.util.ILMSConstants.NOTIFICATION_APPID;
import static org.ilms.util.ILMSConstants.NOTIFICATION_PROPERTYID;
import static org.ilms.util.ILMSConstants.NOTIFICATION_PROPERTY_LINK;
import static org.ilms.util.ILMSConstants.NOTIFICATION_STATUS;
import static org.ilms.util.ILMSConstants.NOTIFICATION_TENANTID;
import static org.ilms.util.ILMSConstants.NOTIFICATION_UPDATED_CREATED_REPLACE;
import static org.ilms.util.ILMSConstants.UPDATED_STRING;
import static org.ilms.util.ILMSConstants.UPDATE_NO_WORKFLOW;
import static org.ilms.util.ILMSConstants.UPDATE_STRING;
import static org.ilms.util.ILMSConstants.WF_NO_WORKFLOW;
import static org.ilms.util.ILMSConstants.WF_STATUS_APPROVED;
import static org.ilms.util.ILMSConstants.WF_STATUS_DOCVERIFIED;
import static org.ilms.util.ILMSConstants.WF_STATUS_DOCVERIFIED_LOCALE;
import static org.ilms.util.ILMSConstants.WF_STATUS_FIELDVERIFIED;
import static org.ilms.util.ILMSConstants.WF_STATUS_FIELDVERIFIED_LOCALE;
import static org.ilms.util.ILMSConstants.WF_STATUS_OPEN;
import static org.ilms.util.ILMSConstants.WF_STATUS_OPEN_LOCALE;
import static org.ilms.util.ILMSConstants.WF_STATUS_REJECTED;
import static org.ilms.util.ILMSConstants.WF_STATUS_REJECTED_LOCALE;
import static org.ilms.util.ILMSConstants.WF_UPDATE_STATUS_APPROVED_CODE;
import static org.ilms.util.ILMSConstants.WF_UPDATE_STATUS_CHANGE_CODE;
import static org.ilms.util.ILMSConstants.WF_UPDATE_STATUS_OPEN_CODE;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.egov.common.contract.request.RequestInfo;
import org.ilms.configs.ILMSConfiguration;
import org.ilms.util.NotificationUtil;
import org.ilms.web.model.ILMSCase;
import org.ilms.web.model.ILMSCaseRequest;
import org.ilms.web.model.SMSRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class NotificationService {
    @Autowired
    NotificationUtil notificationUtil;

    @Autowired
    ILMSConfiguration ilmsConfiguration;

    public void sendNotificationForUpdate(ILMSCaseRequest ilmsCaseRequest) {

        ILMSCase ilmsCase = ilmsCaseRequest.getIlmsCase();
        //        ProcessInstance wf = property.getWorkflow();
        String createOrUpdate = null;
        String msg = null;

        Boolean isCreate = CreationReason.CREATE.equals(property.getCreationReason());
        String state = getStateFromWf(wf, configs.getIsWorkflowEnabled());
        // TODO: 12-Dec-22
        String state = null;
        String completeMsgs = notificationUtil.getLocalizationMessages(ilmsCase.getTenantId(), ilmsCaseRequest.getRequestInfo());
        String localisedState = getLocalisedState(wf, completeMsgs);
        switch (state) {

            case WF_NO_WORKFLOW:
                createOrUpdate = isCreate ? CREATED_STRING : UPDATED_STRING;
                msg = getMsgForUpdate(ilmsCase, UPDATE_NO_WORKFLOW, completeMsgs, createOrUpdate);
                break;

            case WF_STATUS_OPEN:
                createOrUpdate = isCreate ? CREATE_STRING : UPDATE_STRING;
                msg = getMsgForUpdate(ilmsCase, WF_UPDATE_STATUS_OPEN_CODE, completeMsgs, createOrUpdate);
                break;

            case WF_STATUS_APPROVED:
                createOrUpdate = isCreate ? CREATED_STRING : UPDATED_STRING;
                msg = getMsgForUpdate(ilmsCase, WF_UPDATE_STATUS_APPROVED_CODE, completeMsgs, createOrUpdate);
                break;

            default:
                createOrUpdate = isCreate ? CREATE_STRING : UPDATE_STRING;
                msg = getMsgForUpdate(ilmsCase, WF_UPDATE_STATUS_CHANGE_CODE, completeMsgs, createOrUpdate);
                break;
        }

        msg = replaceCommonValues(ilmsCase, msg, localisedState);
        prepareMsgAndSend(ilmsCaseRequest, msg, state);
    }

    private void prepareMsgAndSend(ILMSCaseRequest request, String msg, String state) {

        ILMSCase ilmsCase = request.getIlmsCase();
        RequestInfo requestInfo = request.getRequestInfo();
        Map<String, String> mobileNumberToOwner = new HashMap<>();
        String tenantId = request.getIlmsCase().getTenantId();
        //        String moduleName = request.getWorkflow().getModuleName();

        //        String action;
        //        if(request.getWorkflow()!=null)
        //            action = request.getWorkflow().getAction();
        //        else
        //            action = WF_NO_WORKFLOW;

        // TODO: 12-Dec-22 setting temperary values
        String action = null;
        String moduleName = null;

        List<String> configuredChannelNames = notificationUtil.fetchChannelList(new RequestInfo(), tenantId, moduleName, action);
        Set<String> mobileNumbers = new HashSet<>();

        // TODO: 12-Dec-22 Implement workflow to get the next state mobile no.
        //        request.getWorkflow().getAssignes().forEach(assigne -> {
        //            if (assigne.getMobileNumber() != null)
        //                mobileNumberToOwner.put(assigne.getMobileNumber(), assigne.getName());
        //            mobileNumbers.add(assigne.getMobileNumber());
        //        });

        List<SMSRequest> smsRequests = notificationUtil.createSMSRequest(msg, mobileNumberToOwner);

        if (configuredChannelNames.contains(CHANNEL_NAME_SMS)) {
            notificationUtil.sendSMS(smsRequests);

            Boolean isActionReq = false;
            if (state.equalsIgnoreCase(CORRECTION_PENDING)) {
                isActionReq = true;
            }

            //            List<Event> events = notifUtil.enrichEvent(smsRequests, requestInfo, property.getTenantId(), property, isActionReq);
            //            notifUtil.sendEventNotification(new EventRequest(requestInfo, events));
        }
        //        if (configuredChannelNames.contains(CHANNEL_NAME_EMAIL)) {
        //            List<EmailRequest> emailRequests = notifUtil.createEmailRequestFromSMSRequests(requestInfo, smsRequests, tenantId);
        //            notifUtil.sendEmail(emailRequests);
        //        }
    }

    private String getLocalisedState(ProcessInstance workflow, String completeMsgs) {

        String state = "";
        //        if (ilmsConfiguration.getIsWorkflowEnabled()) {
        //            state = workflow.getState().getState();
        //        }

        switch (state) {

            case WF_STATUS_REJECTED:
                return notificationUtil.getMessageTemplate(WF_STATUS_REJECTED_LOCALE, completeMsgs);

            case WF_STATUS_DOCVERIFIED:
                return notificationUtil.getMessageTemplate(WF_STATUS_DOCVERIFIED_LOCALE, completeMsgs);

            case WF_STATUS_FIELDVERIFIED:
                return notificationUtil.getMessageTemplate(WF_STATUS_FIELDVERIFIED_LOCALE, completeMsgs);

            case WF_STATUS_OPEN:
                return notificationUtil.getMessageTemplate(WF_STATUS_OPEN_LOCALE, completeMsgs);

            //            case PT_UPDATE_OWNER_NUMBER:
            //                return notifUtil.getMessageTemplate(PT_UPDATE_OWNER_NUMBER, completeMsgs);

        }
        return state;
    }

    private String getMsgForUpdate(ILMSCase ilmsCase, String msgCode, String completeMsgs, String createUpdateReplaceString) {

        String url = notificationUtil.getShortenedUrl(ilmsConfiguration.getUiAppHost().concat(ilmsConfiguration.getViewPropertyLink()
                                                                                                               .replace(NOTIFICATION_PROPERTYID,
                                                                                                                       ilmsCase.getId())
                                                                                                               .replace(NOTIFICATION_TENANTID,
                                                                                                                       ilmsCase.getTenantId())));

        return notificationUtil.getMessageTemplate(msgCode, completeMsgs).replace(NOTIFICATION_PROPERTY_LINK, url)
                               .replace(NOTIFICATION_UPDATED_CREATED_REPLACE, createUpdateReplaceString);
    }

    private String replaceCommonValues(ILMSCase ilmsCase, String msg, String localisedState) {

        msg = msg.replace(NOTIFICATION_PROPERTYID, ilmsCase.getId()).replace(NOTIFICATION_APPID, ilmsCase.getCaseNumber());

        if (ilmsConfiguration.getIsWorkflowEnabled()) {
            msg = msg.replace(NOTIFICATION_STATUS, localisedState);
        }
        return msg;
    }
}
