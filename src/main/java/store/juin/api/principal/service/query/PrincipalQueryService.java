package store.juin.api.principal.service.query;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import store.juin.api.account.model.entity.Account;
import store.juin.api.account.service.query.AccountQueryService;
import store.juin.api.common.handler.QueryTransactional;

import java.security.Principal;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrincipalQueryService {
    private final QueryTransactional queryTransactional;

    private final AccountQueryService accountQueryService;

    public Account readByPrincipal(Principal principal) {
        final String identification = principal.getName();

        return queryTransactional.execute(() ->
                accountQueryService.readByIdentification(identification)
        );
    }
}