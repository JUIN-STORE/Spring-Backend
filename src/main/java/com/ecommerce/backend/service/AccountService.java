package com.ecommerce.backend.service;

import com.ecommerce.backend.domain.entity.Account;
import com.ecommerce.backend.domain.entity.Address;
import com.ecommerce.backend.domain.mapper.AccountMapper;
import com.ecommerce.backend.domain.request.AccountRequest;
import com.ecommerce.backend.repository.AccountRepository;
import com.ecommerce.backend.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Optional;

import static com.ecommerce.backend.domain.request.AccountRequest.LoginRequest;
import static com.ecommerce.backend.domain.response.AccountResponse.*;

/** Service Naming
 * C -> save
 * R -> findBy~
 * U -> update
 * D -> delete
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {
    private final AccountRepository accountRepository;
    private final AddressRepository addressRepository;
    private final AccountMapper accountMapper;

    private void validateDuplicateAccountEmail(AccountRequest.RegisterRequest request){
        Optional<Account> validEmail = accountRepository.findByEmail(request.getEmail());
        if (validEmail.isPresent()) throw new EntityExistsException("존재하는 이메일입니다. 다른 이메일을 입력해 주세요.");
    }

    public RegisterResponse saveAccount(AccountRequest.RegisterRequest request) {
        // 검사
        validateDuplicateAccountEmail(request);

        Account account = request.toAccount();
        Address address = request.getAddress().toAddress(account);

        accountRepository.save(account);
        addressRepository.save(address);
        return RegisterResponse.fromAccount(account);
    }

    @Transactional(readOnly = true)
    public ReadResponse findById(Long id) {
        Account account = accountRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return accountMapper.entityToReadResponse(account);
    }

    public DeleteResponse delete(Long id) {
        Account account = accountRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        accountRepository.delete(account);
        return accountMapper.entityToDeleteResponse(account);
    }

    public LoginResponse login(LoginRequest request) {
        Account account = accountRepository.findByEmail(request.getEmail()).orElseThrow(() -> new EntityNotFoundException("계정 정보가 다릅니다."));
        boolean checkEmail = account.getEmail().equals(request.getEmail());
        boolean checkPassword = new BCryptPasswordEncoder().matches(request.getPasswordHash(), account.getPasswordHash());

        // email, password 체크
        if (checkEmail && checkPassword) return accountMapper.entityToLoginResponse(account);

        throw new EntityNotFoundException("계정 정보가 다릅니다.");
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("user name not found!"));
        return new org.springframework.security.core.userdetails.User(account.getEmail(), account.getPasswordHash(), new ArrayList<>());
    }
}
