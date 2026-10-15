package com.example.recover.repository;

import com.example.recover.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Product findByBarcode(String barcode);

    boolean existsByBarcode(String barcode);

    List<Product> findByBarcodeIn(List<String> barcodeList);

}
