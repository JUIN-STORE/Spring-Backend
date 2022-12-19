package com.ecommerce.backend.service;

import com.ecommerce.backend.domain.entity.Account;
import com.ecommerce.backend.domain.entity.Token;
import com.ecommerce.backend.exception.InvalidRefreshTokenException;
import com.ecommerce.backend.exception.Msg;
import com.ecommerce.backend.jwt.TokenMessage;
import com.ecommerce.backend.jwt.TokenProvider;
import com.ecommerce.backend.repository.jpa.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenRepository tokenRepository;

    private final PrincipalService principalService;
    private final TokenProvider tokenProvider;

    // expired time 일부러 파라미터로 안 받음.
    public String addAccessToken(String email) {
        return tokenProvider.createToken(email, TokenMessage.ACCESS_TOKEN_VALIDATION_TIME);
    }

    // expired time 일부러 파라미터로 안 받음.
    public String addRefreshToken(String email) {
        return tokenProvider.createToken(email, TokenMessage.REFRESH_TOKEN_VALIDATION_TIME);
    }

    public Authentication makeAuthenticationByRefreshToken(String refreshToken) {
        UserDetails userDetails = principalService.loadUserByUsername(tokenProvider.getEmailFromToken(refreshToken));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public Token add(String email, String refreshToken) {
        final Token token = Token.builder()
                .email(email)
                .refreshToken(refreshToken)
                .build();

        return tokenRepository.save(token);
    }

    @Transactional
    public String upsertRefreshToken(String email) {
        String refreshToken = addRefreshToken(email);
        Token token = readByEmail(email);

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

        final Account account = readByRefreshToken(refreshToken);
        final String email = account.getEmail();

        final Token token = readByEmail(email);
        if (!token.getRefreshToken().equals(refreshToken))
            throw new InvalidRefreshTokenException(Msg.INVALID_REFRESH_TOKEN);

        return addAccessToken(email);
    }


    public Token readByEmail(String email) {
        return tokenRepository.findByEmail(email);
    }

    public Account readByRefreshToken(String refreshToken) {
        return principalService.readByPrincipal(makeAuthenticationByRefreshToken(refreshToken));
    }

    public void modifyRefreshToken(Token token, String refreshToken) {
        token.updateRefreshToken(refreshToken);
    }
}