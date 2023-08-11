package com.thoughtworks.fast.rent.facade.controller;


import com.thoughtworks.fast.rent.mapper.PaymentMapper;
import com.thoughtworks.fast.rent.model.dto.PaymentInfo;
import com.thoughtworks.fast.rent.model.dto.PaymentResult;
import com.thoughtworks.fast.rent.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentMapper paymentMapper = Mappers.getMapper(PaymentMapper.class);

    @PostMapping(value = "/rent-contracts/{cid}/rental-payment", produces = "application/json;charset=UTF-8")
    public ResponseEntity<PaymentResult> confirmation(@PathVariable("cid") String cid,
            @RequestBody PaymentInfo paymentInfo) {
        var paymentRequest = paymentMapper.toRequest(cid, paymentInfo);
        return ResponseEntity.ok(paymentService.pay(paymentRequest));

    }
}
