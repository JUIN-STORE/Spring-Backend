package store.juin.api.ses.email.service;

import store.juin.api.ses.email.model.request.EmailRequest;

public interface EmailService {
    String send(EmailRequest request);

    boolean isConfirmed(String email);

    int verifyEmailAddress(String email);
}
