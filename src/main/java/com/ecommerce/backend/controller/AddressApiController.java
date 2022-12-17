package com.ecommerce.backend.controller;

import com.ecommerce.backend.MyResponse;
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
    public MyResponse<Void> newAddress(final Principal principal,
                                       @RequestBody AddressRequest.Register request) {
        final Account account = principalService.readByPrincipal(principal);

        try {
            addressService.add(account, request);
            return new MyResponse<>(HttpStatus.OK, null);
        } catch (EntityNotFoundException e) {
            log.warn("POST /api/addresses -> request: {} EntityNotFoundException",  request);
            return new MyResponse<>(HttpStatus.BAD_REQUEST, null);
        }
    }

    @ApiOperation(value = "한 유저의 모든 주소 읽기", notes="한 유저의 모든 주소를 불러온다.")
    @GetMapping("/all")
    public MyResponse<List<AddressResponse.Read>> all(final Principal principal) {
        final Account account = principalService.readByPrincipal(principal);

        try {
            final List<Address> addressList = addressService.readByAccountId(account.getId());

            List<AddressResponse.Read> response = new ArrayList<>();
            for (Address address : addressList) {
                response.add(AddressResponse.Read.from(address));
            }

            return new MyResponse<>(HttpStatus.OK, response);
        } catch (EntityNotFoundException e) {
            log.warn("GET /api/addresses EntityNotFoundException");
            return new MyResponse<>(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    // FIXME: 이게 필요한가?
    @ApiOperation(value = "단건 주소 읽기", notes="주소를 불러온다.")
    @GetMapping("/{addressId}")
    public MyResponse<AddressResponse.Read> one(final Principal principal,
                                                @PathVariable Long addressId) {
        log.info("GET /api/addresses/{addressId} addressId: {}", addressId);

        final Account account = principalService.readByPrincipal(principal);

        try {
            final Address address = addressService.readByIdAndAccountId(addressId, account.getId());
            final AddressResponse.Read response = AddressResponse.Read.from(address);

            return new MyResponse<>(HttpStatus.OK, response);
        } catch (EntityNotFoundException e) {
            log.warn("GET /api/addresses/{addressId} addressId: {} EntityNotFoundException", addressId);
            return new MyResponse<>(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @ApiOperation(value = "주소 수정", notes="주소를 수정한다.")
    @PutMapping
    public MyResponse<Void> update(final Principal principal,
                                   @RequestBody AddressRequest.Update request) {

        log.info("PATCH /api/addresses/update -> request: {}", request);

        final Account account = principalService.readByPrincipal(principal);

        try {
            addressService.modify(account, request);
            return new MyResponse<>(HttpStatus.OK, null);
        } catch (EntityNotFoundException e) {
            log.warn("PATCH /api/addresses/{addressId} -> request: {} EntityNotFoundException", request);
            return new MyResponse<>(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @ApiOperation(value = "주소 삭제", notes = "주소 정보를 삭제한다.")
    @DeleteMapping("/{addressId}")
    public MyResponse<Long> delete(final Principal principal,
                                   @PathVariable Long addressId) {
        log.info("DELETE /api/addresses/{addressId} -> addressId: {}", addressId);

        final Account account = principalService.readByPrincipal(principal);

        try {
            final long action = addressService.remove(account.getId(), addressId);

            return new MyResponse<>(HttpStatus.OK, action);
        } catch (EntityNotFoundException e) {
            return new MyResponse<>(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}