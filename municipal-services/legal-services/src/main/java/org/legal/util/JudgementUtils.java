package org.legal.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.egov.common.contract.request.User;
import org.legal.configs.LEGALConfiguration;
import org.legal.repository.HearingRepository;
import org.legal.repository.JudgementRepository;
import org.legal.service.CaseEnrichmentService;
import org.legal.service.JudgementEnrichmentService;
import org.legal.web.model.Hearing;
import org.legal.web.model.HearingRequest;
import org.legal.web.model.HearingResponse;
import org.legal.web.model.HearingSearchCriteria;
import org.legal.web.model.Judgement;
import org.legal.web.model.JudgementRequest;
import org.legal.web.model.JudgementResponse;
import org.legal.web.model.JudgementSearchCriteria;
import org.legal.web.model.Party;
import org.legal.web.model.enums.CreationReason;
import org.legal.web.model.enums.PartyType;
import org.legal.web.model.workflow.ProcessInstance;
import org.legal.web.model.workflow.ProcessInstanceRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class JudgementUtils {
    @Autowired
    private JudgementEnrichmentService judgementEnrichmentService;

    @Autowired
    private CommonUtils commonUtils;

    @Autowired
    private JudgementRepository judgementRepository;

    @Autowired
    private LEGALConfiguration configuration;

    public ProcessInstanceRequest getWfForJudgementCreate(JudgementRequest request, CreationReason creationReasonForWorkflow) {

        Judgement judgement = request.getJudgement();
        ProcessInstance wf = null != judgement.getWorkflow() ? judgement.getWorkflow() : new ProcessInstance();
        wf.setBusinessId(judgement.getId());

        switch (creationReasonForWorkflow) {
            case CREATE:
                wf.setBusinessService(configuration.getCreatePTWfName());
                wf.setModuleName(configuration.getPropertyModuleName());

                wf.setAction("CREATE_JUDGEMENT");
                wf.setTenantId(request.getJudgement().getTenantId());
                List<User> userList = new ArrayList<>();
                User user = new User();
                user.setUuid(request.getRequestInfo().getUserInfo().getUuid());
                userList.add(user);
                wf.setAssignes(userList);

                break;

            case UPDATE:
                String judgementId = request.getJudgement().getId();
                JudgementSearchCriteria criteria = JudgementSearchCriteria.builder().id(Collections.singletonList(judgementId)).build();
                JudgementResponse judgementResponse = judgementRepository.getJudgementData(criteria);
                String tenantId = judgementResponse.getJudgementList().get(0).getTenantId();
                wf.setTenantId(tenantId);
                wf.setAssignes(request.getJudgement().getWorkflow().getAssignes());
                break;

            default:
                break;
        }
        judgement.setWorkflow(wf);
        return ProcessInstanceRequest.builder().processInstances(Collections.singletonList(wf)).requestInfo(request.getRequestInfo()).build();
    }
}
