package com.example.recover.service;

import com.example.recover.dto.OrderItemCheckRequest;
import com.example.recover.dto.OrderItemQuery;
import com.example.recover.dto.OrderItemRequest;
import com.example.recover.entity.ReceivingOrderItem;
import com.example.recover.exception.ResourceNotFoundException;
import com.example.recover.repository.ReceivingOrderItemRepository;
import com.example.recover.utils.OrderItemConverter;
import com.example.recover.vo.OrderItemVO;
import com.example.recover.vo.PageResponse;
import com.example.recover.vo.Result;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderItemService {

    private final ReceivingOrderItemRepository orderItemRepository;

    private final OrderItemConverter orderItemConverter;

    private final OrderService orderService;

    public Result<PageResponse<OrderItemVO>> findAllByPage(OrderItemQuery itemQuery) {
        Specification<ReceivingOrderItem> spec = (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(
                    cb.equal(
                            root.get("receivingOrderId"),
                            itemQuery.getOrderId()
                    )
            );

            // supplierCode 模糊查询
            if (itemQuery.getSupplierCode() != null && !itemQuery.getSupplierCode().isBlank()) {
                predicates.add(
                        cb.like(
                                root.get("supplierCode"),
                                itemQuery.getSupplierCode().trim() + "%"
                        )
                );
            }

            // 确认状态
            if (itemQuery.getCheckStatus() != null) {
                predicates.add(
                        cb.equal(
                                root.get("checkStatus"),
                                itemQuery.getCheckStatus()
                        )
                );
            }

            // 产品
            if (itemQuery.getProductName() != null && !itemQuery.getProductName().isBlank()) {
                predicates.add(
                        cb.like(
                                root.get("productName"),
                                "%" + itemQuery.getProductName().trim() + "%"
                        )
                );
            }

            return cb.and(
                    predicates.toArray(new Predicate[0])
            );
        };
        Pageable pageable = PageRequest.of(itemQuery.getPageNum(), itemQuery.getPageSize(),
                Sort.by(
                        Sort.Direction.ASC,
                        "supplierCode"
                )
        );
        return Result.success(PageResponse.of(orderItemRepository.findAll(spec, pageable).map(orderItemConverter::toVo)));
    }

    public Result<OrderItemVO> findById(Long id) {
        ReceivingOrderItem orderItem = orderItemRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(404, "ResourceNotFoundException"));
        return Result.success(orderItemConverter.toVo(orderItem));
    }

    public ReceivingOrderItem findEntityById(Long id) {
        return orderItemRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(404, "ResourceNotFoundException"));
    }

    @Transactional
    public Result<OrderItemVO> create(OrderItemRequest request) {
        ReceivingOrderItem orderItem = new ReceivingOrderItem();
        orderItem.setReceivingOrderId(request.getReceivingOrderId());
        orderItem.setSupplierCode(request.getSupplierCode());
        orderItem.setProductName(request.getProductName());
        orderItem.setBarcode(request.getBarcode());
        orderItem.setOrderQty(request.getOrderQty());
        orderItem.setActualQty(request.getActualQty());
        orderItem.setTotal(request.getTotal());
        orderItem.setExpiryDate(Strings.join(deduplicationList(request.getExpiryDate()), ','));
        orderItem.setUnitPrice(request.getUnitPrice());
        orderItem.setCategory(request.getCategory());
        orderItem.setSugar(request.getSugar());
        orderItem.setCheckStatus(request.getCheckStatus());
        return Result.success(orderItemConverter.toVo(orderItemRepository.save(orderItem)));
    }

    private List<String> deduplicationList(List<String> expiryDate) {
        if (expiryDate == null || expiryDate.size() < 2) {
            return expiryDate;
        }
        return expiryDate.stream().distinct().sorted().toList();
    }

    @Transactional
    public Result<OrderItemVO> update(OrderItemRequest request) {
        ReceivingOrderItem orderItem = findEntityById(request.getId());
        orderItem.setSupplierCode(request.getSupplierCode());
        orderItem.setProductName(request.getProductName());
        orderItem.setBarcode(request.getBarcode());
        orderItem.setOrderQty(request.getOrderQty());
        orderItem.setActualQty(request.getActualQty());
        orderItem.setTotal(request.getTotal());
        orderItem.setExpiryDate(Strings.join(deduplicationList(request.getExpiryDate()), ','));
        orderItem.setUnitPrice(request.getUnitPrice());
        orderItem.setCategory(request.getCategory());
        orderItem.setSugar(request.getSugar());
        orderItem.setCheckStatus(request.getCheckStatus());
        return Result.success(orderItemConverter.toVo(orderItemRepository.save(orderItem)));
    }

    @Transactional
    public Result<Boolean> delete(Long id) {
        ReceivingOrderItem item = findEntityById(id);
        orderItemRepository.delete(item);
        return Result.success(true);
    }

    /**
     * 0. 更新order状态
     * 1. 更新 receiving_order_item
     *
     * @param id
     * @param request
     * @return
     */
    @Transactional
    public Result<Boolean> check(Long id, OrderItemCheckRequest request) {
        ReceivingOrderItem orderItem = findEntityById(id);
        orderItem.setBarcode(request.getBarcode());
        orderItem.setActualQty(request.getActualQty());
        orderItem.setExpiryDate(Strings.join(deduplicationList(request.getExpiryDate()), ','));
        orderItem.setCategory(request.getCategory());
        orderItem.setSugar(request.getSugar());
        orderItem.setCheckStatus(request.getStatus());
        orderItemRepository.save(orderItem);
        orderService.updateProcess(orderItem.getReceivingOrderId());
        return Result.success(true);
    }

    public List<OrderItemVO> findAllByOrderId(Long orderId) {
        ReceivingOrderItem orderItem = new ReceivingOrderItem();
        orderItem.setReceivingOrderId(orderId);
        Example<ReceivingOrderItem> example = Example.of(orderItem);
        return orderItemRepository.findAll(example).stream().map(orderItemConverter::toVo).toList();
    }
}
