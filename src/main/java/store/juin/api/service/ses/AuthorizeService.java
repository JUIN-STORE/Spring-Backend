package store.juin.api.service.ses;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import store.juin.api.domain.request.AuthorizeRequest;
import store.juin.api.domain.request.EmailRequest;
import store.juin.api.exception.AuthorizeException;
import store.juin.api.service.query.AccountQueryService;

import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorizeService {
    private static final Random random = new Random();
    private final EmailService emailService;

    private final AccountQueryService accountQueryService;
    private final AuthorizeCacheService authorizeCacheService;

    public String sendEmail(AuthorizeRequest.Send request) {
        final String toEmail = request.getToEmail();
        accountQueryService.checkDuplicateEmail(toEmail);

        final String authNumber = makeAuthNumber();
        authorizeCacheService.putAuthorizeNumber(toEmail, authNumber);

        final EmailRequest emailRequest = EmailRequest.builder()
                .toEmail(toEmail)
                .title("[JUIN.STORE] 회원가입 인증 메일")
                .content(makeMailContent(authNumber))
                .build();

        return emailService.send(emailRequest);
    }


    public void authorize(AuthorizeRequest.Check request) {
        final String hashNumber = authorizeCacheService.getAuthorizeNumber(request.getEmail());

        if (hashNumber == null) {
            throw new AuthorizeException("이메일 인증번호 전송 요청이 필요합니다.");
        }

        if (!hashNumber.equals(request.getHash())) {
            throw new AuthorizeException("인증번호가 일치하지 않습니다.");
        }
    }

    private String makeAuthNumber() {
        int leftLimit = '0';
        int rightLimit = 'z';
        int targetStringLength = 10;

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    private String makeMailContent(String authNumber) {
        return String.format("<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Document</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "    안녕하세요, JUIN.STORE입니다.\n" +
                "    <br />\n" +
                "    아래의 번호를 회원가입 이메일 인증란에 작성해주세요.\n" +
                "    <br />\n" +
                "    <br />\n" +
                "    %s\n" +
                "</body>\n" +
                "</html>", authNumber);
    }
}
