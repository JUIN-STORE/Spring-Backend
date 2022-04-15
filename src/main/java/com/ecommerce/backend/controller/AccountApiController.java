package com.ecommerce.backend.controller;

import com.ecommerce.backend.MyResponse;
import com.ecommerce.backend.domain.request.AccountRequest;
import com.ecommerce.backend.domain.response.AccountResponse;
import com.ecommerce.backend.domain.response.AccountResponse.LoginResponse;
import com.ecommerce.backend.jwt.JwtTokenUtil;
import com.ecommerce.backend.service.AccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.security.Principal;

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
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;

    @ApiOperation(value = "회원가입", notes = "회원가입을 한다.")
    @PostMapping("/register")
    public MyResponse<AccountResponse.RegisterResponse> register(@RequestBody AccountRequest.RegisterRequest request) {
        try {
            log.info("Call POST /api/accounts/register: {}", request);
            return new MyResponse<>(HttpStatus.OK, "POST SUCCESS", accountService.saveAccount(request));
        } catch (EntityNotFoundException e) {
            return new MyResponse<>(HttpStatus.OK, e.getMessage(), null);
        }
    }

    @ApiOperation(value = "로그인", notes = "로그인을 한다.")
    @PostMapping("/login")
    public MyResponse<AccountResponse.LoginResponse> login(@RequestBody AccountRequest.LoginRequest request) {
        final String email = request.getEmail();
        final String password = request.getPasswordHash();
        log.info("Call POST /api/accounts/login {}", request);

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password)); // 이 시점에 쿼리 나감.
            UserDetails userDetails = accountService.loadUserByUsername(email);
            String token = jwtTokenUtil.generateToken(userDetails);
            LoginResponse response = LoginResponse.fromAccount(token);

            return new MyResponse<>(HttpStatus.OK, "POST SUCCESS", response);
        } catch (DisabledException e) {
            log.warn(e.getMessage());
        } catch (BadCredentialsException e) {
            log.warn(e.getMessage());
        } catch (EntityNotFoundException e) {
            log.warn(e.getMessage());
        }
        return new MyResponse<>(HttpStatus.NOT_FOUND, "POST FAIL", null);
    }

    @ApiOperation(value = "회원 정보 읽기", notes = "회원 정보를 읽어온다.")
    @GetMapping("/{accountId}")
    public MyResponse<AccountResponse.ReadResponse> read (@PathVariable Long accountId){
        log.info("Call GET /api/accounts/{accountId} accountId: {}", accountId);

        try {
            return new MyResponse<>(HttpStatus.OK, "GET SUCCESS", accountService.findById(accountId));
        } catch (EntityNotFoundException e) {
            log.warn(e.getMessage());
            return new MyResponse<>(HttpStatus.NOT_FOUND, "GET FAIL", null);
        }
    }

    @ApiOperation(value = "회원 정보 수정", notes = "회원 정보를 수정한다.")
    @PatchMapping("/modify")
    public MyResponse<AccountResponse.UpdateResponse> modify(@RequestBody AccountRequest.UpdateRequest request, Principal principal) {
        log.info("Call /api/accounts/modify request: {}", request);

        try{
            return new MyResponse<>(HttpStatus.OK, "MODIFY SUCCESS", accountService.update(request, principal));
        } catch (EntityNotFoundException e) {
            log.warn("Call /api/accounts/modify: {}", request);
            return new MyResponse<>(HttpStatus.NOT_FOUND, "MODIFY FAIL", null);
        }

    }

    @ApiOperation(value = "회원 정보 삭제", notes = "회원 정보를 삭제한다.")
    @DeleteMapping("/{accountId}")
    public MyResponse<AccountResponse.DeleteResponse> remove(@PathVariable Long accountId){
        log.info("Call /api/accounts/{id} accountId: {}", accountId);

        try {
            return new MyResponse<>(HttpStatus.OK, "DELETE SUCCESS", accountService.delete(accountId));
        } catch (EntityNotFoundException e) {
            log.warn("Call /api/accounts/{id} FAIL accountId: {} ", accountId);
            return new MyResponse<>(HttpStatus.NOT_FOUND, "DELETE FAIL", null);
        }
    }
}