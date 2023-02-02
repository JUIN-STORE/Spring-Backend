package com.ecommerce.backend.controller;

import com.ecommerce.backend.JUINResponse;
import com.ecommerce.backend.domain.entity.Item;
import com.ecommerce.backend.domain.response.ItemResponse;
import com.ecommerce.backend.service.query.ItemQueryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;

@Api(tags = {"05. Item"})
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/items")
public class ItemApiController {
    private final ItemQueryService itemQueryService;

    @ApiOperation(value = "상품 읽기", notes = "상품을 읽는다.")
    @GetMapping("/{itemId}")
    public JUINResponse<ItemResponse.Read> retrieveOne(@PathVariable Long itemId) {
        log.info("[P9][CON][ITEM][ONE_]: GET /api/items/{itemId} itemId({})", itemId);

        try {
            final Item item = itemQueryService.readById(itemId);

            var response = ItemResponse.Read.from(item);
            return new JUINResponse<>(HttpStatus.OK, response);
        } catch (EntityNotFoundException e) {
            log.warn("존재하지 않는 Entity입니다. message: ({})", e.getMessage(), e);
            return new JUINResponse<>(HttpStatus.BAD_REQUEST, null);
        }
    }

    @ApiOperation(value = "상품 목록 읽기", notes = "전체 또는 카테고리 별 상품 조회")
    @GetMapping
    public JUINResponse<Page<ItemResponse.Read>> retrieveAll(@PageableDefault(size = 10) Pageable pageable,
                                                             @RequestParam(required = false) Long categoryId) {
        log.info("[P9][CON][ITEM][ALL_]: GET /api/items pageable({}), categoryId({})", pageable, categoryId);

        try {
            var response = itemQueryService.display(pageable, categoryId);
            return new JUINResponse<>(HttpStatus.OK, response);
        } catch (EntityNotFoundException e) {
            log.warn("존재하지 않는 Entity입니다. message: ({})", e.getMessage(), e);
            return new JUINResponse<>(HttpStatus.BAD_REQUEST, null);
        }
    }

    @ApiOperation(value = "전체 상품의 개수", notes = "전체 상품의 개수를 반환한다.")
    @GetMapping("/count")
    public long retrieveCount() {
        log.info("[P9][CON][ITEM][CNT_]: GET /api/items/count");
        return itemQueryService.total();
    }

    @ApiOperation(value = "상품 검색하기", notes = "전체 또는 특정 카테고리에서 상품을 상품이름으로 검색해서 찾는다")
    @GetMapping("/search")
    public JUINResponse<Page<ItemResponse.Read>> search(@PageableDefault(size = 10) Pageable pageable,
                                                        @RequestParam("name") String searchTitle,
                                                        @RequestParam(required = false) String personalColor,
                                                        @RequestParam(required = false) Long categoryId) {
        log.info("[P9][CON][ITEM][SRCH]: GET /api/items/search pageable=({}), " +
                        "searchTitle=({}), personal-color=({}), categoryId=({})",
                pageable, searchTitle, personalColor, categoryId);

        try {
            var response = itemQueryService.search(pageable, searchTitle, personalColor, categoryId);
            return new JUINResponse<>(HttpStatus.OK, response);
        } catch (EntityNotFoundException e) {
            log.warn("존재하지 않는 Entity입니다. message: ({})", e.getMessage(), e);
            return new JUINResponse<>(HttpStatus.BAD_REQUEST, null);
        }
    }

    @ApiOperation(value = "검색한 상품의 개수", notes = "검색한 상품의 개수를 반환한다.")
    @GetMapping("/search/count")
    public Long retrieveSearchCount(@RequestParam("name") String searchTitle) {
        log.info("[P9][CON][ITEM][SHCT]: GET /api/items/search/count searchTitle({})", searchTitle);

        return itemQueryService.totalByNameContaining(searchTitle);
    }
}

