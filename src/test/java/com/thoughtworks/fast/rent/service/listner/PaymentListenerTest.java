package com.thoughtworks.fast.rent.service.listner;

import com.thoughtworks.fast.rent.FastRentServiceApplicationTests;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

class PaymentListenerTest extends FastRentServiceApplicationTests {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    //@Captor
    //private ArgumentCaptor<LearningManagementRequest> messageCaptor;
    //
    //@SneakyThrows
    //@Test
    //void should_resend_to_learning_management_system_when_listen_to_message_from_topic() {
    //    // given
    //    var message = DataSynchronizationMessage.builder()
    //            .contractId(1L)
    //            .dataId(1L)
    //            .studentAccount("Lucy")
    //            .subject("Math")
    //            .subjectName("李永乐老师讲XXX")
    //            .duration(30)
    //            .unit("min").build();
    //
    //    // when
    //    kafkaTemplate.send(TOPIC_NAME, message.getDataId(), message).get();
    //
    //    //then
    //    verify(dataSynchronizationService, timeout(5000))
    //            .sendData(messageCaptor.capture());
    //    assertThat(messageCaptor.getValue())
    //            .usingRecursiveComparison()
    //            .isEqualTo(message);
    //}
}