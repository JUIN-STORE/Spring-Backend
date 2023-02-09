package store.juin.api.service.query;

import store.juin.api.domain.entity.Account;
import store.juin.api.domain.entity.Token;
import store.juin.api.jwt.TokenProvider;
import store.juin.api.repository.jpa.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TokenQueryService {
    private final TokenRepository tokenRepository;

    private final TokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;
    private final PrincipalQueryService principalQueryService;
    public Authentication makeAuthenticationByRefreshToken(String refreshToken) {
        UserDetails userDetails
                = userDetailsService.loadUserByUsername(
                        tokenProvider.getEmailFromToken(refreshToken));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    @Transactional(readOnly = true)
    public Token readByIdentification(String identification) {
        return tokenRepository.findByIdentification(identification);
    }

    @Transactional(readOnly = true)
    public Account readByRefreshToken(String refreshToken) {
        return principalQueryService.readByPrincipal(makeAuthenticationByRefreshToken(refreshToken));
    }
}