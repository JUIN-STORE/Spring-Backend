package store.juin.api.account.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import store.juin.api.account.model.entity.Account;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByIdentification(String identification);

    Optional<Account> findByEmail(String email);

    Optional<Account> findByIdAndEmail(Long accountId, String email);

    Optional<Account> findByIdentificationAndEmail(String identification, String email);
}
