package com.example.recover.repository;

import com.example.recover.dto.RecordQuery;
import com.example.recover.entity.ExpiryRecord;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class RecordSpec {

    public static Specification<ExpiryRecord> build(RecordQuery query) {
        return (root, cq, cb) -> {
            root.join("product", JoinType.LEFT);

            List<Predicate> predicates = new ArrayList<>();

            if (query.getExpireDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("expiryDate"), query.getExpireDateFrom()));
            }
            if (query.getExpireDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(
                        root.get("expiryDate"), query.getExpireDateTo()));
            }
            if (query.getIsConfirmed() != null) {
                predicates.add(cb.equal(
                        root.get("confirmStatus"), query.getIsConfirmed()));
            }
            if (query.getIsProcessed() != null) {
                predicates.add(cb.equal(
                        root.get("processStatus"), query.getIsConfirmed()));
            }
            if (StringUtils.hasText(query.getCategory())) {
                predicates.add(cb.equal(
                        root.get("category"), query.getCategory()));
            }
            if (StringUtils.hasText(query.getBarcode())) {
                predicates.add(cb.like(
                        root.get("barcode"), query.getBarcode()+ "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
