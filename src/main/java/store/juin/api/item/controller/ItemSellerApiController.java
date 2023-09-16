package store.juin.api.item.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import store.juin.api.common.exception.JUINIOException;
import store.juin.api.common.model.response.JUINResponse;
import store.juin.api.item.model.entity.Item;
import store.juin.api.item.model.request.ItemRequest;
import store.juin.api.item.model.response.ItemResponse;
import store.juin.api.item.service.command.ItemCommandService;
import store.juin.api.item.service.query.ItemQueryService;
import store.juin.api.principal.service.query.PrincipalQueryService;

import javax.persistence.EntityNotFoundException;
import java.security.InvalidParameterException;
import java.security.Principal;
import java.util.List;

@Api(tags = {"07. Seller Item"})
@Slf4j
@RestController
@RequestMapping("/api/seller/items")
@RequiredArgsConstructor
public class ItemSellerApiController {
    private final ItemQueryService itemQueryService;
    private final PrincipalQueryService principalQueryService;

    private final ItemCommandService itemCommandService;

    @ApiOperation(value = "판매자 상품 등록", notes = "관리자가 상품을 등록한다.")
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public JUINResponse<Long> create(final Principal principal
                                    , @RequestPart ItemRequest.Create request
                                    , @RequestPart MultipartFile representativeFile
                                    , @RequestPart(required = false) List<MultipartFile> detailFileList) {
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

    @ApiOperation(value = "판매자 상품 읽기", notes = "관리자 페이지에서 상품을 읽는다.")
    @GetMapping("/{itemId}")
    public JUINResponse<ItemResponse.Read> readSellerOnly(final Principal principal
                                                        , @PathVariable Long itemId) {
        final String identification = principal.getName();

        log.info("[P9][CTRL][SEIT][READ]: GET /api/items/seller/{} identification=({})", itemId, identification);

        try {
            principalQueryService.readByPrincipal(principal);

            final Item item = itemQueryService.readById(itemId);

            var response = ItemResponse.Read.from(item);
            return new JUINResponse<>(HttpStatus.OK, response);
        } catch (EntityNotFoundException e) {
            log.warn("[P2][CTRL][SEIT][READ]: message: ({})", e.getMessage());
            return new JUINResponse<>(HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(value = "판매자 상품 삭제", notes = "관리자 페이지에서 상품을 삭제.")
    @DeleteMapping("/{itemId}")
    public JUINResponse<Long> deleteSellerOnly(final Principal principal
                                             , @PathVariable Long itemId) {
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

