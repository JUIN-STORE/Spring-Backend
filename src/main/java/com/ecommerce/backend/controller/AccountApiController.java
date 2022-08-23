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
    @PostMapping("/sign-up")
    public MyResponse<AccountResponse.SignUp> signUp(@RequestBody AccountRequest.SignUp request) {
        log.info("POST /api/accounts/sign-up request: {}", request);

        try {
            return new MyResponse<>(HttpStatus.OK, accountService.save(request));
        } catch (EntityNotFoundException e) {
            return new MyResponse<>(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @ApiOperation(value = "로그인", notes = "로그인을 한다.")
    @PostMapping("/login")
    public MyResponse<AccountResponse.LoginResponse> login(@RequestBody AccountRequest.Login request) {
        log.info("POST /api/accounts/login request: {}", request);

        final String email = request.getEmail();
        final String password = request.getPasswordHash();

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
            final UserDetails userDetails = accountService.loadUserByUsername(email); // 이 시점에 쿼리 나감.
            final String token = jwtTokenUtil.generateToken(userDetails);
            final LoginResponse response = LoginResponse.fromAccount(email, token);

            return new MyResponse<>(HttpStatus.OK, response);
        } catch (Exception e) {
            loginException(e);
            return new MyResponse<>(HttpStatus.NOT_FOUND, null);
        }
    }

    @ApiOperation(value = "내 정보 읽기", notes = "내 정보를 읽어온다.")
    @GetMapping("/profile")
    public MyResponse<AccountResponse.ReadResponse> profile(Principal principal){
        log.debug("POST /api/accounts/profile principal: {}", principal);

        try {
            return new MyResponse<>(HttpStatus.OK, accountService.findByEmail(principal.getName()));
        } catch (EntityNotFoundException e) {
            log.warn("EntityNotFoundException - GET /api/accounts/profile principal: {}", principal);
            return new MyResponse<>(HttpStatus.NOT_FOUND,null);
        }
    }

    @ApiOperation(value = "회원 정보 수정", notes = "회원 정보를 수정한다.")
    @PatchMapping("/modify")
    public MyResponse<AccountResponse.UpdateResponse> modify(@RequestBody AccountRequest.UpdateRequest request, Principal principal) {
        log.debug("Patch /api/accounts/modify request: {}", request);

        try{
            return new MyResponse<>(HttpStatus.OK, accountService.update(request, principal));
        } catch (EntityNotFoundException e) {
            log.warn("EntityNotFoundException - GET /api/accounts/modify request: {}", request);
            return new MyResponse<>(HttpStatus.NOT_FOUND, null);
        }
    }

    @ApiOperation(value = "회원 정보 삭제", notes = "회원 정보를 삭제한다.")
    @DeleteMapping("/remove")
    public MyResponse<AccountResponse.DeleteResponse> remove(Principal principal){
        log.debug("Delete /api/accounts/{id} principal: {}", principal);

        try {
            return new MyResponse<>(HttpStatus.OK, accountService.delete(principal));
        } catch (EntityNotFoundException e) {
            return new MyResponse<>(HttpStatus.NOT_FOUND, null);
        }
    }

    private void loginException(Exception e){
        if (e instanceof DisabledException) log.warn("DisabledException - 스프링 시큐리티 오류");
        if (e instanceof EntityNotFoundException) log.warn("EntityNotFoundException - 디비에 정보 없음.");
        if (e instanceof BadCredentialsException) log.warn("BadCredentialsException - 스프링 시큐리티 오류");
    }
}