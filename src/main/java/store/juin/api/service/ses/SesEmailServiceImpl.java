package store.juin.api.service.ses;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import store.juin.api.domain.request.EmailRequest;

@Service
@Profile("production")
@RequiredArgsConstructor
public class SesEmailServiceImpl implements EmailService {
    private static final String FROM_EMAIL = "jduck1024@naver.com";

    private final AmazonSimpleEmailService amazonSimpleEmailService;

    public String send(EmailRequest request) {
        SendEmailRequest sendEmailRequest = new SendEmailRequest()
                .withDestination(new Destination().withToAddresses(request.getToEmail()))
                .withMessage(new Message().withBody(new Body().withHtml(new Content().withCharset("UTF-8").withData(request.getContent())))
                        .withSubject(new Content().withCharset("UTF-8").withData(request.getTitle())))
                .withSource(FROM_EMAIL);

        final SendEmailResult sendEmailResult = amazonSimpleEmailService.sendEmail(sendEmailRequest);
        return sendEmailResult.getSdkResponseMetadata().getRequestId();
    }
}
