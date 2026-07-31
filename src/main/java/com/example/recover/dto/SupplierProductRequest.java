package com.example.recover.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SupplierProductRequest {

    @NotNull(message = "supplierId不能为空")
    private Long supplierId;

    @NotBlank(message = "supplierCode不能为空")
    private String supplierCode;

    @NotBlank(message = "barcode不能为空")
    private String barcode;

}
