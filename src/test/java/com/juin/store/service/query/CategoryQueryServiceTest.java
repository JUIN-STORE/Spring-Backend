package com.juin.store.service.query;

import com.juin.store.domain.entity.Category;
import com.juin.store.domain.response.CategoryResponse;
import com.juin.store.exception.Msg;
import com.juin.store.repository.jpa.CategoryRepository;
import org.assertj.core.api.AbstractThrowableAssert;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CategoryQueryServiceTest {
    @InjectMocks
    private CategoryQueryService sut;

    @Mock
    private CategoryRepository categoryRepository;

    @Nested
    @DisplayName("readById 테스트")
    class RetrieveByIdTest {
        @Test
        @DisplayName("최상위 카테고리가 존재할 때")
        void readByIdTest01() {
            // given
            var expected = makeParent(1L, "BOOK", 0L, null);
            given(categoryRepository.findById(anyLong())).willReturn(Optional.of(expected));

            // when
            final Category actual = sut.readById(1L);

            // then
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("하위 카테고리가 존재할 때")
        void readByIdTest02() {
            // given
            var parent = makeParent(1L, "BOOK", 0L, null);
            var expected = makeChild(20L, "IT-BOOK", 1L, parent);

            given(categoryRepository.findById(anyLong())).willReturn(Optional.of(expected));

            // when
            final Category actual = sut.readById(20L);

            // then
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("카테고리가 존재하지 않을 때")
        void readByIdTest03() {
            // given
            given(categoryRepository.findById(anyLong())).willReturn(Optional.empty());

            // when
            final AbstractThrowableAssert<?, ? extends Throwable> actual =
                    Assertions.assertThatThrownBy(() -> sut.readById(anyLong()));

            // then
            actual.isInstanceOf(EntityNotFoundException.class).hasMessage(Msg.CATEGORY_NOT_FOUND);
        }
    }


    @Nested
    @DisplayName("readAllTest 테스트")
    class RetrieveAllTest {
        @Test
        @DisplayName("성공")
        void readAllByParentIdIsNullTest01() {
            // given
            var childCategory = makeChild(2L, "IT-BOOK", 2L, null);
            var childList = List.of(childCategory);
            var category = makeParent(1L, "BOOK", 0L, childList);
            var categoryList = List.of(category);

            given(categoryRepository.findAllByParentIsNull()).willReturn(Optional.of(categoryList));

            // when
            final List<CategoryResponse.Retrieve> actual = sut.readAll();

            // then
            assertAll(
                    () -> assertEquals(categoryList.size(), actual.size()),
                    () -> assertEquals(category.getCategoryName(), actual.get(0).getCategoryName()),
                    () -> assertEquals(category.getId(), actual.get(0).getId()),
                    () -> assertEquals(category.getDepth(), actual.get(0).getDepth()),
                    () -> assertEquals(category.getChildList().size(), actual.get(0).getChildList().size()));
        }
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