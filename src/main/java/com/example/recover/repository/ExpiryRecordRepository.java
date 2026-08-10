package com.example.recover.repository;

import com.example.recover.entity.ExpiryRecord;
import com.example.recover.utils.ConfirmStatus;
import com.example.recover.utils.ProcessStatus;
import com.example.recover.vo.ExpiryRecordVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;


@Repository
public interface ExpiryRecordRepository extends JpaRepository<ExpiryRecord, Long> {

    boolean existsByBarcodeAndExpiryDate(String barcode, LocalDate expiryDate);

    List<ExpiryRecord> findByBarcodeIn(List<String> barcodeList);

    @Query("""
    SELECT new com.example.recover.vo.ExpiryRecordVO(
        e.id,
        e.barcode,
        e.expiryDate,
        e.stock,
        e.confirmStatus,
        e.confirmTime,
        e.processStatus,
        e.processTime,
        e.processRemark,
        e.category,
        e.productName,
        p.imgUrl
    )
    FROM ExpiryRecord e
    LEFT JOIN Product p
        ON p.barcode = e.barcode
    WHERE (:barcode IS NULL
           OR e.barcode LIKE CONCAT(:barcode, '%'))
      AND (:category IS NULL OR e.category = :category)
      AND (:confirmStatus IS NULL OR e.confirmStatus = :confirmStatus)
      AND (:processStatus IS NULL OR e.processStatus = :processStatus)
      AND (:expireDateFrom IS NULL OR e.expiryDate >= :expireDateFrom)
      AND (:expireDateTo IS NULL OR e.expiryDate <= :expireDateTo)
""")
    Page<ExpiryRecordVO> findPage(
            @Param("expireDateFrom") LocalDate expireDateFrom,
            @Param("expireDateTo") LocalDate expireDateTo,
            @Param("confirmStatus") ConfirmStatus confirmStatus,
            @Param("processStatus") ProcessStatus processStatus,
            @Param("category") String category,
            @Param("barcode") String barcode,
            Pageable pageable);
}
