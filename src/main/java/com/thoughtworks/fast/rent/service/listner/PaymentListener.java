package com.thoughtworks.fast.rent.service.listner;

import com.thoughtworks.fast.rent.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.thoughtworks.fast.rent.enums.Constant.PENDING_PAYMENT;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentListener {

    private final PaymentService paymentService;

    @KafkaListener(topics = PENDING_PAYMENT,
            id = "fast-rent-service.payment-status-query.listener",
            properties = {
                    "spring.json.use.type.headers=false",
                    "spring.json.value.default.type=com.thoughtworks.fast.rent.model.message.PaymentMessage"
            })
    public void listenToGetPaymentStatus(String message) {
        log.info("PaymentService.listenToGetPaymentStatus is {}", message);
        paymentService.getPaymentResult(message);
    }

}
