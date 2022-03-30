package com.ecommerce.backend.repository;

import com.ecommerce.backend.domain.entity.Product;
import com.ecommerce.backend.domain.entity.QProduct;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
class ProductRepositoryTest {
    @Autowired
    private ProductRepository productRepository;

    @PersistenceContext
    private EntityManager em;

    @Test
    @DisplayName("Querydsl 조회 테스트2")
    public void queryDsl2(){
        this.createProductList2();
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QProduct qProduct = QProduct.product;

        String description = "테스트";

        booleanBuilder.and(qProduct.description.like("%" + description + "%"));
        booleanBuilder.and(qProduct.quantity.eq(qProduct.soldCount));

        PageRequest pageable =  PageRequest.of(0, 5);
        Page<Product> productPage = productRepository.findAll(booleanBuilder, pageable);
        List<Product> content = productPage.getContent();

        for (Product product : content) {
            System.out.println(product);
        }
    }

    @Test
    @DisplayName("Querydsl 조회 테스트1")
    public void queryDsl(){
        this.createProductList();
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QProduct qProduct = QProduct.product;
        JPAQuery<Product> query = queryFactory.selectFrom(qProduct)
                .where(qProduct.description.like("%" + "테스트" + "%"))
                .orderBy(qProduct.price.desc());

        List<Product> productList = query.fetch();

        for (Product product : productList) {
            System.out.println(product);
        }
    }

    @Test
    @DisplayName("@Query를 이용한 상품 조회 테스트")
    public void findByProductDescription() {
        this.createProductList();
        List<Product> productList = productRepository.findByProductDescription("테스트 상품 설명");
        for (Product product : productList) {
            System.out.println(product.toString());
        }
    }

    @Test
    @DisplayName("가격 내림차순 조회 테스트")
    public void findByPriceLessThanOrderByPriceDesc() {
        this.createProductList();
        List<Product> productList = productRepository.findByPriceLessThanOrderByPriceDesc(10005);

        for (Product product : productList){
            System.out.println(product);
        }
    }


    @Test
    @DisplayName("가격 LessThan 테스트")
    public void findByPriceLessThan(){
        this.createProductList();
        List<Product> productList = productRepository.findByPriceLessThan(10005);

        for (Product product : productList) {
            System.out.println(product);
        }
    }

    @Test
    @DisplayName("상품명, 상품상세설명 or 테스트")
    public void findByProductNameOrDescription(){
        this.createProductList();
        List<Product> findByProductNameOrDescription = productRepository.findByProductNameOrDescription("테스트 상품3", "테스트 상품 상세 설명4");

        for (Product product : findByProductNameOrDescription){
            System.out.println(product);
        }


    }

    @Test
    @DisplayName("상품명 조회 테스트")
    public void findByProductName(){
        this.createProductList();
        List<Product> productList = productRepository.findByProductName("테스트 상품2");
        for (Product product : productList){
            System.out.println(product);
        }
    }

    @Test
    @DisplayName("상품 저장 테스트")
    void createProduct() {
        Product product = Product.builder()
                .productName("테스트 상품1")
                .price(10000)
                .quantity(100)
                .soldCount(1)
                .description("테스트 상품 설명")
                .thumbnailPath("/url")
                .originImagePath("/origin-url")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Product save = productRepository.save(product);
        System.out.println(save);
    }

    public void createProductList(){
        for (int i = 1; i <= 10; i++){
            Product product = Product.builder()
                    .productName("테스트 상품" + i)
                    .price(10000 + i)
                    .quantity(100)
                    .soldCount(1)
                    .description("테스트 상품 설명" + i)
                    .thumbnailPath("/url")
                    .originImagePath("/origin-url")
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            productRepository.save(product);
        }
    }

    public void createProductList2(){
        for (int i = 1; i <= 5; i++){
            Product product = Product.builder()
                    .productName("테스트 상품" + i)
                    .price(10000 + i)
                    .quantity(100)
                    .soldCount(1)
                    .description("테스트 상품 설명" + i)
                    .thumbnailPath("/url")
                    .originImagePath("/origin-url")
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            productRepository.save(product);
        }

        for (int i = 6; i <= 10; i++){
            Product product = Product.builder()
                    .productName("테스트 상품" + i)
                    .price(10000 + i)
                    .quantity(100)
                    .soldCount(100)
                    .description("테스트 상품 설명" + i)
                    .thumbnailPath("/url")
                    .originImagePath("/origin-url")
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            productRepository.save(product);
        }
    }
}