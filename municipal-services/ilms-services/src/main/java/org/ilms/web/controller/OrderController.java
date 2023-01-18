package org.ilms.web.controller;

import org.egov.common.contract.response.ResponseInfo;
import org.ilms.service.OrderService;
import org.ilms.util.ResponseInfoFactory;
import org.ilms.web.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/order")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private ResponseInfoFactory responseInfoFactory;

    @PostMapping(value = "/_create")
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody OrderRequest orderRequest) {
        Order order = orderService.create(orderRequest);
        OrderResponse response = OrderResponse.builder().order(order).responseInfo(
                responseInfoFactory.createResponseInfoFromRequestInfo(orderRequest.getRequestInfo(), true)).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/_search")
    public ResponseEntity<OrderSearchResponse> search(@Valid @RequestBody OrderRequest orderRequest,
                                                      @Valid @ModelAttribute OrderSearchCriteria criteria) {
        OrderSearchResponse response = orderService.OrderSearch(criteria, orderRequest.getRequestInfo());
        response.setResponseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(orderRequest.getRequestInfo(), true));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/_update")
    public ResponseEntity<OrderResponse> update(@Valid @RequestBody OrderRequest orderRequest) {
        Order order = orderService.updateOrder(orderRequest);
        ResponseInfo resInfo = responseInfoFactory.createResponseInfoFromRequestInfo(orderRequest.getRequestInfo(), true);
        OrderResponse response = OrderResponse.builder().order(order).responseInfo(resInfo).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
