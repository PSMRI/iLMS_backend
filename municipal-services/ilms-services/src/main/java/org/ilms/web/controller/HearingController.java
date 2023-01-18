package org.ilms.web.controller;

import org.ilms.service.HearingService;
import org.ilms.util.ResponseInfoFactory;
import org.ilms.web.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/hearing")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class HearingController {
    @Autowired
    private ResponseInfoFactory responseInfoFactory;

    @Autowired
    private HearingService hearingService;

    @PostMapping(value = "/_create")
    public ResponseEntity<HearingResponse> create(@Valid @RequestBody HearingRequest hearingRequest) {
        Hearing hearing = hearingService.create(hearingRequest);
        HearingResponse response = HearingResponse.builder().hearing(hearing).responseInfo(
                responseInfoFactory.createResponseInfoFromRequestInfo(hearingRequest.getRequestInfo(), true)).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/_search")
    public ResponseEntity<HearingSearchResponse> search(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
                                                        @Valid @ModelAttribute HearingSearchCriteria criteria) {
        HearingSearchResponse response = hearingService.hearingSearch(criteria, requestInfoWrapper.getRequestInfo());
        response.setResponseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/_update")
    public ResponseEntity<HearingResponse> update(@Valid @RequestBody HearingRequest hearingDetailsRequest) {
        Hearing hearingDetails = hearingService.update(hearingDetailsRequest);
        HearingResponse response = HearingResponse.builder().hearing(hearingDetails).responseInfo(
                responseInfoFactory.createResponseInfoFromRequestInfo(hearingDetailsRequest.getRequestInfo(), true)).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
