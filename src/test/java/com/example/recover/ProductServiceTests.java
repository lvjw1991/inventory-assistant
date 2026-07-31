package com.example.recover;

import com.example.recover.dto.ProductRequest;
import com.example.recover.entity.Product;
import com.example.recover.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

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

}
