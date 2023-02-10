package store.juin.api.jwt;

import store.juin.api.domain.entity.Account;
import store.juin.api.service.query.AccountQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final AccountQueryService accountQueryService;

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String identification) throws UsernameNotFoundException {
        final Account account = accountQueryService.readByIdentification(identification);

        return User.builder()
                .username(account.getIdentification())
                .password(account.getPasswordHash())
                .roles(account.getAccountRole().name()) // 401 - 인증
//                .authorities("ROLE_" + account.getAccountRole().name()) // 403 - 인가
                .build();
    }
}