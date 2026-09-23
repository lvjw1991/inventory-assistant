package com.example.recover;

import com.alibaba.excel.EasyExcel;
import com.example.recover.dto.BarcodeNameImgRow;
import com.example.recover.dto.ProductRequest;
import com.example.recover.entity.Product;
import com.example.recover.service.ProductService;
import com.example.recover.vo.ImportResultVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

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

}
