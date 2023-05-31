package org.legal.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.mdms.model.MasterDetail;
import org.egov.mdms.model.MdmsCriteria;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.mdms.model.ModuleDetail;
import org.egov.tracer.model.CustomException;
import org.legal.configs.LEGALConfiguration;
import org.legal.repository.ServiceRepository;
import org.legal.web.model.RoleDto;
import org.legal.web.model.idGen.IdGenerationRequest;
import org.legal.web.model.idGen.IdGenerationResponse;
import org.legal.web.model.idGen.IdRequest;
import org.legal.web.model.idGen.IdResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class CommonUtils {
    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private LEGALConfiguration configs;

    @Autowired
    private ServiceRepository restRepo;

    @Autowired
    private LEGALConfiguration legalConfiguration;

    public Map<String, List<String>> getAttributeValues(String tenantId, String moduleName, List<String> names, String filter, String jsonpath,
                                                        RequestInfo requestInfo) {

        StringBuilder uri = new StringBuilder(configs.getMdmsHost()).append(configs.getMdmsEndpoint());
        MdmsCriteriaReq criteriaReq = prepareMdMsRequest(tenantId, moduleName, names, filter, requestInfo);
        Optional<Object> response = restRepo.fetchResult(uri, criteriaReq);

        try {
            if (response.isPresent()) {
                return JsonPath.read(response.get(), jsonpath);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException(LegalErrorConstants.INVALID_TENANT_ID_MDMS_KEY, LegalErrorConstants.INVALID_TENANT_ID_MDMS_MSG);
        }

        return null;
    }

    public MdmsCriteriaReq prepareMdMsRequest(String tenantId, String moduleName, List<String> names, String filter, RequestInfo requestInfo) {

        List<MasterDetail> masterDetails = new ArrayList<>();

        names.forEach(name -> {
            masterDetails.add(MasterDetail.builder().name(name).filter(filter).build());
        });

        ModuleDetail moduleDetail = ModuleDetail.builder().moduleName(moduleName).masterDetails(masterDetails).build();
        List<ModuleDetail> moduleDetails = new ArrayList<>();
        moduleDetails.add(moduleDetail);
        MdmsCriteria mdmsCriteria = MdmsCriteria.builder().tenantId(tenantId).moduleDetails(moduleDetails).build();
        return MdmsCriteriaReq.builder().requestInfo(requestInfo).mdmsCriteria(mdmsCriteria).build();
    }
    
    public List<RoleDto> fetchUsersByUUID(List<String> uuid, String tenantId) {
        StringBuilder uri = new StringBuilder();
        uri.append(configs.getUserHost()).append(configs.getUserSearchEndPoint());
        Map<String, Object> userSearchRequest = new HashMap<>();
        userSearchRequest.put("tenantId", tenantId);
        userSearchRequest.put("uuid", uuid);
        List<RoleDto> roles = new ArrayList<>();
        try {
            Object user = restRepo.fetchUserResult(uri, userSearchRequest);
            if (user != null) {
                Object role = JsonPath.read(user, "$.user[0].roles");
                List<RoleDto> responseRoles = mapper.readValue(new Gson().toJson(role), new TypeReference<List<RoleDto>>() {
                });
                for (RoleDto roleDto : responseRoles) {
                    roles.add(roleDto);
                }
            }
        } catch (Exception e) {
            log.error("Unable to fetch User from system", e);
            throw new CustomException("PARSING_ERROR", "Unable to fetch User from system");
        }
        return roles;
    }

    public boolean isUserOIC(List<String> listUuids, String tenantId, String columnValue) {
        List<RoleDto> userRoles = fetchUsersByUUID(listUuids, tenantId);
        List<String> roleCodes = userRoles.stream().map(RoleDto::getCode).collect(Collectors.toList());
        if (!roleCodes.contains(Constants.OIC)) {
            throw new CustomException(LegalErrorConstants.INVALID_TYPE_ERROR,
                    "Unauthorised User to insert [ " + columnValue + " ]");
        }
        return true;
    }

    public boolean isUserMO(List<String> listUuids, String tenantId, String columnValue) {
        List<RoleDto> userRoles = fetchUsersByUUID(listUuids, tenantId);
        List<String> roleCodes = userRoles.stream().map(RoleDto::getCode).collect(Collectors.toList());
        if (!roleCodes.contains(Constants.MO)) {
            throw new CustomException(LegalErrorConstants.INVALID_TYPE_ERROR,
                    "Unauthorised User to insert [ " + columnValue + " ]");
        }
        return true;
    }

    public StringBuilder getProcessInstanceSearchURL(String tenantId, String id) {
        StringBuilder url = new StringBuilder(legalConfiguration.getWfHost());
        url.append(legalConfiguration.getWfProcessInstanceSearchPath());
        url.append("?tenantId=");
        url.append(tenantId);
        url.append("&businessIds=");
        url.append(id);
        return url;
    }
}
