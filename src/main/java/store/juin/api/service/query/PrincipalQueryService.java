package store.juin.api.service.query;

import store.juin.api.domain.entity.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrincipalQueryService {
    private final AccountQueryService accountQueryService;

    @Transactional(readOnly = true)
    public Account readByPrincipal(Principal principal) {
        final String identification = principal.getName();

        return accountQueryService.readByIdentification(identification);
    }
}