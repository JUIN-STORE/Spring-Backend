package store.juin.api.itemimage.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import store.juin.api.itemimage.model.entity.ItemImage;
import store.juin.api.itemimage.repository.querydsl.QuerydslItemImageRepository;

@Repository
public interface ItemImageRepository extends JpaRepository<ItemImage, Long>, QuerydslItemImageRepository {
}
