package com.example.recover.entity;

import com.example.recover.utils.ProcessMethod;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

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

    private Boolean confirmStatus;
    private LocalDateTime confirmTime;

    private Boolean processStatus;

    @Enumerated(EnumType.STRING)
    private ProcessMethod processMethod;
    private LocalDateTime processTime;
    private String processRemark;
    private String category;
    private String productName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "barcode", referencedColumnName = "barcode",
            insertable = false, updatable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @NotFound(action = NotFoundAction.IGNORE)
    private Product product;

}
