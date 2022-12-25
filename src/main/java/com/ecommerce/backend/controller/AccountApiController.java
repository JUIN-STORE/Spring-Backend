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
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;

import static org.springframework.http.HttpHeaders.SET_COOKIE;

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
        log.info("[P9][CON][ACNT][SIGN]: 회원가입 요청, request = ({})", request);

        try {
            final Account account = accountService.add(request);
            final AccountResponse.SignUp response = AccountResponse.SignUp.from(account);

            return new JZResponse<>(HttpStatus.OK, response);
        } catch (Exception e) {
            log.error("[P1][CON][ACNT][SIGN]: 알 수 없는 예외가 발생했습니다. message=({})", e.getMessage());
            return new JZResponse<>(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @ApiOperation(value = "로그인", notes = "로그인을 한다.")
    @PostMapping("/login")
    public JZResponse<AccountResponse.Login> login(@RequestBody AccountRequest.Login request,
                                                   HttpServletResponse httpServletResponse) {
        log.info("[P9][CON][ACNT][LOIN]: 로그인 요청, request=({})", request);

        try {
            // 이 시점에 １번　쿼리 나감.
            final Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPasswordHash())
                    );

            final String email = authentication.getName();
            final String accessToken = tokenService.addAccessToken(email);
            final String refreshToken = tokenService.upsertRefreshToken(email);

            final AccountResponse.Login response = AccountResponse.Login.of(email, accessToken);

            ResponseCookie cookie = ResponseCookie.from("Refresh-Token", refreshToken)
                    .domain("localhost")
                    .path("/")
                    .httpOnly(true)
                    .secure(true)
                    .sameSite("None")
                    .build();
            httpServletResponse.addHeader(SET_COOKIE, cookie.toString());

            return new JZResponse<>(HttpStatus.OK, response);
        } catch (EntityNotFoundException | BadCredentialsException e) {
            log.warn("[P5][CON][ACNT][LOIN]: 회원 정보가 없습니다. request=({})", request);
            return new JZResponse<>(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @ApiOperation(value = "내 정보 읽기", notes = "내 정보를 읽어온다.")
    @GetMapping("/profile")
    public JZResponse<AccountResponse.Read> profile(Principal principal) {
        final String email = principal.getName();

        log.info("[P9][CON][ACNT][PROF]: 내 정보 읽기, email=({})", email);

        try {
            final Account account = accountService.readByEmail(email);
            final AccountResponse.Read response = AccountResponse.Read.from(account);

            return new JZResponse<>(HttpStatus.OK, response);
        } catch (EntityNotFoundException e) {
            log.warn("[P5][CON][ACNT][PROF]: 회원 정보가 없습니다. email=({})", email);
            return new JZResponse<>(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @ApiOperation(value = "회원 정보 수정", notes = "회원 정보를 수정한다.")
    @PatchMapping("/update")
    public JZResponse<AccountResponse.Update> update(final Principal principal,
                                                     @RequestBody AccountRequest.Update request) {
        log.info("[P9][CON][ACNT][UPDE]: 회원 정보 수정, request=({})", request);

        try {
            final Account account = accountService.modify(principal, request);
            final AccountResponse.Update response = AccountResponse.Update.from(account);

            return new JZResponse<>(HttpStatus.OK, response);
        } catch (EntityNotFoundException e) {
            log.warn("[P5][CON][ACNT][UPDE]: 회원 정보가 없습니다. request=({})", request);
            return new JZResponse<>(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @ApiOperation(value = "회원 정보 삭제", notes = "회원 정보를 삭제한다.")
    @DeleteMapping("/{accountId}")
    public JZResponse<AccountResponse.Delete> delete(final Principal principal,
                                                     @PathVariable Long accountId) {
        log.info("[P9][CON][ACNT][DELE]: 회원 정보 삭제, accountId=({})", accountId);

        try {
            final Account account = accountService.remove(principal, accountId);
            final AccountResponse.Delete response = AccountResponse.Delete.from(account);

            return new JZResponse<>(HttpStatus.OK, response);
        } catch (EntityNotFoundException e) {
            log.warn("[P5][CON][ACNT][DELE]: 회원 정보가 없습니다. accountId=({})", accountId);
            return new JZResponse<>(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}