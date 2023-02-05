package com.juin.store.service.command;

import com.juin.store.domain.entity.Account;
import com.juin.store.domain.entity.Token;
import com.juin.store.exception.InvalidRefreshTokenException;
import com.juin.store.exception.Msg;
import com.juin.store.jwt.TokenMessage;
import com.juin.store.jwt.TokenProvider;
import com.juin.store.repository.jpa.TokenRepository;
import com.juin.store.service.query.TokenQueryService;
import org.assertj.core.api.AbstractThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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

    @Mock private TokenProvider tokenProvider;

    @Nested
    @DisplayName("addAccessToken 테스트")
    class AddAccessTokenTest {
        @Test
        @DisplayName("엑세스 토큰 발급 성공")
        void addAccessTokenTest01() {
            // given
            var email = "ogu@gmail.com";
            var token = "token";
            given(tokenProvider.createToken(email, TokenMessage.ACCESS_TOKEN_VALIDATION_TIME)).willReturn(token);

            // when
            final String actual = sut.addAccessToken(email);

            // then
            assertEquals(token, actual);
        }
    }

    @Nested
    @DisplayName("addAccessToken 테스트")
    class AddRefreshTokenTest {
        @Test
        @DisplayName("리프레쉬 토큰 발급 성공")
        void addAccessTokenTest01() {
            // given
            var email = "ogu@gmail.com";
            var token = "token";
            given(tokenProvider.createToken(email, TokenMessage.ACCESS_TOKEN_VALIDATION_TIME)).willReturn(token);

            // when
            final String actual = sut.addAccessToken(email);

            // then
            assertEquals(token, actual);
        }
    }

    @Nested
    @DisplayName("리프레시 토큰 테스트")
    class UpsertRefreshTokenTest {
        @Test
        @DisplayName("리프레시 토큰 DB에 insert")
        void upsertRefreshTokenTest01() {
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
            final String actual = sut.upsertRefreshToken(email);

            // then
            assertEquals(refreshToken, actual);
        }

        @Test
        @DisplayName("리프레시 토큰 DB에 update")
        void upsertRefreshTokenTest02() {
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
    @DisplayName("ReIssue 테스트")
    class ReIssueTest {
        @Test
        @DisplayName("재발급 성공")
        void reIssueTest01() {
            // given
            var expected = TokenMessage.ACCESS_TOKEN;
            var refreshToken = TokenMessage.REFRESH_TOKEN;
            var email = "email";

            var account = Account.builder()
                    .email(email)
                    .build();

            var token = Token.builder()
                    .refreshToken(refreshToken)
                    .build();

            given(tokenProvider.isValidToken(refreshToken)).willReturn(true);
            given(tokenQueryService.readByRefreshToken(refreshToken)).willReturn(account);
            given(tokenQueryService.readByEmail(anyString())).willReturn(token);
            given(tokenProvider.createToken(email, TokenMessage.ACCESS_TOKEN_VALIDATION_TIME)).willReturn(expected);

            // when
            final String actual = sut.reIssue(refreshToken);

            // then
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("유효하지 않는 토큰")
        void reIssueTest02() {
            // given
            var refreshToken = "refreshToken";
            given(tokenProvider.isValidToken(refreshToken)).willReturn(false);

            // when
            final AbstractThrowableAssert<?, ? extends Throwable> actual =
                    assertThatThrownBy(() -> sut.reIssue(refreshToken));

            // then
            actual
                    .isInstanceOf(InvalidRefreshTokenException.class)
                    .hasMessage(Msg.INVALID_REFRESH_TOKEN);
        }

        @Test
        @DisplayName("존재하지 않는 토큰")
        void reIssueTest03() {
            // given
            var refreshToken1 = "refreshToken1";
            var refreshToken2 = "refreshToken2";

            var email = "email";

            var account = Account.builder()
                    .email(email)
                    .build();

            var token = Token.builder()
                    .refreshToken(refreshToken2)
                    .build();

            given(tokenProvider.isValidToken(refreshToken1)).willReturn(true);
            given(tokenQueryService.readByRefreshToken(refreshToken1)).willReturn(account);
            given(tokenQueryService.readByEmail(anyString())).willReturn(token);

            // when
            final AbstractThrowableAssert<?, ? extends Throwable> actual =
                    assertThatThrownBy(() -> sut.reIssue(refreshToken1));

            // then
            actual
                    .isInstanceOf(InvalidRefreshTokenException.class)
                    .hasMessage(Msg.INVALID_REFRESH_TOKEN);
        }
    }
}