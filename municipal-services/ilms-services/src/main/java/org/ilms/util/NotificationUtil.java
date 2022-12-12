package org.ilms.util;

import static com.jayway.jsonpath.Criteria.where;
import static com.jayway.jsonpath.Filter.filter;
import static org.ilms.util.ILMSConstants.ACTION;
import static org.ilms.util.ILMSConstants.CHANNEL;
import static org.ilms.util.ILMSConstants.CHANNEL_LIST;
import static org.ilms.util.ILMSConstants.MODULE;
import static org.ilms.util.ILMSConstants.NOTIFICATION_LOCALE;
import static org.ilms.util.ILMSConstants.NOTIFICATION_MODULENAME;
import static org.ilms.util.ILMSConstants.NOTIFICATION_OWNERNAME;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.mdms.model.MasterDetail;
import org.egov.mdms.model.MdmsCriteria;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.mdms.model.ModuleDetail;
import org.egov.tracer.model.CustomException;
import org.ilms.configs.ILMSConfiguration;
import org.ilms.producer.Producer;
import org.ilms.repository.ServiceRequestRepository;
import org.ilms.web.model.SMSRequest;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import com.jayway.jsonpath.Filter;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class NotificationUtil {
    @Autowired
    ILMSConfiguration ilmsConfiguration;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    Producer producer;

    @Autowired
    ServiceRequestRepository serviceRequestRepository;

    public List<String> fetchChannelList(RequestInfo requestInfo, String tenantId, String moduleName, String action) {
        List<String> masterData = new ArrayList<>();
        StringBuilder uri = new StringBuilder();
        uri.append(ilmsConfiguration.getMdmsHost()).append(ilmsConfiguration.getMdmsEndpoint());
        if (StringUtils.isEmpty(tenantId)) {
            return masterData;
        }
        MdmsCriteriaReq mdmsCriteriaReq = getMdmsRequestForChannelList(requestInfo, tenantId.split("\\.")[0]);
        Filter masterDataFilter = filter(where(MODULE).is(moduleName).and(ACTION).is(action));

        try {
            Object response = restTemplate.postForObject(uri.toString(), mdmsCriteriaReq, Map.class);
            masterData = JsonPath.parse(response).read("$.MdmsRes.Channel.channelList[?].channelNames[*]", masterDataFilter);
        } catch (Exception e) {
            log.error("Exception while fetching workflow states to ignore: ", e);
        }

        return masterData;
    }

    private MdmsCriteriaReq getMdmsRequestForChannelList(RequestInfo requestInfo, String tenantId) {
        MasterDetail masterDetail = new MasterDetail();
        masterDetail.setName(CHANNEL_LIST);
        List<MasterDetail> masterDetailList = new ArrayList<>();
        masterDetailList.add(masterDetail);

        ModuleDetail moduleDetail = new ModuleDetail();
        moduleDetail.setMasterDetails(masterDetailList);
        moduleDetail.setModuleName(CHANNEL);
        List<ModuleDetail> moduleDetailList = new ArrayList<>();
        moduleDetailList.add(moduleDetail);

        MdmsCriteria mdmsCriteria = new MdmsCriteria();
        mdmsCriteria.setTenantId(tenantId);
        mdmsCriteria.setModuleDetails(moduleDetailList);

        MdmsCriteriaReq mdmsCriteriaReq = new MdmsCriteriaReq();
        mdmsCriteriaReq.setMdmsCriteria(mdmsCriteria);
        mdmsCriteriaReq.setRequestInfo(requestInfo);

        return mdmsCriteriaReq;
    }

    public List<SMSRequest> createSMSRequest(String message, Map<String, String> mobileNumberToOwnerName) {

        List<SMSRequest> smsRequest = new LinkedList<>();
        for (Map.Entry<String, String> entryset : mobileNumberToOwnerName.entrySet()) {
            String customizedMsg = message.replace(NOTIFICATION_OWNERNAME, entryset.getValue());
            smsRequest.add(new SMSRequest(entryset.getKey(), customizedMsg));
        }
        return smsRequest;
    }

    public void sendSMS(List<SMSRequest> smsRequestList) {
        if (ilmsConfiguration.getIsSMSNotificationEnabled()) {
            if (CollectionUtils.isEmpty(smsRequestList)) {
                log.info("Messages from localization couldn't be fetched!");
            }
            for (SMSRequest smsRequest : smsRequestList) {
                producer.push(ilmsConfiguration.getSmsNotifTopic(), smsRequest);
                log.info("Sending SMS notification: ");
                log.info("MobileNumber: " + smsRequest.getMobileNumber() + " Messages: " + smsRequest.getMessage());
            }
        }
    }

    public String getLocalizationMessages(String tenantId, RequestInfo requestInfo) {

        String locale = NOTIFICATION_LOCALE;
        Boolean isRetryNeeded = false;
        String jsonString = null;
        LinkedHashMap responseMap = null;

        if (!StringUtils.isEmpty(requestInfo.getMsgId()) && requestInfo.getMsgId().split("\\|").length >= 2) {
            locale = requestInfo.getMsgId().split("\\|")[1];
            isRetryNeeded = true;
        }

        responseMap = (LinkedHashMap) serviceRequestRepository.fetchResult(getUri(tenantId, requestInfo, locale), requestInfo).get();
        jsonString = new JSONObject(responseMap).toString();

        if (StringUtils.isEmpty(jsonString) && isRetryNeeded) {

            responseMap = (LinkedHashMap) serviceRequestRepository.fetchResult(getUri(tenantId, requestInfo, NOTIFICATION_LOCALE), requestInfo).get();
            jsonString = new JSONObject(responseMap).toString();
            if (StringUtils.isEmpty(jsonString)) {
                throw new CustomException("LOCALE_ERROR", "Localisation values not found for notifications");
            }
        }
        return jsonString;
    }

    public StringBuilder getUri(String tenantId, RequestInfo requestInfo, String locale) {

        if (ilmsConfiguration.getIsLocalizationStateLevel()) {
            tenantId = tenantId.split("\\.")[0];
        }
        StringBuilder uri = new StringBuilder();
        uri.append(ilmsConfiguration.getLocalizationHost()).append(ilmsConfiguration.getLocalizationContextPath())
           .append(ilmsConfiguration.getLocalizationSearchEndpoint()).append("?").append("locale=").append(locale).append("&tenantId=")
           .append(tenantId).append("&module=").append(NOTIFICATION_MODULENAME);

        return uri;
    }

    public String getMessageTemplate(String notificationCode, String localizationMessage) {

        String path = "$..messages[?(@.code==\"{}\")].message";
        path = path.replace("{}", notificationCode);
        String message = "";
        try {
            Object messageObj = JsonPath.parse(localizationMessage).read(path);
            message = ((ArrayList<String>) messageObj).get(0);
        } catch (Exception e) {
            log.warn("Fetching from localization failed", e);
        }
        return message;
    }

    public String getShortenedUrl(String url) {

        HashMap<String, String> body = new HashMap<>();
        body.put("url", url);
        StringBuilder builder = new StringBuilder(ilmsConfiguration.getUrlShortnerHost());
        builder.append(ilmsConfiguration.getUrlShortnerEndpoint());
        String res = restTemplate.postForObject(builder.toString(), body, String.class);

        if (StringUtils.isEmpty(res)) {
            log.error("URL_SHORTENING_ERROR", "Unable to shorten url: " + url);
            return url;
        } else {
            return res;
        }
    }
}
