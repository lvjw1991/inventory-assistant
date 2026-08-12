package com.example.recover.dto;

import com.example.recover.utils.ConfirmStatus;
import com.example.recover.utils.ProcessStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class RecordMonthlyQuery {

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate expireDateFrom;
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate expireDateTo;
    private ConfirmStatus confirmStatus;
    private ProcessStatus processStatus;
    private String category;

}
