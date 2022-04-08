package com.ecommerce.backend.domain.request;

import com.ecommerce.backend.domain.entity.Account;
import com.ecommerce.backend.domain.entity.Order;
import lombok.*;
import lombok.experimental.Accessors;
import org.aspectj.weaver.ast.Or;

public class OrderRequest {
    @Data @Accessors(chain = true)
    public static class OrderCreate {
        private Long productId;

        private int count;

        private Account account;

        public Order toOrder(){
            return Order.builder()
                    .build();
        }
    }
}