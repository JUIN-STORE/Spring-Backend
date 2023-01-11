//package com.ecommerce.backend.service.command;
//
//import com.ecommerce.backend.domain.entity.Item;
//import com.ecommerce.backend.domain.entity.ItemImage;
//import com.ecommerce.backend.domain.enums.ItemStatus;
//import com.ecommerce.backend.domain.request.ItemImageRequest;
//import com.ecommerce.backend.repository.jpa.ItemImageRepository;
//import com.ecommerce.backend.upload.S3FileUploadComponent;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.springframework.http.MediaType;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//
//@ExtendWith(SpringExtension.class)
//class ItemImageCommandServiceTest {
//    @InjectMocks
//    private ItemImageCommandService sut;
//
//    @Mock
//    private ItemImageRepository itemImageRepository;
//
//    @Mock
//    private S3FileUploadComponent s3FileUploadComponent;
//
//    @Nested
//    @DisplayName("add 테스트")
//    class AddTest {
//        @Test
//        @DisplayName("업로드 성공")
//        void addTest01() {
//            // given
//            var originFileName = "cat.jpg";
//            var request =
//                    makeCreateRequest("name_1", "imageAbsUrl_1", "originName_1", true);
//            var mockMultipartFile = makeMockMultipartFile("catList", originFileName);
//            var item = makeItem();
//
//            // when
//            sut.add(request, mockMultipartFile, item);
//
//            // then
//            verify(itemImageRepository, times(1)).save(any(ItemImage.class));
//
//        }
//
//        // FIXME: 실패 케이스...?
//    }
//
//    private MockMultipartFile makeMockMultipartFile(String name, String originalFilename) {
//
//        return new MockMultipartFile(
//                "catList",
//                "cat.jpg",
//                MediaType.MULTIPART_FORM_DATA_VALUE,
//                new byte[0]);
//    }
//
//    private Item makeItem() {
//        return Item.builder()
//                .id(1L)
//                .name("상품1")
//                .price(1000)
//                .quantity(100)
//                .soldCount(2)
//                .description("상품1 설명")
//                .itemStatus(ItemStatus.SOLD_OUT)
//                .category(null)
//                .build();
//    }
//
//    private ItemImageRequest.Create makeCreateRequest(String name
//            , String originName
//            , String imageUrl
//            , Boolean isThumbnail) {
//
//        var request = new ItemImageRequest.Create(isThumbnail);
//
//        return request
//                .setImageName(name)
//                .setOriginImageName(originName)
//                .setImageUrl(imageUrl);
//    }
//}