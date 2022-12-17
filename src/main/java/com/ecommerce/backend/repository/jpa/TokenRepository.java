package com.ecommerce.backend.repository.jpa;

import com.ecommerce.backend.domain.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    Token findByEmail(String email);
}
