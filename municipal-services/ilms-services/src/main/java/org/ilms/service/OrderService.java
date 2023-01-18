package org.ilms.service;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.ilms.configs.ILMSConfiguration;
import org.ilms.producer.Producer;
import org.ilms.repository.HearingRepository;
import org.ilms.repository.OrderRepository;
import org.ilms.util.ILMSErrorConstants;
import org.ilms.validator.OrderValidator;
import org.ilms.web.model.*;
import org.ilms.web.model.enums.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Service
public class OrderService {
    @Autowired
    OrderEnrichmentService orderEnrichmentService;

    @Autowired
    Producer producer;

    @Autowired
    ILMSConfiguration ilmsConfiguration;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderValidator orderValidator;

    @Autowired
    HearingRepository hearingRepository;

    public Order create(OrderRequest orderRequest) {
        HearingSearchResponse hearingResponse = null;
        HearingSearchCriteria criteria = HearingSearchCriteria.builder()
                .caseId(Collections.singletonList(orderRequest.getOrder().getCaseId())).build();
        hearingResponse = hearingRepository.getHearingDetails(criteria);
        if (!hearingResponse.getHearingList().isEmpty()) {
            orderRequest.getOrder().setStatus(Status.ACTIVE);
            orderValidator.createValidator(orderRequest);
            orderEnrichmentService.enrichOrderCreateRequest(orderRequest);
            producer.push(ilmsConfiguration.getCreateJudgementTopic(), orderRequest);
        } else {
            throw new CustomException(ILMSErrorConstants.HEARING_NOT_AVAILABLE, "Hearing is not Available for this Judgement");
        }
        return orderRequest.getOrder();
    }

    public OrderSearchResponse OrderSearch(OrderSearchCriteria criteria, RequestInfo requestInfo) {
        List<Order> Orders = new LinkedList<>();
        OrderSearchResponse orderResponse = null;
        orderResponse = orderRepository.getOrderData(criteria);
        Orders = orderResponse.getOrderList();
        if (!Orders.isEmpty()) {
            orderEnrichmentService.enrichOrderSearch();
        } else {
            throw new CustomException(ILMSErrorConstants.JUDGEMENT_NOT_AVAILABLE, "Judgement is not Available");
        }
        return orderResponse;
    }

    public Order updateOrder(OrderRequest orderRequest) {
        if (orderRequest.getOrder().getId() != null) {
            OrderSearchCriteria criteria = OrderSearchCriteria.builder()
                    .id(Collections.singletonList(orderRequest.getOrder().getId())).build();
            OrderSearchResponse orderResponse = orderRepository.getOrderData(criteria);
            if (!orderResponse.getOrderList().isEmpty()) {
                List<Order> orders = orderResponse.getOrderList();
                Order oldOrder = orders.get(0);
                OrderRequest finalRequest = orderRepository.getMappedData(orderRequest, oldOrder);
                orderValidator.updateValidator(finalRequest.getOrder(), orderRequest);
                producer.push(ilmsConfiguration.getUpdateJudgementTopic(), finalRequest);
            } else {
                throw new CustomException(ILMSErrorConstants.JUDGEMENT_NOT_AVAILABLE, "Judgement is not Available");
            }
        } else {
            throw new CustomException(ILMSErrorConstants.INVALID_TYPE_ERROR, "Id is mandatory");
        }
        return orderRequest.getOrder();
    }
}
