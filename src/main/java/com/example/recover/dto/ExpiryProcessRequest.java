package com.example.recover.dto;

import com.example.recover.utils.ProcessMethod;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ExpiryProcessRequest {

    private Long id;

    @NotNull
    private ProcessMethod processMethod;

    private String processRemark;


}
