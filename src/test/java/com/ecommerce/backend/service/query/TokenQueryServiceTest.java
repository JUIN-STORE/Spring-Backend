package com.ecommerce.backend.service.query;

import com.ecommerce.backend.jwt.TokenProvider;
import com.ecommerce.backend.repository.jpa.TokenRepository;
import org.assertj.core.api.AbstractThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
class TokenQueryServiceTest {
    @InjectMocks
    private TokenQueryService sut;
    @Mock private TokenRepository tokenRepository;

    @Mock private TokenProvider tokenProvider;
    @Mock private UserDetailsService userDetailsService;
    @Mock private PrincipalQueryService principalQueryService;

    @Nested
    @DisplayName("리프레시 토큰으로 인증 정보 만들기 테스트")
    class MakeAuthenticationByRefreshTokenTest {
        @Test
        @DisplayName("발급 성공")
        void test01() {
            // given
            var email = "email";
            var refreshToken = "refreshToken";
            var userDetails = User.builder()
                    .username("username")
                    .password("password")
                    .authorities("authorities")
                    .build();
            var usernamePasswordAuthenticationToken
                    = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());

            given(tokenProvider.getEmailFromToken(refreshToken)).willReturn(email);
            given(userDetailsService.loadUserByUsername(email)).willReturn(userDetails);

            // when
            Authentication actual = sut.makeAuthenticationByRefreshToken(refreshToken);

            // then
            assertEquals(usernamePasswordAuthenticationToken, actual);
        }

        @Test
        @DisplayName("존재하지 않는 계정")
        void test02() {
            // given
            var email = "email";
            var refreshToken = "refreshToken";
            var userDetails = User.builder()
                    .username("username")
                    .password("password")
                    .authorities("authorities")
                    .build();
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                    = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());

            given(tokenProvider.getEmailFromToken(refreshToken)).willReturn(email);
            given(userDetailsService.loadUserByUsername(email)).willThrow(new UsernameNotFoundException(""));

            // when
            AbstractThrowableAssert<?, ? extends Throwable> actual =
                    assertThatThrownBy(() -> sut.makeAuthenticationByRefreshToken(refreshToken));

            // then
            actual.isInstanceOf(UsernameNotFoundException.class);
        }
    }

}