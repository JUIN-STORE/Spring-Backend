//package com.ecommerce.backend.controller;
//
//import com.ecommerce.backend.domain.entity.Account;
//import com.ecommerce.backend.repository.AccountRepository;
//import com.ecommerce.backend.service.AccountServiceImpl;
//import com.ecommerce.backend.ifs.AccountService;
//import org.junit.Before;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class AccountApiControllerTest {
////    @Autowired
////    private MockMvc mockMvc;
////
//    @Autowired
//    private AccountService accountService;
//
//    @Mock
//    AccountRepository accountRepository;
//
//    @Before
//    void setUp(){
//        accountService = new AccountServiceImpl(accountRepository, null);
////        mockMvc = MockMvcBuilders.standaloneSetup(AccountApiController.class).build();
//    }
//
//    @DisplayName("GET")
//    @Test
//    void get_test() throws Exceptioneption {
//        // given
//        Optional<Account> account = Optional.of(Account.builder().id(1L).lastName("kim").build());
//
//        // when
//        when(accountRepository.findById(anyLong())).thenReturn(account);
//
//        // then
//        assertEquals(accountService.findById(1L).getLastName(), "kim");
//
//        verify(accountRepository, times(1)).findById(1L);
//
//        verify(accountRepository, never()).findAll();
//
//        verifyNoMoreInteractions(accountRepository);
//    }
//}