package com.example.recover.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SupplierRequest {

    private Long id;
    @NotBlank(message = "supplierName不能为空")
    private String supplierName;

}
