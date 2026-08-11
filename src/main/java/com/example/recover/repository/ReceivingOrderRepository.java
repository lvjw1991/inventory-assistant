package com.example.recover.repository;

import com.example.recover.entity.ReceivingOrder;
import com.example.recover.vo.ReceivingOrderVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;


@Repository
public interface ReceivingOrderRepository extends JpaRepository<ReceivingOrder, Long> {

    @Query("""
    SELECT new com.example.recover.vo.ReceivingOrderVO(
        r.id,
        r.supplierId,
        r.number,
        r.receiveDate,
        r.progress,
        r.temperature,
        r.transport,
        s.supplierName
    )
    FROM ReceivingOrder r
    LEFT JOIN Supplier s
        ON s.id = r.supplierId
    WHERE (:supplierId IS NULL OR r.supplierId = :supplierId)
      AND (:startDate IS NULL OR r.receiveDate >= :startDate)
      AND (:endDate IS NULL OR r.receiveDate <= :endDate)
""")
    Page<ReceivingOrderVO> findPage(
            @Param("supplierId") Long supplierId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );

}
