package com.example.recover.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCategoryItemVO {

    private Long id;
    private String barcode;
    private String name;
    private String category;
    private String imgUrl;
    private BigDecimal purchasePrice;
}
