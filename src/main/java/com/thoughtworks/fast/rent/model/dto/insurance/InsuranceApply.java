package com.thoughtworks.fast.rent.model.dto.insurance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InsuranceApply {

    private String vehicleId;

    private String address;


}
