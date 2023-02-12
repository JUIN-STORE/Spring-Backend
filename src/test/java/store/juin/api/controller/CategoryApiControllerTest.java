package store.juin.api.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import store.juin.api.service.query.CategoryQueryService;

import java.time.ZonedDateTime;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static store.juin.api.controller.EndPoint.PORT;
import static store.juin.api.domain.ResponseUtil.makeCategoryRetrieveResponseList;
import static store.juin.api.utils.CharterUtil.DOT;

@ExtendWith({MockitoExtension.class, RestDocumentationExtension.class})
class CategoryApiControllerTest {
    private static final String API_CATEGORIES = "/api/categories";
    private MockMvc mockMvc;
    @InjectMocks
    private CategoryApiController sut;

    @Mock private CategoryQueryService categoryQueryService;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentationContextProvider) {
        mockMvc = MockMvcBuilders
                .standaloneSetup(sut)
                .apply(documentationConfiguration(restDocumentationContextProvider)
                        .operationPreprocessors()
                        .and().uris().withPort(PORT)         // 포트 설정
                        .and().operationPreprocessors()
                        .withRequestDefaults(prettyPrint())   // request 본문을 예쁘게 출력
                        .withResponseDefaults(prettyPrint())) // response 본문을 예쁘게 출력
                .build();
    }


    @Nested
    @DisplayName("@GetMapping(" + API_CATEGORIES + ")")
    class RetrieveAllTest {
        @Test
        @DisplayName("모든 카테고리를 읽어온다. - 성공")
        void retrieveAllTest01() throws Exception {
            // given
            var response = makeCategoryRetrieveResponseList();
            given(categoryQueryService.readAll()).willReturn(response);

            // when
            final ResultActions actual = mockMvc.perform(get(API_CATEGORIES)
                    .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON));

            // then
            actual
                    .andExpect(status().isOk())
                    .andDo(document(DOT + API_CATEGORIES + "/retrieveAll"
                            , responseFields(fieldWithPath("apiStatus").type(String.class).description("api 요청에 대한 상태")
                                    // data
                                    , fieldWithPath("data[].id").type(Long.class).description("카테고리 id")
                                    , fieldWithPath("data[].categoryName").type(String.class).description("카테고리명")
                                    , fieldWithPath("data[].depth").type(Long.class).description("카테고리의 뎁스")
                                    , fieldWithPath("data[].childList[].id").type(Long.class).description("하위에 있는 카테고리 id")
                                    , fieldWithPath("data[].childList[].categoryName").type(String.class).description("하위에 있는 카테고리명")
                                    , fieldWithPath("data[].childList[].depth").type(Long.class).description("하위에 있는 카테고리의 뎁스")
                                    , fieldWithPath("data[].childList[].childList[]").type(List.class).description("하하위 카테고리의 뎁스")

                                    , fieldWithPath("timestamp").type(ZonedDateTime.class).description("API 요청 시각")
                                    , fieldWithPath("region").type(String.class).description("리전 정보")
                            )
                    ));
        }
    }
}