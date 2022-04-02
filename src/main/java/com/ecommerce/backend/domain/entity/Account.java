package com.ecommerce.backend.domain.entity;

import com.ecommerce.backend.domain.enums.AccountRole;
import com.sun.istack.NotNull;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account extends BaseEntity{
    @Id @Column(name = "account_id") @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(length = 100, nullable = false, unique = true)
    private String email;

    @NotNull
    @Column(nullable = false)
    private String passwordHash;

    @NotNull
    @Column(nullable = false)
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    private AccountRole accountRole;

    @NotNull
    private LocalDateTime lastLogin;

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY) // 1:N, Account가 Driving Table, Driving Table에 mappedBy 써야 됨.
    private List<Address> addressList = new ArrayList<>();

//    @OneToOne(mappedBy = "account", fetch = FetchType.LAZY) // mapppedBy가 없는 곳이 연관관계의 주인(FK 있는 곳)
//    protected Cart cart;

//    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
//    protected List<Order> orderList = new ArrayList<>();

    public void addAddress(Address address){
        this.addressList.add(address);
        if (address.getAccount() != this){
            address.setAccount(this);
        }
    }
}