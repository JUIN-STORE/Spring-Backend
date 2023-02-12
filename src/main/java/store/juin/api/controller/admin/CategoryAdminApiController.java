package store.juin.api.controller.admin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.juin.api.JUINResponse;
import store.juin.api.domain.request.CategoryRequest;
import store.juin.api.service.command.CategoryCommandService;

import java.security.Principal;

@Api(tags = {"06. Admin Category"})
@Slf4j
@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
public class CategoryAdminApiController {
    private final CategoryCommandService categoryCommandService;

    @ApiOperation(value = "카테고리 추가", notes = "카테고리를 추가한다.")
    @PostMapping
    public JUINResponse<Long> create(final Principal principal
                                    , @RequestBody CategoryRequest.Create request) {

        var response = categoryCommandService.add(request);
        return new JUINResponse<>(HttpStatus.OK, response);
    }

    // FIXME: update, delete 구현해야 됨.
}
