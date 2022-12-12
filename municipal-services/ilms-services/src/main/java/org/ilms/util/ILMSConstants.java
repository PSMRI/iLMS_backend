package org.ilms.util;

import org.springframework.stereotype.Component;

@Component
public class ILMSConstants {
    public static final String Respondent = "Respondent";

    public static final String MDMS_ILMS_MOD_NAME = "common-masters";

    public static final String JSONPATH_CODES = "$.MdmsRes.common-masters";

    public static final String MDMS_ILMS_CASE_TYPE = "CaseType";

    public static final String MDMS_ILMS_CASE_STATUS = "CaseStatus";

    public static final String MDMS_ILMS_CASE_CATEGORY = "CaseCategory";

    public static final String MDMS_ILMS_CASE_STAGE = "CaseStage";

    public static final String MDMS_ILMS_SUB_STAGE = "SubStage";

    public static final String MDMS_ILMS_GENDER_TYPE = "GenderType";

    public static final String MDMS_ILMS_PETITIONER_TYPE = "PetitionerType";

    public static final String MDMS_ILMS_DEPARTMENT_NAME = "Department";

    public static final String MDMS_ILMS_DEPARTMENT_IOC = "DepartmentIOC";

    public static final String MDMS_ILMS_STATUS_OF_COMPLIANCE = "StageCompliance";

    public static final String MDMS_ILMS_ORDER_TYPE = "OrderType";

    public static final String MDMS_ILMS_DOCUMENT_CATEGORY = "DocumentsCategory";

    public static final String MDMS_ILMS_DISTRICT = "District";

    public static final String MDMS_ILMS_COURT_NAME = "CourtName";

    public static final String MDMS_ILMS_STATE = "State";

    public static final String MDMS_ILMS_BENCH = "Bench";

    public static final String MDMS_ILMS_DIVISION = "Division";

    //Notification Enhancement
    public static final String CHANNEL_NAME_SMS = "SMS";

    public static final String CHANNEL_NAME_EVENT = "EVENT";

    public static final String CHANNEL_NAME_EMAIL = "EMAIL";

    public static final String MODULE = "module";

    public static final String ACTION = "action";

    public static final String CHANNEL_LIST = "channelList";

    public static final String CHANNEL = "Channel";

    public static final String LOCALIZATION_CODES_JSONPATH = "$.messages.*.code";

    public static final String LOCALIZATION_MSGS_JSONPATH = "$.messages.*.message";

    //  NOTIFICATION PLACEHOLDER

    public static final String NOTIFICATION_OWNERNAME = "{RO_NAME}";

    public static final String NOTIFICATION_EMAIL = "{EMAIL_ID}";

    public static final String NOTIFICATION_STATUS = "{STATUS}";

    public static final String NOTIFICATION_UPDATED_CREATED_REPLACE = "{updated/created}";

    public static final String CREATE_STRING = "Create";

    public static final String UPDATE_STRING = "Update";

    public static final String CREATED_STRING = "Created";

    public static final String UPDATED_STRING = "Updated";

    public static final String PT_BUSINESSSERVICE = "PT";

    public static final String MUTATION_BUSINESSSERVICE = "PT.MUTATION";

    public static final String NOTIFICATION_PROPERTYID = "{PROPERTYID}";

    public static final String PT_OWNER_NAME = "{ownername}";

    public static final String PT_ALTERNATE_NUMBER = "{alternatenumber}";

    public static final String PT_OLD_MOBILENUMBER = "{oldmobilenumber}";

    public static final String PT_NEW_MOBILENUMBER = "{newmobilenumber}";

    public static final String CORRECTION_PENDING = "CORRECTIONPENDING";

    public static final String NOTIFICATION_LOCALE = "en_IN";

    public static final String NOTIFICATION_MODULENAME = "rainmaker-pt";

    public static final String WF_STATUS_REJECTED = "REJECTED";

    public static final String WF_STATUS_FIELDVERIFIED = "FIELDVERIFIED";

    public static final String WF_STATUS_DOCVERIFIED = "DOCVERIFIED";

    public static final String WF_STATUS_CANCELLED = "CANCELLED";

    public static final String WF_STATUS_APPROVED = "APPROVED";

    public static final String WF_STATUS_OPEN = "OPEN";

    public static final String WF_NO_WORKFLOW = "NO_WORKFLOW";

    public static final String WF_STATUS_OPEN_LOCALE = "PT_NOTIF_WF_STATE_LOCALE_OPEN";

    public static final String WF_STATUS_DOCVERIFIED_LOCALE = "PT_NOTIF_WF_STATE_LOCALE_DOCVERIFIED";

    public static final String WF_STATUS_FIELDVERIFIED_LOCALE = "PT_NOTIF_WF_STATE_LOCALE_FIELDVERIFIED";

    public static final String WF_STATUS_APPROVED_LOCALE = "PT_NOTIF_WF_STATE_LOCALE_APPROVED";

    public static final String WF_STATUS_REJECTED_LOCALE = "PT_NOTIF_WF_STATE_LOCALE_REJECTED";

    //    public static final String CREATE_STRING = "Create";
    //
    //    public static final String UPDATE_STRING = "Update";
    //
    //    public static final String CREATED_STRING = "Created";
    //
    //    public static final String UPDATED_STRING = "Updated";

    /* update */
    public static final String WF_UPDATE_STATUS_OPEN_CODE = "PT_NOTIF_WF_OPEN";

    public static final String WF_UPDATE_STATUS_CHANGE_CODE = "PT_NOTIF_WF_STATUS_CHANGE";

    public static final String WF_UPDATE_STATUS_APPROVED_CODE = "PT_NOTIF_WF_APPROVED";

    public static final String UPDATE_NO_WORKFLOW = "PT_NOTIF_WF_UPDATE_NONE";

    public static final String NOTIFICATION_TENANTID = "{TENANTID}";

    public static final String NOTIFICATION_PROPERTY_LINK = "{PTURL}";

    public static final String NOTIFICATION_APPID = "{APPID}";

}
