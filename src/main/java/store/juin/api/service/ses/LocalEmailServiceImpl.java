package store.juin.api.service.ses;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import store.juin.api.domain.request.EmailRequest;

@Service
@Profile("local")
public class LocalEmailServiceImpl implements EmailService {
    @Override
    public String send(EmailRequest request) {
        return request.getContent();
    }
}
