package com.thoughtworks.fast.rent.service;


import com.thoughtworks.fast.rent.infrastructure.client.PaymentClient;
import com.thoughtworks.fast.rent.infrastructure.repository.PaymentRepository;
import com.thoughtworks.fast.rent.mapper.PaymentMapper;
import com.thoughtworks.fast.rent.model.dto.CommonResult;
import com.thoughtworks.fast.rent.model.entity.PaymentEntity;
import com.thoughtworks.fast.rent.model.thirdparty.request.PaymentRequest;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.thoughtworks.fast.rent.enums.Constant.PENDING_PAYMENT_TOPIC_NAME;
import static com.thoughtworks.fast.rent.enums.PaymentStatus.FAILED;
import static com.thoughtworks.fast.rent.enums.PaymentStatus.PENDING;
import static org.springframework.http.HttpStatus.BAD_GATEWAY;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentClient paymentClient;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final PaymentMapper paymentMapper = Mappers.getMapper(PaymentMapper.class);

    public CommonResult pay(PaymentRequest paymentRequest) {

        var paymentEntity = paymentMapper.toEntity(paymentRequest);
        try {
            var payResult = paymentClient.pay(paymentRequest);
            paymentEntity.setStatus(payResult.getPaymentStatus())
                    .setMessage(payResult.getMessage());
        } catch (FeignException exception) {
            if ( isClientError(exception.status())) {
                paymentEntity.setStatus(FAILED).setMessage("支付失败,请重新支付");
            } else if(serverUnavailable(exception.status())){
                paymentEntity.setStatus(FAILED).setMessage("支付服务异常,请重新支付");
            } else{
                paymentEntity.setStatus(PENDING).setMessage("支付中,请稍后查询支付状态");
                kafkaTemplate.send(PENDING_PAYMENT_TOPIC_NAME, paymentRequest.getContractId(), paymentRequest.getContractId());
            }
        }
        paymentRepository.save(paymentEntity);
        return CommonResult.builder()
                .code(paymentEntity.getStatus().name())
                .message(paymentEntity.getMessage())
                .build();
    }

    public void getPaymentResult(String contractId) {
        final PaymentEntity paymentEntity = paymentRepository.findByContractId(contractId);
        try {
            var payResult = paymentClient.getPayResult(contractId);
            paymentEntity.setStatus(payResult.getPaymentStatus())
                    .setMessage(payResult.getMessage());
            paymentRepository.save(paymentEntity);
        } catch (FeignException exception) {
            log.warn(" call feign to get payment result failed", exception);
            kafkaTemplate.send(PENDING_PAYMENT_TOPIC_NAME, contractId, contractId);
        }
    }

    private static boolean isClientError(int status) {
        return status >= 400 && status < 500;
    }

    private static boolean serverUnavailable(int status) {
        return status == BAD_GATEWAY.value() || status == SERVICE_UNAVAILABLE.value();
    }
}
