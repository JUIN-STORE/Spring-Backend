package store.juin.api.repository.jpa;

import store.juin.api.domain.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByIdentification(String identification);

    Optional<Account> findByEmail(String email);

    Optional<Account> findByIdAndEmail(Long accountId, String email);

    Optional<Account> findByEmailAndPasswordHash(String email, String passwordHash);
}
