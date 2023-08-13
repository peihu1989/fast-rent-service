package com.thoughtworks.fast.rent.mapper;

import com.thoughtworks.fast.rent.enums.InsuranceApplyStatus;
import com.thoughtworks.fast.rent.model.dto.insurance.InsuranceApply;
import com.thoughtworks.fast.rent.model.entity.InsuranceApplyEntity;
import com.thoughtworks.fast.rent.model.thirdparty.request.InsuranceRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.UUID;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {InsuranceApplyStatus.class, UUID.class})
public interface InsuranceMapper {

    @Mapping(target = "insuranceContractId", source = "id")
    @Mapping(target = "applyId", expression = "java(UUID.randomUUID().toString())")
    InsuranceRequest toRequest(String id, InsuranceApply insuranceApply);


    @Mapping(target = "status", expression = "java(InsuranceApplyStatus.SENDING)")
    InsuranceApplyEntity toEntity(InsuranceRequest paymentRequest);

}
