package com.thoughtworks.fast.rent.infrastructure.repository;


import com.thoughtworks.fast.rent.model.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {

    PaymentEntity findByContractId(String contractId);

}
