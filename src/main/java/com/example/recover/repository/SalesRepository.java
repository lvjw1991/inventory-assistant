package com.example.recover.repository;

import com.example.recover.entity.SalesMonthlyAvg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesRepository extends JpaRepository<SalesMonthlyAvg, Long> {

}
