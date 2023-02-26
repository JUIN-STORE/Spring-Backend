package store.juin.api.controller.ses;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.juin.api.JUINResponse;
import store.juin.api.domain.request.AuthorizeRequest;
import store.juin.api.exception.AuthorizeException;
import store.juin.api.service.ses.AuthorizeService;

import javax.persistence.EntityExistsException;

@Slf4j
@RestController
@RequestMapping("/api/authorizes")
@RequiredArgsConstructor
public class AuthorizeApiController {
    private final AuthorizeService authorizeService;

    @GetMapping("/send")
    public JUINResponse<String> sendEmail(@ModelAttribute AuthorizeRequest.Send request) {
        log.info("[P9][CTRL][AUTH][SEND]: /api/authorizes request=({})", request);

        try {
            var response = authorizeService.verifyEmailAddress(request);
            return new JUINResponse<>(HttpStatus.OK, response);
        } catch (EntityExistsException e) {
            return new JUINResponse<>(HttpStatus.BAD_REQUEST);
        }
    }

    // FIXME: 샌드박스 이슈로 프론트에서만 삭제
    @GetMapping("/check")
    public JUINResponse<String> check(@ModelAttribute AuthorizeRequest.Check request) {
        log.info("[P9][CTRL][AUTH][CHCK]: /api/authorizes/check request=({})", request);

        try {
            authorizeService.authorize(request);
            return new JUINResponse<>(HttpStatus.OK);
        } catch (AuthorizeException e) {
            log.warn("[P5][CTRL][AUTH][CHCK]: 이메일 인증에 실패했습니다. message=({})", e.getMessage(), e);
            return new JUINResponse<>(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}