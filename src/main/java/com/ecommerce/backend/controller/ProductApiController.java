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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Api(tags = {"05. Product"})
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/products")
public class ProductApiController {
    private final ProductService productService;
    private final ProductImageService productImageService;

    @ApiOperation(value = "판매자 상품 등록", notes = "관리자가 상품을 등록한다.")
    @PostMapping(value = "/seller/register", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public MyResponse<Long> register(@RequestPart ProductRequest.Create request,
                                     @RequestPart("fileList") List<MultipartFile> productImageFileList) {
        log.info("POST /api/products/admin/register request: {}", request);

        try {
            final Long response = productService.add(request, productImageFileList);

            return new MyResponse<>(HttpStatus.OK, response);
        } catch (EntityNotFoundException e) {
            log.warn("존재하지 않는 Entity입니다. message: ({})", e.getMessage(), e);
            return new MyResponse<>(HttpStatus.BAD_REQUEST, null);
        } catch (Exception e) {
            log.warn("파일 등록에 실패하였습니다.");
            return new MyResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @ApiOperation(value = "판매자 상품 읽기", notes = "관리자 페이지에서 상품을 읽는다.")
    @GetMapping("/seller/{productId}")
    public MyResponse<ProductResponse.Read> adminRead(@PathVariable Long productId) {
        log.info("GET /api/products/admin/{productId} productId: {}", productId);

        try {
            final Product product = productService.readByProductId(productId);
            final ProductResponse.Read response = ProductResponse.Read.from(product);

            return new MyResponse<>(HttpStatus.OK, "상품 읽기 성공", response);
        } catch (EntityNotFoundException e) {
            log.warn("존재하지 않는 Entity입니다. message: ({})", e.getMessage(), e);
            return new MyResponse<>(HttpStatus.BAD_REQUEST, null);
        }
    }

    @ApiOperation(value = "판매자 상품 삭제", notes = "관리자 페이지에서 상품을 삭제.")
    @DeleteMapping("/seller/{productId}")
    public MyResponse<Long> adminRemove(@PathVariable Long productId) {
        log.info("DELETE /api/products/admin/{productId} productId: {}", productId);

        try {
            final Long response = productService.remove(productId);

            return new MyResponse<>(HttpStatus.OK, response);
        } catch (EntityNotFoundException e) {
            log.warn("존재하지 않는 Entity입니다. message: ({})", e.getMessage(), e);
            return new MyResponse<>(HttpStatus.BAD_REQUEST, null);
        }
    }

    @ApiOperation(value = "상품 읽기", notes = "상품을 읽는다.")
    @GetMapping("/{productId}")
    public MyResponse<ProductResponse.Read> one(@PathVariable Long productId) {
        log.info("POST /api/products/{productId} productId: {}", productId);

        try {
            final Product product = productService.readByProductId(productId);
            final ProductResponse.Read read = ProductResponse.Read.from(product);

            return new MyResponse<>(HttpStatus.OK, read);
        } catch (EntityNotFoundException e) {
            log.warn("존재하지 않는 Entity입니다. message: ({})", e.getMessage(), e);
            return new MyResponse<>(HttpStatus.BAD_REQUEST, null);
        }
    }

    @ApiOperation(value = "상품 목록 읽기", notes = "전체 또는 카테고리 별 상품 조회")
    @GetMapping
    public MyResponse<List<ProductResponse.Read>> all(@PageableDefault(size = 10) Pageable pageable,
                                                      @RequestParam(required = false) Long categoryId) {

        log.info("GET /api/products pageable: {}", pageable);

        try {
            final Page<Product> productList = productService.read(pageable, categoryId);
            List<ProductResponse.Read> response = getResponse(productList);
            return new MyResponse<>(HttpStatus.OK, response);
        } catch (EntityNotFoundException e) {
            log.warn("존재하지 않는 Entity입니다. message: ({})", e.getMessage(), e);
            return new MyResponse<>(HttpStatus.BAD_REQUEST, null);
        }
    }

    @ApiOperation(value = "전체 상품의 개수", notes = "전체 상품의 개수를 반환한다.")
    @GetMapping("/count")
    public Long readCount() {
        return productService.readCount();
    }

    @ApiOperation(value = "상품 검색하기", notes = "전체 또는 특정 카테고리에서 상품을 상품이름으로 검색해서 찾는다")
    @GetMapping("/search")
    public MyResponse<List<ProductResponse.Read>> search(@PageableDefault(size = 10) Pageable pageable,
                                                         @RequestParam("productName") String searchTitle,
                                                         @RequestParam(value = "categoryId", required = false) Long categoryId) {
        try {
            final Page<Product> productList = productService.search(pageable, searchTitle, categoryId);
            List<ProductResponse.Read> response = getResponse(productList);
            return new MyResponse<>(HttpStatus.OK, response);
        } catch (EntityNotFoundException e) {
            log.warn("존재하지 않는 Entity입니다. message: ({})", e.getMessage(), e);
            return new MyResponse<>(HttpStatus.BAD_REQUEST, null);
        }

    }

    @ApiOperation(value = "검색한 상품의 개수", notes = "검색한 상품의 개수를 반환한다.")
    @GetMapping("/search/count")
    public Long readSearchCount(@RequestParam("productName") String searchTitle) {
        return productService.readSearchCount(searchTitle);
    }

    private List<ProductResponse.Read> getResponse(Page<Product> productList) {
        final List<Long> productIdList = productList.stream().map(Product::getId).collect(Collectors.toList());
        final List<ProductImage> productImageList = productImageService.readAllByProductId(productIdList);
        final Map<Long, List<ProductImage>> productIdImageMap = new HashMap<>();

        for (ProductImage productImage : productImageList) {
            Product product = productImage.getProduct();
            if (product == null) continue;

            Long productId = product.getId();
            List<ProductImage> imageListInProduct = productIdImageMap.getOrDefault(productId, new ArrayList<>());
            imageListInProduct.add(productImage);
            productIdImageMap.put(productId, imageListInProduct);
        }

        return productList.stream()
                .map(image -> ProductResponse.Read.of(image, productIdImageMap.get(image.getId())))
                .collect(Collectors.toList());
    }
}

