package com.example.recover.vo;

import com.example.recover.utils.NetUnitType;
import lombok.Data;


import java.math.BigDecimal;

@Data
public class PriceTagVO {

    private String name;
    private String barcode;
    private String imgUrl;
    private BigDecimal salePrice;
    private String nameFi;
    private BigDecimal netContent;
    private NetUnitType netUnit;
    private BigDecimal unitPrice;

}
