package com.example.recover.utils;

import com.example.recover.entity.ReceivingOrderItem;
import com.example.recover.vo.OrderItemVO;
import org.springframework.stereotype.Component;

@Component
public class OrderItemConverter {

    public OrderItemVO toVo(ReceivingOrderItem item) {
        if (item == null) {
            return null;
        }
        OrderItemVO orderItemVO = new OrderItemVO();
        orderItemVO.setSupplierCode(item.getSupplierCode());
        orderItemVO.setProductName(item.getProductName());
        orderItemVO.setBarcode(item.getBarcode());
        orderItemVO.setOrderQty(item.getOrderQty());
        orderItemVO.setActualQty(item.getActualQty());
        orderItemVO.setTotal(item.getTotal());
        orderItemVO.setExpiryDate(item.getExpiryDate());
        orderItemVO.setUnitPrice(item.getUnitPrice());
        orderItemVO.setCategory(item.getCategory());
        orderItemVO.setSugar(item.getSugar());
        orderItemVO.setCheckStatus(item.getCheckStatus().name());
        return orderItemVO;
    }
}
