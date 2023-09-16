package store.juin.api.order.controller;

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
import store.juin.api.order.model.request.OrderRequest;
import store.juin.api.order.model.response.OrderJoinResponse;
import store.juin.api.order.service.command.OrderCommandService;
import store.juin.api.order.service.query.OrderQueryService;
import store.juin.api.principal.service.query.PrincipalQueryService;

import java.security.Principal;

@Api(tags = {"04. Order"})
@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderQueryService orderQueryService;
    private final PrincipalQueryService principalQueryService;

    private final OrderCommandService orderCommandService;

    @ApiOperation(value = "주문하기", notes = "주문을 한다.")
    @PostMapping
    public JUINResponse<Long> create(final Principal principal
                                    , @RequestBody OrderRequest.Create request) {
        final String identification = principal.getName();
        log.info("[P9][CON][ORDR][CRTE]: POST /api/orders identification({}), request({})", identification, request);

        try {
            final Account account = principalQueryService.readByPrincipal(principal);

            var response = orderCommandService.add(account, request);
            return new JUINResponse<>(HttpStatus.CREATED, response);
        } catch (Exception e) {
            log.error("[P9][CON][ORDR][CRTE]: message=({}), identification({}), request({})", e.getMessage(), identification, request);
            return new JUINResponse<>(HttpStatus.BAD_REQUEST);
        }
    }

    // FIXME: 검색으로 봐야 될까?
    @ApiOperation(value = "주문 상세보기", notes = "주문 상세 내역을 조회한다.")
    @GetMapping
    public JUINResponse<Page<OrderJoinResponse>> retrieveAll(final Principal principal
                                                            , @ModelAttribute OrderRequest.Retrieve request
                                                            , @PageableDefault(size = 10) Pageable pageable) {
        final String identification = principal.getName();
        log.info("[P9][CON][ORDR][ALL_]: GET /api/orders identification({}), pageable({}), request({})", identification, pageable, request);

        try {
            final Account account = principalQueryService.readByPrincipal(principal);

            var response = orderQueryService.readAll(account, request, pageable);
            return new JUINResponse<>(HttpStatus.OK, response);
        } catch (Exception e) {
            log.error("[P9][CON][ORDR][ALL_]: message=({}), identification({}), pageable({}), request({})", e.getMessage(), identification, pageable, request);
            return new JUINResponse<>(HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(value = "주문 취소하기", notes = "주문 취소를 한다.")
    @PostMapping("/cancel")
    public JUINResponse<Long> cancel(final Principal principal
                                , @RequestBody OrderRequest.Cancel request) {
        final String identification = principal.getName();

        log.info("[P9][CON][ORDR][CNCL]: POST /api/orders/ identification=({}), request=({})", identification, request);
        try {
            final Account account = principalQueryService.readByPrincipal(principal);

            var response = orderCommandService.cancel(request.getOrderId(), account.getId());
            return new JUINResponse<>(HttpStatus.OK, response);
        } catch (Exception e) {
            log.error("[P9][CON][ORDR][CNCL]: message=({}), identification({}), request=({})", e.getMessage(), identification, request);
            return new JUINResponse<>(HttpStatus.BAD_REQUEST);
        }
    }
}