package com.example.recover.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "receiving_order")
@Data
@EqualsAndHashCode(callSuper = true)  // Lombok 继承需加这个
public class ReceivingOrder extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderNO;
    private String supplierId;
    private String invoiceNO;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String receiveDate;
    private String sourceFile;

}
