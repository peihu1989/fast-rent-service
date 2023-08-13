package com.thoughtworks.fast.rent.facade.controller;


import com.thoughtworks.fast.rent.mapper.InsuranceMapper;
import com.thoughtworks.fast.rent.model.dto.insurance.InsuranceApply;
import com.thoughtworks.fast.rent.model.dto.CommonResult;
import com.thoughtworks.fast.rent.service.InsuranceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class InsuranceController {

    private final InsuranceService insuranceService;

    private final InsuranceMapper insuranceMapper = Mappers.getMapper(InsuranceMapper.class);

    @PostMapping(value = "/insurance-contracts/{id}/accident", produces = "application/json;charset=UTF-8")
    public ResponseEntity<CommonResult> insuranceApply(@PathVariable("id") String id,
            @RequestBody InsuranceApply insuranceApply) {
        insuranceService.apply(insuranceMapper.toRequest(id, insuranceApply));
        return ResponseEntity.ok(CommonResult.defaultInstance());
    }
}
