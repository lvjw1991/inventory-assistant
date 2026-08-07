package com.example.recover.repository;

import com.example.recover.entity.ExpiryRecord;
import jakarta.annotation.Nonnull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;


@Repository
public interface ExpiryRecordRepository extends JpaRepository<ExpiryRecord, Long>,
        JpaSpecificationExecutor<ExpiryRecord> {

    boolean existsByBarcodeAndExpiryDate(String barcode, LocalDate expiryDate);

    @EntityGraph(attributePaths = "product")
    Page<ExpiryRecord> findAll(Specification<ExpiryRecord> spec, @Nonnull Pageable pageable);
}
