package store.juin.api.item.repository.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import store.juin.api.account.enumeration.PersonalColor;
import store.juin.api.item.model.entity.Item;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long>, QuerydslItemRepository {
    Page<Item> findAll(Pageable pageable);

    Page<Item> findAllByCategoryId(Pageable pageable, Long category);

    // 참고 https://yonguri.tistory.com/122

    Long countByNameContaining(String name);

    Page<Item> findAllByPersonalColor(Pageable pageable, PersonalColor personalColor);
}
