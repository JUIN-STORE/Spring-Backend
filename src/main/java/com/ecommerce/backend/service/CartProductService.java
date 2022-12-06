package com.ecommerce.backend.service;

import com.ecommerce.backend.domain.entity.Account;
import com.ecommerce.backend.domain.entity.Cart;
import com.ecommerce.backend.domain.entity.CartProduct;
import com.ecommerce.backend.domain.entity.Product;
import com.ecommerce.backend.domain.request.CartProductRequest;
import com.ecommerce.backend.exception.Msg;
import com.ecommerce.backend.repository.jpa.CartProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartProductService {
    private final CartProductRepository cartProductRepository;

    private final CartService cartService;
    private final ProductService productService;


    public CartProduct add(Account account, CartProductRequest.Add request) {
        final Cart cart = cartService.readByAccountId(account.getId());

        final Product product = productService.readByProductId(request.getProductId());

        CartProduct cartProduct = readByCartIdAndProductId(cart.getId(), product.getId());

        if (cartProduct == null) {
            cartProduct = request.toCartProduct(cart, product, request.getCount());
        } else {
            cartProduct.addCount(request.getCount());
        }

        return cartProductRepository.save(cartProduct);
    }


    public List<CartProduct> readByCartId(Long cartId) {
        return cartProductRepository.findByCartId(cartId)
                .orElseThrow(() -> new EntityNotFoundException(Msg.PRODUCT_NOT_FOUND_IN_CART));
    }

    public CartProduct readByCartIdAndProductId(Long cartId, Long productId) {
        return cartProductRepository.findByCartIdAndProductId(cartId, productId);
    }

    public List<CartProduct> readByCartIdAndProductIdList(Long cartId, List<Long> productIdList) {
        return cartProductRepository.findByCartIdAndProductIdIn(cartId, productIdList)
                .orElseThrow(EntityNotFoundException::new);
    }

    public List<Long> getProductIdListByCart(Cart cart) {
        final List<CartProduct> cartProductList = readByCartId(cart.getId());
        return cartProductList.stream().map(cp -> cp.getProduct().getId()).collect(Collectors.toList());
    }


    // FIXME: 쿼리 너무 많이 날아감. 이게 맞나?
    @Transactional
    public int modifyQuantity(Account account, CartProductRequest.Update request) {
        final Cart cart = cartService.readByAccountId(account.getId());

        final Product product = productService.readByProductId(request.getProductId());
        final CartProduct oldCartProduct = readByCartIdAndProductId(cart.getId(), product.getId());

        final CartProduct newCartProduct =
                request.toCartProduct(oldCartProduct.getId(), cart, product, request.getCount());

        oldCartProduct.dirtyChecking(newCartProduct);
        return newCartProduct.getCount();
    }


    public long remove(Account account, CartProductRequest.Clear request) {
        final Cart cart = cartService.readByAccountId(account.getId());

        try {
            return cartProductRepository.deleteByCartIdAndProductId(cart.getId(), request.getProductId());
        } catch (Exception e) {
            log.warn(e.getMessage());
        }

        // 실패했을 때
        return -1;
    }

    public void removeByAccount(Account account) {
        cartProductRepository.deleteByAccountId(account.getId());
    }
}