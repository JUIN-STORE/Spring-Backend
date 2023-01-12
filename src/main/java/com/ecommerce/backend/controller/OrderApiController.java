package com.ecommerce.backend.controller;

import com.ecommerce.backend.JUINResponse;
import com.ecommerce.backend.domain.entity.Account;
import com.ecommerce.backend.domain.entity.Order;
import com.ecommerce.backend.domain.request.OrderRequest;
import com.ecommerce.backend.domain.response.OrderJoinResponse;
import com.ecommerce.backend.domain.response.OrderResponse;
import com.ecommerce.backend.service.command.OrderCommandService;
import com.ecommerce.backend.service.query.OrderQueryService;
import com.ecommerce.backend.service.query.PrincipalQueryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@Api(tags = {"04. Order"})
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderApiController {
    private final OrderQueryService orderQueryService;
    private final PrincipalQueryService principalQueryService;

    private final OrderCommandService orderCommandService;

    @ApiOperation(value = "주문 상세보기", notes = "주문 상세 내역을 조회한다.")
    @GetMapping
    public JUINResponse<Page<OrderJoinResponse>> retrieveAll(Principal principal,
                                                            @Valid @ModelAttribute OrderRequest.Retrieve request,
                                                            @PageableDefault(size = 10) Pageable pageable) {
        log.info("[P9][CON][ORDR][ALL_]: GET /api/orders pageable({}), request({})", pageable, request);

        final Account account = principalQueryService.readByPrincipal(principal);

        var response = orderQueryService.readAll(account, request, pageable);
        return new JUINResponse<>(HttpStatus.OK, response);
    }

    @ApiOperation(value = "주문하기", notes = "주문을 한다.")
    @PostMapping("/new")
    public JUINResponse<OrderResponse.Create> create(final Principal principal,
                                                     @RequestBody OrderRequest.Create request) {
        log.info("[P9][CON][ORDR][NEW_]: POST /api/orders/new request({})", request);

        final Account account = principalQueryService.readByPrincipal(principal);
        final Order order = orderCommandService.add(account, request);

        var response = OrderResponse.Create.of(order);
        return new JUINResponse<>(HttpStatus.OK, response);
    }

    @ApiOperation(value = "주문 취소하기", notes = "주문 취소를 한다.")
    @DeleteMapping("/cancel/{orderId}")
    public JUINResponse<OrderResponse.Create> cancel(final Principal principal,
                                                     @PathVariable Long orderId) {
        log.info("[P9][CON][ORDR][CNCL]: DELETE /api/orders/cancel orderId({})", orderId);
        final Account account = principalQueryService.readByPrincipal(principal);

        orderCommandService.cancel(orderId, account.getId());
        return new JUINResponse<>(HttpStatus.OK, null);
    }
}