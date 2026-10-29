package com.example.recover.controller;

import com.example.recover.vo.ImportResultVO;
import com.example.recover.vo.PageResponse;
import com.example.recover.vo.PurchaseRecommendationVO;
import com.example.recover.service.PurchaseRecommendationService;
import com.example.recover.vo.Result;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 订货管理
 */
@RestController
@RequestMapping("/api/purchase-recommendations")
public class PurchaseRecommendationController {

    private final PurchaseRecommendationService service;

    public PurchaseRecommendationController(
            PurchaseRecommendationService service) {
        this.service = service;
    }

    @GetMapping
    public Result<PageResponse<PurchaseRecommendationVO>> findRecommendations(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String barcode,
            @RequestParam(required = false) Long supplierId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {

        return Result.success(service.findRecommendations(
                name,
                barcode,
                supplierId,
                pageNum,
                pageSize
        ));
    }

    @PostMapping("/importStock")
    public Result<ImportResultVO> importStock(@RequestParam("file") MultipartFile file) throws IOException {
        return service.importStock(file);
    }

    @PostMapping("/importSales")
    public Result<ImportResultVO> importSales(@RequestParam("file") MultipartFile file) throws IOException {
        return service.importSales(file);
    }
}
