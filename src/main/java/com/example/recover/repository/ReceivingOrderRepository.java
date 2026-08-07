package com.example.recover.repository;

import com.example.recover.entity.ReceivingOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ReceivingOrderRepository extends JpaRepository<ReceivingOrder, Long> {

}
