package store.juin.api.ses.email.service.impl;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import store.juin.api.ses.email.model.request.EmailRequest;
import store.juin.api.ses.email.service.EmailService;

@Service
@Profile("local")
public class LocalEmailServiceImpl implements EmailService {
    @Override
    public String send(EmailRequest request) {
        return request.getContent();
    }

    @Override
    public boolean isConfirmed(String email) {
        return true;
    }

    @Override
    public int verifyEmailAddress(String email) {
        return HttpStatus.OK.value();
    }
}
