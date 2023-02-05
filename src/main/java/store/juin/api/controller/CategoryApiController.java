package store.juin.api.controller;

import store.juin.api.JUINResponse;
import store.juin.api.domain.request.CategoryRequest;
import store.juin.api.domain.response.CategoryResponse;
import store.juin.api.service.command.CategoryCommandService;
import store.juin.api.service.query.CategoryQueryService;
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
    public JUINResponse<List<CategoryResponse.Retrieve>> retrieveAll() {
        log.info("[P9][CTRL][CTGR][ALL_]: GET /api/categories");

        var response = categoryQueryService.readAll();
        return new JUINResponse<>(HttpStatus.OK, response);
    }

    @ApiOperation(value = "카테고리 추가", notes = "카테고리를 추가한다.")
    @PostMapping("/admin/new")
    public JUINResponse<Long> create(@RequestBody CategoryRequest.Create request) {
        log.info("[P9][CTRL][CTGR][BUY_]: POST /api/categories/admin/new, request=({})", request);

        var response = categoryCommandService.add(request);
        return new JUINResponse<>(HttpStatus.OK, response);
    }

    // FIXME: update, delete 구현해야 됨.
}
