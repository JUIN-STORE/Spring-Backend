package store.juin.api.account.service.query;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import store.juin.api.account.enumeration.AccountRole;
import store.juin.api.account.model.entity.Account;
import store.juin.api.account.repository.jpa.AccountRepository;
import store.juin.api.common.exception.Msg;
import store.juin.api.common.handler.QueryTransactional;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountQueryService {
    private final QueryTransactional queryTransactional;

    private final AccountRepository accountRepository;

    public Account readById(Long id) {
        return queryTransactional.execute(() ->
                // select * from cart where account_id = ? 쿼리도 나감
                accountRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException(Msg.WRONG_ID_PASSWORD))
        );
    }

    public Account readByIdentification(String identification) {
        return queryTransactional.execute(() -> accountRepository.findByIdentification(identification)
                .orElseThrow(() -> new EntityNotFoundException(Msg.WRONG_ID_PASSWORD))
        );
    }

    public Account readByEmail(String email) {
        return queryTransactional.execute(() -> accountRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(Msg.ACCOUNT_NOT_FOUND))
        );
    }

    public Account readByIdAndEmail(Long accountId, String email) {
        return queryTransactional.execute(() -> accountRepository.findByIdAndEmail(accountId, email)
                .orElseThrow(() -> new EntityNotFoundException(Msg.WRONG_ID_PASSWORD))
        );
    }

    public boolean checkNotUser(Account account) {
        final AccountRole accountRole = readByEmail(account.getEmail()).getAccountRole();
        return accountRole == AccountRole.ADMIN || accountRole == AccountRole.SELLER;
    }

    public void checkDuplicatedIdentification(String identification) {
        Optional<Account> account = accountRepository.findByIdentification(identification);
        if (account.isPresent()) throw new EntityExistsException(Msg.DUPLICATED_IDENTIFICATION);
    }

    public void checkDuplicateEmail(String email) {
        final Optional<Account> account = accountRepository.findByEmail(email);
        if (account.isPresent()) throw new EntityExistsException(Msg.DUPLICATED_IDENTIFICATION);
    }
}
