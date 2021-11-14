package com.ecommerce.backend.service;

import com.ecommerce.backend.domain.entity.Account;
import com.ecommerce.backend.domain.mapper.AccountMapper;
import com.ecommerce.backend.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import static com.ecommerce.backend.domain.request.AccountRequest.CreateRequest;
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
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    @Transactional
    public ReadResponse findById(Long id) {
        Account account = accountRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return accountMapper.entityToReadResponse(account);
    }

    @Transactional
    public CreateResponse save(CreateRequest request) {
        Account account = accountMapper.createRequestToEntity(request);
        accountRepository.save(account);
        return accountMapper.entityToCreateResponse(account);
//        // 검사
//        validateDuplicateAccountEmail(request);
//        validateDuplicatePhoneNumber(request);
//
////        // 비밀번호 해시적용 및 기본적으로 유저타입 지정
////        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
////        accountApiRequest.setPassword(encoder.encode(accountApiRequest.getPassword()));
//
//        save(account);
//
//        // 기본 카드 생성
//        log.info("{}", account);
//        accountRepository.save(account);
//        return AccountMapper.INSTANCE.entityToResponse(account);

    }

    @Transactional
    public DeleteResponse delete(Long id) {
        Account account = accountRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        accountRepository.delete(account);
        return accountMapper.entityToDeleteResponse(account);
    }


//    private void validateDuplicateAccountEmail(RegisterRequest request) {
//        if (findByEmail(request.getEmail()) != null)
//            throw new EntityExistsException("존재하는 이메일");
//    }
//
//    private void validateDuplicatePhoneNumber(AccountRequest accountRequest){
//        if(findByPhoneNumber(accountRequest.getPhoneNumber()) != null)
//            throw new EntityExistsException("존재하는 휴대폰번호");
//    }

}
