package com.example.recover.repository;

import com.example.recover.entity.ExpiryRecord;
import com.example.recover.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

}
