package com.example.recover.utils;

import com.example.recover.entity.ExpiryRecord;
import com.example.recover.vo.ExpiryRecordVO;
import org.springframework.stereotype.Component;

@Component
public class ExpiryRecordConverter {

    public ExpiryRecordVO toVO(ExpiryRecord entity){
        ExpiryRecordVO vo = new ExpiryRecordVO();
        vo.setId(entity.getId());
        vo.setBarcode(entity.getBarcode());
        vo.setExpiryDate(entity.getExpiryDate());
        vo.setStock(entity.getStock());
        vo.setConfirmStatus(entity.getConfirmStatus());
        vo.setConfirmTime(entity.getConfirmTime());
        vo.setProcessStatus(entity.getProcessStatus());
        vo.setProcessTime(entity.getProcessTime());
        vo.setProcessMethod(entity.getProcessMethod());
        vo.setProcessRemark(entity.getProcessRemark());
        vo.setCategory(entity.getCategory());
        vo.setProductName(entity.getProductName());
        if(entity.getProduct()!=null){
            vo.setImgUrl(entity.getProduct().getImgUrl());
        }
        return vo;
    }
}
