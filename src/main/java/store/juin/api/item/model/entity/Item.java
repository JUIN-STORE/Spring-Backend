package store.juin.api.item.model.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Page;
import store.juin.api.account.enumeration.PersonalColor;
import store.juin.api.category.model.entity.Category;
import store.juin.api.common.model.entity.BaseEntity;
import store.juin.api.item.enumeration.ItemStatus;
import store.juin.api.item.enumeration.NotEnoughStockException;
import store.juin.api.itemimage.model.entity.ItemImage;
import store.juin.api.orderitem.model.entity.OrderItem;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Entity
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item extends BaseEntity {
    @Id @Column(name = "item_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(length = 50)
    private String name;

    @NotNull
    private Integer price;

    @NotNull
    private Integer quantity;   // 제품의 총 개수

    private Integer soldCount;  // 제품의 판매 개수, quantity가 업데이트될 수 있어서 필요

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ItemStatus itemStatus;

    @Enumerated(EnumType.STRING)
    private PersonalColor personalColor;

    // item 테이블에 category_id 컬럼을 만들어 준다.
    @NotNull
    @JoinColumn(name = "category_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;

    // 테이블에 아무 것도 안 생김.
    // 읽기 전용, 연관관계 주인 아님
    @Builder.Default
    @OneToMany(mappedBy = "item", fetch = FetchType.LAZY)
    private List<ItemImage> itemImageList = new ArrayList<>();

    // 테이블에 아무 것도 안 생김.
    // 읽기 전용, 연관관계 주인 아님
    @Builder.Default
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private List<OrderItem> orderItemList = new ArrayList<>();

    // 읽기용 매핑
    public void addItemImageList(ItemImage itemImage){
        this.itemImageList.add(itemImage);
        if (itemImage.getItem() != this){
            itemImage.initItem(this);
        }
    }

    // 재고 증가
    public void addQuantity(Integer quantity){
        this.quantity += quantity;
        this.soldCount -= quantity;
    }
    
    // 재고 삭제
    public void removeQuantity(Integer quantity){
        int restQuantity = this.quantity - quantity;

        if(restQuantity < 0){
            throw new NotEnoughStockException("Need More Stock. Current Stock: " + restQuantity);
        }
        this.quantity = restQuantity;
        this.soldCount += quantity;
    }

    public void updateStatus(ItemStatus itemStatus) {
        this.itemStatus = itemStatus;
    }

    public Integer getTotalPrice(){
        return this.price * this.soldCount;
    }

    public static List<Long> makeItemIdList(Page<Item> itemList) {
        return itemList.stream()
                .map(Item::getId)
                .collect(Collectors.toList());
    }
}