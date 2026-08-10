package com.example.recover.dto;

import com.example.recover.utils.ConfirmStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ExpiryConfirmRequest {

    private Long id;

    @NotNull
    private Integer stock;

    @NotNull
    private ConfirmStatus confirmStatus;


}
