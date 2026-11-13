package com.example.recover.entity;

import com.example.recover.utils.NetUnitType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Entity
@Table(name = "product")
@Data
@EqualsAndHashCode(callSuper = true)  // Lombok 继承需加这个
public class Product extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String barcode;
    private String imgUrl;
    private String category;
    private BigDecimal purchasePrice;

    private BigDecimal salePrice;
    private String nameFi;
    private BigDecimal netContent;
    @Enumerated(EnumType.STRING)
    private NetUnitType netUnit;
}
