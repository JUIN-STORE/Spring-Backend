package com.ecommerce.backend.repository.jpa;

import com.ecommerce.backend.domain.entity.ItemImage;
import com.ecommerce.backend.repository.querydsl.QuerydslItemImageRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemImageRepository extends JpaRepository<ItemImage, Long>, QuerydslItemImageRepository {
}
