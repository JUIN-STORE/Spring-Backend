package com.ecommerce.backend.service.query;

import com.ecommerce.backend.domain.entity.Category;
import com.ecommerce.backend.domain.entity.Item;
import com.ecommerce.backend.repository.jpa.ItemCategoryRepository;
import com.ecommerce.backend.service.command.ItemCategoryCommandService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ItemCategoryQueryServiceTest {
    @InjectMocks
    private ItemCategoryCommandService sut;

    @Mock
    private ItemCategoryRepository mockItemCategoryRepository;

    @Nested
    @DisplayName("ItemCategory 등록")
    class AddTest {
        @Test
        @DisplayName("성공")
        void addTest01() {
            // given
            var item = new Item();
            var category = new Category();

            given(mockItemCategoryRepository.save(any())).willReturn(any());

            // when
            sut.add(item, category);

            // then
            verify(mockItemCategoryRepository, times(1)).save(any());
        }
    }
}