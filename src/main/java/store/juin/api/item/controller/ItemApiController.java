package store.juin.api.item.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import store.juin.api.account.enumeration.PersonalColor;
import store.juin.api.common.model.response.JUINResponse;
import store.juin.api.item.model.entity.Item;
import store.juin.api.item.model.response.ItemResponse;
import store.juin.api.item.service.query.ItemQueryService;

import javax.persistence.EntityNotFoundException;

@Api(tags = {"05. Item"})
@Slf4j
@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemApiController {
    private final ItemQueryService itemQueryService;

    @ApiOperation(value = "상품 읽기", notes = "상품을 읽는다.")
    @GetMapping("/{itemId}")
    public JUINResponse<ItemResponse.Read> retrieveOne(@PathVariable Long itemId) {
        log.info("[P9][CTRL][ITEM][RONE]: GET /api/items/{}", itemId);

        try {
            final Item item = itemQueryService.readById(itemId);

            var response = ItemResponse.Read.from(item);
            return new JUINResponse<>(HttpStatus.OK, response);
        } catch (EntityNotFoundException e) {
            log.warn("[P5][CTRL][ITEM][RONE]: ({})", e.getMessage());
            return new JUINResponse<>(HttpStatus.BAD_REQUEST, null);
        }
    }

    @ApiOperation(value = "상품 전체 조회", notes = "전체 또는 조건 별로 검색한다.")
    @GetMapping
    public JUINResponse<Page<ItemResponse.Read>> retrieveAll(@PageableDefault(size = 10) Pageable pageable) {
        log.info("[P9][CTRL][ITEM][RALL]: GET /api/items pageable=({})", pageable);

        try {

            var response = itemQueryService.display(pageable);
            return new JUINResponse<>(HttpStatus.OK, response);
        } catch (EntityNotFoundException e) {
            log.warn("[P5][CTRL][ITEM][RALL]: ({})", e.getMessage());
            return new JUINResponse<>(HttpStatus.BAD_REQUEST, null);
        }
    }

    @ApiOperation(value = "상품 검색", notes = "전체 또는 조건 별로 검색한다.")
    @GetMapping("/search")
    public JUINResponse<Page<ItemResponse.Read>> search(@PageableDefault(size = 10) Pageable pageable
                                                      , @RequestParam(required = false) Long categoryId
                                                      , @RequestParam(value = "name", required = false) String searchTitle
                                                      , @RequestParam(required = false) PersonalColor personalColor) {
        log.info("[P9][CTRL][ITEM][SARH]: GET /api/items/search pageable=({}), categoryId=({}), searchTitle=({}), personalColor=({})",
                pageable, categoryId, searchTitle, personalColor);

        try {

            var response = itemQueryService.search(pageable, searchTitle, categoryId, personalColor);
            return new JUINResponse<>(HttpStatus.OK, response);
        } catch (EntityNotFoundException e) {
            log.warn("[P5][CTRL][ITEM][SARH]: ({}), pageable=({}), categoryId=({}), searchTitle=({}), personalColor=({})",
                    e.getMessage(), pageable, categoryId, searchTitle, personalColor);
            return new JUINResponse<>(HttpStatus.BAD_REQUEST, null);
        }
    }
}

