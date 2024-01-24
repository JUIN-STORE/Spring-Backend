package store.juin.api.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.juin.api.cart.model.response.CategoryRetrieveResponse;
import store.juin.api.category.service.query.CategoryQueryService;
import store.juin.api.common.model.response.JUINResponse;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryApiController {
    private final CategoryQueryService categoryQueryService;

    // https://bestinu.tistory.com/52
    @GetMapping
    public JUINResponse<List<CategoryRetrieveResponse>> retrieveAll() {
        log.info("[P9][CTRL][CTGR][ALL_]: GET /api/categories");

        var response = categoryQueryService.readAll();
        return new JUINResponse<>(HttpStatus.OK, response);
    }
}
