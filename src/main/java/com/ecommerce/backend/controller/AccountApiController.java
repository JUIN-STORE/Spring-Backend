package com.ecommerce.backend.controller;

import com.ecommerce.backend.MyResponse;
import com.ecommerce.backend.domain.request.AccountRequest;
import com.ecommerce.backend.domain.request.AccountRequest.LoginRequest;
import com.ecommerce.backend.domain.response.AccountResponse.LoginResponse;
import com.ecommerce.backend.domain.response.AccountResponse.RegisterResponse;
import com.ecommerce.backend.jwt.JwtTokenUtil;
import com.ecommerce.backend.service.AccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;

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
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;

    private final String ERROR_MESSAGE = "ERROR";

    @ApiOperation(value = "회원가입", notes = "회원가입을 한다.")
    @PostMapping("/register")
    public MyResponse<RegisterResponse> register(@RequestBody AccountRequest.RegisterRequest request) {
        try {
            log.info("__Call POST /api/accounts/register__");
            return new MyResponse<>(HttpStatus.OK, "POST SUCCESS", accountService.saveAccount(request));
        } catch (EntityNotFoundException e) {
            log.warn(e.getMessage());
            return new MyResponse<>(HttpStatus.NOT_FOUND, ERROR_MESSAGE, null);
        }
    }

    @ApiOperation(value = "로그인", notes = "로그인을 한다.")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) throws Exception {
        final String email = request.getEmail();
        final String password = request.getPasswordHash();
        log.info("__Call POST /api/accounts/login__");

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email,password));
            UserDetails userDetails = accountService.loadUserByUsername(email);
            String token = jwtTokenUtil.generateToken(userDetails);
            LoginResponse response = LoginResponse.builder().token(token).build();

            return new ResponseEntity<>(response, HttpStatus.OK);
//            return new MyResponse<>(HttpStatus.OK, "POST SUCCESS", accountService.login(request));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        } catch (EntityNotFoundException e) {
            log.warn(e.getMessage());
//            return new MyResponse<>(HttpStatus.NOT_FOUND, ERROR_MESSAGE, null);
        }
        return null;
    }

    @ApiOperation(value = "회원 정보 읽기", notes = "회원 정보를 읽어온다.")
    @GetMapping("/{id}")
    public MyResponse<ReadResponse> read (@PathVariable Long id){
        try {
            log.info("__Call GET /api/accounts/{id}__");
            return new MyResponse<>(HttpStatus.OK, "GET SUCCESS", accountService.findById(id));
        } catch (EntityNotFoundException e) {
            log.info(e.toString());
            log.warn("__Call GET /api/accounts/{id} EntityNotFoundException__");
            return new MyResponse<>(HttpStatus.NOT_FOUND, ERROR_MESSAGE, null);
        }
    }

    @ApiOperation(value = "회원 정보 삭제", notes = "회원 정보를 삭제한다.")
    @DeleteMapping("/{id}")
    public MyResponse<DeleteResponse> remove (@PathVariable Long id){
        try {
            log.info("__Call DELETE /api/accounts/{id}__");
            return new MyResponse<>(HttpStatus.OK, "DELETE SUCCESS", accountService.delete(id));
        } catch (EntityNotFoundException e) {
            log.warn("__Call DELETE /api/accounts/{id} EntityNotFoundException__");
            return new MyResponse<>(HttpStatus.NOT_FOUND, "DELETE" + ERROR_MESSAGE, null);
        }
    }
}