package com.thoughtworks.fast.rent.infrastructure.repository;

import com.thoughtworks.fast.rent.enums.InsuranceApplyStatus;
import com.thoughtworks.fast.rent.model.entity.InsuranceApplyEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
class InsuranceApplyRepositoryTest {

    @Autowired
    private InsuranceApplyRepository insuranceApplyRepository;

    @AfterEach
    void tearDown() {
        insuranceApplyRepository.deleteAll();
    }


    @Test
    @DisplayName("当调用insuranceApplyRepository保存一条数据,查询出来的结果除了自增ID以外其他的字段应该与保存的数据一致")
    void findByApplyId() {
        //given
        InsuranceApplyEntity original = InsuranceApplyEntity.builder()
                .insuranceContractId("IN-001")
                .applyId("applyId01")
                .status(InsuranceApplyStatus.SEND)
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .build();

        insuranceApplyRepository.save(original);

        // when
        InsuranceApplyEntity result = insuranceApplyRepository.findByApplyId(original.getApplyId());

        // then
        assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(original);
    }
}