package com.example.recover.repository;

import com.example.recover.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Product findByBarcode(String barcode);

    boolean existsByBarcode(String barcode);

}
