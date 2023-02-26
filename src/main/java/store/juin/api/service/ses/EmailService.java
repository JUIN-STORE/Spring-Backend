package store.juin.api.service.ses;

import store.juin.api.domain.request.EmailRequest;

public interface EmailService {
    String send(EmailRequest request);

    boolean isConfirmed(String email);

    int verifyEmailAddress(String email);
}
