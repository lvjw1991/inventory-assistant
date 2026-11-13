package com.example.recover.dto;

import com.example.recover.utils.NetUnitType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PriceTagRequest {

    private Long id;

    @NotBlank(message = "Name不能为空")
    private String name;
    private BigDecimal salePrice;
    @NotBlank(message = "Name不能为空")
    private String nameFi;
    private BigDecimal netContent;
    private NetUnitType netUnit;

}
