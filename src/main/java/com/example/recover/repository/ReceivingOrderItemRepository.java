package com.example.recover.repository;

import com.example.recover.entity.ReceivingOrderItem;
import com.example.recover.vo.OrderItemListBarcodeVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ReceivingOrderItemRepository extends JpaRepository<ReceivingOrderItem, Long>,
        JpaSpecificationExecutor<ReceivingOrderItem> {

    @Modifying
    @Query(value = "DELETE FROM receiving_order_item WHERE receiving_order_id = :orderId", nativeQuery = true)
    void deleteByOrderId(Long orderId);

    List<ReceivingOrderItem> findByReceivingOrderId(Long receivingOrderId);

    @Query("""
            SELECT new com.example.recover.vo.OrderItemListBarcodeVO(
                 s.supplierName,
                 o.receiveDate,
                 o.progress,
                 i.id,
                 i.productName,
                 i.orderQty,
                 i.total,
                 i.barcode,
                 i.expiryDate,
                 i.checkStatus
             )
             FROM ReceivingOrderItem i
             LEFT JOIN ReceivingOrder o ON o.id = i.receivingOrderId
             LEFT JOIN Supplier s ON s.id = o.supplierId
             WHERE i.barcode LIKE CONCAT(:barcode, '%')
             ORDER BY o.receiveDate DESC
            """)
    Page<OrderItemListBarcodeVO> findAllByBarcode(@Param("barcode") String barcode, Pageable pageable);
}
