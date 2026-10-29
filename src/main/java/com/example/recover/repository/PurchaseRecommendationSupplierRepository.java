package com.example.recover.repository;

import com.example.recover.dto.SupplierOptionProjection;
import com.example.recover.entity.SupplierProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface PurchaseRecommendationSupplierRepository
        extends JpaRepository<SupplierProduct, Long> {

    @Query(value = """
        SELECT
            sp.barcode AS barcode,
            s.supplier_name AS supplierName,
            sp.supplier_code AS supplierCode
        FROM supplier_product sp
        JOIN supplier s
            ON s.id = sp.supplier_id
        WHERE 1=1
          AND sp.barcode IN (:barcodes)
        ORDER BY sp.barcode, s.supplier_name
        """, nativeQuery = true)
    List<SupplierOptionProjection> findSuppliersByBarcodes(
            @Param("barcodes") Collection<String> barcodes
    );
}