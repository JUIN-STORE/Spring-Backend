package com.ecommerce.backend.domain.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

public class OrderRequest {

    @NoArgsConstructor @ToString
    @Getter @Setter @Accessors(chain = true)
    public static class CreateRequest{
        private Long productId;

        private int count;
    }
}