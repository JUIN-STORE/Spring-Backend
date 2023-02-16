package store.juin.api.controller.user;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import store.juin.api.JUINResponse;
import store.juin.api.domain.entity.Item;
import store.juin.api.domain.response.ItemResponse;
import store.juin.api.service.query.ItemQueryService;
import store.juin.api.service.query.PrincipalQueryService;

import javax.persistence.EntityNotFoundException;
import java.security.Principal;

@Api(tags = {"05. Item"})
@Slf4j
@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemApiController {
    private final ItemQueryService itemQueryService;
    private final PrincipalQueryService principalQueryService;

    @ApiOperation(value = "상품 읽기", notes = "상품을 읽는다.")
    @GetMapping("/{itemId}")
    public JUINResponse<ItemResponse.Read> retrieveOne(final Principal principal
                                                       , @PathVariable Long itemId) {
        final String identification = principal.getName();
        log.info("[P9][CTRL][ITEM][RONE]: GET /api/items/{} identification=({})", itemId, identification);

        try {
            principalQueryService.readByPrincipal(principal);

            final Item item = itemQueryService.readById(itemId);

            var response = ItemResponse.Read.from(item);
            return new JUINResponse<>(HttpStatus.OK, response);
        } catch (EntityNotFoundException e) {
            log.warn("[P5][CTRL][ITEM][RONE]: ({}), identification=({})", e.getMessage(), identification);
            return new JUINResponse<>(HttpStatus.BAD_REQUEST, null);
        }
    }

    @ApiOperation(value = "상품 전체 조회", notes = "전체 또는 조건 별로 검색한다.")
    @GetMapping
    public JUINResponse<Page<ItemResponse.Read>> retrieveAll(final Principal principal
                                                            , @PageableDefault(size = 10) Pageable pageable) {
        final String identification = principal.getName();
        log.info("[P9][CTRL][ITEM][RALL]: GET /api/items pageable=({}), identification=({})", pageable, identification);

        try {
            principalQueryService.readByPrincipal(principal);

            var response = itemQueryService.display(pageable);
            return new JUINResponse<>(HttpStatus.OK, response);
        } catch (EntityNotFoundException e) {
            log.warn("[P5][CTRL][ITEM][RALL]: ({}), identification=({})", e.getMessage(), identification);
            return new JUINResponse<>(HttpStatus.BAD_REQUEST, null);
        }
    }

    @ApiOperation(value = "상품 검색", notes = "전체 또는 조건 별로 검색한다.")
    @GetMapping("/search")
    public JUINResponse<Page<ItemResponse.Read>> search(final Principal principal
                                                        , @PageableDefault(size = 10) Pageable pageable
                                                        , @RequestParam(required = false) Long categoryId
                                                        , @RequestParam(value = "name", required = false) String searchTitle
                                                        , @RequestParam(required = false) String personalColor) {
        final String identification = principal.getName();
        log.info("[P9][CTRL][ITEM][SARH]: GET /api/items/search identification=({}), pageable=({}), categoryId=({}), searchTitle=({}), personalColor=({})",
                identification, pageable, categoryId, searchTitle, personalColor);

        try {

            var response = itemQueryService.search(pageable, searchTitle, categoryId, personalColor);
            return new JUINResponse<>(HttpStatus.OK, response);
        } catch (EntityNotFoundException e) {
            log.warn("[P5][CTRL][ITEM][SARH]: ({}), identification=({}), pageable=({}), categoryId=({}), searchTitle=({}), personalColor=({})",
                    e.getMessage(), identification, pageable, categoryId, searchTitle, personalColor);
            return new JUINResponse<>(HttpStatus.BAD_REQUEST, null);
        }
    }
}

