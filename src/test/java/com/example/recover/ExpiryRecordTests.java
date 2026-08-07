package com.example.recover;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson2.JSON;
import com.example.recover.dto.BarcodeStockRow;
import com.example.recover.dto.ExpiryConfirmRequest;
import com.example.recover.dto.ExpiryProcessRequest;
import com.example.recover.dto.RecordQuery;
import com.example.recover.service.ExpiryRecordService;
import com.example.recover.utils.ProcessMethod;
import com.example.recover.vo.ExpiryRecordVO;
import com.example.recover.vo.ImportResultVO;
import com.example.recover.vo.PageResponse;
import com.example.recover.vo.Result;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class ExpiryRecordTests {

    @Autowired
    private ExpiryRecordService service;

    // 从本地excel读取行数据
    private List<BarcodeStockRow> readExcel(String fileName) {
        String path = "src/test/resources/" + fileName;
        return EasyExcel.read(path)
                .head(BarcodeStockRow.class)
                .sheet()
                .doReadSync();
    }

    @Test
    void importXls() {
        List<BarcodeStockRow> rows = readExcel("stock.xls");
        ImportResultVO result = service.updateStock(rows);
        assertEquals(2, result.getSuccess());
    }

    @Test
    void importXlsx() {
        List<BarcodeStockRow> rows = readExcel("expiry_record.xlsx");
        ImportResultVO result = service.updateStock(rows);
        assertEquals(48, result.getSuccess());
    }

    @Test
    void search(){
        RecordQuery recordQuery = new RecordQuery();
        recordQuery.setIsConfirmed(false);
        recordQuery.setCategory("Fresh");
        Result<PageResponse<ExpiryRecordVO>> search = service.search(recordQuery);
        System.out.println(JSON.toJSONString(search));
        assertEquals("success", search.getMessage());
        ExpiryRecordVO first = search.getData().getList().getFirst();
        ExpiryConfirmRequest expiryConfirmRequest = new ExpiryConfirmRequest();
        expiryConfirmRequest.setId(first.getId());
        expiryConfirmRequest.setStock(first.getStock());
        Result<Boolean> confirm = service.confirm(expiryConfirmRequest);
        assertEquals("success", confirm.getMessage());
        ExpiryProcessRequest expiryProcessRequest = new ExpiryProcessRequest();
        expiryProcessRequest.setId(first.getId());
        expiryProcessRequest.setProcessMethod(ProcessMethod.PROMOTE);
        expiryProcessRequest.setProcessRemark("已打折");
        Result<Boolean> process = service.process(expiryProcessRequest);
        assertEquals("success", process.getMessage());
    }

}
