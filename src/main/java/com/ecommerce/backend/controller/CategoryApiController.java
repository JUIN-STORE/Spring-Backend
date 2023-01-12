package com.ecommerce.backend.controller;

import com.ecommerce.backend.JZResponse;
import com.ecommerce.backend.domain.request.CategoryRequest;
import com.ecommerce.backend.domain.response.CategoryResponse;
import com.ecommerce.backend.service.command.CategoryCommandService;
import com.ecommerce.backend.service.query.CategoryQueryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = {"06. Category"})
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/categories")
public class CategoryApiController {
    private final CategoryQueryService categoryQueryService;
    private final CategoryCommandService categoryCommandService;

    // https://bestinu.tistory.com/52
    @ApiOperation(value = "모든 카테고리 읽기", notes = "모든 카테고리를 읽어온다.")
    @GetMapping
    public JZResponse<List<CategoryResponse.Read>> all() {
        final List<CategoryResponse.Read> response = categoryQueryService.readAll();
        return new JZResponse<>(HttpStatus.OK, response);
    }

    @ApiOperation(value = "카테고리 추가", notes = "카테고리를 추가한다.")
    @PostMapping("/admin/new")
    public JZResponse<Long> createCategory(@RequestBody CategoryRequest.Create request) {
        final Long response = categoryCommandService.add(request);
        return new JZResponse<>(HttpStatus.OK, response);
    }

    // FIXME: update, delete 구현해야 됨.
}
