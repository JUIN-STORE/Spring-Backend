package com.ecommerce.backend.service;

import com.ecommerce.backend.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

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
}