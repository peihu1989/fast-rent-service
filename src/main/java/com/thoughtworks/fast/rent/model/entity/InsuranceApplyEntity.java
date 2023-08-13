package com.thoughtworks.fast.rent.model.entity;

import com.thoughtworks.fast.rent.enums.InsuranceApplyStatus;
import com.thoughtworks.fast.rent.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Table(name = "insurance_apply")
@EntityListeners(AuditingEntityListener.class)
public class InsuranceApplyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "apply_id",unique = true)
    private String applyId;

    @Column(name = "insurance_contract_id")
    private String insuranceContractId;

    @Column(name = "vehicle_id")
    private String vehicleId;

    @Column
    private String address;

    @Column
    @Enumerated(value = EnumType.STRING)
    private InsuranceApplyStatus status;

    @Column
    private String message;

    @Column
    @CreatedDate
    private LocalDateTime createAt;

    @Column
    @LastModifiedDate
    private LocalDateTime updateAt;

}
