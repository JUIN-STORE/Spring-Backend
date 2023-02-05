package com.juin.store.domain.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@Entity
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "uk_receiver_address",
                columnNames = {"city", "street", "zipCode", "account_id"})
})
public class Address extends BaseEntity {
    @Id @Column(name = "address_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @NotNull
    private Integer zipCode;          // 우편 번호

    @Column
    @NotNull
    private String city;             // 어느 도시에서 거주하는지

    @Column
    @NotNull
    private String street;          // 상세주소

    private boolean defaultAddress; // 기본 주소로 쓸 건지

    // 연관관계 주인
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY) // addressRepository.save(address)만 하고 싶으면 cascade = CascadeType.PERSIST 옵션 설정
    @JoinColumn(name = "account_id")
    private Account account;

    public void fillAccountRelation(Account account) {
        // 기존 Account와의 연관관계 제거
        if (this.account != null) this.account.getAddressList().remove(this);

        this.account = account;

        // 무한루프 빠지지 않도록 처리
        if (!account.getAddressList().contains(this)) account.getAddressList().add(this);
    }

    public void dirtyChecking(Address newAddress) {
        this.zipCode = newAddress.getZipCode();
        this.city = newAddress.getCity();
        this.street = newAddress.getStreet();
        this.defaultAddress = newAddress.isDefaultAddress();
    }
}

