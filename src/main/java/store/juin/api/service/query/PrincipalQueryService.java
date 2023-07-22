package store.juin.api.service.query;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import store.juin.api.domain.entity.Account;
import store.juin.api.handler.QueryTransactional;

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