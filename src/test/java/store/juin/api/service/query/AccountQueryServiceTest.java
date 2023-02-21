package store.juin.api.service.query;

import store.juin.api.config.SecurityConfig;
import store.juin.api.domain.entity.Account;
import store.juin.api.domain.enums.AccountRole;
import store.juin.api.exception.Msg;
import store.juin.api.repository.jpa.AccountRepository;
import org.assertj.core.api.AbstractThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import store.juin.api.utils.PasswordUtil;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountQueryServiceTest {
    private static final String EMAIL = "js@gmail.com";

    @InjectMocks
    private AccountQueryService sut;

    @Mock
    private AccountRepository accountRepository;

    @Nested
    @DisplayName("readById 테스트")
    class RetrieveByIdTest {
        @Test
        @DisplayName("정상 케이스")
        void readByIdTest01() {
            // given
            var expected = makeAccount(AccountRole.USER);
            given(accountRepository.findById(any())).willReturn(Optional.of(expected));

            // when
            final Account actual = sut.readById(1L);

            // then
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("존재하지 않는 계정일 때")
        void readByIdTest02() {
            // given
            given(accountRepository.findById(any())).willReturn(Optional.empty());

            // when
            final AbstractThrowableAssert<?, ? extends Throwable> actual =
                    assertThatThrownBy(() -> sut.readById(1L));

            // then
            actual.isInstanceOf(EntityNotFoundException.class).hasMessage(Msg.WRONG_ID_PASSWORD);
        }
    }

    @Nested
    @DisplayName("readByEmail 테스트")
    class RetrieveByEmailTest {
        @Test
        @DisplayName("정상 케이스")
        void readByEmailTest01() {
            // given
            var expected = makeAccount(AccountRole.USER);
            given(accountRepository.findByEmail(any())).willReturn(Optional.of(expected));

            // when
            final Account actual = sut.readByEmail(EMAIL);

            // then
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("존재하지 않는 계정일 때")
        void readByEmailTest02() {
            // given
            given(accountRepository.findByEmail(any())).willReturn(Optional.empty());

            // when
            final AbstractThrowableAssert<?, ? extends Throwable> actual =
                    assertThatThrownBy(() -> sut.readByEmail(EMAIL));

            // then
            actual.isInstanceOf(EntityNotFoundException.class).hasMessage(Msg.ACCOUNT_NOT_FOUND);
        }
    }


    @Nested
    @DisplayName("readByEmail 테스트")
    class RetrieveByIdAndEmailTest {
        @Test
        @DisplayName("id와 email을 통해 계정을 찾음")
        void readByIdAndEmailTest01() {
            // given
            var expected = makeAccount(AccountRole.USER);
            given(accountRepository.findByIdAndEmail(anyLong(), anyString())).willReturn(Optional.of(expected));

            // when
            final Account actual = sut.readByIdAndEmail(1L, EMAIL);

            // then
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("id와 email을 통해 계정을 찾지 못함")
        void readByEmailTest02() {
            // given
            given(accountRepository.findByIdAndEmail(anyLong(), anyString())).willReturn(Optional.empty());

            // when
            final AbstractThrowableAssert<?, ? extends Throwable> actual =
                    assertThatThrownBy(() -> sut.readByIdAndEmail(1L, EMAIL));

            // then
            actual.isInstanceOf(EntityNotFoundException.class).hasMessage(Msg.WRONG_ID_PASSWORD);
        }
    }

    @Nested
    @DisplayName("checkNotUser 테스트")
    class CheckNotUser {
        @Test
        @DisplayName("ADMIN일 떄")
        void checkNotUserTest01() {
            // given
            var account = makeAccount(AccountRole.ADMIN);
            given(accountRepository.findByEmail(anyString())).willReturn(Optional.of(account));

            // when
            final boolean actual = sut.checkNotUser(account);

            // then
            assertTrue(actual);
        }

        @Test
        @DisplayName("SELLER일 떄")
        void checkNotUserTest02() {
            // given
            var account = makeAccount(AccountRole.SELLER);
            given(accountRepository.findByEmail(anyString())).willReturn(Optional.of(account));

            // when
            final boolean actual = sut.checkNotUser(account);

            // then
            assertTrue(actual);
        }

        @Test
        @DisplayName("USER일 떄")
        void checkNotUserTest03() {
            // given
            var account = makeAccount(AccountRole.USER);
            given(accountRepository.findByEmail(anyString())).willReturn(Optional.of(account));

            // when
            final boolean actual = sut.checkNotUser(account);

            // then
            assertFalse(actual);
        }
    }

    @Nested
    @DisplayName("checkDuplicatedIdentification 테스트")
    class CheckDuplicatedIdentificationTest {
        @Test
        @DisplayName("중복된 아이디일 때")
        void checkDuplicatedIdentificationTest01() {
            // given
            var identification = "juin";
            final Account account = makeAccount(AccountRole.USER);
            given(accountRepository.findByIdentification(identification)).willReturn(Optional.of(account));

            // when
            final AbstractThrowableAssert<?, ? extends Throwable> actual =
                    assertThatThrownBy(() -> sut.checkDuplicatedIdentification(identification));

            // then
            actual.isInstanceOf(EntityExistsException.class).hasMessage(Msg.DUPLICATED_IDENTIFICATION);
        }

        @Test
        @DisplayName("중복된 아이디가 아닐 때")
        void checkDuplicatedIdentificationTest02() {
            // given
            var identification = "juin";
            given(accountRepository.findByIdentification(identification)).willReturn(Optional.empty());

            // when
            sut.checkDuplicatedIdentification(identification);

            // then
            verify(accountRepository, times(1)).findByIdentification(identification);

        }
    }

    private Account makeAccount(AccountRole accountRole) {
        return Account.builder()
                .id(1L)
                .email(EMAIL)
                .passwordHash(PasswordUtil.makePasswordHash("passwordHash"))
                .name("지수")
                .accountRole(accountRole)
                .phoneNumber("01011112222")
                .build();
    }
}