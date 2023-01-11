package com.ecommerce.backend.service.command;

import com.ecommerce.backend.domain.entity.Account;
import com.ecommerce.backend.domain.entity.Token;
import com.ecommerce.backend.exception.InvalidRefreshTokenException;
import com.ecommerce.backend.exception.Msg;
import com.ecommerce.backend.jwt.TokenMessage;
import com.ecommerce.backend.jwt.TokenProvider;
import com.ecommerce.backend.repository.jpa.TokenRepository;
import com.ecommerce.backend.service.query.PrincipalQueryService;
import com.ecommerce.backend.service.query.TokenQueryService;
import org.assertj.core.api.AbstractThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
class TokenCommandServiceTest {
    @InjectMocks
    private TokenCommandService sut;

    @Mock private TokenRepository tokenRepository;
    @Mock private TokenQueryService tokenQueryService;
    @Mock private PrincipalQueryService principalQueryService;
    @Mock private TokenProvider tokenProvider;

    @Nested
    @DisplayName("액세스 토큰 발급")
    class AddAccessTokenTest {
        @Test
        @DisplayName("발급 성공")
        void test01() {
            // given
            var email = "ogu@gmail.com";
            var token = "token";
            given(tokenProvider.createToken(email, TokenMessage.ACCESS_TOKEN_VALIDATION_TIME))
                    .willReturn(token);

            // when
            String actual = sut.addAccessToken(email);

            // then
            assertEquals(token, actual);
        }
    }

    @Nested
    @DisplayName("리프레시 토큰 발급")
    class AddRefreshTokenTest {
        @Test
        @DisplayName("발급 성공")
        void test01() {
            // given
            var email = "ogu@gmail.com";
            var token = "token";
            given(tokenProvider.createToken(email, TokenMessage.ACCESS_TOKEN_VALIDATION_TIME)).willReturn(token);

            // when
            String actual = sut.addAccessToken(email);

            // then
            assertEquals(token, actual);
        }
    }

    @Nested
    @DisplayName("리프레시 토큰 테스트")
    class UpsertRefreshTokenTest {
        @Test
        @DisplayName("리프레시 토큰 DB에 insert")
        void test01() {
            // given
            var email = "ogu@gmail.com";
            var refreshToken = "refreshToken";
            var token = Token.builder()
                    .refreshToken(refreshToken)
                    .build();
            given(tokenProvider.createToken(email, TokenMessage.REFRESH_TOKEN_VALIDATION_TIME))
                    .willReturn(refreshToken);
            given(tokenRepository.findByEmail(email)).willReturn(null);
            given(tokenRepository.save(any())).willReturn(token);

            // when
            String actual = sut.upsertRefreshToken(email);

            // then
            assertEquals(refreshToken, actual);
        }

        @Test
        @DisplayName("리프레시 토큰 DB에 update")
        void test02() {
            // given
            var email = "ogu@gmail.com";
            var beforeRefreshToken = "beforeRefreshToken";
            var afterRefreshToken = "afterRefreshToken";

            var token = Token.builder()
                    .refreshToken(beforeRefreshToken)
                    .build();
            given(tokenProvider.createToken(email, TokenMessage.REFRESH_TOKEN_VALIDATION_TIME))
                    .willReturn(afterRefreshToken);
            given(tokenQueryService.readByEmail(email)).willReturn(token);

            // when
            String actual = sut.upsertRefreshToken(email);

            // then
            assertEquals(afterRefreshToken, actual);
        }
    }

    @Nested
    @DisplayName("토큰 리프레시")
    class ReIssueTest {
        @Test
        @DisplayName("성공")
        void test01() {
            // given
            var accessToken = "accessToken";
            var refreshToken = "refreshToken";
            var email = "email";
            var account = Account.builder()
                    .email(email)
                    .build();
            var userDetails = User.builder()
                    .username("username")
                    .password("password")
                    .authorities("authorities")
                    .build();
            var token = Token.builder()
                    .refreshToken(refreshToken)
                    .build();

            given(tokenProvider.isValidToken(refreshToken)).willReturn(true);
            given(tokenProvider.getEmailFromToken(refreshToken)).willReturn(email);
            given(principalQueryService.loadUserByUsername(email)).willReturn(userDetails);
            given(principalQueryService.readByPrincipal(any())).willReturn(account);
            given(tokenQueryService.readByEmail(anyString())).willReturn(token);
            given(tokenProvider.createToken(email, TokenMessage.ACCESS_TOKEN_VALIDATION_TIME)).willReturn(accessToken);

            // when
            String actual = sut.reIssue(refreshToken);

            // then
            assertEquals(accessToken, actual);
        }

        @Test
        @DisplayName("유효하지 않는 토큰")
        void test02() {
            // given
            var refreshToken = "refreshToken";
            given(tokenProvider.isValidToken(refreshToken)).willReturn(false);

            // when
            AbstractThrowableAssert<?, ? extends Throwable> actual =
                    assertThatThrownBy(() -> sut.reIssue(refreshToken));

            // then
            actual.isInstanceOf(InvalidRefreshTokenException.class).hasMessage(Msg.INVALID_REFRESH_TOKEN);
        }

        @Test
        @DisplayName("존재하지 않는 토큰")
        void test03() {
            // given
            var refreshToken1 = "refreshToken1";
            var refreshToken2 = "refreshToken2";

            var email = "email";
            var account = Account.builder()
                    .email(email)
                    .build();
            var userDetails = User.builder()
                    .username("username")
                    .password("password")
                    .authorities("authorities")
                    .build();
            var token = Token.builder()
                    .refreshToken(refreshToken2)
                    .build();

            given(tokenProvider.isValidToken(refreshToken1)).willReturn(true);
            given(tokenProvider.getEmailFromToken(refreshToken1)).willReturn(email);
            given(principalQueryService.loadUserByUsername(email)).willReturn(userDetails);
            given(principalQueryService.readByPrincipal(any())).willReturn(account);
            given(tokenQueryService.readByEmail(anyString())).willReturn(token);

            // when
            AbstractThrowableAssert<?, ? extends Throwable> actual =
                    assertThatThrownBy(() -> sut.reIssue(refreshToken1));

            // then
            actual.isInstanceOf(InvalidRefreshTokenException.class).hasMessage(Msg.INVALID_REFRESH_TOKEN);
        }
    }
}