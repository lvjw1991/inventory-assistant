package com.example.recover.vo;

import com.example.recover.utils.ConfirmStatus;
import com.example.recover.utils.ProcessStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpiryRecordVO {

    private Long id;

    private String barcode;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiryDate;

    private Integer stock;

    @Schema(description = "确认方式", example = "CONFIRM,NOT_FOUND")
    private ConfirmStatus confirmStatus;

    private LocalDateTime confirmTime;

    @Schema(description = "处理方式", example = "NORMAL,PROMOTE,DAMAGE")
    private ProcessStatus processStatus;

    private LocalDateTime processTime;

    private String processRemark;

    private String category;

    private String productName;

    private String imgUrl;


}
