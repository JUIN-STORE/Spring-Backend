package store.juin.api.token.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.juin.api.common.model.response.JUINResponse;
import store.juin.api.token.exception.InvalidRefreshTokenException;
import store.juin.api.token.model.response.TokenResponse;
import store.juin.api.token.service.TokenCommandService;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/tokens")
public final class TokenApiController {
    private final TokenCommandService tokenCommandService;

    @PostMapping("/re-issue")
    public JUINResponse<TokenResponse> create(@CookieValue(value = "Refresh-Token") String refreshToken) {
        log.info("[P9][CON][TOKN][NEW_]: POST /api/tokens/re-issue, accessToken이 만료되어 새로운 토큰을 요청합니다. refreshToken=({})", refreshToken);

        try {
            final String accessToken = tokenCommandService.reIssue(refreshToken);

            var response = TokenResponse.of(accessToken);
            return new JUINResponse<>(HttpStatus.OK, response);
        } catch (InvalidRefreshTokenException e) {
            log.warn("[P2][CON][TOKN][NEW_]: Refresh-Token이 만료되었습니다. refreshToken=({})", refreshToken);
            return new JUINResponse<>(HttpStatus.UNAUTHORIZED);
        }
    }
}