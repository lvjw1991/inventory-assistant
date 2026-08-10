package com.example.recover.entity;

import com.example.recover.utils.ConfirmStatus;
import com.example.recover.utils.ProcessStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "expiry_record")
@Data
@EqualsAndHashCode(callSuper = true)  // Lombok 继承需加这个
public class ExpiryRecord extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String barcode;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiryDate;
    private Integer stock;

    @Enumerated(EnumType.STRING)
    private ConfirmStatus confirmStatus;
    private LocalDateTime confirmTime;

    @Enumerated(EnumType.STRING)
    private ProcessStatus processStatus;
    private LocalDateTime processTime;
    private String processRemark;
    private String category;
    private String productName;

}
