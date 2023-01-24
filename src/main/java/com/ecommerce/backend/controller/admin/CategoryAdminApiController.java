package com.ecommerce.backend.controller.admin;

import com.ecommerce.backend.JUINResponse;
import com.ecommerce.backend.domain.request.CategoryRequest;
import com.ecommerce.backend.service.command.CategoryCommandService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"06. Admin Category"})
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/admin/categories")
public class CategoryAdminApiController {
    private final CategoryCommandService categoryCommandService;

    @ApiOperation(value = "카테고리 추가", notes = "카테고리를 추가한다.")
    @PostMapping
    public JUINResponse<Long> create(@RequestBody CategoryRequest.Create request) {
        var response = categoryCommandService.add(request);
        return new JUINResponse<>(HttpStatus.OK, response);
    }
}
