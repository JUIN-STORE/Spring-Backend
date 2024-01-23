package store.juin.api.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.juin.api.cart.model.request.CategoryCreateRequest;
import store.juin.api.category.service.command.CategoryCommandService;
import store.juin.api.common.model.response.JUINResponse;
import store.juin.api.principal.service.query.PrincipalQueryService;

import java.security.Principal;

@Slf4j
@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
public class CategoryAdminApiController {
    private final PrincipalQueryService principalQueryService;

    private final CategoryCommandService categoryCommandService;

    @PostMapping
    public JUINResponse<Long> create(final Principal principal,
                                     @RequestBody CategoryCreateRequest request) {
        final String identification = principal.getName();
        log.info("[P9][CTRL][CATG][CRTE]: POST /api/admin/categories, identification=({}), request=({})", identification, request);

        try {
            // FIXME: creatorId 컬럼이 필요하다.
            principalQueryService.readByPrincipal(principal);

            var response = categoryCommandService.add(request);
            return new JUINResponse<>(HttpStatus.OK, response);
        } catch (Exception e) {
            log.warn("[P5][CTRL][CATG][CRTE]: 카테고리 추가 실패, message=({}), identification=({}), request=({})", e.getMessage(), identification, request);
            return new JUINResponse<>(HttpStatus.BAD_REQUEST, null);
        }
    }

    // FIXME: update, delete 구현해야 됨.
}
