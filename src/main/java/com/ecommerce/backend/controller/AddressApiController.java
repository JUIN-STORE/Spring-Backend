package com.ecommerce.backend.controller;

import com.ecommerce.backend.JZResponse;
import com.ecommerce.backend.domain.entity.Account;
import com.ecommerce.backend.domain.entity.Address;
import com.ecommerce.backend.domain.request.AddressRequest;
import com.ecommerce.backend.domain.response.AddressResponse;
import com.ecommerce.backend.service.AddressService;
import com.ecommerce.backend.service.PrincipalService;
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
    private final PrincipalService principalService;

    private final AddressService addressService;

    @ApiOperation(value = "주소 추가", notes="주소를 추가한다.")
    @PostMapping
    public JZResponse<Void> newAddress(final Principal principal,
                                       @RequestBody AddressRequest.Register request) {
        final String email = principal.getName();
        log.info("[P9][CON][ADDR][NEW_]: 주소 추가, email=({}), request=({})", email, request);

        final Account account = principalService.readByPrincipal(principal);

        try {
            addressService.add(account, request);
            return new JZResponse<>(HttpStatus.OK, null);
        } catch (EntityNotFoundException e) {
            log.warn("[P5][CON][ADDR][NEW_]: 회원 정보가 없습니다. email=({}), request=({})", email, request);
            return new JZResponse<>(HttpStatus.BAD_REQUEST, null);
        }
    }

    @ApiOperation(value = "한 유저의 모든 주소 읽기", notes="한 유저의 모든 주소를 불러온다.")
    @GetMapping("/all")
    public JZResponse<List<AddressResponse.Read>> all(final Principal principal) {
        log.info("[P9][CON][ADDR][ALL_]: 한 유저의 모든 주소 읽기, email=({})", principal.getName());

        final Account account = principalService.readByPrincipal(principal);

        try {
            final List<Address> addressList = addressService.readByAccountId(account.getId());

            List<AddressResponse.Read> response = new ArrayList<>();
            for (Address address : addressList) {
                response.add(AddressResponse.Read.from(address));
            }

            return new JZResponse<>(HttpStatus.OK, response);
        } catch (EntityNotFoundException e) {
            log.warn("GET /api/addresses EntityNotFoundException");
            return new JZResponse<>(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    // FIXME: 이게 필요한가?
    @ApiOperation(value = "단건 주소 읽기", notes="주소를 불러온다.")
    @GetMapping("/{addressId}")
    public JZResponse<AddressResponse.Read> one(final Principal principal,
                                                @PathVariable Long addressId) {
        log.info("[P9][CON][ADDR][ONE_]: 주소 읽기, email=({}), addressId=({})", principal.getName(), addressId);

        final Account account = principalService.readByPrincipal(principal);

        try {
            final Address address = addressService.readByIdAndAccountId(addressId, account.getId());
            final AddressResponse.Read response = AddressResponse.Read.from(address);

            return new JZResponse<>(HttpStatus.OK, response);
        } catch (EntityNotFoundException e) {
            log.warn("GET /api/addresses/{addressId} addressId: {} EntityNotFoundException", addressId);
            return new JZResponse<>(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @ApiOperation(value = "주소 수정", notes="주소를 수정한다.")
    @PutMapping
    public JZResponse<Void> update(final Principal principal,
                                   @RequestBody AddressRequest.Update request) {

        log.info("PATCH /api/addresses/update -> request: {}", request);

        final Account account = principalService.readByPrincipal(principal);

        try {
            addressService.modify(account, request);
            return new JZResponse<>(HttpStatus.OK, null);
        } catch (EntityNotFoundException e) {
            log.warn("PATCH /api/addresses/{addressId} -> request: {} EntityNotFoundException", request);
            return new JZResponse<>(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @ApiOperation(value = "주소 삭제", notes = "주소 정보를 삭제한다.")
    @DeleteMapping("/{addressId}")
    public JZResponse<Long> delete(final Principal principal,
                                   @PathVariable Long addressId) {
        log.info("DELETE /api/addresses/{addressId} -> addressId: {}", addressId);

        final Account account = principalService.readByPrincipal(principal);

        try {
            final long action = addressService.remove(account.getId(), addressId);

            return new JZResponse<>(HttpStatus.OK, action);
        } catch (EntityNotFoundException e) {
            return new JZResponse<>(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}