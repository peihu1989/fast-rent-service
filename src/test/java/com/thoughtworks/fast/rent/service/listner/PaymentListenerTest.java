package com.thoughtworks.fast.rent.service.listner;

import com.thoughtworks.fast.rent.FastRentServiceApplicationTests;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

import static com.thoughtworks.fast.rent.enums.Constant.PENDING_PAYMENT_TOPIC_NAME;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

class PaymentListenerTest extends FastRentServiceApplicationTests {

    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;


    @SneakyThrows
    @Test
    @DisplayName("当监听到topic:fast-rent-service.pending-payment中有新的消息时,应该调用paymentService的getPaymentResult方法获取支付状态")
    void listenerTest1() {
        // given
        final String CONTRACT_ID = "C-0000000001";

        // when
        kafkaTemplate.send(PENDING_PAYMENT_TOPIC_NAME, CONTRACT_ID, CONTRACT_ID).get();

        //then
        verify(paymentService, timeout(5000))
                .getPaymentResult(argThat(contractId -> contractId.equals(CONTRACT_ID)));
    }
}