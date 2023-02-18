package store.juin.api.controller.ses;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.juin.api.JUINResponse;
import store.juin.api.domain.request.AuthorizeRequest;
import store.juin.api.service.ses.AuthorizeService;

@RestController
@RequestMapping("/api/authorizes")
@RequiredArgsConstructor
public class AuthorizeApiController {
    private final AuthorizeService authorizeService;

    @GetMapping
    public JUINResponse<String> sendEmail(AuthorizeRequest request) {

        final String result = authorizeService.sendEmail(request);
        return new JUINResponse<>(HttpStatus.OK, result);
    }
}