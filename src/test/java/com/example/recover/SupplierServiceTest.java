package com.example.recover;

import com.alibaba.fastjson.JSON;
import com.example.recover.dto.*;
import com.example.recover.entity.Supplier;
import com.example.recover.entity.SupplierProduct;
import com.example.recover.service.SupplierProductService;
import com.example.recover.service.SupplierService;
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
public class SupplierServiceTest {

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private SupplierProductService service;

    @Test
    void add() {
        SupplierRequest request = new SupplierRequest();
        request.setSupplierName("fruit");
        Result<Supplier> supplierResult = supplierService.create(request);
        assertEquals(request.getSupplierName(), supplierResult.getData().getSupplierName());
    }

    @Test
    void update() {
        Result<PageResponse<Supplier>> allByPage = supplierService.findAllByPage(0, 10);
        List<Supplier> list = allByPage.getData().getList();
        if (list != null) {
            Supplier first = list.getFirst();
            SupplierRequest request = new SupplierRequest();
            request.setId(first.getId());
            request.setSupplierName("水果");
            Result<Supplier> update = supplierService.update(request);
            assertEquals(request.getSupplierName(), update.getData().getSupplierName());
        }
    }

    @Test
    void delete() {
        Result<PageResponse<Supplier>> allByPage = supplierService.findAllByPage(0, 10);
        List<Supplier> list = allByPage.getData().getList();
        if (list != null) {
            Supplier first = list.getFirst();
            Result<Boolean> delete = supplierService.delete(first.getId());
            assertEquals(true, delete.getData());
        }
    }

    @Test
    void find() {
        Result<PageResponse<Supplier>> allByPage = supplierService.findAllByPage(0, 10);
        System.out.println(JSON.toJSONString(allByPage));
        List<Supplier> list = allByPage.getData().getList();
        if (list != null) {
            Supplier first = list.getFirst();
            Result<Supplier> byId = supplierService.findById(first.getId());
            assertEquals(200, byId.getCode());
        }
    }

    @Test
    void find2() {
        Result<PageResponse<SupplierProduct>> allByPage = service.findAllByPage(0, 10, null);
        System.out.println(JSON.toJSONString(allByPage));
        List<SupplierProduct> list = allByPage.getData().getList();
        if (list != null) {
            SupplierProduct first = list.getFirst();
            Result<SupplierProduct> byId = service.findById(first.getId());
            assertEquals(200, byId.getCode());
        }
    }
}
