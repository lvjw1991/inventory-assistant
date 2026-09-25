package com.example.recover.utils;

import com.example.recover.entity.ExpiryRecord;
import com.example.recover.vo.ExpiryRecordDetailVO;
import org.springframework.stereotype.Component;

@Component
public class ExpiryRecordDetailConverter {

    public ExpiryRecordDetailVO toVO(ExpiryRecord entity) {
        ExpiryRecordDetailVO vo = new ExpiryRecordDetailVO();
        vo.setId(entity.getId());
        vo.setBarcode(entity.getBarcode());
        vo.setExpiryDate(entity.getExpiryDate());
        vo.setStock(entity.getStock());
        vo.setConfirmStatus(entity.getConfirmStatus());
        vo.setConfirmTime(entity.getConfirmTime());
        vo.setProcessStatus(entity.getProcessStatus());
        vo.setProcessTime(entity.getProcessTime());
        vo.setProcessRemark(entity.getProcessRemark());
        vo.setCategory(entity.getCategory());
        vo.setProductName(entity.getProductName());
        return vo;
    }
}
