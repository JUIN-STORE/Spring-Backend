package store.juin.api.review.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import store.juin.api.account.model.entity.Account;
import store.juin.api.common.model.response.JUINResponse;
import store.juin.api.principal.service.query.PrincipalQueryService;
import store.juin.api.review.model.request.ReviewRequest;
import store.juin.api.review.model.response.ReviewResponse;
import store.juin.api.review.service.command.ReviewCommandService;
import store.juin.api.review.service.query.ReviewQueryService;

import javax.persistence.EntityNotFoundException;
import java.security.Principal;

@Api(tags = {"Review"})
@Slf4j
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewCommandService reviewCommandService;
    private final ReviewQueryService reviewQueryService;
    private final PrincipalQueryService principalQueryService;

    @ApiOperation(value = "리뷰 등록", notes = "상품 리뷰를 작성한다.")
    @PostMapping
    public JUINResponse<Long> create(final Principal principal,
                                     @RequestBody ReviewRequest.Add request) {
        log.info("[P9][CTRL][REV_][CRTE]: GET /api/reviews identification=({}), request=({})", principal.getName(), request);

        try {
            final Account account = principalQueryService.readByPrincipal(principal);
            final var response = reviewCommandService.add(account, request);
            return new JUINResponse<>(HttpStatus.OK, response);
        } catch (EntityNotFoundException e) {
            log.warn("[P2][CTRL][REV_][CRTE]: message=({})", e.getMessage());
            return new JUINResponse<>(HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(value = "리뷰 수정", notes = "본인이 작성한 리뷰를 수정한다.")
    @PutMapping("/{reviewId}")
    public JUINResponse<Long> update(final Principal principal,
                                     @PathVariable long reviewId,
                                     @RequestBody ReviewRequest.Update request) {
        log.info("[P9][CTRL][REV_][UPDT]: GET /api/reviews identification=({}), reviewId=({}), request=({})", principal.getName(), reviewId, request);

        try {
            final Account account = principalQueryService.readByPrincipal(principal);
            final var response = reviewCommandService.modify(account.getId(), reviewId, request);
            return new JUINResponse<>(HttpStatus.OK, response);
        } catch (EntityNotFoundException e) {
            log.warn("[P2][CTRL][REV_][UPDT]: message=({})", e.getMessage());
            return new JUINResponse<>(HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(value = "리뷰 삭제", notes = "본인이 작성한 리뷰를 삭제한다.")
    @DeleteMapping("/{reviewId}")
    public JUINResponse<Long> delete(final Principal principal,
                                     @PathVariable long reviewId) {
        log.info("[P9][CTRL][REV_][DELE]: GET /api/reviews identification=({}), reviewId=({})", principal.getName(), reviewId);

        try {
            final Account account = principalQueryService.readByPrincipal(principal);
            reviewCommandService.remove(account.getId(), reviewId);
            return new JUINResponse<>(HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            log.warn("[P9][CTRL][REV_][DELE]: message=({})", e.getMessage());
            return new JUINResponse<>(HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(value = "리뷰 조회", notes = "특정 상품에 해당하는 리뷰 목록을 조회한다.")
    @GetMapping
    public JUINResponse<Page<ReviewResponse.Read>> retrieveAll(@RequestParam long itemId,
                                                               @PageableDefault(size = 10) Pageable pageable) {
        log.info("[P9][CTRL][REV_][RALL]: GET /api/reviews itemId=({}), pageable=({})", itemId, pageable);

        try {
            var response = reviewQueryService.readAll(itemId, pageable);
            return new JUINResponse<>(HttpStatus.OK, response);
        } catch (EntityNotFoundException e) {
            log.warn("[P2][CTRL][REV_][RALL]: message=({})", e.getMessage());
            return new JUINResponse<>(HttpStatus.BAD_REQUEST);
        }
    }
}
