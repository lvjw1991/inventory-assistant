package com.example.recover.repository;

import com.example.recover.entity.InventoryStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockRepository extends JpaRepository<InventoryStock, Long> {

}
