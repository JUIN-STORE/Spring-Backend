package store.juin.api.controller.seller;

import store.juin.api.JUINResponse;
import store.juin.api.domain.entity.Item;
import store.juin.api.domain.request.ItemRequest;
import store.juin.api.domain.response.ItemResponse;
import store.juin.api.exception.JUINIOException;
import store.juin.api.service.command.ItemCommandService;
import store.juin.api.service.query.ItemQueryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.security.InvalidParameterException;
import java.util.List;

@Api(tags = {"07. Seller Item"})
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/seller/items")
public class ItemSellerApiController {
    private final ItemQueryService itemQueryService;

    private final ItemCommandService itemCommandService;

    @ApiOperation(value = "판매자 상품 등록", notes = "관리자가 상품을 등록한다.")
    @PostMapping(value = "/register", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public JUINResponse<Long> register(@RequestPart ItemRequest.Create request,
                                       @RequestPart(value = "thumbnail") MultipartFile thumbnailImage,
                                       @RequestPart(value = "fileList", required = false) List<MultipartFile> itemImageFileList) {

        // FIXME: 프론트 작업 하면서 실제 파라미터를 thumbnail -> representative 로 변경
        log.info("[P9][CON][ITEM][REGI]: GET /api/items/seller/register request({}), thumbnail({}), fileList({})"
                , request
                , thumbnailImage
                , itemImageFileList);

        try {

            var response = itemCommandService.add(request, thumbnailImage, itemImageFileList);
            return new JUINResponse<>(HttpStatus.OK, response);
        } catch (InvalidParameterException e) {
            log.warn("[P2][CON][ITEM][REGI]: 상품 썸네일은 필수입니다. message: ({})", e.getMessage(), e);
            return new JUINResponse<>(HttpStatus.BAD_REQUEST, null);
        } catch (EntityNotFoundException e) {
            log.warn("[P2][CON][ITEM][REGI]: 존재하지 않는 Entity입니다. message: ({})", e.getMessage(), e);
            return new JUINResponse<>(HttpStatus.BAD_REQUEST, null);
        } catch (JUINIOException e) {
            log.warn("[P2][CON][ITEM][REGI]: 파일 등록에 실패하였습니다. message=({})", e.getMessage(), e);
            return new JUINResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, null);
        } catch (Exception e) {
            log.warn("[P1][CON][ITEM][REGI]: 알 수 없는 에러가 발생했습니다. message=({})", e.getMessage(), e);
            return new JUINResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @ApiOperation(value = "판매자 상품 읽기", notes = "관리자 페이지에서 상품을 읽는다.")
    @GetMapping("/{itemId}")
    public JUINResponse<ItemResponse.Read> adminRead(@PathVariable Long itemId) {
        log.info("[P9][CON][ITEM][AMRD]: GET /api/items/seller/{itemId} itemId({})", itemId);

        try {
            final Item item = itemQueryService.readById(itemId);

            var response = ItemResponse.Read.from(item);
            return new JUINResponse<>(HttpStatus.OK, "상품 읽기 성공", response);
        } catch (EntityNotFoundException e) {
            log.warn("[P2][CON][ITEM][AMRD]: 존재하지 않는 Entity입니다. message: ({})", e.getMessage(), e);
            return new JUINResponse<>(HttpStatus.BAD_REQUEST, null);
        }
    }

    @ApiOperation(value = "판매자 상품 삭제", notes = "관리자 페이지에서 상품을 삭제.")
    @DeleteMapping("/{itemId}")
    public JUINResponse<Long> adminRemove(@PathVariable Long itemId) {
        log.info("[P9][CON][ITEM][AMRM]: DELETE /api/items/seller/{itemId} itemId({})", itemId);

        try {

            var response = itemCommandService.remove(itemId);
            return new JUINResponse<>(HttpStatus.OK, response);
        } catch (EntityNotFoundException e) {
            log.warn("[P2][CON][ITEM][AMRM]: 존재하지 않는 Entity입니다. message: ({})", e.getMessage(), e);
            return new JUINResponse<>(HttpStatus.BAD_REQUEST, null);
        }
    }
}

