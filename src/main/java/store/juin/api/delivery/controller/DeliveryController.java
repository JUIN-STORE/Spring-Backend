package store.juin.api.delivery.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.juin.api.account.model.entity.Account;
import store.juin.api.common.model.response.JUINResponse;
import store.juin.api.delivery.model.entity.Delivery;
import store.juin.api.delivery.model.response.DeliveryResponse;
import store.juin.api.delivery.service.DeliveryQueryService;
import store.juin.api.principal.service.query.PrincipalQueryService;

import javax.persistence.EntityNotFoundException;
import java.security.InvalidParameterException;
import java.security.Principal;

@Api(tags = {"07. Delivery"})
@Slf4j
@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
public class DeliveryController {
    private final DeliveryQueryService deliveryQueryService;
    private final PrincipalQueryService principalQueryService;

    @ApiOperation(value = "배송 상세 조회", notes = "주문에 대한 배송 상세 내용을 조회한다.")
    @GetMapping("/{deliveryId}")
    public JUINResponse<DeliveryResponse.Read> retrieveOne(final Principal principal,
                                                           @PathVariable Long deliveryId) {
        final String identification = principal.getName();
        log.info("[P9][CTRL][DLVR][ONE_]: identification({}), deliveryId({})", identification, deliveryId);

        try {
            final Account account = principalQueryService.readByPrincipal(principal);
            final Delivery delivery = deliveryQueryService.readById(deliveryId, account.getId());

            var response = DeliveryResponse.Read.from(delivery);
            return new JUINResponse<>(HttpStatus.OK, response);
        } catch (EntityNotFoundException | InvalidParameterException e) {
            log.warn("[P9][CTRL][DLVR][ONE_]: ({}), identification({}), deliveryId({})", e.getMessage(), identification, deliveryId);
            return new JUINResponse<>(HttpStatus.NOT_FOUND);
        }
    }
}
