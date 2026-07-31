package com.example.recover.repository;

import com.example.recover.entity.SupplierProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SupplierProductRepository extends JpaRepository<SupplierProduct, Long> {

    boolean existsBySupplierIdAndSupplierCodeAndBarcode(Long supplierId, String supplierCode, String barcode);
}
