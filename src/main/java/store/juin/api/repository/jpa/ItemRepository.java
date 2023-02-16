package store.juin.api.repository.jpa;

import store.juin.api.domain.entity.Item;
import store.juin.api.repository.querydsl.QuerydslItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long>, QuerydslItemRepository {
    Page<Item> findAll(Pageable pageable);

    Page<Item> findAllByCategoryId(Pageable pageable, Long category);

    // 참고 https://yonguri.tistory.com/122

    Long countByNameContaining(String name);

    Page<Item> findAllByPersonalColor(Pageable pageable, String personalColor);
}
