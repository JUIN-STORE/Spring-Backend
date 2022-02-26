package com.ecommerce.backend.domain.entity;

import com.ecommerce.backend.domain.enums.AccountRole;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Builder
@Entity
public class Account {
    @Id
    @Column(name = "account_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(length = 100, nullable = false, unique = true)
    private String email;

    @NotNull
    private String passwordHash;

    @NotNull
    private String name;

    @NotNull
    private String phoneNumber;

    // 1:N, Account가 Driving Table, Driving Table에 mappedBy 써야 됨.
//    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
//    private List<Address> addressList = new ArrayList<>();

    @NotNull
    @Column(columnDefinition = "enum('USER', 'SELLER', 'ADMIN')")
    @Enumerated(EnumType.STRING)
    private AccountRole accountRole;

    @NotNull
    @CreationTimestamp
    private LocalDateTime registeredAt;

    @NotNull
    @CreationTimestamp
    private LocalDateTime updatedAt;

    @NotNull
    private LocalDateTime lastLogin;

//    @OneToMany(mappedBy = "account")
//    private List<Order> orderList = new ArrayList<>();

}
