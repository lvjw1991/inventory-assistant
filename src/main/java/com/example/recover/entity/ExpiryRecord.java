package com.example.recover.entity;

import com.example.recover.utils.ProcessMethod;
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
    private LocalDate expiredDate;
    private Long stock;

    private Boolean confirmStatus;
    private LocalDateTime confirmedTime;

    private Boolean processStatus;

    @Enumerated(EnumType.STRING)
    private ProcessMethod processMethod;
    private LocalDateTime processTime;
    private String process_remark;

}
