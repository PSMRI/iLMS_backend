package org.legal.service;

import java.util.*;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.common.contract.request.User;
import org.egov.tracer.model.CustomException;
import org.legal.configs.LEGALConfiguration;
import org.legal.repository.ServiceRepository;
import org.legal.util.CaseUtils;
import org.legal.util.CommonUtils;
import org.legal.util.HearingUtils;
import org.legal.web.model.*;

import org.legal.web.model.workflow.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

@Service
public class WorkflowService {
    @Autowired
    private LEGALConfiguration legalConfiguration;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private CommonUtils commonUtils;

    @Autowired
    private HearingUtils hearingUtils;

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
        StringBuilder url = new StringBuilder(legalConfiguration.getWfHost().concat(legalConfiguration.getWfTransitionPath()));
        Optional<Object> optional = serviceRepository.fetchResult(url, workflowReq);
        response = mapper.convertValue(optional.get(), ProcessInstanceResponse.class);
        return response.getProcessInstances().get(0).getState();
    }

    /**
     * Get the workflow config for the given tenant
     *
     * @param tenantId    The tenantId for which businessService is requested
     * @param requestInfo The RequestInfo object of the request
     * @return BusinessService for the the given tenantId
     */
    public BusinessService getBusinessService(String tenantId, String businessService, RequestInfo requestInfo) {

        StringBuilder url = getSearchURLWithParams(tenantId, businessService);
        RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();
        Optional<Object> result = serviceRepository.fetchResult(url, requestInfoWrapper);
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

        StringBuilder url = new StringBuilder(legalConfiguration.getWfHost());
        url.append(legalConfiguration.getWfBusinessServiceSearchPath());
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

//    CASE
    public String updateCaseWorkflowStatus(CaseRequest caseRequest) {
        ProcessInstance processInstance = getProcessInstanceForCase(caseRequest);
        ProcessInstanceRequest workflowRequest = new ProcessInstanceRequest(caseRequest.getRequestInfo(), Collections.singletonList(processInstance));
        State state = callWorkFlow(workflowRequest);
        caseRequest.getCaseObj().setApplicationStatus(state.getApplicationStatus());
        return state.getApplicationStatus();
    }

    public String updateCaseWorkflow(CaseRequest caseRequest, String action) {
        ProcessInstance processInstance = caseUtils.changeCaseWF(caseRequest, action);
        ProcessInstanceRequest workflowRequest = new ProcessInstanceRequest(caseRequest.getRequestInfo(), Collections.singletonList(processInstance));
        State state = callWorkFlow(workflowRequest);
        caseRequest.getCaseObj().setApplicationStatus(state.getApplicationStatus());
        return state.getApplicationStatus();
    }


    private ProcessInstance getProcessInstanceForCase(CaseRequest request) {

        Case caseObj = request.getCaseObj();
        Workflow workflow = request.getWorkflow();
        ProcessInstance processInstance = new ProcessInstance();
        processInstance.setBusinessId(caseObj.getId());
        processInstance.setAction(request.getWorkflow().getAction());
        processInstance.setModuleName(legalConfiguration.getModuleName());
        processInstance.setTenantId(caseObj.getTenantId());
        processInstance.setBusinessService(legalConfiguration.getCreateCaseWfName());
        processInstance.setComment(workflow.getComments());

        if (!CollectionUtils.isEmpty(workflow.getAssignes())) {
            List<User> users = new ArrayList<>();

            workflow.getAssignes().forEach(uuid -> {
                User user = new User();
                user.setUuid(uuid);
                users.add(user);
            });
            processInstance.setAssignes(users);
        }
        return processInstance;
    }

    //    HEARING

    public String updateHearingWorkflowStatus(HearingRequest hearingRequest) {
        ProcessInstance processInstance = getProcessInstanceForHearing(hearingRequest);
        ProcessInstanceRequest workflowRequest = new ProcessInstanceRequest(hearingRequest.getRequestInfo(), Collections.singletonList(processInstance));
        State state = callWorkFlow(workflowRequest);
        hearingRequest.getHearing().setApplicationStatus(state.getApplicationStatus());
        return state.getApplicationStatus();
    }

    public String updateHearingWorkflow(HearingRequest hearingRequest, String action) {
        ProcessInstance processInstance = hearingUtils.hearingWFUpdate(hearingRequest, action);
        ProcessInstanceRequest workflowRequest = new ProcessInstanceRequest(hearingRequest.getRequestInfo(), Collections.singletonList(processInstance));
        State state = callWorkFlow(workflowRequest);
        hearingRequest.getHearing().setApplicationStatus(state.getApplicationStatus());
        return state.getApplicationStatus();
    }


    private ProcessInstance getProcessInstanceForHearing(HearingRequest request) {

        Hearing hearing = request.getHearing();
        Workflow workflow = request.getWorkflow();

        ProcessInstance processInstance = new ProcessInstance();
        processInstance.setBusinessId(hearing.getId());
        processInstance.setAction(request.getWorkflow().getAction());
        processInstance.setModuleName(legalConfiguration.getModuleName());
        processInstance.setTenantId(hearing.getTenantId());
        processInstance.setBusinessService(legalConfiguration.getCreateHearingWfName());
        processInstance.setComment(workflow.getComments());
        if (!CollectionUtils.isEmpty(workflow.getAssignes())) {
            List<User> users = new ArrayList<>();
            workflow.getAssignes().forEach(uuid -> {
                User user = new User();
                user.setUuid(uuid);
                users.add(user);
            });
            processInstance.setAssignes(users);
        }
        return processInstance;
    }

    //    JUDGEMENT

    public String updateJudgementWorkflowStatus(JudgementRequest judgementRequest) {
        ProcessInstance processInstance = getProcessInstanceForJudgement(judgementRequest);
        ProcessInstanceRequest workflowRequest = new ProcessInstanceRequest(judgementRequest.getRequestInfo(), Collections.singletonList(processInstance));
        State state = callWorkFlow(workflowRequest);
        judgementRequest.getJudgement().setApplicationStatus(state.getApplicationStatus());
        return state.getApplicationStatus();
    }


    private ProcessInstance getProcessInstanceForJudgement(JudgementRequest request) {

        Judgement judgement = request.getJudgement();
        Workflow workflow = request.getWorkflow();

        ProcessInstance processInstance = new ProcessInstance();
        processInstance.setBusinessId(judgement.getId());
        processInstance.setAction(request.getWorkflow().getAction());
        processInstance.setModuleName(legalConfiguration.getModuleName());
        processInstance.setTenantId(judgement.getTenantId());
        processInstance.setBusinessService(legalConfiguration.getCreateJudgementWfName());
        processInstance.setComment(workflow.getComments());

        if (!CollectionUtils.isEmpty(workflow.getAssignes())) {
            List<User> users = new ArrayList<>();

            workflow.getAssignes().forEach(uuid -> {
                User user = new User();
                user.setUuid(uuid);
                users.add(user);
            });
            processInstance.setAssignes(users);
        }
        return processInstance;
    }

    /**
     * Returns boolean value to specifying if the state is updatable
     *
     * @param stateCode       The stateCode of the license
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

        StringBuilder url = new StringBuilder(legalConfiguration.getWfHost());
        url.append(legalConfiguration.getWfProcessInstanceSearchPath());
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

        Optional<Object> res = serviceRepository.fetchResult(url, requestInfoWrapper);
        ProcessInstanceResponse response = null;

        try {
            response = mapper.convertValue(res.get(), ProcessInstanceResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("PARSING_ERROR", "Failed to parse workflow search response");
        }

        if (response != null && !CollectionUtils.isEmpty(response.getProcessInstances()) && response.getProcessInstances().get(0) != null) {
            return response;
        }

        return null;
    }

    public List<HashMap<String, Object>> getProcessStatusCount(RequestInfo requestInfo,
                                                               ProcessInstanceSearchCriteria criteria) {
        List<String> listOfBusinessServices = new ArrayList<>(criteria.getBusinessService());
        List<HashMap<String, Object>> finalResponse = null;
        for (String businessSrv : listOfBusinessServices) {
            criteria.setBusinessService(Collections.singletonList(businessSrv));
            StringBuilder url = new StringBuilder(legalConfiguration.getWfHost());
            url.append(legalConfiguration.getProcessStatusCountPath());
            criteria.setIsProcessCountCall(true);
            // For BPA having large request, so that it was sending from the body
            List<String> roles = requestInfo.getUserInfo().getRoles().stream().map(Role::getCode).collect(Collectors.toList());
            if (!ObjectUtils.isEmpty(criteria.getModuleName()))
                url = this.buildWorkflowUrl(criteria, url, Boolean.FALSE);
            RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();
            if (finalResponse == null) {
                finalResponse = (List<HashMap<String, Object>>) serviceRepository.fetchListResult(url,
                        requestInfoWrapper);
            }
        }
        criteria.setBusinessService(listOfBusinessServices);
        return finalResponse;
    }

    private StringBuilder buildWorkflowUrl(ProcessInstanceSearchCriteria criteria, StringBuilder url, boolean noStatus) {
        url.append("?tenantId=").append(criteria.getTenantId());
        if (!CollectionUtils.isEmpty(criteria.getStatus()) && noStatus == Boolean.FALSE) {
            url.append("&status=").append(StringUtils.arrayToDelimitedString(criteria.getStatus().toArray(), ","));
        }

        if (!CollectionUtils.isEmpty(criteria.getBusinessIds())) {
            url.append("&businessIds=").append(StringUtils.arrayToDelimitedString(criteria.getBusinessIds().toArray(), ","));
        }

        if (!CollectionUtils.isEmpty(criteria.getIds())) {
            url.append("&ids=").append(StringUtils.arrayToDelimitedString(criteria.getIds().toArray(), ","));
        }
        if (!StringUtils.isEmpty(criteria.getAssignee())) {
            url.append("&assignee=").append(criteria.getAssignee());
        }
        if (criteria.getHistory() != null) {
            url.append("&history=").append(criteria.getHistory());
        }
        if (criteria.getFromDate() != null) {
            url.append("&fromDate=").append(criteria.getFromDate());
        }
        if (criteria.getToDate() != null) {
            url.append("&toDate=").append(criteria.getToDate());
        }

        if (!StringUtils.isEmpty(criteria.getModuleName())) {
            url.append("&moduleName=").append(criteria.getModuleName());
        }
        if (criteria.getIsProcessCountCall() || ObjectUtils.isEmpty(criteria.getModuleName()) && !StringUtils.isEmpty(criteria.getBusinessService())) {
            url.append("&businessService=").append(StringUtils.arrayToDelimitedString(criteria.getBusinessService().toArray(), ","));
        }
        if (!StringUtils.isEmpty(criteria.getLimit())) {
            url.append("&limit=").append(criteria.getLimit());
        }
        if (!StringUtils.isEmpty(criteria.getOffset())) {
            url.append("&offset=").append(criteria.getOffset());
        }

        return url;
    }

}