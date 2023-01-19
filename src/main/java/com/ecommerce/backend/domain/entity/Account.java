package com.ecommerce.backend.domain.entity;

import com.ecommerce.backend.domain.enums.AccountRole;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "account", indexes = {
        @Index(name = "account__email", columnList = "email")
})
public class Account extends BaseEntity {
    @Id
    @Column(name = "account_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(length = 100, unique = true)
    private String email;

    @NotNull
    private String passwordHash;

    @NotNull
    private String name;

    @NotNull
    @Pattern(regexp = "[0-9]{10,11}", message = "10~11자리의 숫자만 입력가능합니다")
    private String phoneNumber;

    @NotNull
    @Enumerated(EnumType.STRING)
    private AccountRole accountRole;

    @NotNull
    private LocalDateTime lastLogin;

    // 읽기 전용, 연관관계 주인 아님
    @Setter
    @OneToOne(mappedBy = "account", fetch = FetchType.LAZY)
    private Cart cart;

    // 읽기 전용 -> addAddressList 만들어야 됨.
    @Builder.Default
    @Setter(value = AccessLevel.PRIVATE)
    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private List<Address> addressList = new ArrayList<>();

    // 읽기 전용 -> addOrderList 만들어야 됨.
    @Builder.Default
    @Setter(value = AccessLevel.PRIVATE)
    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private List<Order> orderList = new ArrayList<>();

    // 읽기용 매핑, 연관관계 주인 아님
    public void addAddressList(Address address) {
        this.addressList.add(address);
        if (address.getAccount() != this) {
            address.fillAccountRelation(this);
        }
    }

    // 읽기용 매핑, 연관관계 주인 아님
    public void addOrderList(Order order) {
        this.orderList.add(order);
        if (order.getAccount() != this) {
            order.fillAccountRelation(this);
        }
    }

    public void updateAccount(Account newAccount) {
        this.passwordHash = newAccount.passwordHash;
        this.name = newAccount.name;
        this.phoneNumber = newAccount.phoneNumber;
        this.accountRole = newAccount.accountRole;
    }
}