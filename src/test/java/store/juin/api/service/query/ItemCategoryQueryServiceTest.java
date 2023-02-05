package store.juin.api.service.query;

import store.juin.api.domain.entity.Category;
import store.juin.api.domain.entity.Item;
import store.juin.api.repository.jpa.ItemCategoryRepository;
import store.juin.api.service.command.ItemCategoryCommandService;
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
            var item = makeItem();
            var category = makeCategory();

            given(mockItemCategoryRepository.save(any())).willReturn(any());

            // when
            sut.add(item, category);

            // then
            verify(mockItemCategoryRepository, times(1)).save(any());
        }
    }

    private Item makeItem() {
        return Item.builder()
                .id(1L)
                .build();
    }

    private Category makeCategory() {
        return Category.builder()
                .id(1L)
                .build();
    }
}