package com.example.recover.repository;

import com.example.recover.entity.ReceivingDamage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ReceivingDamageRepository extends JpaRepository<ReceivingDamage, Long> {

    List<ReceivingDamage> findByReceivingOrderItemId(Long itemId);

    @Modifying
    @Query("DELETE FROM ReceivingDamage d WHERE d.receivingOrderItemId = :itemId")
    void deleteByReceivingOrderItemIdDirect(@Param("itemId") Long itemId);
}
