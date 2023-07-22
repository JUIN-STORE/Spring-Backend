package store.juin.api.service.command;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import store.juin.api.domain.entity.Category;
import store.juin.api.domain.request.CategoryRequest;
import store.juin.api.handler.CommandTransactional;
import store.juin.api.repository.jpa.CategoryRepository;
import store.juin.api.service.query.CategoryQueryService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CategoryCommandServiceTest {
    @InjectMocks
    private CategoryCommandService sut;

    @Spy
    private CommandTransactional commandTransactional;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryQueryService categoryQueryService;

    @Nested
    @DisplayName("add 테스트")
    class AddTest {
        @Test
        @DisplayName("최상위 카테고리로 추가하기")
        void addTest01() {
            // given
            var expected = 1L;
            var request = makeCategoryCreateRequest("BOOK", 0L);
            var parentCategory = makeParent(expected, "BOOK", 0L, null);

            given(categoryRepository.save(any())).willReturn(parentCategory);

            // when
            final Long actual = sut.add(request);

            // then
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("최상위 아래 하위 카테고리로 추가하기")
        void addTest02() {
            // given
            var expected = 3L;
            var request = makeCategoryCreateRequest("BOOK", 1L);
            var parentCategory = makeParent(expected, "BOOK", 0L, null);
            var childCategory = makeChild(expected, "BOOK", 3L, parentCategory);

            given(categoryQueryService.readById(any())).willReturn(childCategory);
            given(categoryRepository.save(any())).willReturn(childCategory);

            // when
            final Long actual = sut.add(request);

            // then
            assertEquals(expected, actual);
        }
    }

    private CategoryRequest.Create makeCategoryCreateRequest(String categoryName, Long parentId) {
        var request = new CategoryRequest.Create();

        return request
                .setCategoryName(categoryName)
                .setParentId(parentId);
    }

    private Category makeParent(Long id, String categoryName, Long depth, List<Category> childList) {
        return Category.builder()
                .id(id)
                .categoryName(categoryName)
                .childList(childList)
                .depth(depth)
                .build();
    }

    private Category makeChild(Long id, String categoryName, Long depth, Category parent) {
        return Category.builder()
                .id(id)
                .categoryName(categoryName)
                .depth(depth)
                .parent(parent)
                .build();
    }
}