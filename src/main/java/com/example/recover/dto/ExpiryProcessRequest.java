package com.example.recover.dto;

import com.example.recover.utils.ProcessStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ExpiryProcessRequest {

    private Long id;

    @NotNull
    private ProcessStatus processStatus;

    private String processRemark;


}
