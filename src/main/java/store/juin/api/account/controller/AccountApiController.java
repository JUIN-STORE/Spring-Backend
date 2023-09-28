package store.juin.api.account.controller;

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
import store.juin.api.account.model.entity.Account;
import store.juin.api.account.model.request.*;
import store.juin.api.account.model.response.*;
import store.juin.api.account.service.command.AccountCommandService;
import store.juin.api.account.service.query.AccountQueryService;
import store.juin.api.common.model.response.JUINResponse;
import store.juin.api.principal.service.query.PrincipalQueryService;
import store.juin.api.token.jwt.TokenMessage;
import store.juin.api.token.model.entity.Token;
import store.juin.api.token.service.TokenCommandService;
import store.juin.api.token.service.TokenQueryService;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;

import static org.springframework.http.HttpHeaders.SET_COOKIE;

@Api(tags = {"Account"})
@Slf4j
@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountApiController {
    private final AuthenticationManager authenticationManager;

    private final TokenQueryService tokenQueryService;
    private final AccountQueryService accountQueryService;
    private final PrincipalQueryService principalQueryService;

    private final TokenCommandService tokenCommandService;
    private final AccountCommandService accountCommandService;

    @Value("${front.cookie.domain}")
    private String cookieDomain;

    @ApiOperation(value = "회원가입", notes = "회원가입을 한다.")
    @PostMapping("/sign-up")
    public JUINResponse<AccountSignUpResponse> signUp(@RequestBody AccountSignUpRequest request) {
        log.info("[P9][CTRL][ACNT][SIGN]: POST /api/accounts/sign-up, request = ({})", request);

        final boolean confirmed = accountCommandService.isConfirmed(request.getEmail());

        if (!confirmed) {
            log.warn("[P5][CTRL][ACNT][SIGN]: 이메일 인증이 되지 않았습니다. request=({})", request);
            return new JUINResponse<>(HttpStatus.BAD_REQUEST);
        }

        try {
            final Account account = accountCommandService.add(request);

            var response = AccountSignUpResponse.from(account);
            return new JUINResponse<>(HttpStatus.OK, response);
        } catch (EntityExistsException e) {
            log.warn("[P5][CTRL][ACNT][SIGN]: message=({})", e.getMessage());
            return new JUINResponse<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("[P1][CTRL][ACNT][SIGN]: 알 수 없는 예외가 발생했습니다. message=({})", e.getMessage());
            return new JUINResponse<>(HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation(value = "로그인", notes = "로그인을 한다.")
    @PostMapping("/sign-in")
    public JUINResponse<AccountSignInResponse> signIn(@RequestBody AccountSignInRequest request
                                                   , HttpServletResponse httpServletResponse) {
        log.info("[P9][CTRL][ACNT][LOIN]: POST /api/accounts/sign-in, request=({})", request);

        try {
            // 이 시점에 １번　쿼리 나감.
            final Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(request.getIdentification(), request.getPasswordHash())
                    );

            final String identification = authentication.getName();
            final String accessToken = tokenCommandService.addAccessToken(identification);
            final String refreshToken = tokenCommandService.upsertRefreshToken(identification);

            ResponseCookie cookie = ResponseCookie.from(TokenMessage.REFRESH_TOKEN, refreshToken)
                    .domain(cookieDomain)
                    .path("/")
                    .httpOnly(true)
                    .secure(true)
                    .sameSite("None")
                    .build();
            httpServletResponse.addHeader(SET_COOKIE, cookie.toString());

            var response = AccountSignInResponse.of(identification, accessToken);
            return new JUINResponse<>(HttpStatus.OK, response);
        } catch (EntityNotFoundException | BadCredentialsException e) {
            log.warn("[P5][CTRL][ACNT][LOIN]: 회원 정보가 없습니다. request=({})", request);
            return new JUINResponse<>(HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation(value = "로그아웃", notes = "로그아웃을 한다.")
    @GetMapping("/logout")
    public JUINResponse<String> logout(final Principal principal
                                     , HttpServletRequest httpServletRequest
                                     , HttpServletResponse httpServletResponse) {
        final String identification = principal.getName();
        log.info("[P9][CTRL][ACNT][LOUT]: GET /api/accounts/logout, identification=({})", identification);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(httpServletRequest, httpServletResponse, auth);
        }

        // 쿠키 삭제
        Cookie[] cookies = httpServletRequest.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                cookie.setDomain(cookieDomain);
                cookie.setPath("/");
                cookie.setHttpOnly(true);
                cookie.setSecure(true);
                cookie.setMaxAge(0);
                httpServletResponse.addCookie(cookie);
            }
        }

        Token token = tokenQueryService.readByIdentification(identification);
        tokenCommandService.modifyRefreshToken(token, "");

        return new JUINResponse<>(HttpStatus.OK, "로그아웃 되었습니다.");
    }

    @ApiOperation(value = "내 정보 읽기", notes = "내 정보를 읽어온다.")
    @GetMapping("/profile")
    public JUINResponse<AccountRetrieveResponse> profile(final Principal principal) {
        final String identification = principal.getName();
        log.info("[P9][CTRL][ACNT][PROF]: GET /api/accounts/profile, identification=({})", identification);

        try {
            final Account account = principalQueryService.readByPrincipal(principal);

            var response = AccountRetrieveResponse.from(account);
            return new JUINResponse<>(HttpStatus.OK, response);
        } catch (EntityNotFoundException e) {
            log.warn("[P5][CTRL][ACNT][PROF]: 회원 정보가 없습니다. identification=({})", identification);
            return new JUINResponse<>(HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation(value = "회원 정보 수정", notes = "회원 정보를 수정한다.")
    @PatchMapping
    public JUINResponse<AccountUpdateResponse> update(final Principal principal
                                                     , @RequestBody AccountUpdateRequest request) {
        final String identification = principal.getName();
        log.info("[P9][CTRL][ACNT][UPDE]: PATCH /api/accounts, identification=({}), request=({})", identification, request);

        try {
            final Account account = principalQueryService.readByPrincipal(principal);
            final Account updateAccount = accountCommandService.modify(account, request);

            var response = AccountUpdateResponse.from(updateAccount);
            return new JUINResponse<>(HttpStatus.OK, response);
        } catch (EntityNotFoundException e) {
            log.warn("[P5][CTRL][ACNT][UPDE]: ({}) request=({})", e.getMessage(), request);
            return new JUINResponse<>(HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation(value = "회원 정보 삭제", notes = "회원 정보를 삭제한다.")
    @DeleteMapping("/{accountId}")
    public JUINResponse<AccountDeleteResponse> delete(final Principal principal
                                                     , @PathVariable Long accountId) {
        final String identification = principal.getName();
        log.info("[P9][CTRL][ACNT][DELE]: DELETE /api/accounts/{}, identification=({})", accountId, identification);

        try {
            final Account account = principalQueryService.readByPrincipal(principal);
            final Account deleteAccount = accountCommandService.remove(account, accountId);

            var response = AccountDeleteResponse.from(deleteAccount);
            return new JUINResponse<>(HttpStatus.OK, response);
        } catch (EntityNotFoundException e) {
            log.warn("[P5][CTRL][ACNT][DELE]: 회원 정보가 없습니다. accountId=({})", accountId);
            return new JUINResponse<>(HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation(value = "아이디 중복 체크")
    @GetMapping("/duplication/{identification}")
    public JUINResponse<Void> checkIdentification(@PathVariable String identification) {
        log.info("[P9][CTRL][ACNT][DUPL]: GET /api/accounts/{accountId}, identification=({})", identification);

        try {
            accountQueryService.checkDuplicatedIdentification(identification);

            return new JUINResponse<>(HttpStatus.OK);
        } catch (EntityExistsException e) {
            log.warn("[P5][CTRL][ACNT][DUPL]: 이미 존재하는 아이디입니다. identification=({})", identification);
            return new JUINResponse<>(HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(value = "비밀번호 변경 이메일 전송")
    @GetMapping(value = "/mail")
    public JUINResponse<String> sendEmail(@ModelAttribute AccountSendEmailRequest request) {
        var response = accountCommandService.sendEmail(request);
        return new JUINResponse<>(HttpStatus.OK, response);
    }

    @ApiOperation(value = "비밀번호 변경")
    @PutMapping("/password")
    public JUINResponse<Void> changePassword(@RequestBody AccountChangePasswordRequest request) {
        try {
            accountCommandService.changePassword(request);
            return new JUINResponse<>(HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            log.warn("[P5][CTRL][ACNT][DUPL]: ");
            return new JUINResponse<>(HttpStatus.BAD_REQUEST);
        }
    }
}