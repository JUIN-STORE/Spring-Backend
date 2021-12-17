package com.ecommerce.backend.service;

import com.ecommerce.backend.domain.entity.Account;
import com.ecommerce.backend.domain.mapper.AccountMapper;
import com.ecommerce.backend.repository.AccountRepository;
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
import java.util.Optional;

import static com.ecommerce.backend.domain.request.AccountRequest.CreateRequest;
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
    private final AccountMapper accountMapper;

    @Transactional(readOnly = true)
    public ReadResponse findById(Long id) {
        Account account = accountRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return accountMapper.entityToReadResponse(account);
    }
    public CreateResponse save(CreateRequest request) {
        // 검사
        validateDuplicateAccountEmail(request);
        validateDuplicatePhoneNumber(request);

        // 비밀번호 암호화
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        request.setPassword(encoder.encode(request.getPassword()));

        Account account = accountMapper.createRequestToEntity(request);
        accountRepository.save(account);
        return accountMapper.entityToCreateResponse(account);
    }

    public DeleteResponse delete(Long id) {
        Account account = accountRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        accountRepository.delete(account);
        return accountMapper.entityToDeleteResponse(account);
    }

    public LoginResponse login(LoginRequest request) {
        Account account = accountRepository.findByEmail(request.getEmail()).orElseThrow(() -> new EntityNotFoundException("계정 정보가 다릅니다."));
        boolean checkEmail = account.getEmail().equals(request.getEmail());
        boolean checkPassword = new BCryptPasswordEncoder().matches(request.getPassword(), account.getPasswordHash());

        // email, password 체크
        if (checkEmail && checkPassword) return accountMapper.entityToLoginResponse(account);

        throw new EntityNotFoundException("계정 정보가 다릅니다.");
    }

    private void validateDuplicateAccountEmail(CreateRequest request){
        Optional<Account> validEmail = accountRepository.findByEmail(request.getEmail());
        if (validEmail.isPresent()) throw new EntityExistsException("존재하는 이메일입니다. 다른 이메일을 입력해 주세요.");
    }

    private void validateDuplicatePhoneNumber(CreateRequest request) {
        Optional<Account> validPhoneNumber = accountRepository.findByPhoneNumber(request.getPhoneNumber());
        if(validPhoneNumber.isPresent()) throw new EntityExistsException("존재하는 휴대전화 번호입니다. 다른 휴대전화 번호를 입력해 주세요.");
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return (UserDetails) accountRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("일치하는 사용자를 찾을 수 없습니다."));
    }
}
