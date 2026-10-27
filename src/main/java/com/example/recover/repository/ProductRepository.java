package com.example.recover.repository;

import com.example.recover.entity.Product;
import com.example.recover.vo.ProductCategoryItemVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Product findByBarcode(String barcode);

    boolean existsByBarcode(String barcode);

    List<Product> findByBarcodeIn(List<String> barcodeList);

    @Query("""
        SELECT new com.example.recover.vo.ProductCategoryItemVO(
            p.id,
            p.barcode,
            p.name,
            p.category,
            p.imgUrl,
            p.purchasePrice
        )
        FROM Product p
        WHERE
            (
                :categoryStatus = 'ALL'
                OR (
                    :categoryStatus = 'UNCLASSIFIED'
                    AND (
                        p.category IS NULL
                        OR TRIM(p.category) = ''
                    )
                )
                OR (
                    :categoryStatus = 'CLASSIFIED'
                    AND p.category IS NOT NULL
                    AND TRIM(p.category) <> ''
                )
            )
            AND (
                :keyword IS NULL
                OR LOWER(COALESCE(p.name, ''))
                    LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(COALESCE(p.barcode, ''))
                    LIKE LOWER(CONCAT('%', :keyword, '%'))
            )
        """)
    Page<ProductCategoryItemVO> findForCategoryManagement(
            @Param("categoryStatus") String categoryStatus,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    @Modifying
    @Query("""
        UPDATE Product p
        SET p.category = :category
        WHERE p.id IN :productIds
        """)
    int updateCategoryByIds(
            @Param("productIds") List<Long> productIds,
            @Param("category") String category
    );

}
