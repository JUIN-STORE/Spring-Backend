package com.ecommerce.backend.service;

import com.ecommerce.backend.domain.entity.Account;
import com.ecommerce.backend.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

@DataJpaTest
//@Import({AccountServiceTests.AccountServiceTestContextConfiguration.class})
class AccountServiceTests {
//    @Autowired
//    private AccountService accountService;
//

    @Autowired
    private AccountRepository accountRepository;

//    @TestConfiguration
//    static class AccountServiceTestContextConfiguration {
//
//        @Bean
//        public AccountRepository accountRepository() {
//            return Mockito.mock(AccountRepository.class);
//        }
//
//        @Bean
//        public AccountService accountService(){
//            return new AccountService(accountRepository());
//        }
//    }


    @Test
    void test() {
        // given

        Optional<Account> byId = accountRepository.findById(2L);
        System.out.println(byId.get().getLastName());
//        Account byId = accountService.findById(2L);
//        System.out.println(byId.getLastName());

        // when

        // then
    }

}