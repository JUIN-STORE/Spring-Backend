package com.ecommerce.backend.controller;

import com.ecommerce.backend.MyResponse;
import com.ecommerce.backend.domain.entity.Category;
import com.ecommerce.backend.domain.request.CategoryRequest;
import com.ecommerce.backend.domain.response.CategoryResponse;
import com.ecommerce.backend.service.CategoryService;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Api(tags = {"06. Category"})
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/categories")
public class CategoryApiController {
    private final CategoryService categoryService;

    // https://bestinu.tistory.com/52
    @GetMapping
    public MyResponse<List<CategoryResponse.Read>> all() {
        final List<Category> categoryList = categoryService.readAll();

        final List<CategoryResponse.Read> response = new ArrayList<>();

        // FIXME: 쿼리 N방 날아감
        for (Category category : categoryList) {
            final List<CategoryResponse.ReadChildList> childListResponse = CategoryResponse.ReadChildList.from(category.getChildList());
            response.add(CategoryResponse.Read.from(category, childListResponse));
        }

        return new MyResponse<>(HttpStatus.OK, response);
    }

    @PostMapping("/admin/new")
    public MyResponse<Long> createCategory(@RequestBody CategoryRequest.Create request) {
        final Long response = categoryService.add(request);
        return new MyResponse<>(HttpStatus.OK, response);
    }
}
