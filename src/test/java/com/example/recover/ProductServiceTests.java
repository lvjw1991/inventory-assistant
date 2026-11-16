package com.example.recover;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson2.JSON;
import com.example.recover.dto.*;
import com.example.recover.entity.Product;
import com.example.recover.service.ProductService;
import com.example.recover.utils.CategoryStatus;
import com.example.recover.utils.NetUnitType;
import com.example.recover.vo.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class ProductServiceTests {

    @Autowired
    private ProductService productService;

    @Test
    void crud() {
        ProductRequest productRequest = new ProductRequest();
        productRequest.setBarcode("barcode");
        productRequest.setCategory("category");
        productRequest.setName("name");
        productRequest.setImgUrl("imgUrl");
        Product product = productService.create(productRequest).getData();
        Long id = product.getId();
        assertEquals(productRequest.getBarcode(), product.getBarcode());
        productRequest.setId(id);
        productRequest.setBarcode("barcode1");
        assertEquals(productRequest.getBarcode(), productService.update(productRequest).getData().getBarcode());
        productRequest.setBarcode("8850058008389");
        assertEquals("barcode已存在", productService.update(productRequest).getMessage());
        assertEquals(true, productService.delete(id).getData());
    }

    // 从本地excel读取行数据
    private List<BarcodeNameImgRow> readExcel(String fileName) {
        String path = "src/test/resources/" + fileName;
        return EasyExcel.read(path)
                .head(BarcodeNameImgRow.class)
                .sheet()
                .doReadSync();
    }

    @Test
    void importProduct() {
        List<BarcodeNameImgRow> barcodeNameImgRows = readExcel("productImport.xlsx");
        ImportResultVO importResultVO = productService.dealWith(barcodeNameImgRows);
        System.out.println("success" + importResultVO.getSuccess());
        System.out.println("skip" + importResultVO.getSkip());
    }

    private List<BarcodeNamePriceRow> readPrice(String fileName) {
        String path = "src/test/resources/" + fileName;
        return EasyExcel.read(path)
                .head(BarcodeNamePriceRow.class)
                .sheet()
                .doReadSync();
    }

    @Test
    void importPrice() {
        List<BarcodeNamePriceRow> barcodeNameImgRows = readPrice("price.xlsx");
        ImportResultVO importResultVO = productService.dealWithPrice(barcodeNameImgRows);
        System.out.println("success" + importResultVO.getSuccess());
        System.out.println("skip" + importResultVO.getSkip());
    }

    @Test
    void query(){
        PageResponse<ProductCategoryItemVO> query = productService.query(0, 10, "",
                CategoryStatus.ALL);
        System.out.println(JSON.toJSONString(query));
    }

    @Test
    void batch(){
        ProductCategoryBatchRequest productCategoryBatchRequest = new ProductCategoryBatchRequest();
        List<Long> productIds = new ArrayList<>();
        productIds.add(11598L);
        productIds.add(11608L);
        productCategoryBatchRequest.setProductIds(productIds);
        productCategoryBatchRequest.setCategory("NF");
        productService.batchUpdate(productCategoryBatchRequest);
    }

    @Test
    void findTagList(){
        List<Long> productIds = new ArrayList<>();
        productIds.add(2657L);
        productIds.add(2658L);
        Result<List<PriceTagVO>> tagList = productService.findTagList(productIds);
        System.out.println(JSON.toJSONString(tagList));
        assertEquals("success", tagList.getMessage());
    }

    @Test
    void updatePriceTag(){
        PriceTagRequest priceTagRequest = new PriceTagRequest();
        priceTagRequest.setId(2657L);
        priceTagRequest.setSalePrice(new BigDecimal("4.5"));
        priceTagRequest.setName("Taihe natto zanmai 167,4g");
        priceTagRequest.setNameFi("Taihei natto zanmai 167.4g");
        priceTagRequest.setNetContent(new BigDecimal("0.167"));
        priceTagRequest.setNetUnit(NetUnitType.KG);
        priceTagRequest.setImgUrl("https://imageproxy.wolt.com/assets/68839d5602a50c046d1a2384");
        Result<PriceTagVO> priceTagVOResult = productService.updatePriceTag(priceTagRequest);
        System.out.println(JSON.toJSONString(priceTagVOResult));
        assertEquals("success", priceTagVOResult.getMessage());

    }

}
