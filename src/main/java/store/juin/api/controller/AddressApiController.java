package store.juin.api.controller;

import store.juin.api.JUINResponse;
import store.juin.api.domain.entity.Account;
import store.juin.api.domain.entity.Address;
import store.juin.api.domain.request.AddressRequest;
import store.juin.api.domain.response.AddressResponse;
import store.juin.api.service.command.AddressCommandService;
import store.juin.api.service.query.AddressQueryService;
import store.juin.api.service.query.PrincipalQueryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Api(tags = {"02. Address"})
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/addresses")
public class AddressApiController {
    private final AddressQueryService addressQueryService;
    private final PrincipalQueryService principalQueryService;

    private final AddressCommandService addressCommandService;

    @ApiOperation(value = "주소 추가", notes="주소를 추가한다.")
    @PostMapping
    public JUINResponse<Void> create(final Principal principal,
                                     @RequestBody AddressRequest.Register request) {
        final String email = principal.getName();
        log.info("[P9][CTRL][ADDR][NEW_]: POST /api/addresses, email=({}), request=({})", email, request);

        final Account account = principalQueryService.readByPrincipal(principal);

        try {
            addressCommandService.add(account, request);
            return new JUINResponse<>(HttpStatus.OK, null);
        } catch (EntityNotFoundException e) {
            log.warn("[P5][CTRL][ADDR][NEW_]: 회원 정보가 없습니다. email=({}), request=({})", email, request);
            return new JUINResponse<>(HttpStatus.BAD_REQUEST, null);
        }
    }

    @ApiOperation(value = "한 유저의 모든 주소 읽기", notes="한 유저의 모든 주소를 불러온다.")
    @GetMapping("/all")
    public JUINResponse<List<AddressResponse.Retrieve>> retrieveAll(final Principal principal) {
        final String email = principal.getName();
        log.info("[P9][CTRL][ADDR][ALL_]: GET /api/addresses/all 한 유저의 모든 주소 읽기, email=({})", email);

        final Account account = principalQueryService.readByPrincipal(principal);

        try {
            final List<Address> addressList = addressQueryService.readAllByAccountId(account.getId());

            List<AddressResponse.Retrieve> response = new ArrayList<>();
            for (Address address : addressList) {
                response.add(AddressResponse.Retrieve.from(address));
            }

            return new JUINResponse<>(HttpStatus.OK, response);
        } catch (EntityNotFoundException e) {
            log.warn("[P5][CTRL][ADDR][ALL_]: 존재하지 않는 Entity입니다. email=({}), message=({})", email, e.getMessage(), e);
            return new JUINResponse<>(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    // FIXME: 이게 필요한가?
    @ApiOperation(value = "단건 주소 읽기", notes="주소를 불러온다.")
    @GetMapping("/{addressId}")
    public JUINResponse<AddressResponse.Retrieve> retrieveOne(final Principal principal,
                                                              @PathVariable Long addressId) {
        final String email = principal.getName();
        log.info("[P9][CTRL][ADDR][ONE_]: GET /api/addresses/{addressId} 주소 읽기, email=({}), addressId=({})", email, addressId);

        final Account account = principalQueryService.readByPrincipal(principal);

        try {
            final Address address = addressQueryService.readByIdAndAccountId(addressId, account.getId());
            final AddressResponse.Retrieve response = AddressResponse.Retrieve.from(address);

            return new JUINResponse<>(HttpStatus.OK, response);
        } catch (EntityNotFoundException e) {
            log.info("[P9][CTRL][ADDR][ONE_]: 존재하지 않는 Entity입니다. " +
                    "email=({}), addressId=({}), message=({})", email, addressId, e.getMessage(), e);
            return new JUINResponse<>(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @ApiOperation(value = "주소 수정", notes="주소를 수정한다.")
    @PutMapping
    public JUINResponse<Void> update(final Principal principal,
                                     @RequestBody AddressRequest.Update request) {

        final Account account = principalQueryService.readByPrincipal(principal);
        log.info("[P9][CTRL][ADDR][UPDT]: PUT /api/addresses, email=({}), request=({})", account.getEmail(), request);

        try {
            addressCommandService.modify(account, request);
            return new JUINResponse<>(HttpStatus.OK, null);
        } catch (EntityNotFoundException e) {
            log.warn("[P9][CTRL][ADDR][UPDT]: 존재하지 않는 Entity입니다. email=({}), message=({})", account.getEmail(), e.getMessage(), e);
            return new JUINResponse<>(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @ApiOperation(value = "주소 삭제", notes = "주소 정보를 삭제한다.")
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
            return new JUINResponse<>(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}