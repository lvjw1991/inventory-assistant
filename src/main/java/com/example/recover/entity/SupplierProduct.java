package com.example.recover.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "supplier_product")
@Data
@EqualsAndHashCode(callSuper = true)  // Lombok 继承需加这个
public class SupplierProduct extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long supplierId;
    private String supplierCode;
    private String barcode;

}
