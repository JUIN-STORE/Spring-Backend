package com.ecommerce.backend.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Entity @Getter @Builder
public class Address {
    @Id @Column(name = "address_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 연관관계 주인
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    private String city;

    private String street;

    private String zipCode;

    private boolean defaultAddress; // 기본 주소로 쓸 건지

    /** Account와의 연관관계 설정, @ManyToOne
     * @param account
     */
    public void setAccount(Account account) {
        // 기존 Account와의 연관관계 제거
        if (this.account != null) this.account.getAddressList().remove(this);

        this.account = account;

        // 무한루프 빠지지 않도록 처리
        if (!account.getAddressList().contains(this)) account.getAddressList().add(this);
    }
}

