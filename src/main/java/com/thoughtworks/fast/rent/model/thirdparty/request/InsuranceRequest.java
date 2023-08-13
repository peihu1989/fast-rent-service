package com.thoughtworks.fast.rent.model.thirdparty.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class InsuranceRequest {

    private String insuranceContractId;

    private String applyId;

    private String vehicleId;

    private String address;

}
