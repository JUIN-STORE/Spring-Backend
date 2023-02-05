package store.juin.api.controller;

import store.juin.api.JUINResponse;
import store.juin.api.domain.entity.Account;
import store.juin.api.domain.entity.Token;
import store.juin.api.domain.request.AccountRequest;
import store.juin.api.domain.response.AccountResponse;
import store.juin.api.jwt.TokenMessage;
import store.juin.api.service.command.AccountCommandService;
import store.juin.api.service.command.TokenCommandService;
import store.juin.api.service.query.PrincipalQueryService;
import store.juin.api.service.query.TokenQueryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;

import static org.springframework.http.HttpHeaders.SET_COOKIE;

@Api(tags = {"01. Account"})
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accounts")
public class AccountApiController {
    private final AuthenticationManager authenticationManager;

    private final TokenQueryService tokenQueryService;
    private final PrincipalQueryService principalQueryService;

    private final TokenCommandService tokenCommandService;
    private final AccountCommandService accountCommandService;

    @Value("${front.cookie.domain}")
    private String cookieDomain;

    @ApiOperation(value = "회원가입", notes = "회원가입을 한다.")
    @PostMapping("/sign-up")
    public JUINResponse<AccountResponse.SignUp> signUp(@RequestBody AccountRequest.SignUp request) {
        log.info("[P9][CTRL][ACNT][SIGN]: POST /api/accounts/sign-up, request = ({})", request);

        try {
            final Account account = accountCommandService.add(request);

            var response = AccountResponse.SignUp.from(account);
            return new JUINResponse<>(HttpStatus.OK, response);
        } catch (EntityExistsException e) {
            log.warn("[P5][CTRL][ACNT][SIGN]: message=({})", e.getMessage());
            return new JUINResponse<>(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            log.error("[P1][CTRL][ACNT][SIGN]: 알 수 없는 예외가 발생했습니다. message=({})", e.getMessage());
            return new JUINResponse<>(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @ApiOperation(value = "로그인", notes = "로그인을 한다.")
    @PostMapping("/login")
    public JUINResponse<AccountResponse.Login> login(@RequestBody AccountRequest.Login request,
                                                     HttpServletResponse httpServletResponse) {
        log.info("[P9][CTRL][ACNT][LOIN]: POST /api/accounts/login, request=({})", request);

        try {
            // 이 시점에 １번　쿼리 나감.
            final Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPasswordHash())
                    );

            final String email = authentication.getName();
            final String accessToken = tokenCommandService.addAccessToken(email);
            final String refreshToken = tokenCommandService.upsertRefreshToken(email);

            ResponseCookie cookie = ResponseCookie.from(TokenMessage.REFRESH_TOKEN, refreshToken)
                    .domain(cookieDomain)
                    .path("/")
                    .httpOnly(true)
                    .secure(true)
                    .sameSite("None")
                    .build();
            httpServletResponse.addHeader(SET_COOKIE, cookie.toString());

            var response = AccountResponse.Login.of(email, accessToken);
            return new JUINResponse<>(HttpStatus.OK, response);
        } catch (EntityNotFoundException | BadCredentialsException e) {
            log.warn("[P5][CTRL][ACNT][LOIN]: 회원 정보가 없습니다. request=({})", request);
            return new JUINResponse<>(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @ApiOperation(value = "로그아웃", notes = "로그아웃을 한다.")
    @GetMapping("/logout")
    public JUINResponse<String> logout(final Principal principal
                                    , HttpServletRequest httpServletRequest
                                    , HttpServletResponse httpServletResponse) {

        final String email = principal.getName();
        log.info("[P9][CTRL][ACNT][LOUT]: GET /api/accounts/logout, email=({})", email);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(httpServletRequest, httpServletResponse, auth);
        }

        // 쿠키 삭제
        Cookie[] cookies = httpServletRequest.getCookies();
        for (Cookie cookie : cookies) {
            cookie.setDomain(cookieDomain);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            cookie.setMaxAge(0);
            httpServletResponse.addCookie(cookie);
        }

        Token token = tokenQueryService.readByEmail(email);
        tokenCommandService.modifyRefreshToken(token, "");

        return new JUINResponse<>(HttpStatus.OK, "로그아웃 되었습니다.");
    }

    @ApiOperation(value = "내 정보 읽기", notes = "내 정보를 읽어온다.")
    @GetMapping("/profile")
    public JUINResponse<AccountResponse.Retrieve> profile(final Principal principal) {
        final String email = principal.getName();

        log.info("[P9][CTRL][ACNT][PROF]: GET /api/accounts/profile, email=({})", email);

        try {
            final Account account = principalQueryService.readByPrincipal(principal);

            var response = AccountResponse.Retrieve.from(account);
            return new JUINResponse<>(HttpStatus.OK, response);
        } catch (EntityNotFoundException e) {
            log.warn("[P5][CTRL][ACNT][PROF]: 회원 정보가 없습니다. email=({})", email);
            return new JUINResponse<>(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @ApiOperation(value = "회원 정보 수정", notes = "회원 정보를 수정한다.")
    @PatchMapping
    public JUINResponse<AccountResponse.Update> update(final Principal principal,
                                                       @RequestBody AccountRequest.Update request) {
        log.info("[P9][CTRL][ACNT][UPDE]: PATCH /api/accounts request=({})", request);

        try {
            final Account account = principalQueryService.readByPrincipal(principal);
            final Account updateAccount = accountCommandService.modify(account, request);

            var response = AccountResponse.Update.from(updateAccount);
            return new JUINResponse<>(HttpStatus.OK, response);
        } catch (EntityNotFoundException e) {
            log.warn("[P5][CTRL][ACNT][UPDE]: 회원 정보가 없습니다. request=({})", request);
            return new JUINResponse<>(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @ApiOperation(value = "회원 정보 삭제", notes = "회원 정보를 삭제한다.")
    @DeleteMapping("/{accountId}")
    public JUINResponse<AccountResponse.Delete> delete(final Principal principal,
                                                       @PathVariable Long accountId) {
        log.info("[P9][CTRL][ACNT][DELE]: DELETE /api/accounts/{accountId}, accountId=({})", accountId);

        try {
            final Account account = principalQueryService.readByPrincipal(principal);
            final Account deleteAccount = accountCommandService.remove(account, accountId);

            var response = AccountResponse.Delete.from(deleteAccount);
            return new JUINResponse<>(HttpStatus.OK, response);
        } catch (EntityNotFoundException e) {
            log.warn("[P5][CTRL][ACNT][DELE]: 회원 정보가 없습니다. accountId=({})", accountId);
            return new JUINResponse<>(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}