package com.thoughtworks.fast.rent.infrastructure.repository;


import com.thoughtworks.fast.rent.model.entity.InsuranceApplyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InsuranceApplyRepository extends JpaRepository<InsuranceApplyEntity, Long> {

    InsuranceApplyEntity findByApplyId(String applyId);

}
