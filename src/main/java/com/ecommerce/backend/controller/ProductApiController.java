package com.ecommerce.backend.controller;

import com.ecommerce.backend.MyResponse;
import com.ecommerce.backend.domain.request.ProductRequest;
import com.ecommerce.backend.service.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/** Controller Naming
 * C -> create
 * R -> read
 * U -> modify
 * D -> remove
 */

@Api(tags = {"04. Product"})
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/products")
public class ProductApiController {
    private final ProductService productService;

    @ApiOperation(value = "상품 등록", notes="상품을 등록한다.")
    @PostMapping(value ="/admin/register", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE, "multipart/form-data"})
    public MyResponse<Void> register(@RequestPart ProductRequest.CreateRequest request,
                                     @RequestPart("file") List<MultipartFile> productImageFileList) throws IOException {
        log.info("POST /api/products/admin/register request: {}", request);
        productService.saveProduct(request, productImageFileList);
        return new MyResponse<>(HttpStatus.OK, "상품 등록 성공", null);
    }

    @ExceptionHandler(IOException.class)
    public MyResponse<Void> multipartFileException(Exception e) {
        log.warn("파일 등록에 실패하였습니다.");
        return new MyResponse<>(HttpStatus.OK, "상품 등록 실패", null);
    }
}

