package com.ecommerce.backend.controller;

import com.ecommerce.backend.JZResponse;
import com.ecommerce.backend.domain.entity.Account;
import com.ecommerce.backend.domain.request.AccountRequest;
import com.ecommerce.backend.domain.response.AccountResponse;
import com.ecommerce.backend.jwt.TokenProvider;
import com.ecommerce.backend.service.AccountService;
import com.ecommerce.backend.service.TokenService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.security.Principal;

@Api(tags = {"01. Account"})
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/accounts")
public class AccountApiController {
    private final AccountService accountService;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final TokenService tokenService;

    @ApiOperation(value = "회원가입", notes = "회원가입을 한다.")
    @PostMapping("/sign-up")
    public JZResponse<AccountResponse.SignUp> signUp(@RequestBody AccountRequest.SignUp request) {
        log.info("POST /api/accounts/sign-up request: {}", request);

        try {
            final Account account = accountService.add(request);
            final AccountResponse.SignUp response = AccountResponse.SignUp.from(account);

            return new JZResponse<>(HttpStatus.OK, response);
        } catch (EntityNotFoundException e) {
            return new JZResponse<>(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @ApiOperation(value = "로그인", notes = "로그인을 한다.")
    @PostMapping("/login")
    public JZResponse<AccountResponse.Login> login(@RequestBody AccountRequest.Login request) {
        log.info("POST /api/accounts/login request: {}", request);

        try {
            // 이 시점에 １번　쿼리 나감.
            final Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(request.getEmail(),  request.getPasswordHash())
                    );

            final String email = authentication.getName();
            final String accessToken = tokenService.addAccessToken(email);

            final AccountResponse.Login response = AccountResponse.Login.of(email, accessToken, tokenService.upsertRefreshToken(email));

            return new JZResponse<>(HttpStatus.OK, response);
        } catch (Exception e) {
            loginException(e);
            return new JZResponse<>(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @ApiOperation(value = "내 정보 읽기", notes = "내 정보를 읽어온다.")
    @GetMapping("/profile")
    public JZResponse<AccountResponse.Read> profile(Principal principal) {
        log.info("POST /api/accounts/profile principal: {}", principal);

        try {
            final Account account = accountService.readByEmail(principal.getName());
            final AccountResponse.Read response = AccountResponse.Read.from(account);

            return new JZResponse<>(HttpStatus.OK, response);
        } catch (EntityNotFoundException e) {
            log.warn("EntityNotFoundException - GET /api/accounts/profile principal: {}", principal);
            return new JZResponse<>(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @ApiOperation(value = "회원 정보 수정", notes = "회원 정보를 수정한다.")
    @PatchMapping("/update")
    public JZResponse<AccountResponse.Update> update(final Principal principal,
                                                     @RequestBody AccountRequest.Update request) {
        log.debug("Patch /api/accounts/modify request: {}", request);

        try {
            final Account account = accountService.modify(principal, request);
            final AccountResponse.Update response = AccountResponse.Update.from(account);

            return new JZResponse<>(HttpStatus.OK, response);
        } catch (EntityNotFoundException e) {
            log.warn("EntityNotFoundException - Patch /api/accounts/modify request: {}", request);
            return new JZResponse<>(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @ApiOperation(value = "회원 정보 삭제", notes = "회원 정보를 삭제한다.")
    @DeleteMapping("/{accountId}")
    public JZResponse<AccountResponse.Delete> delete(final Principal principal,
                                                     @PathVariable Long accountId) {
        log.debug("Delete /api/accounts/{id} principal: {}", principal);

        try {
            final Account account = accountService.remove(principal, accountId);
            final AccountResponse.Delete response = AccountResponse.Delete.from(account);

            return new JZResponse<>(HttpStatus.OK, response);
        } catch (EntityNotFoundException e) {
            return new JZResponse<>(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    private void loginException(Exception e) {
        if (e instanceof DisabledException) log.warn("DisabledException - 스프링 시큐리티 오류");
        if (e instanceof EntityNotFoundException) log.warn("EntityNotFoundException - 디비에 정보 없음.");
        if (e instanceof BadCredentialsException) log.warn("BadCredentialsException - 스프링 시큐리티 오류");
    }
}