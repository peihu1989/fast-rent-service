package com.thoughtworks.fast.rent.mapper;

import com.thoughtworks.fast.rent.enums.PaymentStatus;
import com.thoughtworks.fast.rent.model.dto.payment.PaymentInfo;
import com.thoughtworks.fast.rent.model.entity.PaymentEntity;
import com.thoughtworks.fast.rent.model.thirdparty.request.PaymentRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {PaymentStatus.class})
public interface PaymentMapper {

    PaymentRequest toRequest(String contractId, PaymentInfo paymentInfo);


    @Mapping(target = "status", expression = "java(PaymentStatus.PENDING)")
    PaymentEntity toEntity(PaymentRequest paymentRequest);

}
