package com.ecommerce.backend.service;

import com.ecommerce.backend.domain.entity.Account;
import com.ecommerce.backend.domain.entity.Cart;
import com.ecommerce.backend.domain.entity.CartProduct;
import com.ecommerce.backend.domain.entity.Product;
import com.ecommerce.backend.domain.request.CartProductRequest;
import com.ecommerce.backend.domain.response.CartProductResponse;
import com.ecommerce.backend.exception.Msg;
import com.ecommerce.backend.repository.jpa.CartProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartProductService {
    private final CartProductRepository cartProductRepository;

    private final CartService cartService;
    private final ProductService productService;


    @Transactional
    public int add(Account account, CartProductRequest.Add request) {
        final Cart cart = cartService.readByAccountId(account.getId());

        final Product product = productService.readById(request.getProductId());

        CartProduct cartProduct = readByCartIdAndProductId(cart.getId(), product.getId());

        if (cartProduct == null) {
            // 카트에 처음 넣는 제품이라면 새로 생성
            cartProduct = request.toCartProduct(cart, product, request.getCount());
            cartProductRepository.save(cartProduct);
        } else {
            // 카트에 이미 있던 제품이라면 count+1
            cartProduct.addCount(request.getCount());
        }

        return cartProduct.getCount();
    }


    public List<CartProduct> readByCartId(Long cartId) {
        return cartProductRepository.findByCartId(cartId)
                .orElseThrow(() -> new EntityNotFoundException(Msg.PRODUCT_NOT_FOUND_IN_CART));
    }

    public CartProduct readByCartIdAndProductId(Long cartId, Long productId) {
        return cartProductRepository.findByCartIdAndProductId(cartId, productId);
    }

    public List<CartProductResponse.Read>
    readAllByCartIdAndProductIdListAndThumbnail(Long cartId, List<Long> productIdList, boolean isThumbnail) {
        return cartProductRepository.findAllByCartIdAndProductIdListAndThumbnail(cartId, productIdList, isThumbnail)
                .orElse(new ArrayList<>());
    }

    @Transactional
    public int modifyQuantity(Account account, CartProductRequest.Update request) {
        final Long productId = request.getProductId();
        final int count = request.getCount();

        final Cart cart = cartService.readByAccountId(account.getId());

        final CartProduct oldCartProduct = readByCartIdAndProductId(cart.getId(), productId);

        if (oldCartProduct == null) {
            throw new EntityNotFoundException(Msg.CART_PRODUCT_NOT_FOUND);
        }

        oldCartProduct.updateCount(count);
        return oldCartProduct.getCount();
    }


    public long remove(Account account, CartProductRequest.Clear request) {
        final Cart cart = cartService.readByAccountId(account.getId());
        return cartProductRepository.deleteByCartIdAndProductId(cart.getId(), request.getProductId());
    }

    public int removeByAccountId(Long accountId) {
        return cartProductRepository.deleteByAccountId(accountId);
    }
}