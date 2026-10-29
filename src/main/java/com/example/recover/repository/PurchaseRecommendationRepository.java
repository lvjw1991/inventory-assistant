package com.example.recover.repository;

import com.example.recover.dto.PurchaseRecommendationProjection;
import com.example.recover.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseRecommendationRepository
        extends JpaRepository<Product, Long> {

    @Query(
            value = """
            SELECT
                p.id AS productId,
                p.barcode AS barcode,
                p.name AS productName,
                p.category AS category,
                p.img_url AS imgUrl,
                p.purchase_price AS purchasePrice,
                i.stock AS stock,
                sa.avg_monthly_sales_qty AS avgMonthlySalesQty
            FROM product p
            LEFT JOIN inventory_stock i
                ON i.barcode = p.barcode
            LEFT JOIN sales_monthly_avg sa
                ON sa.barcode = p.barcode
            WHERE 1=1
              AND (:keyword IS NULL
                   OR :keyword = ''
                   OR p.name LIKE CONCAT('%', :keyword, '%'))
              AND (:barcode IS NULL
                   OR :barcode = ''
                   OR p.barcode LIKE CONCAT('%', :barcode, '%'))
              AND (
                  :supplierId IS NULL
                  OR EXISTS (
                      SELECT 1
                      FROM supplier_product sp
                      JOIN supplier s
                        ON s.id = sp.supplier_id
                      WHERE sp.barcode = p.barcode
                        AND sp.supplier_id = :supplierId
                  )
              )
            """,
            countQuery = """
            SELECT COUNT(*)
            FROM product p
            WHERE 1=1
              AND (:keyword IS NULL
                   OR :keyword = ''
                   OR p.name LIKE CONCAT('%', :keyword, '%'))
              AND (:barcode IS NULL
                   OR :barcode = ''
                   OR p.barcode LIKE CONCAT('%', :barcode, '%'))
              AND (
                  :supplierId IS NULL
                  OR EXISTS (
                      SELECT 1
                      FROM supplier_product sp
                      JOIN supplier s
                        ON s.id = sp.supplier_id
                      WHERE sp.barcode = p.barcode
                        AND sp.supplier_id = :supplierId
                  )
              )
            """,
            nativeQuery = true
    )
    Page<PurchaseRecommendationProjection> findRecommendations(
            @Param("keyword") String keyword,
            @Param("barcode") String barcode,
            @Param("supplierId") Long supplierId,
            Pageable pageable
    );
}
