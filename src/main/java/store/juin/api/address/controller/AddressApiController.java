package store.juin.api.address.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import store.juin.api.account.model.entity.Account;
import store.juin.api.address.model.entity.Address;
import store.juin.api.address.model.request.AddressCreateRequest;
import store.juin.api.address.model.request.AddressUpdateRequest;
import store.juin.api.address.model.response.AddressRetrieveResponse;
import store.juin.api.address.service.command.AddressCommandService;
import store.juin.api.address.service.query.AddressQueryService;
import store.juin.api.common.model.response.JUINResponse;
import store.juin.api.principal.service.query.PrincipalQueryService;

import javax.persistence.EntityNotFoundException;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/addresses")
public class AddressApiController {
    private final AddressQueryService addressQueryService;
    private final PrincipalQueryService principalQueryService;

    private final AddressCommandService addressCommandService;

    @PostMapping
    public JUINResponse<Void> create(final Principal principal,
                                     @RequestBody AddressCreateRequest request) {
        final String identification = principal.getName();
        log.info("[P9][CTRL][ADDR][CRTE]: POST /api/addresses, identification=({}), request=({})", identification, request);

        final Account account = principalQueryService.readByPrincipal(principal);

        try {
            addressCommandService.add(account, request);
            return new JUINResponse<>(HttpStatus.CREATED, null);
        } catch (EntityNotFoundException e) {
            log.warn("[P5][CTRL][ADDR][CRTE]: 회원 정보가 없습니다. identification=({}), request=({})", identification, request);
            return new JUINResponse<>(HttpStatus.BAD_REQUEST, null);
        }
    }

    @GetMapping("/all")
    public JUINResponse<List<AddressRetrieveResponse>> retrieveAll(final Principal principal) {
        final String identification = principal.getName();
        log.info("[P9][CTRL][ADDR][ALL_]: GET /api/addresses/all 한 유저의 모든 주소 읽기, identification=({})", identification);

        final Account account = principalQueryService.readByPrincipal(principal);

        try {
            final List<Address> addressList = addressQueryService.readAllByAccountId(account.getId());

            var response = addressList.stream().map(AddressRetrieveResponse::from).collect(Collectors.toList());
            return new JUINResponse<>(HttpStatus.OK, response);
        } catch (EntityNotFoundException e) {
            log.warn("[P5][CTRL][ADDR][ALL_]: 존재하지 않는 Entity입니다. identification=({}), message=({})", identification, e.getMessage(), e);
            return new JUINResponse<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{addressId}")
    public JUINResponse<AddressRetrieveResponse> retrieveOne(final Principal principal,
                                                                             @PathVariable Long addressId) {
        final String identification = principal.getName();
        log.info("[P9][CTRL][ADDR][ONE_]: GET /api/addresses/{addressId} 주소 읽기, identification=({}), addressId=({})", identification, addressId);

        final Account account = principalQueryService.readByPrincipal(principal);

        try {
            final Address address = addressQueryService.readByIdAndAccountId(addressId, account.getId());

            var response = AddressRetrieveResponse.from(address);
            return new JUINResponse<>(HttpStatus.OK, response);
        } catch (EntityNotFoundException e) {
            log.info("[P9][CTRL][ADDR][ONE_]: 존재하지 않는 Entity입니다. " +
                    "identification=({}), addressId=({}), message=({})", identification, addressId, e.getMessage(), e);
            return new JUINResponse<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping
    public JUINResponse<Void> update(final Principal principal,
                                     @RequestBody AddressUpdateRequest request) {

        final Account account = principalQueryService.readByPrincipal(principal);
        log.info("[P9][CTRL][ADDR][UPDT]: PUT /api/addresses, email=({}), request=({})", account.getEmail(), request);

        try {
            addressCommandService.modify(account, request);
            return new JUINResponse<>(HttpStatus.OK, null);
        } catch (EntityNotFoundException e) {
            log.warn("[P9][CTRL][ADDR][UPDT]: 존재하지 않는 Entity입니다. email=({}), message=({})", account.getEmail(), e.getMessage(), e);
            return new JUINResponse<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{addressId}")
    public JUINResponse<Long> delete(final Principal principal,
                                     @PathVariable Long addressId) {

        final Account account = principalQueryService.readByPrincipal(principal);
        log.info("[P9][CTRL][ADDR][DEL_]: DELETE /api/addresses/{addressId}, addressId=({}), email=({})", addressId, account.getEmail());

        try {
            final long action = addressCommandService.remove(account.getId(), addressId);

            return new JUINResponse<>(HttpStatus.OK, action);
        } catch (EntityNotFoundException e) {
            log.info("[P9][CTRL][ADDR][DEL_]: 주소 삭제, addressId=({}), email=({}), message=({})", addressId, account.getEmail(), e.getMessage(), e);
            return new JUINResponse<>(HttpStatus.BAD_REQUEST);
        }
    }
}