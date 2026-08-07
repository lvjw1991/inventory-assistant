package com.example.recover.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Entity
@Table(name = "receiving_order_item")
@Data
@EqualsAndHashCode(callSuper = true)  // Lombok 继承需加这个
public class ReceivingOrderItem extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long receivingOrderId;
    private String supplierCode;
    private String productName;
    private String barcode;
    private Integer orderQty;
    private Integer actualQty;
    private Integer total;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String expiryDate;
    private BigDecimal unitPrice;
    private String category;
    private String sugar;

}
