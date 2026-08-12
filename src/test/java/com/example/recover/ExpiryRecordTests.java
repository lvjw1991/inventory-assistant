package com.example.recover;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson2.JSON;
import com.example.recover.dto.*;
import com.example.recover.service.ExpiryRecordService;
import com.example.recover.utils.ConfirmStatus;
import com.example.recover.utils.ProcessStatus;
import com.example.recover.vo.ExpiryRecordVO;
import com.example.recover.vo.ImportResultVO;
import com.example.recover.vo.PageResponse;
import com.example.recover.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@Slf4j
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
        assertEquals(2, result.getSuccess());
    }

    @Test
    void search(){
        RecordQuery recordQuery = new RecordQuery();
        recordQuery.setConfirmStatus(ConfirmStatus.UNCONFIRM);
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
        expiryProcessRequest.setProcessStatus(ProcessStatus.PROMOTE);
        expiryProcessRequest.setProcessRemark("已打折");
        Result<Boolean> process = service.process(expiryProcessRequest);
        assertEquals("success", process.getMessage());
    }

    @Test
    void searchPage() {
        RecordQuery recordQuery = new RecordQuery();
        recordQuery.setConfirmStatus(ConfirmStatus.UNCONFIRM);
        recordQuery.setCategory("Fresh");
        recordQuery.setBarcode("8082");
        recordQuery.setProcessStatus(ProcessStatus.UNPROCESS);
        recordQuery.setExpireDateFrom(LocalDate.of(2026, 9, 1));
        recordQuery.setExpireDateTo(LocalDate.of(2026, 9, 30));
        Result<PageResponse<ExpiryRecordVO>> search = service.search(recordQuery);
        System.out.println(JSON.toJSONString(search));
        assertEquals("success", search.getMessage());
    }

    @Test
    void searchMonthly() {
        RecordMonthlyQuery recordQuery = new RecordMonthlyQuery();
        recordQuery.setConfirmStatus(ConfirmStatus.UNCONFIRM);
        recordQuery.setCategory("Fresh");
        recordQuery.setProcessStatus(ProcessStatus.UNPROCESS);
        recordQuery.setExpireDateFrom(LocalDate.of(2026, 9, 1));
        recordQuery.setExpireDateTo(LocalDate.of(2026, 9, 30));
        Result<List<ExpiryRecordVO>> search = service.searchMonthly(recordQuery);
        System.out.println(JSON.toJSONString(search));
        assertEquals("success", search.getMessage());
    }

}
