package com.example.recover.repository;

import com.example.recover.entity.ReceivingOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
public interface ReceivingOrderItemRepository extends JpaRepository<ReceivingOrderItem, Long>,
        JpaSpecificationExecutor<ReceivingOrderItem> {

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM receiving_order_item WHERE receiving_order_id = :orderId", nativeQuery = true)
    void deleteByOrderId(Long orderId);


}
