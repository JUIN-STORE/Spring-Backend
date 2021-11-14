package com.ecommerce.backend.controller;

import com.ecommerce.backend.MyResponse;
import com.ecommerce.backend.domain.response.AccountResponse.CreateResponse;
import com.ecommerce.backend.service.AccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;

import static com.ecommerce.backend.domain.request.AccountRequest.CreateRequest;
import static com.ecommerce.backend.domain.response.AccountResponse.DeleteResponse;
import static com.ecommerce.backend.domain.response.AccountResponse.ReadResponse;

/** Controller Naming
 * C -> create
 * R -> read
 * U -> modify
 * D -> remove
 */

@Api(tags = {"01. Account"})
@Slf4j
@RestController
@RequestMapping("/api/accounts")
@AllArgsConstructor
public class AccountApiController {
    private final AccountService accountService;
    private final String ERROR_MESSAGE = "ERROR";

    @ApiOperation(value = "회원가입", notes = "회원가입을 한다.")
    @PostMapping("/register")
    public MyResponse<CreateResponse> create(@RequestBody CreateRequest request) {
        try {
            log.info("__Call POST /api/accounts/register__");
            return new MyResponse<>(HttpStatus.OK, "POST SUCCESS", accountService.save(request));
        } catch (Exception e) {
            log.warn("__Call POST /api/accounts/register Exception__");
            return new MyResponse<>(HttpStatus.NOT_FOUND, ERROR_MESSAGE, null);
        }
    }

    @ApiOperation(value = "회원 정보 읽기", notes = "회원 정보를 읽어온다.")
    @GetMapping("/{id}")
    public MyResponse<ReadResponse> read(@PathVariable Long id) {
        try {
            log.info("__Call GET /api/accounts/{id}__");
            return new MyResponse<>(HttpStatus.OK, "GET SUCCESS", accountService.findById(id));
        } catch (EntityNotFoundException e) {
            log.warn("__Call GET /api/accounts/{id} EntityNotFoundException__");
            return new MyResponse<>(HttpStatus.NOT_FOUND, ERROR_MESSAGE, null);
        }
    }

    @ApiOperation(value = "회원 정보 삭제", notes = "회원 정보를 삭제한다.")
    @DeleteMapping("/{id}")
    public MyResponse<DeleteResponse> remove(@PathVariable Long id) {
        try {
            log.info("__Call DELETE /api/accounts/{id}__");
            return new MyResponse<>(HttpStatus.OK, "DELETE SUCCESS", accountService.delete(id));
        } catch (EntityNotFoundException e) {
            log.warn("__Call DELETE /api/accounts/{id} EntityNotFoundException__");
            return new MyResponse<>(HttpStatus.NOT_FOUND, "DELETE" + ERROR_MESSAGE, null);
        }
    }
}