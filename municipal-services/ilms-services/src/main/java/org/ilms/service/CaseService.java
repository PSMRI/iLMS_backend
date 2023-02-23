package org.ilms.service;

import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.ilms.configs.ILMSConfiguration;
import org.ilms.producer.Producer;
import org.ilms.repository.CaseRepository;
import org.ilms.repository.HearingRepository;
import org.ilms.repository.JudgementRepository;
import org.ilms.util.CaseUtils;
import org.ilms.util.ILMSErrorConstants;
import org.ilms.validator.CaseValidator;
import org.ilms.web.model.*;
import org.ilms.web.model.enums.CaseHierarchy;
import org.ilms.web.model.enums.CreationReason;
import org.ilms.web.model.enums.PartyType;
import org.ilms.web.model.enums.Status;
import org.ilms.web.model.workflow.ProcessInstanceSearchCriteria;
import org.ilms.web.model.workflow.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CaseService {
    @Autowired
    private CaseRepository caseRepository;

    @Autowired
    private HearingRepository hearingRepository;

    @Autowired
    private JudgementRepository judgementRepository;

    @Autowired
    private CaseValidator caseValidator;

    @Autowired
    private Producer producer;

    @Autowired
    private CaseEnrichmentService caseEnrichmentService;

    @Autowired
    private ILMSConfiguration ilmsConfiguration;

    @Autowired
    private CaseUtils caseUtils;

    @Autowired
    private WorkflowService workflowService;

    @Autowired
    private NotificationService notificationService;

    public CaseService() {
    }

    public CaseResponse ilmsCaseSearch(CaseSearchCriteria criteria, RequestInfo requestInfo, ProcessInstanceSearchCriteria processInstanceSearchCriteria) {
        List<Case> caseList = new ArrayList<>();
        CaseResponse caseResponse = null;
        List<HashMap<String, Object>> statusCountMap = workflowService.getProcessStatusCount(requestInfo, processInstanceSearchCriteria);
        caseResponse = caseRepository.getILMSCaseData(criteria);
        if (!caseResponse.getCaseList().isEmpty()) {
            caseResponse.getCaseList().forEach(caseObj -> {
                caseList.add(caseObj);
            });
        } else {
            throw new CustomException(ILMSErrorConstants.CASE_NOT_AVAILABLE, "Case is not Available");
        }
        CaseResponse finalResult = new CaseResponse();
        String userRole = requestInfo.getUserInfo().getRoles().get(0).getCode();

        Integer dec = null;
        Integer ro = null;
        Integer oica = null;
        Integer ao = null;
        Integer oic = null;

        OfficersCount officersCount=new OfficersCount();

        if (userRole.equals("DEC")) {
            dec = caseRepository.getCountOfUser("DEC");
            officersCount.setDEC(dec);
        } else if (userRole.equals("RO")) {
            dec = caseRepository.getCountOfUser("DEC");
            ro = caseRepository.getCountOfUser("RO");
            officersCount.setDEC(dec);
            officersCount.setRO(ro);
        } else if (userRole.equals("OICA")) {
            dec = caseRepository.getCountOfUser("DEC");
            ro = caseRepository.getCountOfUser("RO");
            oica = caseRepository.getCountOfUser("OICA");
            officersCount.setDEC(dec);
            officersCount.setRO(ro);
            officersCount.setOICA(oica);
        } else if (userRole.equals("AO")) {
            dec = caseRepository.getCountOfUser("DEC");
            ro = caseRepository.getCountOfUser("RO");
            oica = caseRepository.getCountOfUser("OICA");
            ao = caseRepository.getCountOfUser("AO");
            officersCount.setDEC(dec);
            officersCount.setRO(ro);
            officersCount.setOICA(oica);
            officersCount.setAO(ao);
        } else if (userRole.equals("OIC")||userRole.equals("MO")) {
            dec = caseRepository.getCountOfUser("DEC");
            ro = caseRepository.getCountOfUser("RO");
            oica = caseRepository.getCountOfUser("OICA");
            ao = caseRepository.getCountOfUser("AO");
            oic = caseRepository.getCountOfUser("OIC");
            officersCount.setDEC(dec);
            officersCount.setRO(ro);
            officersCount.setOICA(oica);
            officersCount.setAO(ao);
            officersCount.setOIC(oic);
        }
        finalResult.setTotalCount(caseResponse.getTotalCount());
        finalResult.setCaseList(caseList);
        finalResult.setStatusMap(statusCountMap);
        finalResult.setOfficersCount(officersCount);
        return finalResult;
    }

    public CaseDetailsResponse caseDetailsSearch(CaseSearchCriteria criteria, RequestInfo requestInfo) {
        CaseDetailsResponse downloadResponse = new CaseDetailsResponse();
        List<Case> caseList = new ArrayList<>();
        List<Hearing> hearingList = new ArrayList<>();
        List<Order> orderList = new ArrayList<>();
        CaseResponse caseResponse = null;
        HearingResponse hearingResponse = null;
        JudgementResponse judgementResponse = null;
        caseResponse = caseRepository.getILMSCaseData(criteria);
        HearingSearchCriteria hearingCriteria = HearingSearchCriteria.builder()
                .caseId(Collections.singletonList(caseResponse.getCaseList().get(0).getId()))
                .build();
        hearingResponse = hearingRepository.getHearingDetails(hearingCriteria);
        JudgementSearchCriteria judgementSearchCriteria = JudgementSearchCriteria.builder().caseId(Collections.singletonList(
                caseResponse.getCaseList().get(0).getId())).build();
        judgementResponse = judgementRepository.getJudgementData(judgementSearchCriteria);
        caseResponse.getCaseList().forEach(caseObj -> {
            if (caseObj.getStatus() == Status.ACTIVE) {
                caseList.add(caseObj);
            }
        });
        hearingResponse.getHearingList().forEach(hearing -> {
            if (hearing.getStatus() == Status.ACTIVE) {
                hearingList.add(hearing);
            }
        });
        judgementResponse.getOrderList().forEach(judgement -> {
            if (judgement.getStatus() == Status.ACTIVE) {
                orderList.add(judgement);
            }
        });
        downloadResponse.setCaseList(caseList);
        downloadResponse.setHearingList(hearingList);
        downloadResponse.setOrderList(orderList);
        downloadResponse.setTotalCount(caseResponse.getTotalCount());
        downloadResponse.setResponseInfo(caseResponse.getResponseInfo());
        return downloadResponse;
    }

    public Case create(CaseRequest caseRequest) {
        if (Objects.nonNull(caseRequest.getCaseObj().getCaseHierarchy())) {
            if (caseRequest.getCaseObj().getCaseHierarchy().equals(CaseHierarchy.INDEPENDENT)) {
                if (StringUtils.isNotBlank(caseRequest.getCaseObj().getParentCaseId())) {
                    throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                            "ParentCaseId must be null to create " + CaseHierarchy.INDEPENDENT + " Case");
                }
            } else if (caseRequest.getCaseObj().getCaseHierarchy().equals(CaseHierarchy.PARENT)) {
                if (StringUtils.isNotBlank(caseRequest.getCaseObj().getParentCaseId())) {
                    throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                            "ParentCaseId must be null to create " + CaseHierarchy.PARENT + " Case");
                }
            } else if (caseRequest.getCaseObj().getCaseHierarchy().equals(CaseHierarchy.CHILD)) {
                if (StringUtils.isBlank(caseRequest.getCaseObj().getParentCaseId())) {
                    throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
                            "ParentCaseId is mandatory to create " + CaseHierarchy.CHILD + " Case");
                }
                List<String> caseIds = new ArrayList<>();
                caseIds.add(caseRequest.getCaseObj().getParentCaseId());
                CaseSearchCriteria criteria = CaseSearchCriteria.builder().id(caseIds).build();
                CaseResponse response = caseRepository.getILMSCaseData(criteria);
                if (response.getCaseList().size() != 1) {
                    throw new CustomException(ILMSErrorConstants.PARENT_CASE_NOT_FOUND, "Parent Case does not exist");
                }
            }
        }
        if (Objects.nonNull(caseRequest.getCaseObj().getCourt())) {
            caseRequest.getCaseObj().getCourt().setStatus(Status.ACTIVE);
        }
        if (Objects.nonNull(caseRequest.getCaseObj().getPetitioner())) {
            caseRequest.getCaseObj().getPetitioner().setPartyType(PartyType.PETITIONER.toString());
            caseRequest.getCaseObj().getPetitioner().setStatus(Status.ACTIVE);
            if (Objects.nonNull(caseRequest.getCaseObj().getPetitioner().getAdvocate())) {
                caseRequest.getCaseObj().getPetitioner().getAdvocate().setPartyType(PartyType.PETITIONER);
                caseRequest.getCaseObj().getPetitioner().getAdvocate().setStatus(Status.ACTIVE);
            }
        }
        if (Objects.nonNull(caseRequest.getCaseObj().getRespondent())) {
            caseRequest.getCaseObj().getRespondent().setPartyType(PartyType.RESPONDENT.toString());
            caseRequest.getCaseObj().getRespondent().setStatus(Status.ACTIVE);
            if (Objects.nonNull(caseRequest.getCaseObj().getRespondent().getAdvocate())) {
                caseRequest.getCaseObj().getRespondent().getAdvocate().setPartyType(PartyType.RESPONDENT);
                caseRequest.getCaseObj().getRespondent().getAdvocate().setStatus(Status.ACTIVE);
            }
        }
        if (Objects.nonNull(caseRequest.getCaseObj().getAct())) {
            caseRequest.getCaseObj().getAct().setStatus(Status.ACTIVE);
        }
        if (Objects.nonNull(caseRequest.getCaseObj().getPetitioner())) {
            if (Objects.nonNull(caseRequest.getCaseObj().getPetitioner().getDepartmentName())) {
                caseRequest.getCaseObj().getPetitioner().setFirstName(null);
                caseRequest.getCaseObj().getPetitioner().setLastName(null);
                caseRequest.getCaseObj().getPetitioner().setGender(null);
                caseRequest.getCaseObj().getPetitioner().setPetitionerType(null);
                caseRequest.getCaseObj().getPetitioner().setAddress(null);
                caseRequest.getCaseObj().getPetitioner().setContactNumber(null);
                caseRequest.getCaseObj().getPetitioner().setDepartmentName(caseRequest.getCaseObj().getPetitioner().getDepartmentName());
            } else {
                caseRequest.getCaseObj().getPetitioner().setDepartmentName(null);
            }
            caseRequest.getCaseObj().getPetitioner().setPartyType(PartyType.PETITIONER.toString());
            caseRequest.getCaseObj().getPetitioner().setStatus(Status.ACTIVE);
            if (Objects.nonNull(caseRequest.getCaseObj().getPetitioner().getAdvocate())) {
                caseRequest.getCaseObj().getPetitioner().getAdvocate().setPartyType(PartyType.PETITIONER);
                caseRequest.getCaseObj().getPetitioner().getAdvocate().setStatus(Status.ACTIVE);
            }
        }
        if (Objects.nonNull(caseRequest.getCaseObj().getRespondent())) {
            if (Objects.nonNull(caseRequest.getCaseObj().getRespondent().getDepartmentName())) {
                caseRequest.getCaseObj().getRespondent().setFirstName(null);
                caseRequest.getCaseObj().getRespondent().setLastName(null);
                caseRequest.getCaseObj().getRespondent().setGender(null);
                caseRequest.getCaseObj().getRespondent().setPetitionerType(null);
                caseRequest.getCaseObj().getRespondent().setAddress(null);
                caseRequest.getCaseObj().getRespondent().setContactNumber(null);
                caseRequest.getCaseObj().getRespondent().setDepartmentName(caseRequest.getCaseObj().getRespondent().getDepartmentName());
            } else {
                caseRequest.getCaseObj().getRespondent().setDepartmentName(null);
            }
            caseRequest.getCaseObj().getRespondent().setPartyType(PartyType.RESPONDENT.toString());
            caseRequest.getCaseObj().getRespondent().setStatus(Status.ACTIVE);
            if (Objects.nonNull(caseRequest.getCaseObj().getRespondent().getAdvocate())) {
                caseRequest.getCaseObj().getRespondent().getAdvocate().setPartyType(PartyType.RESPONDENT);
                caseRequest.getCaseObj().getRespondent().getAdvocate().setStatus(Status.ACTIVE);
            }
        }
        if (Objects.nonNull(caseRequest.getCaseObj().getAct())) {
            caseRequest.getCaseObj().getAct().setStatus(Status.ACTIVE);
        }
        caseRequest.getCaseObj().setStatus(Status.ACTIVE);

        caseValidator.validateCreate(caseRequest);
        caseValidator.cnrDuplicacyCheck(caseRequest);
        caseValidator.caseNumberDuplicacyCheck(caseRequest);
        caseEnrichmentService.enrichCaseCreateRequest(caseRequest);
        if (ilmsConfiguration.getIsWorkflowEnabled()) {
            workflowService.updateWorkflow(caseRequest, CreationReason.CREATE);
            notificationService.process(ilmsConfiguration.getCreateCaseTopic(), caseRequest);
        }
        producer.push(ilmsConfiguration.getCreateCaseTopic(), caseRequest);
        return caseRequest.getCaseObj();
    }

    /**
     * Updates the ilms_case
     *
     * @param caseRequest The update Request
     * @return Updated ilmsCase
     */
    public Case update(CaseRequest caseRequest) {
        if (caseRequest.getCaseObj().getId() != null) {
            CaseSearchCriteria criteria = CaseSearchCriteria.builder().id(Collections.singletonList(caseRequest.getCaseObj().getId())).build();
            CaseResponse caseResponse = caseRepository.getILMSCaseData(criteria);
            if (!caseResponse.getCaseList().isEmpty()) {
                CaseRequest updatedCaseRequest = caseUtils.prepareObjectMapperForUpdate(caseResponse.getCaseList().get(0), caseRequest);
                Case cases = caseResponse.getCaseList().get(0);
                caseValidator.validateUpdate(cases, caseRequest);
                if (Objects.nonNull(caseRequest.getCaseObj().getCaseHierarchy())) {
                    if (caseRequest.getCaseObj().getCaseHierarchy().equals(CaseHierarchy.INDEPENDENT)) {
                        updatedCaseRequest.getCaseObj().setParentCaseId(null);
                        if (Objects.nonNull(caseRequest.getCaseObj().getParentCaseId())) {
                            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "Independent Case does not exist Parent Case");
                        }
                    } else if (caseRequest.getCaseObj().getCaseHierarchy().equals(CaseHierarchy.PARENT)) {
                        updatedCaseRequest.getCaseObj().setParentCaseId(null);
                        if (Objects.nonNull(caseRequest.getCaseObj().getParentCaseId())) {
                            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "Parent Case does not exist Parent Case");
                        }
                    } else if (caseRequest.getCaseObj().getCaseHierarchy().equals(CaseHierarchy.CHILD)) {
                        List<String> caseIds = new ArrayList<>();
                        caseIds.add(caseRequest.getCaseObj().getParentCaseId());
                        CaseSearchCriteria criteria1 = CaseSearchCriteria.builder().id(caseIds).build();
                        CaseResponse response = caseRepository.getILMSCaseData(criteria1);
                        if (response.getCaseList().size() != 1) {
                            throw new CustomException(ILMSErrorConstants.PARENT_CASE_NOT_FOUND, "Parent Case does not exist");
                        }
                    }
                }
                producer.push(ilmsConfiguration.getUpdateCaseTopic(), updatedCaseRequest);
                //                todo : notification has send to all the officers who has worked on this case.
                if (Objects.nonNull(caseRequest.getCaseObj().getWorkflow())) {
                    processCaseUpdate(caseRequest, updatedCaseRequest.getCaseObj());
                    notificationService.process(ilmsConfiguration.getUpdateCaseTopic(), caseRequest);
                }
                caseRequest.setCaseObj(updatedCaseRequest.getCaseObj());
            } else {
                throw new CustomException(ILMSErrorConstants.CASE_NOT_AVAILABLE, "Case is not Available");
            }
        } else {
            throw new CustomException(ILMSErrorConstants.CASE_NOT_AVAILABLE, "id is mandatory");
        }
        return caseRequest.getCaseObj();
    }

    private void processCaseUpdate(CaseRequest request, Case cases) {
        if (ilmsConfiguration.getIsWorkflowEnabled()) {
            State state = workflowService.updateWorkflow(request, CreationReason.UPDATE);
            if (state.getIsStartState() && state.getApplicationStatus().equalsIgnoreCase(Status.ACTIVE.toString()) && !cases.getStatus()
                    .equals(Status.ACTIVE)) {
            }
        }
    }
}

//    public ByteArrayInputStream generatePDF(final CaseSearchCriteria criteria) {
//        RequestInfo requestInfo = null;
//        CaseDetailsResponse caseResponse = caseDetailsSearch(criteria, requestInfo);
//        List<Case> caseList = caseResponse.getCaseList();
//        List<Hearing> hearingList = caseResponse.getHearingList();
//        List<Order> orderList = caseResponse.getOrderList();
//        com.itextpdf.text.Document pdfDoc = new com.itextpdf.text.Document(PageSize.A4);
//        ByteArrayInputStream bis = null;
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        try {
//            PdfWriter.getInstance(pdfDoc, out);
//            pdfDoc.open();
//            caseList.forEach(ilmsCase -> {
//                Font font = new Font(Font.FontFamily.HELVETICA, 20.0f, Font.BOLD, BaseColor.BLACK);
//                Chunk chunk = new Chunk("CASES DETAILS : ", font);
//                Paragraph borrowerDetails = new Paragraph(chunk);
//                borrowerDetails.setAlignment(Element.ALIGN_MIDDLE);
//
//                PdfPTable table = new PdfPTable(2);
//                table.setWidthPercentage(100.0f);
//                table.setSpacingBefore(4f);
//
//                PdfPCell cell3 = new PdfPCell();
//                PdfPCell cell4 = new PdfPCell();
//
//                Paragraph crn = new Paragraph(new Chunk("Crn Number :"));
//                crn.setAlignment(Element.ALIGN_LEFT);
//                cell3.setMinimumHeight(12f);
//                cell3.setBorder(0);
//                cell3.addElement(crn);
//                cell3.setPaddingTop(-1f);
//
//                Paragraph crnNumber = new Paragraph(new Chunk(ilmsCase.getCnrNumber()));
//                cell4.setMinimumHeight(12f);
//                cell4.addElement(crnNumber);
//                cell4.setPaddingTop(-1f);
//                cell4.setBorder(0);
//                table.addCell(cell3);
//                table.addCell(cell4);
//
//                PdfPCell cell1 = new PdfPCell();
//                PdfPCell cell2 = new PdfPCell();
//
//                Paragraph para = new Paragraph(new Chunk("Case Number :"));
//                para.setAlignment(Element.ALIGN_LEFT);
//                cell1.setMinimumHeight(12f);
//                cell1.addElement(para);
//                cell1.setBorder(0);
//                cell1.setPaddingTop(-1f);
//
//                Paragraph casenumber = new Paragraph(new Chunk(ilmsCase.getCaseNumber()));
//                cell2.setMinimumHeight(12f);
//                cell2.addElement(casenumber);
//                cell2.setBorder(0);
//                cell2.setPaddingTop(-1f);
//
//                table.addCell(cell1);
//                table.addCell(cell2);
//
//                PdfPCell cell5 = new PdfPCell();
//                PdfPCell cell6 = new PdfPCell();
//
//                Paragraph caseType = new Paragraph(new Chunk("Case Type :\n"));
//                caseType.setAlignment(Element.ALIGN_LEFT);
//                cell5.setMinimumHeight(12f);
//                cell5.setBorder(0);
//                cell5.addElement(caseType);
//                cell5.setPaddingTop(-1f);
//
//                Paragraph casetype = new Paragraph(new Chunk(ilmsCase.getCaseType()));
//                cell6.setMinimumHeight(12f);
//                cell6.addElement(casetype);
//                cell6.setPaddingTop(-1f);
//                cell6.setBorder(0);
//
//                table.addCell(cell5);
//                table.addCell(cell6);
//
//                PdfPCell cell7 = new PdfPCell();
//                PdfPCell cell8 = new PdfPCell();
//
//                Paragraph category = new Paragraph(new Chunk("Case Category :"));
//                category.setAlignment(Element.ALIGN_LEFT);
//                cell7.setMinimumHeight(12f);
//                cell7.setBorder(0);
//                cell7.addElement(category);
//                cell7.setPaddingTop(-1f);
//
//                Paragraph caseCategory = new Paragraph(new Chunk(ilmsCase.getCaseCategory()));
//                cell8.setMinimumHeight(12f);
//                cell8.addElement(caseCategory);
//                cell8.setPaddingTop(-1f);
//                cell8.setBorder(0);
//
//                table.addCell(cell7);
//                table.addCell(cell8);
//
//                PdfPCell cell9 = new PdfPCell();
//                PdfPCell cell10 = new PdfPCell();
//
//                Paragraph year = new Paragraph(new Chunk("Case Year :"));
//                year.setAlignment(Element.ALIGN_LEFT);
//                cell9.setMinimumHeight(12f);
//                cell9.setBorder(0);
//                cell9.addElement(year);
//                cell9.setPaddingTop(-1f);
//
////                Paragraph caseyear = new Paragraph(new Chunk(ilmsCase.getCaseYear().toString()));
////                cell10.setMinimumHeight(12f);
////                cell10.addElement(caseyear);
////                cell10.setPaddingTop(-1f);
////                cell10.setBorder(0);
//
//                table.addCell(cell9);
//                table.addCell(cell10);
//
//                PdfPCell cell11 = new PdfPCell();
//                PdfPCell cell12 = new PdfPCell();
//
//                Paragraph filing = new Paragraph(new Chunk("Filling Number :"));
//                filing.setAlignment(Element.ALIGN_LEFT);
//                cell11.setMinimumHeight(12f);
//                cell11.setBorder(0);
//                cell11.addElement(filing);
//                cell11.setPaddingTop(-1f);
//
//                Paragraph fillingnumber = new Paragraph(new Chunk(ilmsCase.getFilingNumber()));
//                cell12.setMinimumHeight(12f);
//                cell12.addElement(fillingnumber);
//                cell12.setPaddingTop(-1f);
//                cell12.setBorder(0);
//
//                table.addCell(cell11);
//                table.addCell(cell12);
//
//                PdfPCell cell13 = new PdfPCell();
//                PdfPCell cell14 = new PdfPCell();
//
//                Paragraph filling = new Paragraph(new Chunk("Filling Date :"));
//                filling.setAlignment(Element.ALIGN_LEFT);
//                cell13.setMinimumHeight(12f);
//                cell13.setBorder(0);
//                cell13.addElement(filling);
//                cell13.setPaddingTop(-1f);
//
//                Paragraph fillingDate = new Paragraph(new Chunk(ilmsCase.getFilingDate().toString()));
//                cell14.setMinimumHeight(12f);
//                cell14.addElement(fillingDate);
//                cell14.setPaddingTop(-1f);
//                cell14.setBorder(0);
//
//                table.addCell(cell13);
//                table.addCell(cell14);
//
//                PdfPCell cell15 = new PdfPCell();
//                PdfPCell cell16 = new PdfPCell();
//
//                Paragraph reg_date = new Paragraph(new Chunk("Registration Date :"));
//                reg_date.setAlignment(Element.ALIGN_LEFT);
//                cell15.setMinimumHeight(12f);
//                cell15.setBorder(0);
//                cell15.addElement(reg_date);
//                cell15.setPaddingTop(-1f);
//
//                Paragraph registration = new Paragraph(new Chunk(ilmsCase.getRegistrationDate().toString()));
//                cell16.setMinimumHeight(12f);
//                cell16.addElement(registration);
//                cell16.setPaddingTop(-1f);
//                cell16.setBorder(0);
//
//                table.addCell(cell15);
//                table.addCell(cell16);
//
//                PdfPCell cell17 = new PdfPCell();
//                PdfPCell cell18 = new PdfPCell();
//                Paragraph Summary = new Paragraph(new Chunk("Case Summary :"));
//                Summary.setAlignment(Element.ALIGN_LEFT);
//                cell17.setMinimumHeight(12f);
//                cell17.setBorder(0);
//                cell17.addElement(Summary);
//                cell17.setPaddingTop(-1f);
//
//                Paragraph caseSummary = new Paragraph(new Chunk(ilmsCase.getCaseSummary()));
//                cell18.setMinimumHeight(12f);
//                cell18.addElement(caseSummary);
//                cell18.setPaddingTop(-1f);
//                cell18.setBorder(0);
//
//                table.addCell(cell17);
//                table.addCell(cell18);
//
//                PdfPCell cell19 = new PdfPCell();
//                PdfPCell cell20 = new PdfPCell();
//                Paragraph arising = new Paragraph(new Chunk("Arising Details :"));
//                arising.setAlignment(Element.ALIGN_LEFT);
//                cell19.setMinimumHeight(12f);
//                cell19.setBorder(0);
//                cell19.addElement(arising);
//                cell19.setPaddingTop(-1f);
//
//                Paragraph arisingDetails = new Paragraph(new Chunk(ilmsCase.getArisingDetails()));
//                cell20.setMinimumHeight(12f);
//                cell20.addElement(arisingDetails);
//                cell20.setPaddingTop(-1f);
//                cell20.setBorder(0);
//
//                table.addCell(cell19);
//                table.addCell(cell20);
//
//                PdfPCell cell21 = new PdfPCell();
//                PdfPCell cell22 = new PdfPCell();
//                Paragraph policy = new Paragraph(new Chunk("Policy Or NonPolicyMatter :"));
//                policy.setAlignment(Element.ALIGN_LEFT);
//                cell21.setMinimumHeight(12f);
//                cell21.setBorder(0);
//                cell21.addElement(policy);
//                cell21.setPaddingTop(-1f);
//
//                Paragraph nonPolicyMatter = new Paragraph(new Chunk(ilmsCase.getPolicyOrNonPolicyMatter()));
//                cell22.setMinimumHeight(12f);
//                cell22.addElement(nonPolicyMatter);
//                cell22.setPaddingTop(-1f);
//                cell22.setBorder(0);
//
//                table.addCell(cell21);
//                table.addCell(cell22);
//
//                PdfPCell cell23 = new PdfPCell();
//                PdfPCell cell24 = new PdfPCell();
//                Paragraph application = new Paragraph(new Chunk("Application Number :"));
//                application.setAlignment(Element.ALIGN_LEFT);
//                cell23.setMinimumHeight(12f);
//                cell23.setBorder(0);
//                cell23.addElement(application);
//                cell23.setPaddingTop(-1f);
//
//                Paragraph applicationnumber = new Paragraph(new Chunk(ilmsCase.getApplicationNumber()));
//                cell24.setMinimumHeight(12f);
//                cell24.addElement(applicationnumber);
//                cell24.setPaddingTop(-1f);
//                cell24.setBorder(0);
//
//                table.addCell(cell23);
//                table.addCell(cell24);
//
//                PdfPCell cell25 = new PdfPCell();
//                PdfPCell cell26 = new PdfPCell();
//                Paragraph isCase = new Paragraph(new Chunk("Is Case Number Correct :"));
//                isCase.setAlignment(Element.ALIGN_LEFT);
//                cell25.setMinimumHeight(12f);
//                cell25.setBorder(0);
//                cell25.addElement(isCase);
//                cell25.setPaddingTop(-1f);
//
////                Paragraph iscasenumbercorrect = new Paragraph(new Chunk(ilmsCase.getIsCaseNumberCorrect().toString()));
////                cell26.setMinimumHeight(12f);
////                cell26.addElement(iscasenumbercorrect);
////                cell26.setPaddingTop(-1f);
////                cell26.setBorder(0);
//
//                table.addCell(cell25);
//                table.addCell(cell26);
//
//                PdfPCell cell27 = new PdfPCell();
//                PdfPCell cell28 = new PdfPCell();
//                Paragraph casestatus = new Paragraph(new Chunk("Case Status :"));
//                casestatus.setAlignment(Element.ALIGN_LEFT);
//                cell27.setMinimumHeight(12f);
//                cell27.setBorder(0);
//                cell27.addElement(casestatus);
//                cell27.setPaddingTop(-1f);
//
//                Paragraph status = new Paragraph(new Chunk(ilmsCase.getCaseStatus()));
//                cell28.setMinimumHeight(12f);
//                cell28.addElement(status);
//                cell28.setPaddingTop(-1f);
//                cell28.setBorder(0);
//
//                table.addCell(cell27);
//                table.addCell(cell28);
//
//                PdfPCell cell29 = new PdfPCell();
//                PdfPCell cell30 = new PdfPCell();
//                Paragraph firsthearing = new Paragraph(new Chunk("First Hearing Date :"));
//                firsthearing.setAlignment(Element.ALIGN_LEFT);
//                cell29.setMinimumHeight(12f);
//                cell29.setBorder(0);
//                cell29.addElement(firsthearing);
//                cell29.setPaddingTop(-1f);
//
////                Paragraph hearing = new Paragraph(new Chunk(ilmsCase.getFirstHearingDate().toString()));
////                cell30.setMinimumHeight(12f);
////                cell30.addElement(hearing);
////                cell30.setPaddingTop(-1f);
////                cell30.setBorder(0);
////
////                table.addCell(cell29);
////                table.addCell(cell30);
////
////                PdfPCell cell31 = new PdfPCell();
////                PdfPCell cell32 = new PdfPCell();
////                Paragraph previousHearingDate = new Paragraph(new Chunk("Previous Hearing Date :"));
////                previousHearingDate.setAlignment(Element.ALIGN_LEFT);
////                cell31.setMinimumHeight(12f);
////                cell31.setBorder(0);
////                cell31.addElement(previousHearingDate);
////                cell31.setPaddingTop(-1f);
////
////                Paragraph previousHearing = new Paragraph(new Chunk(ilmsCase.getPreviousHearingDate().toString()));
////                cell32.setMinimumHeight(12f);
////                cell32.addElement(previousHearing);
////                cell32.setPaddingTop(-1f);
////                cell32.setBorder(0);
//
////                table.addCell(cell31);
////                table.addCell(cell32);
//
//                PdfPCell cell33 = new PdfPCell();
//                PdfPCell cell34 = new PdfPCell();
//                Paragraph stage = new Paragraph(new Chunk("Case Stage :"));
//                stage.setAlignment(Element.ALIGN_LEFT);
//                cell33.setMinimumHeight(12f);
//                cell33.setBorder(0);
//                cell33.addElement(stage);
//                cell33.setPaddingTop(-1f);
//
//                Paragraph caseStage = new Paragraph(new Chunk(ilmsCase.getCaseStage()));
//                cell34.setMinimumHeight(12f);
//                cell34.addElement(caseStage);
//                cell34.setPaddingTop(-1f);
//                cell34.setBorder(0);
//
//                table.addCell(cell33);
//                table.addCell(cell34);
//
//                PdfPCell cell35 = new PdfPCell();
//                PdfPCell cell36 = new PdfPCell();
//                Paragraph substage = new Paragraph(new Chunk("Case Sub Stage :"));
//                substage.setAlignment(Element.ALIGN_LEFT);
//                cell35.setMinimumHeight(12f);
//                cell35.setBorder(0);
//                cell35.addElement(substage);
//                cell35.setPaddingTop(-1f);
//
//                Paragraph caseSubStage = new Paragraph(new Chunk(ilmsCase.getCaseSubStage()));
//                cell36.setMinimumHeight(12f);
//                cell36.addElement(caseSubStage);
//                cell36.setPaddingTop(-1f);
//                cell36.setBorder(0);
//
//                table.addCell(cell35);
//                table.addCell(cell36);
//
//                PdfPCell cell37 = new PdfPCell();
//                PdfPCell cell38 = new PdfPCell();
//                Paragraph flag = new Paragraph(new Chunk("Case Flag :"));
//                flag.setAlignment(Element.ALIGN_LEFT);
//                cell37.setMinimumHeight(12f);
//                cell37.setBorder(0);
//                cell37.addElement(flag);
//                cell37.setPaddingTop(-1f);
//
//                Paragraph caseFlag = new Paragraph(new Chunk(ilmsCase.getPriority()));
//                cell38.setMinimumHeight(12f);
//                cell38.addElement(caseFlag);
//                cell38.setPaddingTop(-1f);
//                cell38.setBorder(0);
//
//                table.addCell(cell37);
//                table.addCell(cell38);
//
//                PdfPCell cell39 = new PdfPCell();
//                PdfPCell cell40 = new PdfPCell();
//                Paragraph de_name = new Paragraph(new Chunk("Department Name :"));
//                de_name.setAlignment(Element.ALIGN_LEFT);
//                cell39.setMinimumHeight(12f);
//                cell39.setBorder(0);
//                cell39.addElement(de_name);
//                cell39.setPaddingTop(-1f);
//
//                Paragraph departmentName = new Paragraph(new Chunk(ilmsCase.getDepartmentName()));
//                cell40.setMinimumHeight(12f);
//                cell40.addElement(departmentName);
//                cell40.setPaddingTop(-1f);
//                cell40.setBorder(0);
//
//                table.addCell(cell39);
//                table.addCell(cell40);
//
//                PdfPCell cell41 = new PdfPCell();
//                PdfPCell cell42 = new PdfPCell();
//                Paragraph Oic = new Paragraph(new Chunk("Recommend OIC :"));
//                Oic.setAlignment(Element.ALIGN_LEFT);
//                cell41.setMinimumHeight(12f);
//                cell41.setBorder(0);
//                cell41.addElement(Oic);
//                cell41.setPaddingTop(-1f);
//
//                Paragraph recommendOIC = new Paragraph(new Chunk(ilmsCase.getRecommendOIC()));
//                cell42.setMinimumHeight(12f);
//                cell42.addElement(recommendOIC);
//                cell42.setPaddingTop(-1f);
//                cell42.setBorder(0);
//
//                table.addCell(cell41);
//                table.addCell(cell42);
//                try {
//                    pdfDoc.add(borrowerDetails);
//                    pdfDoc.add(table);
//                } catch (DocumentException e) {
//                    throw new RuntimeException(e);
//                }
//            });
//
//            caseList.forEach(ilmsCase -> {
//                Font font1 = new Font(Font.FontFamily.HELVETICA, 20.0f, Font.BOLD, BaseColor.BLACK);
//                Chunk chunk1 = new Chunk("\nPETITIONER DETAILS : ", font1);
//                Paragraph borrowerDetail = new Paragraph(chunk1);
//                borrowerDetail.setAlignment(Element.ALIGN_MIDDLE);
//
//                PdfPTable table1 = new PdfPTable(2);
//                table1.setWidthPercentage(100.0f);
//                table1.setSpacingBefore(4f);
//
//                PdfPCell cell1 = new PdfPCell();
//                PdfPCell cell2 = new PdfPCell();
//                Paragraph FName = new Paragraph(new Chunk("First Name :"));
//                FName.setAlignment(Element.ALIGN_LEFT);
//                cell1.setMinimumHeight(12f);
//                cell1.setBorder(0);
//                cell1.addElement(FName);
//                cell1.setPaddingTop(-1f);
//
//                Paragraph firstName = new Paragraph(new Chunk(ilmsCase.getPetitioner().getFirstName()));
//                cell2.setMinimumHeight(12f);
//                cell2.addElement(firstName);
//                cell2.setPaddingTop(-1f);
//                cell2.setBorder(0);
//
//                table1.addCell(cell1);
//                table1.addCell(cell2);
//
//                PdfPCell cell3 = new PdfPCell();
//                PdfPCell cell4 = new PdfPCell();
//                Paragraph LName = new Paragraph(new Chunk("Last Name :"));
//                LName.setAlignment(Element.ALIGN_LEFT);
//                cell3.setMinimumHeight(12f);
//                cell3.setBorder(0);
//                cell3.addElement(LName);
//                cell3.setPaddingTop(-1f);
//
//                Paragraph lastName = new Paragraph(new Chunk(ilmsCase.getPetitioner().getLastName()));
//                cell4.setMinimumHeight(12f);
//                cell4.addElement(lastName);
//                cell4.setPaddingTop(-1f);
//                cell4.setBorder(0);
//
//                table1.addCell(cell3);
//                table1.addCell(cell4);
//
//                PdfPCell cell5 = new PdfPCell();
//                PdfPCell cell6 = new PdfPCell();
//                Paragraph address = new Paragraph(new Chunk("Address :"));
//                address.setAlignment(Element.ALIGN_LEFT);
//                cell5.setMinimumHeight(12f);
//                cell5.setBorder(0);
//                cell5.addElement(address);
//                cell5.setPaddingTop(-1f);
//
//                Paragraph Adds = new Paragraph(new Chunk(ilmsCase.getPetitioner().getAddress()));
//                cell6.setMinimumHeight(12f);
//                cell6.addElement(Adds);
//                cell6.setPaddingTop(-1f);
//                cell6.setBorder(0);
//
//                table1.addCell(cell5);
//                table1.addCell(cell6);
//
//                PdfPCell cell7 = new PdfPCell();
//                PdfPCell cell8 = new PdfPCell();
//                Paragraph CNO = new Paragraph(new Chunk("Contact Number :"));
//                CNO.setAlignment(Element.ALIGN_LEFT);
//                cell7.setMinimumHeight(12f);
//                cell7.setBorder(0);
//                cell7.addElement(CNO);
//                cell7.setPaddingTop(-1f);
//
//                Paragraph contectno = new Paragraph(new Chunk(ilmsCase.getPetitioner().getContactNumber()));
//                cell8.setMinimumHeight(12f);
//                cell8.addElement(contectno);
//                cell8.setPaddingTop(-1f);
//                cell8.setBorder(0);
//
//                table1.addCell(cell7);
//                table1.addCell(cell8);
//
//                PdfPCell cell9 = new PdfPCell();
//                PdfPCell cell10 = new PdfPCell();
//                Paragraph party = new Paragraph(new Chunk("Party Type :"));
//                party.setAlignment(Element.ALIGN_LEFT);
//                cell9.setMinimumHeight(12f);
//                cell9.setBorder(0);
//                cell9.addElement(party);
//                cell9.setPaddingTop(-1f);
//
//                Paragraph partyTpe = new Paragraph(new Chunk(ilmsCase.getPetitioner().getPartyType()));
//                cell10.setMinimumHeight(12f);
//                cell10.addElement(partyTpe);
//                cell10.setPaddingTop(-1f);
//                cell10.setBorder(0);
//
//                table1.addCell(cell9);
//                table1.addCell(cell10);
//
//                try {
//                    pdfDoc.add(borrowerDetail);
//                    pdfDoc.add(table1);
//                } catch (DocumentException e) {
//                    throw new RuntimeException(e);
//                }
//            });
//
//            caseList.forEach(ilmsCase -> {
//                Font font1 = new Font(Font.FontFamily.HELVETICA, 20.0f, Font.BOLD, BaseColor.BLACK);
//                Chunk chunk1 = new Chunk("\nPETITIONER ADVOCATE DETAILS : ", font1);
//                Paragraph borrowerDetail = new Paragraph(chunk1);
//                borrowerDetail.setAlignment(Element.ALIGN_MIDDLE);
//
//                PdfPTable table1 = new PdfPTable(2);
//                table1.setWidthPercentage(100.0f);
//                table1.setSpacingBefore(4f);
//
//                PdfPCell cell1 = new PdfPCell();
//                PdfPCell cell2 = new PdfPCell();
//                Paragraph FName = new Paragraph(new Chunk("First Name :"));
//                FName.setAlignment(Element.ALIGN_LEFT);
//                cell1.setMinimumHeight(12f);
//                cell1.setBorder(0);
//                cell1.addElement(FName);
//                cell1.setPaddingTop(-1f);
//
//                Paragraph firstName = new Paragraph(new Chunk(ilmsCase.getPetitioner().getAdvocate().getFirstName()));
//                cell2.setMinimumHeight(12f);
//                cell2.addElement(firstName);
//                cell2.setPaddingTop(-1f);
//                cell2.setBorder(0);
//
//                table1.addCell(cell1);
//                table1.addCell(cell2);
//
//                PdfPCell cell3 = new PdfPCell();
//                PdfPCell cell4 = new PdfPCell();
//                Paragraph LName = new Paragraph(new Chunk("Last Name :"));
//                LName.setAlignment(Element.ALIGN_LEFT);
//                cell3.setMinimumHeight(12f);
//                cell3.setBorder(0);
//                cell3.addElement(LName);
//                cell3.setPaddingTop(-1f);
//
//                Paragraph lastName = new Paragraph(new Chunk(ilmsCase.getPetitioner().getAdvocate().getLastName()));
//                cell4.setMinimumHeight(12f);
//                cell4.addElement(lastName);
//                cell4.setPaddingTop(-1f);
//                cell4.setBorder(0);
//
//                table1.addCell(cell3);
//                table1.addCell(cell4);
//
//                try {
//                    pdfDoc.add(borrowerDetail);
//                    pdfDoc.add(table1);
//                } catch (DocumentException e) {
//                    throw new RuntimeException(e);
//                }
//            });
//
//            caseList.forEach(ilmsCase -> {
//                Font font1 = new Font(Font.FontFamily.HELVETICA, 20.0f, Font.BOLD, BaseColor.BLACK);
//                Chunk chunk1 = new Chunk("\n\nRESPONDENT DETAILS : ", font1);
//                Paragraph borrowerDetail = new Paragraph(chunk1);
//                borrowerDetail.setAlignment(Element.ALIGN_MIDDLE);
//
//                PdfPTable table1 = new PdfPTable(2);
//                table1.setWidthPercentage(100.0f);
//                table1.setSpacingBefore(4f);
//
//                PdfPCell cell1 = new PdfPCell();
//                PdfPCell cell2 = new PdfPCell();
//                Paragraph FName = new Paragraph(new Chunk("First Name :"));
//                FName.setAlignment(Element.ALIGN_LEFT);
//                cell1.setMinimumHeight(12f);
//                cell1.setBorder(0);
//                cell1.addElement(FName);
//                cell1.setPaddingTop(-1f);
//
//                Paragraph firstName = new Paragraph(new Chunk(ilmsCase.getRespondent().getFirstName()));
//                cell2.setMinimumHeight(12f);
//                cell2.addElement(firstName);
//                cell2.setPaddingTop(-1f);
//                cell2.setBorder(0);
//
//                table1.addCell(cell1);
//                table1.addCell(cell2);
//
//                PdfPCell cell3 = new PdfPCell();
//                PdfPCell cell4 = new PdfPCell();
//                Paragraph LName = new Paragraph(new Chunk("Last Name :"));
//                LName.setAlignment(Element.ALIGN_LEFT);
//                cell3.setMinimumHeight(12f);
//                cell3.setBorder(0);
//                cell3.addElement(LName);
//                cell3.setPaddingTop(-1f);
//
//                Paragraph lastName = new Paragraph(new Chunk(ilmsCase.getRespondent().getLastName()));
//                cell4.setMinimumHeight(12f);
//                cell4.addElement(lastName);
//                cell4.setPaddingTop(-1f);
//                cell4.setBorder(0);
//
//                table1.addCell(cell3);
//                table1.addCell(cell4);
//
//                PdfPCell cell5 = new PdfPCell();
//                PdfPCell cell6 = new PdfPCell();
//                Paragraph address = new Paragraph(new Chunk("Address :"));
//                address.setAlignment(Element.ALIGN_LEFT);
//                cell5.setMinimumHeight(12f);
//                cell5.setBorder(0);
//                cell5.addElement(address);
//                cell5.setPaddingTop(-1f);
//
//                Paragraph Adds = new Paragraph(new Chunk(ilmsCase.getRespondent().getAddress()));
//                cell6.setMinimumHeight(12f);
//                cell6.addElement(Adds);
//                cell6.setPaddingTop(-1f);
//                cell6.setBorder(0);
//
//                table1.addCell(cell5);
//                table1.addCell(cell6);
//
//                PdfPCell cell7 = new PdfPCell();
//                PdfPCell cell8 = new PdfPCell();
//                Paragraph CNO = new Paragraph(new Chunk("Contact Number :"));
//                CNO.setAlignment(Element.ALIGN_LEFT);
//                cell7.setMinimumHeight(12f);
//                cell7.setBorder(0);
//                cell7.addElement(CNO);
//                cell7.setPaddingTop(-1f);
//
//                Paragraph contectno = new Paragraph(new Chunk(ilmsCase.getRespondent().getContactNumber()));
//                cell8.setMinimumHeight(12f);
//                cell8.addElement(contectno);
//                cell8.setPaddingTop(-1f);
//                cell8.setBorder(0);
//
//                table1.addCell(cell7);
//                table1.addCell(cell8);
//
//                PdfPCell cell9 = new PdfPCell();
//                PdfPCell cell10 = new PdfPCell();
//                Paragraph party = new Paragraph(new Chunk("Party Type :"));
//                party.setAlignment(Element.ALIGN_LEFT);
//                cell9.setMinimumHeight(12f);
//                cell9.setBorder(0);
//                cell9.addElement(party);
//                cell9.setPaddingTop(-1f);
//
//                Paragraph partyTpe = new Paragraph(new Chunk(ilmsCase.getRespondent().getPartyType()));
//                cell10.setMinimumHeight(12f);
//                cell10.addElement(partyTpe);
//                cell10.setPaddingTop(-1f);
//                cell10.setBorder(0);
//
//                table1.addCell(cell9);
//                table1.addCell(cell10);
//
//                try {
//                    pdfDoc.add(borrowerDetail);
//                    pdfDoc.add(table1);
//                } catch (DocumentException e) {
//                    throw new RuntimeException(e);
//                }
//            });
//
//            caseList.forEach(ilmsCase -> {
//                Font font1 = new Font(Font.FontFamily.HELVETICA, 20.0f, Font.BOLD, BaseColor.BLACK);
//                Chunk chunk1 = new Chunk("\nRESPONDENT ADVOCATE DETAILS : ", font1);
//                Paragraph borrowerDetail = new Paragraph(chunk1);
//                borrowerDetail.setAlignment(Element.ALIGN_MIDDLE);
//
//                PdfPTable table1 = new PdfPTable(2);
//                table1.setWidthPercentage(100.0f);
//                table1.setSpacingBefore(4f);
//
//                PdfPCell cell1 = new PdfPCell();
//                PdfPCell cell2 = new PdfPCell();
//                Paragraph FName = new Paragraph(new Chunk("First Name :"));
//                FName.setAlignment(Element.ALIGN_LEFT);
//                cell1.setMinimumHeight(12f);
//                cell1.setBorder(0);
//                cell1.addElement(FName);
//                cell1.setPaddingTop(-1f);
//
//                Paragraph firstName = new Paragraph(new Chunk(ilmsCase.getRespondent().getAdvocate().getFirstName()));
//                cell2.setMinimumHeight(12f);
//                cell2.addElement(firstName);
//                cell2.setPaddingTop(-1f);
//                cell2.setBorder(0);
//
//                table1.addCell(cell1);
//                table1.addCell(cell2);
//
//                PdfPCell cell3 = new PdfPCell();
//                PdfPCell cell4 = new PdfPCell();
//                Paragraph LName = new Paragraph(new Chunk("Last Name :"));
//                LName.setAlignment(Element.ALIGN_LEFT);
//                cell3.setMinimumHeight(12f);
//                cell3.setBorder(0);
//                cell3.addElement(LName);
//                cell3.setPaddingTop(-1f);
//
//                Paragraph lastName = new Paragraph(new Chunk(ilmsCase.getRespondent().getAdvocate().getLastName()));
//                cell4.setMinimumHeight(12f);
//                cell4.addElement(lastName);
//                cell4.setPaddingTop(-1f);
//                cell4.setBorder(0);
//
//                table1.addCell(cell3);
//                table1.addCell(cell4);
//
//                PdfPCell cell9 = new PdfPCell();
//                PdfPCell cell10 = new PdfPCell();
//                Paragraph party = new Paragraph(new Chunk("Party Type :"));
//                party.setAlignment(Element.ALIGN_LEFT);
//                cell9.setMinimumHeight(12f);
//                cell9.setBorder(0);
//                cell9.addElement(party);
//                cell9.setPaddingTop(-1f);
//
//                Paragraph partyTpe = new Paragraph(new Chunk(ilmsCase.getRespondent().getAdvocate().getPartyType().toString()));
//                cell10.setMinimumHeight(12f);
//                cell10.addElement(partyTpe);
//                cell10.setPaddingTop(-1f);
//                cell10.setBorder(0);
//
//                table1.addCell(cell9);
//                table1.addCell(cell10);
//
//                try {
//                    pdfDoc.add(borrowerDetail);
//                    pdfDoc.add(table1);
//                } catch (DocumentException e) {
//                    throw new RuntimeException(e);
//                }
//            });
//
//            caseList.forEach(ilmsCase -> {
//                Font font1 = new Font(Font.FontFamily.HELVETICA, 20.0f, Font.BOLD, BaseColor.BLACK);
//                Chunk chunk1 = new Chunk("\nACT : ", font1);
//                Paragraph borrowerDetail = new Paragraph(chunk1);
//                borrowerDetail.setAlignment(Element.ALIGN_MIDDLE);
//
//                PdfPTable table1 = new PdfPTable(2);
//                table1.setWidthPercentage(100.0f);
//                table1.setSpacingBefore(4f);
//
//                PdfPCell cell1 = new PdfPCell();
//                PdfPCell cell2 = new PdfPCell();
//                Paragraph FName = new Paragraph(new Chunk("Act Name :"));
//                FName.setAlignment(Element.ALIGN_LEFT);
//                cell1.setMinimumHeight(12f);
//                cell1.setBorder(0);
//                cell1.addElement(FName);
//                cell1.setPaddingTop(-1f);
//
//                Paragraph firstName = new Paragraph(new Chunk(ilmsCase.getAct().getActName()));
//                cell2.setMinimumHeight(12f);
//                cell2.addElement(firstName);
//                cell2.setPaddingTop(-1f);
//                cell2.setBorder(0);
//
//                table1.addCell(cell1);
//                table1.addCell(cell2);
//                PdfPCell cell3 = new PdfPCell();
//                PdfPCell cell4 = new PdfPCell();
//                Paragraph LName = new Paragraph(new Chunk("Section Number :"));
//                LName.setAlignment(Element.ALIGN_LEFT);
//                cell3.setMinimumHeight(12f);
//                cell3.setBorder(0);
//                cell3.addElement(LName);
//                cell3.setPaddingTop(-1f);
//
//                Paragraph lastName = new Paragraph(new Chunk(ilmsCase.getAct().getSectionNumber()));
//                cell4.setMinimumHeight(12f);
//                cell4.addElement(lastName);
//                cell4.setPaddingTop(-1f);
//                cell4.setBorder(0);
//
//                table1.addCell(cell3);
//                table1.addCell(cell4);
//
//                try {
//                    pdfDoc.add(borrowerDetail);
//                    pdfDoc.add(table1);
//                } catch (DocumentException e) {
//                    throw new RuntimeException(e);
//                }
//            });
//            caseList.forEach(ilmsCase -> {
//                Font font1 = new Font(Font.FontFamily.HELVETICA, 20.0f, Font.BOLD, BaseColor.BLACK);
//                Chunk chunk1 = new Chunk("\nDOCUMENT : ", font1);
//                Paragraph Detail = new Paragraph(chunk1);
//                Detail.setAlignment(Element.ALIGN_MIDDLE);
//                PdfPTable table1 = new PdfPTable(1);
//                table1.setWidthPercentage(10.0f);
//                table1.setSpacingBefore(4f);
//                Phrase phrase = new Phrase("");
//                Font anchorFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, BaseColor.RED);
//                Anchor anchor = new Anchor(new Chunk("Aadhaar", anchorFont));
//                anchor.setReference("http://14.97.12.97/digit-ui/citizen/select-language");
//                phrase.add(anchor);
//                try {
//                    pdfDoc.add(Detail);
//                    Image image = Image.getInstance(
//                            "https://www.adobe.com/express/create/media_127a4cd0c28c2753638768caf8967503d38d01e4c.jpeg?width=400&format=jpeg&optimize=medium");
//                    image.setAlignment(Image.ALIGN_LEFT);
//                    image.setPaddingTop(-80);
//                    image.setAbsolutePosition(10f, 290f);
//                    image.scalePercent(40, 45);
//                    Chunk chunk = new Chunk(image, 0, -20);
//                    pdfDoc.add(image);
//                    pdfDoc.add(phrase);
//                } catch (DocumentException e) {
//                    throw new RuntimeException(e);
//                } catch (MalformedURLException e) {
//                    throw new RuntimeException(e);
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            });
//
//            caseList.forEach(ilmsCase -> {
//                Phrase phrase1 = new Phrase("");
//                Font anchorFont1 = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, BaseColor.RED);
//                Anchor anchor1 = new Anchor(new Chunk("\n\n\n\n\n\n\nPAN", anchorFont1));
//                anchor1.setReference("http://14.97.12.97/digit-ui/citizen/select-language");
//                phrase1.add(anchor1);
//
//                try {
//                    Image image1 = Image.getInstance(
//                            "https://www.adobe.com/express/create/media_127a4cd0c28c2753638768caf8967503d38d01e4c.jpeg?width=400&format=jpeg&optimize=medium");
//                    image1.setAlignment(Image.ALIGN_LEFT);
//                    image1.setPaddingTop(-80);
//                    image1.setAbsolutePosition(10f, 180f);
//                    image1.scalePercent(40, 45);
//                    pdfDoc.add(phrase1);
//                    pdfDoc.add(image1);
//                } catch (DocumentException e) {
//                    throw new RuntimeException(e);
//                } catch (MalformedURLException e) {
//                    throw new RuntimeException(e);
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            });
//            hearingList.forEach(hearing -> {
//                Font font1 = new Font(Font.FontFamily.HELVETICA, 20.0f, Font.BOLD, BaseColor.BLACK);
//                Chunk chunk1 = new Chunk("\n\n\n\nHEARING DETAILS : ", font1);
//                Paragraph detail = new Paragraph(chunk1);
//                detail.setAlignment(Element.ALIGN_MIDDLE);
//
//                PdfPTable table1 = new PdfPTable(2);
//                table1.setWidthPercentage(100.0f);
//                table1.setSpacingBefore(4f);
//
//                PdfPCell cell1 = new PdfPCell();
//                PdfPCell cell2 = new PdfPCell();
//                Paragraph hearingNum = new Paragraph(new Chunk("Hearing Number :"));
//                hearingNum.setAlignment(Element.ALIGN_LEFT);
//                cell1.setMinimumHeight(12f);
//                cell1.setBorder(0);
//                cell1.addElement(hearingNum);
//                cell1.setPaddingTop(-1f);
//
//                Paragraph hnum = new Paragraph(new Chunk(hearing.getHearingNumber()));
//                cell2.setMinimumHeight(12f);
//                cell2.addElement(hnum);
//                cell2.setPaddingTop(-1f);
//                cell2.setBorder(0);
//
//                table1.addCell(cell1);
//                table1.addCell(cell2);
//                PdfPCell cell3 = new PdfPCell();
//                PdfPCell cell4 = new PdfPCell();
//                Paragraph courtId = new Paragraph(new Chunk("Court Id :"));
//                courtId.setAlignment(Element.ALIGN_LEFT);
//                cell3.setMinimumHeight(12f);
//                cell3.setBorder(0);
//                cell3.addElement(courtId);
//                cell3.setPaddingTop(-1f);
//
////                Paragraph court = new Paragraph(new Chunk(hearing.getCourtId()));
////                cell4.setMinimumHeight(12f);
////                cell4.addElement(court);
////                cell4.setPaddingTop(-1f);
////                cell4.setBorder(0);
////
////                table1.addCell(cell3);
////                table1.addCell(cell4);
////
////                PdfPCell cell5 = new PdfPCell();
////                PdfPCell cell6 = new PdfPCell();
////                Paragraph hearingid = new Paragraph(new Chunk("Hearing Id :"));
////                hearingid.setAlignment(Element.ALIGN_LEFT);
////                cell5.setMinimumHeight(12f);
////                cell5.setBorder(0);
////                cell5.addElement(hearingid);
////                cell5.setPaddingTop(-1f);
////
////                Paragraph hid = new Paragraph(new Chunk(hearing.getHearingId()));
////                cell6.setMinimumHeight(12f);
////                cell6.addElement(hid);
////                cell6.setPaddingTop(-1f);
////                cell6.setBorder(0);
////
////                table1.addCell(cell5);
////                table1.addCell(cell6);
//
//                PdfPCell cell7 = new PdfPCell();
//                PdfPCell cell8 = new PdfPCell();
//                Paragraph CNO = new Paragraph(new Chunk("Court Number :"));
//                CNO.setAlignment(Element.ALIGN_LEFT);
//                cell7.setMinimumHeight(12f);
//                cell7.setBorder(0);
//                cell7.addElement(CNO);
//                cell7.setPaddingTop(-1f);
//
//                Paragraph courtno = new Paragraph(new Chunk(hearing.getCourtNumber()));
//                cell8.setMinimumHeight(12f);
//                cell8.addElement(courtno);
//                cell8.setPaddingTop(-1f);
//                cell8.setBorder(0);
//
//                table1.addCell(cell7);
//                table1.addCell(cell8);
//
//                PdfPCell cell9 = new PdfPCell();
//                PdfPCell cell10 = new PdfPCell();
//                Paragraph courtna = new Paragraph(new Chunk("Court Name :"));
//                courtna.setAlignment(Element.ALIGN_LEFT);
//                cell9.setMinimumHeight(12f);
//                cell9.setBorder(0);
//                cell9.addElement(courtna);
//                cell9.setPaddingTop(-1f);
//
////                Paragraph courtname = new Paragraph(new Chunk(hearing.getCourtName()));
////                cell10.setMinimumHeight(12f);
////                cell10.addElement(courtname);
////                cell10.setPaddingTop(-1f);
////                cell10.setBorder(0);
////
////                table1.addCell(cell9);
////                table1.addCell(cell10);
////
////                PdfPCell cell11 = new PdfPCell();
////                PdfPCell cell12 = new PdfPCell();
//
////                Paragraph district = new Paragraph(new Chunk("District :"));
////                district.setAlignment(Element.ALIGN_LEFT);
////                cell11.setMinimumHeight(12f);
////                cell11.setBorder(0);
////                cell11.addElement(district);
////                cell11.setPaddingTop(-1f);
////
////                Paragraph distri = new Paragraph(new Chunk(hearing.getCourt().getDistrict()));
////                cell12.setMinimumHeight(12f);
////                cell12.addElement(distri);
////                cell12.setPaddingTop(-1f);
////                cell12.setBorder(0);
////
////                table1.addCell(cell11);
////                table1.addCell(cell12);
////
////                PdfPCell cell13 = new PdfPCell();
////                PdfPCell cell14 = new PdfPCell();
////
////                Paragraph state = new Paragraph(new Chunk("State :"));
////                state.setAlignment(Element.ALIGN_LEFT);
////                cell13.setMinimumHeight(12f);
////                cell13.setBorder(0);
////                cell13.addElement(state);
////                cell13.setPaddingTop(-1f);
////
////                Paragraph stat = new Paragraph(new Chunk(hearing.getCourt().getState()));
////                cell14.setMinimumHeight(12f);
////                cell14.addElement(stat);
////                cell14.setPaddingTop(-1f);
////                cell14.setBorder(0);
////
////                table1.addCell(cell13);
////                table1.addCell(cell14);
////
////                PdfPCell cell15 = new PdfPCell();
////                PdfPCell cell16 = new PdfPCell();
////
////                Paragraph bench = new Paragraph(new Chunk("Bench :"));
////                bench.setAlignment(Element.ALIGN_LEFT);
////                cell15.setMinimumHeight(12f);
////                cell15.setBorder(0);
////                cell15.addElement(bench);
////                cell15.setPaddingTop(-1f);
////
////                Paragraph benc = new Paragraph(new Chunk(hearing.getCourt().getBench()));
////                cell16.setMinimumHeight(12f);
////                cell16.addElement(benc);
////                cell16.setPaddingTop(-1f);
////                cell16.setBorder(0);
////
////                table1.addCell(cell15);
////                table1.addCell(cell16);
////
////                PdfPCell cell17 = new PdfPCell();
////                PdfPCell cell18 = new PdfPCell();
////                Paragraph division = new Paragraph(new Chunk("Division :"));
////                division.setAlignment(Element.ALIGN_LEFT);
////                cell17.setMinimumHeight(12f);
////                cell17.setBorder(0);
////                cell17.addElement(division);
////                cell17.setPaddingTop(-1f);
////
////                Paragraph divisio = new Paragraph(new Chunk(hearing.getCourt().getDivision()));
////                cell18.setMinimumHeight(12f);
////                cell18.addElement(divisio);
////                cell18.setPaddingTop(-1f);
////                cell18.setBorder(0);
////
////                table1.addCell(cell17);
////                table1.addCell(cell18);
//
//                PdfPCell cell19 = new PdfPCell();
////                PdfPCell cell20 = new PdfPCell();
////                Paragraph status = new Paragraph(new Chunk("Status :"));
////                status.setAlignment(Element.ALIGN_LEFT);
////                cell19.setMinimumHeight(12f);
////                cell19.setBorder(0);
////                cell19.addElement(status);
////                cell19.setPaddingTop(-1f);
////
////                Paragraph stats = new Paragraph(new Chunk(hearing.getCourt().getStatus().toString()));
////                cell20.setMinimumHeight(12f);
////                cell20.addElement(stats);
////                cell20.setPaddingTop(-1f);
////                cell20.setBorder(0);
////
////                table1.addCell(cell19);
////                table1.addCell(cell20);
////
////                try {
////                    pdfDoc.add(detail);
////                    pdfDoc.add(table1);
////                } catch (DocumentException e) {
////                    throw new RuntimeException(e);
////                }
////            });
//
////            orderList.forEach(judgement -> {
////                Font font1 = new Font(Font.FontFamily.HELVETICA, 20.0f, Font.BOLD, BaseColor.BLACK);
////                Chunk chunk1 = new Chunk("\nJUDGEMENT DETAILS : ", font1);
////                Paragraph detail = new Paragraph(chunk1);
////                detail.setAlignment(Element.ALIGN_MIDDLE);
////
////                PdfPTable table1 = new PdfPTable(2);
////                table1.setWidthPercentage(100.0f);
////                table1.setSpacingBefore(4f);
////
////                PdfPCell cell1 = new PdfPCell();
////                PdfPCell cell2 = new PdfPCell();
////                Paragraph FName = new Paragraph(new Chunk("Case Id :"));
////                FName.setAlignment(Element.ALIGN_LEFT);
////                cell1.setMinimumHeight(12f);
////                cell1.setBorder(0);
////                cell1.addElement(FName);
////                cell1.setPaddingTop(-1f);
////
////                Paragraph firstName = new Paragraph(new Chunk(judgement.getCaseId()));
////                cell2.setMinimumHeight(12f);
////                cell2.addElement(firstName);
////                cell2.setPaddingTop(-1f);
////                cell2.setBorder(0);
////
////                table1.addCell(cell1);
////                table1.addCell(cell2);
////
////                PdfPCell cell3 = new PdfPCell();
////                PdfPCell cell4 = new PdfPCell();
////                Paragraph order = new Paragraph(new Chunk("Order Type :"));
////                order.setAlignment(Element.ALIGN_LEFT);
////                cell3.setMinimumHeight(12f);
////                cell3.setBorder(0);
////                cell3.addElement(order);
////                cell3.setPaddingTop(-1f);
////
////                Paragraph ordertype = new Paragraph(new Chunk(judgement.getOrderType()));
////                cell4.setMinimumHeight(12f);
////                cell4.addElement(ordertype);
////                cell4.setPaddingTop(-1f);
////                cell4.setBorder(0);
////
////                table1.addCell(cell3);
////                table1.addCell(cell4);
////
////                PdfPCell cell5 = new PdfPCell();
////                PdfPCell cell6 = new PdfPCell();
////                Paragraph orderd = new Paragraph(new Chunk("Order Date :"));
////                orderd.setAlignment(Element.ALIGN_LEFT);
////                cell5.setMinimumHeight(12f);
////                cell5.setBorder(0);
////                cell5.addElement(orderd);
////                cell5.setPaddingTop(-1f);
////
////                Paragraph orderDate = new Paragraph(new Chunk(judgement.getOrderDate().toString()));
////                cell6.setMinimumHeight(12f);
////                cell6.addElement(orderDate);
////                cell6.setPaddingTop(-1f);
////                cell6.setBorder(0);
////
////                table1.addCell(cell5);
////                table1.addCell(cell6);
////
////                PdfPCell cell7 = new PdfPCell();
////                PdfPCell cell8 = new PdfPCell();
////                Paragraph decision = new Paragraph(new Chunk("Decision status :"));
////                decision.setAlignment(Element.ALIGN_LEFT);
////                cell7.setMinimumHeight(12f);
////                cell7.setBorder(0);
////                cell7.addElement(decision);
////                cell7.setPaddingTop(-1f);
////
////                Paragraph dstatus = new Paragraph(new Chunk(judgement.getDecisionStatus()));
////                cell8.setMinimumHeight(12f);
////                cell8.addElement(dstatus);
////                cell8.setPaddingTop(-1f);
////                cell8.setBorder(0);
////
////                table1.addCell(cell7);
////                table1.addCell(cell8);
////
////                PdfPCell cell9 = new PdfPCell();
////                PdfPCell cell10 = new PdfPCell();
////                Paragraph complain = new Paragraph(new Chunk("Complaince Date :"));
////                complain.setAlignment(Element.ALIGN_LEFT);
////                cell9.setMinimumHeight(12f);
////                cell9.setBorder(0);
////                cell9.addElement(complain);
////                cell9.setPaddingTop(-1f);
////
////                Paragraph complaindate = new Paragraph(new Chunk(judgement.getComplianceStatus()));
////                cell10.setMinimumHeight(12f);
////                cell10.addElement(complaindate);
////                cell10.setPaddingTop(-1f);
////                cell10.setBorder(0);
////
////                table1.addCell(cell9);
////                table1.addCell(cell10);
////
////                PdfPCell cell11 = new PdfPCell();
////                PdfPCell cell12 = new PdfPCell();
////
////                Paragraph revised = new Paragraph(new Chunk("Revised Complaince Date :"));
////                revised.setAlignment(Element.ALIGN_LEFT);
////                cell11.setMinimumHeight(12f);
////                cell11.setBorder(0);
////                cell11.addElement(revised);
////                cell11.setPaddingTop(-1f);
////
////                Paragraph distri = new Paragraph(new Chunk(judgement.getRevisedComplianceDate().toString()));
////                cell12.setMinimumHeight(12f);
////                cell12.addElement(distri);
////                cell12.setPaddingTop(-1f);
////                cell12.setBorder(0);
////
////                table1.addCell(cell11);
////                table1.addCell(cell12);
////
////                PdfPCell cell13 = new PdfPCell();
////                PdfPCell cell14 = new PdfPCell();
////
////                Paragraph override = new Paragraph(new Chunk("Order No Override :"));
////                override.setAlignment(Element.ALIGN_LEFT);
////                cell13.setMinimumHeight(12f);
////                cell13.setBorder(0);
////                cell13.addElement(override);
////                cell13.setPaddingTop(-1f);
////
////                Paragraph stat = new Paragraph(new Chunk(judgement.getOrderNoOverride()));
////                cell14.setMinimumHeight(12f);
////                cell14.addElement(stat);
////                cell14.setPaddingTop(-1f);
////                cell14.setBorder(0);
////
////                table1.addCell(cell13);
////                table1.addCell(cell14);
////
////                PdfPCell cell15 = new PdfPCell();
////                PdfPCell cell16 = new PdfPCell();
////
////                Paragraph reason = new Paragraph(new Chunk("Revised Complaince Reason :"));
////                reason.setAlignment(Element.ALIGN_LEFT);
////                cell15.setMinimumHeight(12f);
////                cell15.setBorder(0);
////                cell15.addElement(reason);
////                cell15.setPaddingTop(-1f);
////
////                Paragraph registration = new Paragraph(new Chunk(judgement.getRevisedComplainceReason()));
////                cell16.setMinimumHeight(12f);
////                cell16.addElement(registration);
////                cell16.setPaddingTop(-1f);
////                cell16.setBorder(0);
////
////                table1.addCell(cell15);
////                table1.addCell(cell16);
////
////                PdfPCell cell17 = new PdfPCell();
////                PdfPCell cell18 = new PdfPCell();
////                Paragraph statusComp = new Paragraph(new Chunk("Complaince status :"));
////                statusComp.setAlignment(Element.ALIGN_LEFT);
////                cell17.setMinimumHeight(12f);
////                cell17.setBorder(0);
////                cell17.addElement(statusComp);
////                cell17.setPaddingTop(-1f);
////
////                Paragraph compdate = new Paragraph(new Chunk(judgement.getDecisionStatus()));
////                cell18.setMinimumHeight(12f);
////                cell18.addElement(compdate);
////                cell18.setPaddingTop(-1f);
////                cell18.setBorder(0);
////
////                table1.addCell(cell17);
////                table1.addCell(cell18);
////
////                PdfPCell cell19 = new PdfPCell();
////                PdfPCell cell20 = new PdfPCell();
////                Paragraph remark = new Paragraph(new Chunk("Remark :"));
////                remark.setAlignment(Element.ALIGN_LEFT);
////                cell19.setMinimumHeight(12f);
////                cell19.setBorder(0);
////                cell19.addElement(remark);
////                cell19.setPaddingTop(-1f);
////
////                Paragraph remak = new Paragraph(new Chunk(judgement.getRemarks()));
////                cell20.setMinimumHeight(12f);
////                cell20.addElement(remak);
////                cell20.setPaddingTop(-1f);
////                cell20.setBorder(0);
////
////                table1.addCell(cell19);
////                table1.addCell(cell20);
////
////                PdfPCell cell21 = new PdfPCell();
////                PdfPCell cell22 = new PdfPCell();
////                Paragraph stats = new Paragraph(new Chunk("Status:"));
////                stats.setAlignment(Element.ALIGN_LEFT);
////                cell21.setMinimumHeight(12f);
////                cell21.setBorder(0);
////                cell21.addElement(stats);
////                cell21.setPaddingTop(-1f);
////
////                Paragraph nonPolicyMatter = new Paragraph(new Chunk(judgement.getStatus().toString()));
////                cell22.setMinimumHeight(12f);
////                cell22.addElement(nonPolicyMatter);
////                cell22.setPaddingTop(-1f);
////                cell22.setBorder(0);
////
////                table1.addCell(cell21);
////                table1.addCell(cell22);
////
////                PdfPCell cell23 = new PdfPCell();
////                PdfPCell cell24 = new PdfPCell();
////                Paragraph Addetail = new Paragraph(new Chunk("Additional Details :"));
////                Addetail.setAlignment(Element.ALIGN_LEFT);
////                cell23.setMinimumHeight(12f);
////                cell23.setBorder(0);
////                cell23.addElement(Addetail);
////                cell23.setPaddingTop(-1f);
////
////                Paragraph additional = new Paragraph(new Chunk(judgement.getAdditionalDetails().toString()));
////                cell24.setMinimumHeight(12f);
////                cell24.addElement(additional);
////                cell24.setPaddingTop(-1f);
////                cell24.setBorder(0);
////
////                table1.addCell(cell23);
////                table1.addCell(cell24);
////
////                try {
////                    pdfDoc.add(detail);
////                    pdfDoc.add(table1);
////                } catch (DocumentException e) {
////                    throw new RuntimeException(e);
////                }
////            });
////            pdfDoc.close();
////            bis = new ByteArrayInputStream(out.toByteArray());
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
////        return bis;
////    }
//
////    public ChildCase addChildCases(ChildCaseRequest childCaseRequest) {
////        if (StringUtils.isNotBlank(childCaseRequest.getChildCase().getCaseHierarchy().toString())) {
////            if (childCaseRequest.getChildCase().getCaseHierarchy().equals(CaseHierarchy.INDEPENDENT)) {
////                if (StringUtils.isBlank(childCaseRequest.getChildCase().getParentCaseId())) {
////                    throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
////                            "ParentCaseId is mandatory to create Independent child case [ " + childCaseRequest.getChildCase()
////                                    .getParentCaseId() + " ]");
////                }
////                List<String> caseIds = caseRepository.getCaseIdsByParentCaseId(childCaseRequest.getChildCase().getParentCaseId());
////                List<CaseIds> caseIdsList = new ArrayList<>();
////                caseIds.forEach(id -> {
////                    CaseIds caseIds1 = new CaseIds();
////                    caseIds1.setId(id);
////                    caseIdsList.add(caseIds1);
////                });
////                childCaseRequest.getChildCase().setCaseIds(caseIdsList);
////                childCaseRequest.getChildCase().setParentCaseId(null);
////            } else if (childCaseRequest.getChildCase().getCaseHierarchy().equals(CaseHierarchy.CHILD)) {
////                if (!StringUtils.isNotBlank(childCaseRequest.getChildCase().getParentCaseId())) {
////                    throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
////                            "ParentCaseId is mandatory to create child case [ " + childCaseRequest.getChildCase().getParentCaseId() + " ]");
////                } else if (Objects.isNull(childCaseRequest.getChildCase().getCaseIds())) {
////                    throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
////                            "CaseIds  are mandatory to create child case [ " + childCaseRequest.getChildCase().getCaseIds() + " ]");
////                }
////            } else if (childCaseRequest.getChildCase().getCaseHierarchy().equals(CaseHierarchy.PARENT)) {
////                throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "We are not considering the [ " + CaseHierarchy.PARENT + " ] ");
////            }
////        } else {
////            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR,
////                    "CaseHierarchy is mandatory to create child case [ " + childCaseRequest.getChildCase().getCaseHierarchy() + " ]");
////        }
////        producer.push(ilmsConfiguration.getUpdateChildCaseTopic(), childCaseRequest);
////        return childCaseRequest.getChildCase();
////    }
//}
//
