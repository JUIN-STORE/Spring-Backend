package store.juin.api.token.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import store.juin.api.token.model.entity.Token;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    Token findByIdentification(String identification);
}
