package store.juin.api.service.command;

import store.juin.api.domain.entity.Account;
import store.juin.api.domain.entity.Token;
import store.juin.api.exception.InvalidRefreshTokenException;
import store.juin.api.exception.Msg;
import store.juin.api.jwt.TokenMessage;
import store.juin.api.jwt.TokenProvider;
import store.juin.api.repository.jpa.TokenRepository;
import store.juin.api.service.query.TokenQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TokenCommandService {
    private final TokenRepository tokenRepository;

    private final TokenQueryService tokenQueryService;

    private final TokenProvider tokenProvider;

    // expired time 일부러 파라미터로 안 받음.
    public String addAccessToken(String email) {
        return tokenProvider.createToken(email, TokenMessage.ACCESS_TOKEN_VALIDATION_TIME);
    }

    // expired time 일부러 파라미터로 안 받음.
    public String addRefreshToken(String email) {
        return tokenProvider.createToken(email, TokenMessage.REFRESH_TOKEN_VALIDATION_TIME);
    }

    @Transactional
    public String upsertRefreshToken(String email) {
        String refreshToken = addRefreshToken(email);
        Token token = tokenQueryService.readByEmail(email);

        if (token == null) {
            token = add(email, refreshToken);
        } else {
            modifyRefreshToken(token, refreshToken);
        }

        return token.getRefreshToken();
    }

    @Transactional
    public String reIssue(String refreshToken) {
        if (!tokenProvider.isValidToken(refreshToken))
            throw new InvalidRefreshTokenException(Msg.INVALID_REFRESH_TOKEN);

        final Account account = tokenQueryService.readByRefreshToken(refreshToken);
        final String email = account.getEmail();

        final Token token = tokenQueryService.readByEmail(email);
        if (!token.getRefreshToken().equals(refreshToken))
            throw new InvalidRefreshTokenException(Msg.INVALID_REFRESH_TOKEN);

        return addAccessToken(email);
    }

    private Token add(String email, String refreshToken) {
        final Token token = Token.builder()
                .email(email)
                .refreshToken(refreshToken)
                .build();

        return tokenRepository.save(token);
    }

    @Transactional
    public void modifyRefreshToken(Token token, String refreshToken) {
        token.updateRefreshToken(refreshToken);
    }
}