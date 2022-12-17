package com.ecommerce.backend.controller;

import com.ecommerce.backend.MyResponse;
import com.ecommerce.backend.domain.request.TokenRequest;
import com.ecommerce.backend.domain.response.TokenResponse;
import com.ecommerce.backend.exception.InvalidRefreshTokenException;
import com.ecommerce.backend.service.TokenService;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"10. Token"})
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/tokens")
public final class TokenApiController {
    private final TokenService tokenService;

    @PostMapping("/re-issue")
    public MyResponse<TokenResponse> newToken(@RequestBody TokenRequest request) {
        try {
            final String accessToken = tokenService.reIssue(request);

            var response = TokenResponse.of(accessToken);
            return new MyResponse<>(HttpStatus.OK, response);
        } catch (InvalidRefreshTokenException e) {
            return new MyResponse<>(HttpStatus.UNAUTHORIZED, "Refresh-Token 만료되었습니다.");
        }
    }
}