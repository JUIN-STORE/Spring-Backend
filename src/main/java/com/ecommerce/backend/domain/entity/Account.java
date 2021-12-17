package com.ecommerce.backend.domain.entity;

import com.ecommerce.backend.domain.enums.AccountType;
import com.ecommerce.backend.domain.enums.GenderType;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class) // 객체가 부모 자식 관계를 가질 때 사용
@Getter @Builder
@Entity
public class Account {
    @Id
    @Column(name = "account_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false, unique = true)
    @NotNull
    private String email;

    @NotNull
    private String passwordHash;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @NotNull
    private String phoneNumber;

    @Column(columnDefinition = "enum('MALE', 'FEMALE')")
    @Enumerated(EnumType.STRING)
    private GenderType gender;

    @Column(columnDefinition = "VARCHAR")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    @NotNull
    @Column(columnDefinition = "enum('USER', 'SELLER', 'ADMIN')")
    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @NotNull
    @CreationTimestamp
    private LocalDateTime registeredAt;

    @NotNull
    private LocalDateTime lastLogin;

//    @OneToOne
//    @JoinTable(schema = "shop",                                     // DB명
//            name = "default_address",                               // 조인 테이블명(릴레이션)
//            joinColumns = @JoinColumn(name = "account_id"),          // Account.account_id
//            inverseJoinColumns = @JoinColumn(name = "address_id")   // Address.address_id
//    )
//    private Address defaultAddress;

//    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
//    List<Address> addresses;

//    자신의 카트를 가져옴
//    @OneToOne(mappedBy = "account")
//    private Cart cart;
}
