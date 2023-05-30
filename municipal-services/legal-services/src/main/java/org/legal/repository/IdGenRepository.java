package org.legal.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.egov.tracer.model.ServiceCallException;
import org.legal.configs.LEGALConfiguration;
import org.legal.web.model.idGen.IdGenerationRequest;
import org.legal.web.model.idGen.IdGenerationResponse;
import org.legal.web.model.idGen.IdRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Repository
public class IdGenRepository {
    private final RestTemplate restTemplate;

    private final LEGALConfiguration legalConfiguration;

    @Autowired
    public IdGenRepository(RestTemplate restTemplate, LEGALConfiguration legalConfiguration) {
        this.restTemplate = restTemplate;
        this.legalConfiguration = legalConfiguration;
    }

    public IdGenerationResponse getId(RequestInfo requestInfo, String tenantId, String name, String format, int count) {

        List<IdRequest> reqList = new ArrayList<>();
        reqList.add(IdRequest.builder().idName(name).format(format).tenantId(tenantId).build());
        IdGenerationRequest req = IdGenerationRequest.builder().idRequests(reqList).requestInfo(requestInfo).build();
        IdGenerationResponse response = null;
        try {
            response = restTemplate.postForObject(legalConfiguration.getIdGenHost() + legalConfiguration.getIdGenPath(), req,
                    IdGenerationResponse.class);
        } catch (HttpClientErrorException e) {
            throw new ServiceCallException(e.getResponseBodyAsString());
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> map = new HashMap<>();
            map.put(e.getClass().getName(), e.getMessage());
            throw new CustomException(map);
        }
        return response;
    }
}
