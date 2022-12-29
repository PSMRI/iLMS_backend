package org.ilms.service;

import java.util.Optional;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.ilms.configs.ILMSConfiguration;
import org.ilms.repository.ServiceRepository;
import org.ilms.util.CaseUtils;
import org.ilms.util.CommonUtils;
import org.ilms.web.model.Case;
import org.ilms.web.model.CaseRequest;
import org.ilms.web.model.RequestInfoWrapper;
import org.ilms.web.model.enums.CreationReason;
import org.ilms.web.model.workflow.BusinessService;
import org.ilms.web.model.workflow.BusinessServiceResponse;
import org.ilms.web.model.workflow.ProcessInstanceRequest;
import org.ilms.web.model.workflow.ProcessInstanceResponse;
import org.ilms.web.model.workflow.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class WorkflowService {
    @Autowired
    private ILMSConfiguration ilmsConfiguration;

    @Autowired
    private ServiceRepository restRepo;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private CommonUtils commonUtils;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private CaseUtils caseUtils;

    /**
     * Method to integrate with workflow
     * <p>
     * takes the trade-license request as parameter constructs the work-flow request
     * <p>
     * and sets the resultant status from wf-response back to trade-license object
     */
    public State callWorkFlow(ProcessInstanceRequest workflowReq) {

        ProcessInstanceResponse response = null;
        StringBuilder url = new StringBuilder(ilmsConfiguration.getWfHost().concat(ilmsConfiguration.getWfTransitionPath()));
        Optional<Object> optional = serviceRepository.fetchResult(url, workflowReq);
        response = mapper.convertValue(optional.get(), ProcessInstanceResponse.class);
        return response.getProcessInstances().get(0).getState();
    }

    /**
     * Get the workflow config for the given tenant
     *
     * @param tenantId The tenantId for which businessService is requested
     * @param requestInfo The RequestInfo object of the request
     * @return BusinessService for the the given tenantId
     */
    public BusinessService getBusinessService(String tenantId, String businessService, RequestInfo requestInfo) {

        StringBuilder url = getSearchURLWithParams(tenantId, businessService);
        RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();
        Optional<Object> result = restRepo.fetchResult(url, requestInfoWrapper);
        BusinessServiceResponse response = null;
        try {
            response = mapper.convertValue(result.get(), BusinessServiceResponse.class);
        } catch (IllegalArgumentException e) {
            throw new CustomException("PARSING ERROR", "Failed to parse response of workflow business service search");
        }

        if (CollectionUtils.isEmpty(response.getBusinessServices())) {
            throw new CustomException("BUSINESSSERVICE_NOT_FOUND", "The businessService " + businessService + " is not found");
        }

        return response.getBusinessServices().get(0);
    }

    /**
     * Creates url for search based on given tenantId
     *
     * @param tenantId The tenantId for which url is generated
     * @return The search url
     */
    private StringBuilder getSearchURLWithParams(String tenantId, String businessService) {

        StringBuilder url = new StringBuilder(ilmsConfiguration.getWfHost());
        url.append(ilmsConfiguration.getWfBusinessServiceSearchPath());
        url.append("?tenantId=");
        url.append(tenantId);
        url.append("&businessServices=");
        url.append(businessService);
        return url;
    }

    /**
     * method to prepare process instance request
     * and assign status back to property
     */
    public State updateWorkflow(CaseRequest request, CreationReason creationReasonForWorkflow) {

        Case cases = request.getCaseObj();

        ProcessInstanceRequest workflowReq = caseUtils.getWfForCaseCreate(request, creationReasonForWorkflow);
        State state = callWorkFlow(workflowReq);

        if (state.getApplicationStatus().equalsIgnoreCase(ilmsConfiguration.getWfStatusActive()) && cases.getId() == null) {

            String pId = commonUtils.getIdList(request.getRequestInfo(), cases.getTenantId(), ilmsConfiguration.getCaseIdgenName(),
                    ilmsConfiguration.getCaseIdgenFormat(), 1).get(0);
            request.getCaseObj().setId(pId);
        }

        request.getCaseObj().getWorkflow().setState(state);
        return state;
    }

    /**
     * Returns boolean value to specifying if the state is updatable
     *
     * @param stateCode The stateCode of the license
     * @param businessService The BusinessService of the application flow
     * @return State object to be fetched
     */
    public Boolean isStateUpdatable(String stateCode, BusinessService businessService) {
        for (State state : businessService.getStates()) {
            if (state.getState() != null && state.getState().equalsIgnoreCase(stateCode)) {
                return state.getIsStateUpdatable();
            }
        }
        return null;
    }

    /**
     * Creates url for searching processInstance
     *
     * @return The search url
     */
    private StringBuilder getWorkflowSearchURLWithParams(String tenantId, String businessId) {

        StringBuilder url = new StringBuilder(ilmsConfiguration.getWfHost());
        url.append(ilmsConfiguration.getWfProcessInstanceSearchPath());
        url.append("?tenantId=");
        url.append(tenantId);
        url.append("&businessIds=");
        url.append(businessId);
        return url;
    }

    /**
     * Fetches the workflow object for the given assessment
     */
    public ProcessInstanceResponse getWorkflow(RequestInfo requestInfo, String tenantId, String businessId) {

        RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();

        StringBuilder url = getWorkflowSearchURLWithParams(tenantId, businessId);

        Optional<Object> res = restRepo.fetchResult(url, requestInfoWrapper);
        ProcessInstanceResponse response = null;

        try {
            response = mapper.convertValue(res.get(), ProcessInstanceResponse.class);
        } catch (Exception e) {
            throw new CustomException("PARSING_ERROR", "Failed to parse workflow search response");
        }

        if (response != null && !CollectionUtils.isEmpty(response.getProcessInstances()) && response.getProcessInstances().get(0) != null) {
            return response;
        }

        return null;
    }

}