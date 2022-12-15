package org.ilms.service;

import static org.ilms.util.ILMSConstants.CHANNEL_NAME_EMAIL;
import static org.ilms.util.ILMSConstants.CHANNEL_NAME_SMS;
import static org.ilms.util.ILMSConstants.CORRECTION_PENDING;
import static org.ilms.util.ILMSConstants.CREATED_STRING;
import static org.ilms.util.ILMSConstants.CREATE_STRING;
import static org.ilms.util.ILMSConstants.NOTIFICATION_APPID;
import static org.ilms.util.ILMSConstants.NOTIFICATION_CASEID;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.egov.common.contract.request.RequestInfo;
import org.ilms.configs.ILMSConfiguration;
import org.ilms.util.NotificationUtil;
import org.ilms.web.model.EmailRequest;
import org.ilms.web.model.Event;
import org.ilms.web.model.EventRequest;
import org.ilms.web.model.ILMSCase;
import org.ilms.web.model.ILMSCaseRequest;
import org.ilms.web.model.SMSRequest;
import org.ilms.web.model.enums.CreationReason;
import org.ilms.web.model.enums.Status;
import org.ilms.web.model.workflow.Action;
import org.ilms.web.model.workflow.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class NotificationService {
    @Autowired
    NotificationUtil notificationUtil;

    @Autowired
    ILMSConfiguration ilmsConfiguration;

    @Value ("${notification.url}")
    private String notificationURL;

    public void sendNotificationForUpdate(ILMSCaseRequest ilmsCaseRequest) {

        ILMSCase ilmsCase = ilmsCaseRequest.getIlmsCase();
               ProcessInstance wf = ilmsCase.getWorkflow();
        String createOrUpdate = null;
        String msg = null;

        Boolean isCreate = CreationReason.CREATE.equals(ilmsCase.getCreationReason());
        String state = getStateFromWf(wf, ilmsConfiguration.getIsWorkflowEnabled());
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
                String moduleName = request.getIlmsCase().getWorkflow().getModuleName();

                String action;
                if(ilmsCase.getWorkflow()!=null)
                    action = ilmsCase.getWorkflow().getAction();
                else
                    action = WF_NO_WORKFLOW;

        List<String> configuredChannelNames = notificationUtil.fetchChannelList(new RequestInfo(), tenantId, moduleName, action);
        Set<String> mobileNumbers = new HashSet<>();


                request.getIlmsCase().getWorkflow().getAssignes().forEach(assigne -> {
                    if (assigne.getMobileNumber() != null)
                        mobileNumberToOwner.put(assigne.getMobileNumber(), assigne.getName());
                    mobileNumbers.add(assigne.getMobileNumber());
                });

        List<SMSRequest> smsRequests = notificationUtil.createSMSRequest(msg, mobileNumberToOwner);

        if (configuredChannelNames.contains(CHANNEL_NAME_SMS)) {
            notificationUtil.sendSMS(smsRequests);

            Boolean isActionReq = false;
            if (state.equalsIgnoreCase(CORRECTION_PENDING)) {
                isActionReq = true;
            }


            List<Event> events = notificationUtil.enrichEvent(smsRequests, requestInfo, ilmsCase.getTenantId(), ilmsCase, isActionReq);
            notificationUtil.sendEventNotification(new EventRequest(requestInfo, events));
        }
        if(configuredChannelNames.contains(CHANNEL_NAME_EMAIL)){
            List<EmailRequest> emailRequests = notificationUtil.createEmailRequestFromSMSRequests(requestInfo,smsRequests, tenantId);
            notificationUtil.sendEmail(emailRequests);
        }
    }


    private String getLocalisedState(ProcessInstance workflow, String completeMsgs) {

        String state = "";
                if (ilmsConfiguration.getIsWorkflowEnabled()) {
                    state = workflow.getState().getState();
                }

        switch (state) {

            case WF_STATUS_REJECTED:
                return notificationUtil.getMessageTemplate(WF_STATUS_REJECTED_LOCALE, completeMsgs);

            case WF_STATUS_DOCVERIFIED:
                return notificationUtil.getMessageTemplate(WF_STATUS_DOCVERIFIED_LOCALE, completeMsgs);

            case WF_STATUS_FIELDVERIFIED:
                return notificationUtil.getMessageTemplate(WF_STATUS_FIELDVERIFIED_LOCALE, completeMsgs);

            case WF_STATUS_OPEN:
                return notificationUtil.getMessageTemplate(WF_STATUS_OPEN_LOCALE, completeMsgs);
        }
        return state;
    }

    private String getMsgForUpdate(ILMSCase ilmsCase, String msgCode, String completeMsgs, String createUpdateReplaceString) {

        String url = notificationUtil.getShortenedUrl(ilmsConfiguration.getUiAppHost().concat(ilmsConfiguration.getViewCaseLink()
                                                                                                               .replace(NOTIFICATION_CASEID,
                                                                                                                       ilmsCase.getId())
                                                                                                               .replace(NOTIFICATION_TENANTID,
                                                                                                                       ilmsCase.getTenantId())));

        return notificationUtil.getMessageTemplate(msgCode, completeMsgs).replace(NOTIFICATION_PROPERTY_LINK, url)
                               .replace(NOTIFICATION_UPDATED_CREATED_REPLACE, createUpdateReplaceString);
    }

    private String replaceCommonValues(ILMSCase ilmsCase, String msg, String localisedState) {

        msg = msg.replace(NOTIFICATION_CASEID, ilmsCase.getId()).replace(NOTIFICATION_APPID, ilmsCase.getCaseNumber());

        if (ilmsConfiguration.getIsWorkflowEnabled()) {
            msg = msg.replace(NOTIFICATION_STATUS, localisedState);
        }
        return msg;
    }
    private String getStateFromWf(ProcessInstance wf, Boolean isWorkflowEnabled) {

        String state;
        if (isWorkflowEnabled) {

            Boolean isPropertyActive = wf.getState().getApplicationStatus().equalsIgnoreCase(Status.ACTIVE.toString());
            Boolean isTerminateState = wf.getState().getIsTerminateState();
            Set<String> actions = null != wf.getState().getActions()
                    ? actions = wf.getState().getActions().stream().map(Action::getAction).collect(Collectors.toSet())
                    : Collections.emptySet();

            if (isTerminateState && CollectionUtils.isEmpty(actions)) {

                state = isPropertyActive ? WF_STATUS_APPROVED : WF_STATUS_REJECTED;
            } else {

                state = wf.getState().getState();
            }

        } else {
            state = WF_NO_WORKFLOW;
        }
        return state;
    }
}
