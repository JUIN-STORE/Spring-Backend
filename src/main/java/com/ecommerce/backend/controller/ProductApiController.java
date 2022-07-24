package com.ecommerce.backend.controller;

import com.ecommerce.backend.MyResponse;
import com.ecommerce.backend.domain.entity.Product;
import com.ecommerce.backend.domain.request.ProductRequest;
import com.ecommerce.backend.domain.response.ProductResponse;
import com.ecommerce.backend.service.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Controller Naming
 * C -> create
 * R -> read
 * U -> modify
 * D -> remove
 */

@Api(tags = {"03. Product"})
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/products")
public class ProductApiController {
    private final ProductService productService;

    @ApiOperation(value = "관리자 상품 등록", notes = "관리자가 상품을 등록한다.")
    @PostMapping(value = "/admin/register", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public MyResponse<Long> register(@RequestPart ProductRequest.CreateRequest request,
                                     @RequestPart("fileList") List<MultipartFile> productImageFileList) {
        log.info("POST /api/products/admin/register request: {}", request);

        try {
            final Long aLong = productService.saveProduct(request, productImageFileList);

            return new MyResponse<>(HttpStatus.OK, aLong);
        } catch (Exception e){
            log.warn("파일 등록에 실패하였습니다.");
            return new MyResponse<>(HttpStatus.FORBIDDEN, "상품 등록 실패", null);
        }
    }

    @ApiOperation(value = "관리자 상품 읽기", notes = "관리자 페이지에서 상품을 읽는다.")
    @GetMapping("/admin/{productId}")
    public MyResponse<ProductResponse.ReadResponse> adminRead(@PathVariable Long productId) {
        final Product product = productService.findById(productId);
        final ProductResponse.ReadResponse readResponse = ProductResponse.ReadResponse.fromProduct(product);

        return new MyResponse<>(HttpStatus.OK, "상품 읽기 성공", readResponse);
    }

    @ApiOperation(value = "상품 읽기", notes = "상품을 읽는다.")
    @GetMapping("/{productId}")
    public MyResponse<ProductResponse.ReadResponse> read(@PathVariable Long productId) {
        final Product product = productService.findById(productId);
        final ProductResponse.ReadResponse readResponse = ProductResponse.ReadResponse.fromProduct(product);

        return new MyResponse<>(HttpStatus.OK, "상품 읽기 성공", readResponse);
    }
}

