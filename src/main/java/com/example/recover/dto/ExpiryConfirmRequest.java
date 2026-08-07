package com.example.recover.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ExpiryConfirmRequest {

    private Long id;

    @NotNull
    private Integer stock;


}
