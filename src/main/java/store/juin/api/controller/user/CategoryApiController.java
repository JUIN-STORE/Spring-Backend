package store.juin.api.controller.user;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.juin.api.JUINResponse;
import store.juin.api.domain.response.CategoryResponse;
import store.juin.api.service.query.CategoryQueryService;

import java.util.List;

@Api(tags = {"06. Category"})
@Slf4j
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryApiController {
    private final CategoryQueryService categoryQueryService;

    // https://bestinu.tistory.com/52
    @ApiOperation(value = "모든 카테고리 읽기", notes = "모든 카테고리를 읽어온다.")
    @GetMapping
    public JUINResponse<List<CategoryResponse.Retrieve>> retrieveAll() {
        log.info("[P9][CTRL][CTGR][ALL_]: GET /api/categories");

        var response = categoryQueryService.readAll();
        return new JUINResponse<>(HttpStatus.OK, response);
    }
}
