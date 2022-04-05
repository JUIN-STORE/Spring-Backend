package com.ecommerce.backend.controller;

import com.ecommerce.backend.MyResponse;
import com.ecommerce.backend.domain.mapper.AddressMapper;
import com.ecommerce.backend.domain.request.AddressRequest;
import com.ecommerce.backend.domain.response.AddressRes;
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
    private final AddressMapper addressMapper;

    @ApiOperation(value = "주소 생성", notes="주소를 생성한다.")
    @PostMapping("/create")
    public MyResponse<AddressRes.AddressCreateRes> create(@RequestBody AddressRequest.RegisterAddress request) {
        try {
            return new MyResponse<>(HttpStatus.OK, "POST SUCCESS", addressService.save(request));
        } catch (EntityNotFoundException e) {
            log.info(e.toString());
            log.warn("__Call POST /api/addresses/{id} EntityNotFoundException__");
            return new MyResponse<>(HttpStatus.NOT_FOUND, "ERROR", null);
        }
    }

    @ApiOperation(value = "주소 읽기", notes="주소를 불러온다.")
    @GetMapping("/{id}")
    public MyResponse<AddressRes.AddressReadRes> read(@PathVariable long id) {
        try {
            log.info("__Call GET /api/addresses/{id}__");
            return new MyResponse<>(HttpStatus.OK, "GET SUCCESS", addressService.findById(id));
        } catch (EntityNotFoundException e) {
            log.info(e.toString());
            log.warn("__Call GET /api/addresses/{id} EntityNotFoundException__");
            return new MyResponse<>(HttpStatus.NOT_FOUND, "ERROR", null);
        }
    }
}