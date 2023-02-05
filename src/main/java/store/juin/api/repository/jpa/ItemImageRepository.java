package store.juin.api.repository.jpa;

import store.juin.api.domain.entity.ItemImage;
import store.juin.api.repository.querydsl.QuerydslItemImageRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemImageRepository extends JpaRepository<ItemImage, Long>, QuerydslItemImageRepository {
}
