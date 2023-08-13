package com.thoughtworks.fast.rent.infrastructure.repository;

import com.thoughtworks.fast.rent.enums.PaymentStatus;
import com.thoughtworks.fast.rent.model.entity.PaymentEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @AfterEach
    void tearDown() {
        paymentRepository.deleteAll();
    }

    @Test
    @DisplayName("当调用paymentRepository保存一条数据,查询出来的结果除了自增ID以外其他的字段应该与保存的数据一致")
    void should_get_correct_payment_entity_data_when_retrieve_from_database() {
        //given
        PaymentEntity original = PaymentEntity.builder()
                .contractId("C-00000001")
                .amount(BigDecimal.ONE)
                .status(PaymentStatus.SUCCESS)
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .build();

        paymentRepository.save(original);

        // when
        PaymentEntity result = paymentRepository.findByContractId(original.getContractId());

        // then
        assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(original);
    }
}