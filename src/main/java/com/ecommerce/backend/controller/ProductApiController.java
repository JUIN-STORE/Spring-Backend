package com.ecommerce.backend.controller;

import com.ecommerce.backend.MyResponse;
import com.ecommerce.backend.domain.entity.Product;
import com.ecommerce.backend.domain.entity.ProductImage;
import com.ecommerce.backend.domain.request.ProductRequest;
import com.ecommerce.backend.domain.response.ProductResponse;
import com.ecommerce.backend.service.ProductImageService;
import com.ecommerce.backend.service.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    private final ProductImageService productImageService;

    @ApiOperation(value = "관리자 상품 등록", notes = "관리자가 상품을 등록한다.")
    @PostMapping(value = "/admin/register", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public MyResponse<Long> register(@RequestPart ProductRequest.CreateRequest request,
                                     @RequestPart("fileList") List<MultipartFile> productImageFileList) {
        log.info("POST /api/products/admin/register request: {}", request);

        try {
            final Long aLong = productService.saveProduct(request, productImageFileList);

            return new MyResponse<>(HttpStatus.OK, aLong);
        } catch (Exception e) {
            log.warn("파일 등록에 실패하였습니다.");
            return new MyResponse<>(HttpStatus.FORBIDDEN, null);
        }
    }

    @ApiOperation(value = "관리자 상품 읽기", notes = "관리자 페이지에서 상품을 읽는다.")
    @GetMapping("/admin/{productId}")
    public MyResponse<ProductResponse.Read> adminRead(@PathVariable Long productId) {
        log.info("GET /api/products/admin/{productId} productId: {}", productId);

        try {
            final Product product = productService.findByProductId(productId);
            final ProductResponse.Read read = ProductResponse.Read.fromProduct(product);
            return new MyResponse<>(HttpStatus.OK, "상품 읽기 성공", read);
        } catch (Exception e) {
            return null;
        }
    }

    @ApiOperation(value = "관리자 상품 삭제", notes = "관리자 페이지에서 상품을 삭제.")
    @DeleteMapping("/admin/{productId}")
    public MyResponse<Long> adminRemove(@PathVariable Long productId) {
        log.info("DELETE /api/products/admin/{productId} productId: {}", productId);

        try {
            return new MyResponse<>(HttpStatus.OK, productService.delete(productId));
        } catch (EntityNotFoundException e) {
            log.warn("EntityNotFoundException 발생");
            return null;
        }
    }

    @ApiOperation(value = "상품 읽기", notes = "상품을 읽는다.")
    @GetMapping("/{productId}")
    public MyResponse<ProductResponse.Read> read(@PathVariable Long productId) {
        log.info("POST /api/products/{productId} productId: {}", productId);

        try {
            final Product product = productService.findByProductId(productId);
            final ProductResponse.Read read = ProductResponse.Read.fromProduct(product);

            return new MyResponse<>(HttpStatus.OK, read);
        } catch (Exception e) {
            log.warn("Exception: {}", e.getMessage());
            return new MyResponse<>(HttpStatus.FORBIDDEN, null);
        }
    }

    @ApiOperation(value = "전체 상품 읽기", notes = "전체 상품을 읽는다.")
    @GetMapping
    public MyResponse<List<ProductResponse.Read>> readAll(@PageableDefault(size = 10) Pageable pageable) {
        log.info("GET /api/products pageable: {}", pageable);

        final Page<Product> productList = productService.findAll(pageable);
        final List<Long> productIdList = productList.stream().map(Product::getId).collect(Collectors.toList());

        final List<ProductImage> productImageList = productImageService.findAllByProductId(productIdList);
        final List<ProductResponse.Read> readAllResponse = new ArrayList<>();

        final int size = productImageList.size();

        for (int i = 0; i < size; i++){
            readAllResponse.add(ProductResponse.Read.fromProduct(productList.getContent().get(i), productImageList.get(i)));
        }

        return new MyResponse<>(HttpStatus.OK, readAllResponse);
    }

    @ApiOperation(value = "전체 상품의 개수", notes = "전체 상품의 개수를 반환한다.")
    @GetMapping("/count")
    public Long readCount() {
        return productService.count();
    }

    @ApiOperation(value = "상품 검색하기", notes = "상품을 상품이름으로 검색해서 찾는다")
    @GetMapping("/search")
    public MyResponse<List<ProductResponse.Read>> search(@PageableDefault(size = 10) Pageable pageable, @RequestParam("productName") String searchTitle) {
        final Page<Product> productList = productService.search(pageable, searchTitle);
        final List<Long> productIdList = productList.stream().map(Product::getId).collect(Collectors.toList());

        final List<ProductImage> productImageList = productImageService.findAllByProductId(productIdList);
        final List<ProductResponse.Read> readAllResponse = new ArrayList<>();

        final int size = productImageList.size();

        for (int i = 0; i < size; i++){
            readAllResponse.add(ProductResponse.Read.fromProduct(productList.getContent().get(i), productImageList.get(i)));
        }

        return new MyResponse<>(HttpStatus.OK, readAllResponse);
    }

    @ApiOperation(value = "전체 상품의 개수", notes = "전체 상품의 개수를 반환한다.")
    @GetMapping("/search/count")
    public Long readSearchCount(@RequestParam("productName") String searchTitle) {
        return productService.searchCount(searchTitle);
    }
}

