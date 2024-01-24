package store.juin.api.delivery.controller;

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
import store.juin.api.delivery.model.response.DeliveryRetrieveResponse;
import store.juin.api.delivery.service.DeliveryQueryService;
import store.juin.api.principal.service.query.PrincipalQueryService;

import javax.persistence.EntityNotFoundException;
import java.security.InvalidParameterException;
import java.security.Principal;

@Slf4j
@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
public class DeliveryApiController {
    private final DeliveryQueryService deliveryQueryService;
    private final PrincipalQueryService principalQueryService;

    @GetMapping("/{deliveryId}")
    public JUINResponse<DeliveryRetrieveResponse> retrieveOne(final Principal principal, @PathVariable Long deliveryId) {
        final String identification = principal.getName();
        log.info("[P9][CTRL][DLVR][ONE_]: identification({}), deliveryId({})", identification, deliveryId);

        try {
            final Account account = principalQueryService.readByPrincipal(principal);
            final Delivery delivery = deliveryQueryService.readById(deliveryId, account.getId());

            var response = DeliveryRetrieveResponse.from(delivery);
            return new JUINResponse<>(HttpStatus.OK, response);
        } catch (EntityNotFoundException | InvalidParameterException e) {
            log.warn("[P9][CTRL][DLVR][ONE_]: ({}), identification({}), deliveryId({})", e.getMessage(), identification, deliveryId);
            return new JUINResponse<>(HttpStatus.NOT_FOUND);
        }
    }
}
