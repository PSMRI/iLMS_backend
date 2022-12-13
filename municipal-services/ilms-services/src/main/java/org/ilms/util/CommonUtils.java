package org.ilms.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.common.contract.request.RequestInfo;
import org.egov.mdms.model.MasterDetail;
import org.egov.mdms.model.MdmsCriteria;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.mdms.model.ModuleDetail;
import org.egov.tracer.model.CustomException;
import org.ilms.configs.ILMSConfiguration;
import org.ilms.repository.ServiceRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.jayway.jsonpath.JsonPath;
import org.ilms.web.model.idGen.IdGenerationRequest;
import org.ilms.web.model.idGen.IdGenerationResponse;
import org.ilms.web.model.idGen.IdRequest;
import org.ilms.web.model.idGen.IdResponse;
import org.springframework.util.CollectionUtils;

@Component
public class CommonUtils {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private ILMSConfiguration configs;

    @Autowired
    private ServiceRequestRepository restRepo;

    public boolean isCorrectDate(long milliseconds) {
        final LocalDate todayDate = LocalDate.now();
        final Instant instant = Instant.ofEpochMilli(milliseconds);
        LocalDate inputDate = null;
        if (String.valueOf(milliseconds).length() == 13) {
            inputDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();
            if (!inputDate.isBefore(todayDate)) {
                throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "Date should be before then today's date [ " + todayDate + " ]");
            }
        } else {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "Date should be in proper timestamp format [ " + milliseconds + " ] ");
        }
        return true;
    }
    public Map<String, List<String>> getAttributeValues(String tenantId, String moduleName, List<String> names, String filter,String jsonpath, RequestInfo requestInfo){

        StringBuilder uri = new StringBuilder(configs.getMdmsHost()).append(configs.getMdmsEndpoint());
        MdmsCriteriaReq criteriaReq = prepareMdMsRequest(tenantId,moduleName,names,filter,requestInfo);
        Optional<Object> response = restRepo.fetchResult(uri, criteriaReq);

        try {
            if(response.isPresent()) {
                return JsonPath.read(response.get(),jsonpath);
            }
        } catch (Exception e) {
            throw new CustomException(ILMSErrorConstants.INVALID_TENANT_ID_MDMS_KEY,
                    ILMSErrorConstants.INVALID_TENANT_ID_MDMS_MSG);
        }

        return null;
    }

    public MdmsCriteriaReq prepareMdMsRequest(String tenantId,String moduleName, List<String> names, String filter, RequestInfo requestInfo) {

        List<MasterDetail> masterDetails = new ArrayList<>();

        names.forEach(name -> {
            masterDetails.add(MasterDetail.builder().name(name).filter(filter).build());
        });

        ModuleDetail moduleDetail = ModuleDetail.builder()
                                                .moduleName(moduleName).masterDetails(masterDetails).build();
        List<ModuleDetail> moduleDetails = new ArrayList<>();
        moduleDetails.add(moduleDetail);
        MdmsCriteria mdmsCriteria = MdmsCriteria.builder().tenantId(tenantId).moduleDetails(moduleDetails).build();
        return MdmsCriteriaReq.builder().requestInfo(requestInfo).mdmsCriteria(mdmsCriteria).build();
    }
    public List<String> getIdList(RequestInfo requestInfo, String tenantId, String idName, String idformat, int count) {

        List<IdRequest> reqList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            reqList.add(IdRequest.builder().idName(idName).format(idformat).tenantId(tenantId).build());
        }

        IdGenerationRequest request = IdGenerationRequest.builder().idRequests(reqList).requestInfo(requestInfo).build();
        StringBuilder uri = new StringBuilder(configs.getIdGenHost()).append(configs.getIdGenPath());
        IdGenerationResponse response = mapper.convertValue(restRepo.fetchResult(uri, request).get(), IdGenerationResponse.class);

        List<IdResponse> idResponses = response.getIdResponses();

        if (CollectionUtils.isEmpty(idResponses))
            throw new CustomException("IDGEN ERROR", "No ids returned from idgen Service");

        return idResponses.stream().map(IdResponse::getId).collect(Collectors.toList());
    }

}
