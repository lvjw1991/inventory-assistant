package com.example.recover.utils;

import com.example.recover.entity.ReceivingOrder;
import com.example.recover.vo.ReceivingOrderVO;
import org.springframework.stereotype.Component;

@Component
public class OrderConverter {

    public ReceivingOrderVO toVO(ReceivingOrder entity){
        ReceivingOrderVO vo = new ReceivingOrderVO();
        vo.setId(entity.getId());
        vo.setSupplierId(entity.getSupplierId());
        vo.setNumber(entity.getNumber());
        vo.setReceiveDate(entity.getReceiveDate());
        vo.setProgress(entity.getProgress());
        vo.setTemperature(entity.getTemperature());
        vo.setTransport(entity.getTransport());
        return vo;
    }
}
