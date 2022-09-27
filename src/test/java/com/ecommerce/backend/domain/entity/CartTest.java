package com.ecommerce.backend.domain.entity;

import com.ecommerce.backend.domain.enums.AccountRole;
import com.ecommerce.backend.domain.request.AccountRequest;
import com.ecommerce.backend.repository.jpa.AccountRepository;
import com.ecommerce.backend.repository.jpa.CartRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class CartTest {
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private AccountRepository accountRepository;

    @PersistenceContext
    private EntityManager em;

    public Account createAccount(AccountRequest.SignUp request){
        return request.toAccount();
    }

    public Account createAccount() {
        return Account.builder()
                .email("cart@test.com")
                .passwordHash("test")
                .name("릴러말즈")
                .accountRole(AccountRole.USER)
                .lastLogin(LocalDateTime.now())
                .build();
    }

    @Test
    @Transactional
    @DisplayName("장바구니 회원 엔티티 매핑 조회 테스트")
    public void findCartAndAccountTest(){
        Account account = createAccount();
        accountRepository.save(account);

        Cart cart = Cart.builder().account(account).build();
        cartRepository.save(cart);

        em.flush();
        em.clear();
        Cart savedCart = cartRepository.findById(cart.getId()).orElseThrow(EntityNotFoundException::new);

        assertEquals(savedCart.getAccount().getId(), account.getId());
    }
}