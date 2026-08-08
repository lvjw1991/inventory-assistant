package com.example.recover.entity;

import com.example.recover.utils.OrderProcess;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Entity
@Table(name = "receiving_order")
@Data
@EqualsAndHashCode(callSuper = true)  // Lombok 继承需加这个
public class ReceivingOrder extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long supplierId;
    private String invoiceNo;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate receiveDate;
    private String sourceFile;
    @Enumerated(EnumType.STRING)
    private OrderProcess progress;

}
