package com.example.recover.repository;

import com.example.recover.entity.ReceivingDamage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ReceivingDamageRepository extends JpaRepository<ReceivingDamage, Long> {

    List<ReceivingDamage> findByReceivingOrderItemId(Long itemId);
}
