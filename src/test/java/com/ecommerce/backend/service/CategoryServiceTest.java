package com.ecommerce.backend.service;

import com.ecommerce.backend.domain.entity.Category;
import com.ecommerce.backend.domain.request.CategoryRequest;
import com.ecommerce.backend.exception.Msg;
import com.ecommerce.backend.repository.jpa.CategoryRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
    @InjectMocks
    CategoryService sut;

    @Mock
    CategoryRepository categoryRepository;

    @Nested
    @DisplayName("readById 테스트")
    class ReadByIdTest {
        @Test
        @DisplayName("최상위 카테고리가 존재할 때")
        void readByIdTest01() {
            // given
            var expected = makeParent(1L, "BOOK", 0L);
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
            var parent = makeParent(1L, "BOOK", 0L);
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
    @DisplayName("readAllByParentIdIsNull 테스트")
    class ReadAllByParentIdIsNullTest {
        @Test
        @DisplayName("최상위 카테고리가 하나이상 있을 때")
        void readAllByParentIdIsNullTest01() {
            // given
            var expected = List.of(makeParent(1L, "BOOK", 0L));

            given(categoryRepository.findAllByParentIsNull()).willReturn(Optional.of(expected));

            // when
            final List<Category> actual = sut.readAllByParentIdIsNull();

            // then
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("최상위 카테고리가 하나도 없을 때")
        void readAllByParentIdIsNullTest02() {
            // given
            var expected = new ArrayList<>();
            given(categoryRepository.findAllByParentIsNull()).willReturn(Optional.empty());

            // when
            final List<Category> actual = sut.readAllByParentIdIsNull();

            // then
            assertEquals(expected, actual);
        }
    }

    private Category makeParent(Long id, String categoryName, Long depth) {
        return Category.builder()
                .id(id)
                .categoryName(categoryName)
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


    @Nested
    @DisplayName("add 테스트")
    class AddTest {
        @Test
        @DisplayName("최상위 카테고리로 추가하기")
        void addTest01() {
            // given
            var expected = 1L;
            var request = makeCategoryCreateRequest("BOOK", 0L);
            var parentCategory = makeParent(expected, "BOOK", 0L);

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
            var parentCategory = makeParent(expected, "BOOK", 0L);
            var childCategory = makeChild(expected, "BOOK", 3L, parentCategory);

            given( categoryRepository.findById(any())).willReturn(Optional.of(childCategory));
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
}