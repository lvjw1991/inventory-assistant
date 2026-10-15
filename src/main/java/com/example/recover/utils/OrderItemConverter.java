package com.example.recover.utils;

import com.example.recover.entity.ReceivingOrderItem;
import com.example.recover.vo.OrderItemExportVO;
import com.example.recover.vo.OrderItemVO;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class OrderItemConverter {

    public OrderItemVO toVo(ReceivingOrderItem item) {
        if (item == null) {
            return null;
        }
        OrderItemVO orderItemVO = new OrderItemVO();
        orderItemVO.setId(item.getId());
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
        orderItemVO.setCheckStatus(item.getCheckStatus() != null ? item.getCheckStatus().name() : "");
        orderItemVO.setDamageQty(item.getDamageQty());
        orderItemVO.setRemark(item.getRemark());
        orderItemVO.setCartonQty(item.getCartonQty());
        return orderItemVO;
    }

    public OrderItemExportVO toExportVo(ReceivingOrderItem item) {
        if (item == null) {
            return null;
        }
        OrderItemExportVO orderItemVO = new OrderItemExportVO();
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
        orderItemVO.setCheckStatus(item.getCheckStatus() != null ? item.getCheckStatus().name() : "");
        orderItemVO.setDamageQty(item.getDamageQty());
        orderItemVO.setRemark(item.getRemark());
        orderItemVO.setCartonQty(item.getCartonQty());
        orderItemVO.setPosPurchasePrice(item.getPosPurchasePrice());
        if (item.getUnitPrice() != null
                && item.getPosPurchasePrice() != null) {
            orderItemVO.setPriceDiff(item.getUnitPrice()
                    .subtract(item.getPosPurchasePrice()));

        }
        orderItemVO.setResult(getPriceCompareResult(item));
        return orderItemVO;
    }

    private String getPriceCompareResult(ReceivingOrderItem item) {
        BigDecimal purchasePrice = item.getUnitPrice();
        BigDecimal posPurchasePrice = item.getPosPurchasePrice();

        if (purchasePrice == null || posPurchasePrice == null) {
            return "价格缺失";
        }

        int compare = purchasePrice.compareTo(posPurchasePrice);

        if (compare > 0) {
            return "高于POS";
        } else if (compare < 0) {
            return "低于POS";
        } else {
            return "价格相同";
        }
    }
}
