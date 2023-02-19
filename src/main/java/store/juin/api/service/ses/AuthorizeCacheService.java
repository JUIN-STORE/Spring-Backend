package store.juin.api.service.ses;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;


@Service
public class AuthorizeCacheService {
    @Cacheable(value = "AuthNumber", key = "#email", unless = "#result == null")
    public String getAuthorizeNumber(String email) {
        return null;
    }

    @CachePut(value = "AuthNumber", key = "#email")
    public String putAuthorizeNumber(String email, String hashValue) {
        return hashValue;
    }
}
