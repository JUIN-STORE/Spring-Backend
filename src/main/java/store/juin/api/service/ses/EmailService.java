package store.juin.api.service.ses;

import store.juin.api.domain.request.EmailRequest;

public interface EmailService {
    String send(EmailRequest request);
}
