package com.ecommerce.backend.controller;

import com.ecommerce.backend.MyResponse;
import com.ecommerce.backend.domain.request.AddressRequest;
import com.ecommerce.backend.domain.response.AddressResponse;
import com.ecommerce.backend.service.AddressService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;

/** Controller Naming
 * C -> create
 * R -> read
 * U -> modify
 * D -> remove
 */

@Api(tags = {"02. Address"})
@Slf4j
@RestController
@RequestMapping("/api/addresses")
@AllArgsConstructor
public class AddressApiController {
    private final AddressService addressService;

    @ApiOperation(value = "주소 추가", notes="주소를 추가한다.")
    @PostMapping("/create")
    public MyResponse<AddressResponse.AddressCreate> create(Long accountId, @RequestBody AddressRequest.RegisterAddress request) {
        try {
            log.info("POST /api/addresses/create accountId: {}, request: {}", accountId, request);
            return new MyResponse<>(HttpStatus.OK, "주소 추가 성공", addressService.save(accountId, request));
        } catch (EntityNotFoundException e) {
            log.warn("__Call POST /api/addresses/{id} EntityNotFoundException__");
            return new MyResponse<>(HttpStatus.NOT_FOUND, "ERROR", null);
        }
    }

    @ApiOperation(value = "주소 읽기", notes="주소를 불러온다.")
    @GetMapping("/{addressId}")
    public MyResponse<AddressResponse.AddressRead> read(@PathVariable Long addressId) {
        try {
            log.info("GET /api/addresses/{id} id: {}", addressId);
            return new MyResponse<>(HttpStatus.OK, "GET SUCCESS", addressService.findById(addressId));
        } catch (EntityNotFoundException e) {
            log.warn("__Call GET /api/addresses/{id} EntityNotFoundException__");
            return new MyResponse<>(HttpStatus.NOT_FOUND, "ERROR", null);
        }
    }

    @ApiOperation(value = "주소 삭제", notes = "주소 정보를 삭제한다.")
    @DeleteMapping("/{addressId}")
    public MyResponse<AddressResponse.AddressDelete> remove(@PathVariable Long addressId){
        try {
            log.info("DELETE /api/addresses/{addressId}");
            return new MyResponse<>(HttpStatus.OK, "DELETE SUCCESS", addressService.delete(addressId));
        } catch (EntityNotFoundException e) {
            return new MyResponse<>(HttpStatus.OK, "ENTITY NOT FOUND", null);
        }
    }
}