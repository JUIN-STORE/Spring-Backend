package com.ecommerce.backend.controller;

import com.ecommerce.backend.JZResponse;
import com.ecommerce.backend.domain.response.TokenResponse;
import com.ecommerce.backend.exception.InvalidRefreshTokenException;
import com.ecommerce.backend.service.TokenService;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Api(tags = {"10. Token"})
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/tokens")
public final class TokenApiController {
    private final TokenService tokenService;
    @PostMapping("/re-issue")
    public JZResponse<TokenResponse> newToken(@CookieValue(value = "Refresh-Token") String refreshToken) {
        try {
            final String accessToken = tokenService.reIssue(refreshToken);

            var response = TokenResponse.of(accessToken);
            return new JZResponse<>(HttpStatus.OK, response);
        } catch (InvalidRefreshTokenException e) {
            return new JZResponse<>(HttpStatus.UNAUTHORIZED, "Refresh-Token 만료되었습니다.");
        }
    }
}