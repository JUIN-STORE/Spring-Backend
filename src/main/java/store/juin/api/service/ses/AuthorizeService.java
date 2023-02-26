package store.juin.api.service.ses;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import store.juin.api.domain.request.AuthorizeRequest;
import store.juin.api.exception.AuthorizeException;
import store.juin.api.service.query.AccountQueryService;
import store.juin.api.utils.AuthNumberUtil;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorizeService {
    private final EmailService emailService;

    private final AccountQueryService accountQueryService;
    private final AuthorizeCacheService authorizeCacheService;

    public String verifyEmailAddress(AuthorizeRequest.Send request) {
        final String toEmail = request.getToEmail();
        accountQueryService.checkDuplicateEmail(toEmail);

        final String authNumber = AuthNumberUtil.makeAuthNumber();
        authorizeCacheService.putAuthorizeNumber(toEmail, authNumber);

       return emailService.verifyEmailAddress(toEmail) == HttpStatus.OK.value()
               ? "인증 이메일을 전송하였습니다. 이메일을 확인해 주세요."
               : "인증 이메일 전송에 실패하였습니다.";
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

    private String makeMailContent(String authNumber) {
        return String.format("<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "<meta charset=\"UTF-8\">\n" +
                "<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "<title>Document</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "안녕하세요, JUIN.STORE입니다.\n" +
                "<br />\n" +
                "아래의 번호를 회원가입 이메일 인증란에 작성해주세요.\n" +
                "<br />\n" +
                "<br />\n" +
                "%s\n" +
                "</body>\n" +
                "</html>", authNumber);
    }
}
