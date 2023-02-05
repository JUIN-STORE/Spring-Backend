package com.juin.store.repository.jpa;

import com.juin.store.domain.entity.ItemImage;
import com.juin.store.repository.querydsl.QuerydslItemImageRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemImageRepository extends JpaRepository<ItemImage, Long>, QuerydslItemImageRepository {
}
