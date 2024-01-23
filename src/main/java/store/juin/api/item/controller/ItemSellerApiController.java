package store.juin.api.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import store.juin.api.common.exception.JUINIOException;
import store.juin.api.common.model.response.JUINResponse;
import store.juin.api.item.model.entity.Item;
import store.juin.api.item.model.request.ItemCreateRequest;
import store.juin.api.item.model.response.ItemRetrieveResponse;
import store.juin.api.item.service.command.ItemCommandService;
import store.juin.api.item.service.query.ItemQueryService;
import store.juin.api.principal.service.query.PrincipalQueryService;

import javax.persistence.EntityNotFoundException;
import java.security.InvalidParameterException;
import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/seller/items")
@RequiredArgsConstructor
public class ItemSellerApiController {
    private final ItemQueryService itemQueryService;
    private final PrincipalQueryService principalQueryService;

    private final ItemCommandService itemCommandService;

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public JUINResponse<Long> create(final Principal principal,
                                     @RequestPart ItemCreateRequest request,
                                     @RequestPart MultipartFile representativeFile,
                                     @RequestPart(required = false) List<MultipartFile> detailFileList) {
        final String identification = principal.getName();

        log.info("[P9][CTRL][SEIT][CRTE]: GET /api/items/seller " +
                        "identification=({}), request=({}), representative=({}), fileList=({})"
                , identification, request, representativeFile, detailFileList);

        try {
            principalQueryService.readByPrincipal(principal);

            var response = itemCommandService.add(request, representativeFile, detailFileList);
            return new JUINResponse<>(HttpStatus.OK, response);
        } catch (EntityNotFoundException e) {
            log.warn("[P2][CTRL][SEIT][CRTE]: message=({})", e.getMessage());
            return new JUINResponse<>(HttpStatus.BAD_REQUEST);
        } catch (InvalidParameterException e) {
            log.warn("[P2][CTRL][SEIT][CRTE]: 상품 썸네일은 필수입니다. message=({})", e.getMessage(), e);
            return new JUINResponse<>(HttpStatus.BAD_REQUEST);
        } catch (JUINIOException e) {
            log.warn("[P2][CTRL][SEIT][CRTE]: 파일 등록에 실패하였습니다. message=({})", e.getMessage(), e);
            return new JUINResponse<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            log.warn("[P1][CTRL][SEIT][CRTE]: 알 수 없는 에러가 발생했습니다. message=({})", e.getMessage(), e);
            return new JUINResponse<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{itemId}")
    public JUINResponse<ItemRetrieveResponse> readSellerOnly(final Principal principal, @PathVariable Long itemId) {
        final String identification = principal.getName();

        log.info("[P9][CTRL][SEIT][READ]: GET /api/items/seller/{} identification=({})", itemId, identification);

        try {
            principalQueryService.readByPrincipal(principal);

            final Item item = itemQueryService.readById(itemId);

            var response = ItemRetrieveResponse.from(item);
            return new JUINResponse<>(HttpStatus.OK, response);
        } catch (EntityNotFoundException e) {
            log.warn("[P2][CTRL][SEIT][READ]: message: ({})", e.getMessage());
            return new JUINResponse<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{itemId}")
    public JUINResponse<Long> deleteSellerOnly(final Principal principal, @PathVariable Long itemId) {
        final String identification = principal.getName();
        log.info("[P9][CON][SEIT][DELE]: DELETE /api/items/seller/{} identification=({})", itemId, identification);

        try {
            principalQueryService.readByPrincipal(principal);

            var response = itemCommandService.remove(itemId);
            return new JUINResponse<>(HttpStatus.OK, response);
        } catch (EntityNotFoundException e) {
            log.warn("[P2][CON][SEIT][DELE]: message: ({})", e.getMessage());
            return new JUINResponse<>(HttpStatus.BAD_REQUEST);
        }
    }
}