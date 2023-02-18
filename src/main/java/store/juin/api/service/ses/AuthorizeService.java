package store.juin.api.service.ses;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import store.juin.api.domain.request.AuthorizeRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorizeService {
    private final AmazonSimpleEmailService amazonSES;

    public String sendEmail(AuthorizeRequest request) {
        String mailTitle = "[JUIN.STORE] 회원가입 인증 메일";
        String mailContent = "안녕하세요, " + request.getToName() + "님,  JUIN.STORE입니다. 아래 링크를 통해 인증을 해주세요.";

        SendEmailRequest sendEmailRequest = new SendEmailRequest()
                .withDestination(new Destination().withToAddresses(request.getToEmail()))
                .withMessage(new Message().withBody(new Body().withHtml(new Content().withCharset("UTF-8").withData(mailContent)))
                        .withSubject(new Content().withCharset("UTF-8").withData(mailTitle)))
                .withSource(request.getFromEmail());
        final SendEmailResult sendEmailResult = amazonSES.sendEmail(sendEmailRequest);

        final String requestId = sendEmailResult.getSdkResponseMetadata().getRequestId();
        return requestId;
    }
}
