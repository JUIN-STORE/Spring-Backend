package store.juin.api.itemcategory.service.command;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import store.juin.api.category.model.entity.Category;
import store.juin.api.common.handler.CommandTransactional;
import store.juin.api.item.model.entity.Item;
import store.juin.api.itemcategory.repository.jpa.ItemCategoryRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ItemCategoryCommandServiceTest {
    @InjectMocks
    private ItemCategoryCommandService sut;

    @Spy
    private CommandTransactional commandTransactional;

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