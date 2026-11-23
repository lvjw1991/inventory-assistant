package com.example.recover.controller;

import com.alibaba.excel.EasyExcel;
import com.example.recover.entity.SupplierProduct;
import com.example.recover.service.SupplierProductService;
import com.example.recover.vo.PageResponse;
import com.example.recover.vo.Result;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/codes")
@RequiredArgsConstructor
public class SupplierProductController {

    private final SupplierProductService supplierProductService;

    /**
     * 查询全部
     */
    @GetMapping
    public Result<PageResponse<SupplierProduct>> search(
            @RequestParam(required = false) Long supplierId,
            @RequestParam(required = false) String supplierCode,
            @RequestParam(defaultValue = "0") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return supplierProductService.findAllByPage(pageNum, pageSize, supplierId, supplierCode);
    }

    /**
     * 查询单个
     */
    @GetMapping("/{id}")
    public Result<SupplierProduct> getById(@PathVariable Long id) {
        return supplierProductService.findById(id);
    }

    @GetMapping("/export")
    public void export(@RequestParam(required = false) Long supplierId,
                       @RequestParam(required = false) String supplierCode,
                       HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment;filename=" +
                URLEncoder.encode("映射列表.xlsx", StandardCharsets.UTF_8));

        List<SupplierProduct> list = supplierProductService.findAllBy(supplierId, supplierCode);

        EasyExcel.write(response.getOutputStream(), SupplierProduct.class)
                .sheet("货单列表")
                .doWrite(list);
    }


}
