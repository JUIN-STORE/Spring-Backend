package com.ecommerce.backend.service;

import com.ecommerce.backend.domain.entity.Account;
import com.ecommerce.backend.domain.entity.Cart;
import com.ecommerce.backend.domain.entity.CartProduct;
import com.ecommerce.backend.domain.entity.Product;
import com.ecommerce.backend.domain.request.CartProductRequest;
import com.ecommerce.backend.domain.response.CartProductResponse;
import com.ecommerce.backend.repository.CartProductRepository;
import com.ecommerce.backend.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Service Naming
 * C -> save
 * R -> findBy~
 * U -> update
 * D -> delete
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class CartProductService {
    private final AccountService accountService;
    private final ProductService productService;

    private final CartRepository cartRepository;
    private final CartProductRepository cartProductRepository;

    private List<CartProduct> findByCartId(Long cartId) {
        return cartProductRepository.findByCartIdIn(cartId);
    }

    @Transactional
    public  List<CartProductResponse.Read> readCart(Principal principal) {
        final Cart cart = check(principal);
        final List<CartProduct> cartProductList = this.findByCartId(cart.getId());
        final List<Long> productIdList = new ArrayList<>();

        for (CartProduct cartProduct : cartProductList) {
            productIdList.add(cartProduct.getProduct().getId());
        }

        final List<Product> productList = productService.findByIdIn(productIdList);

        final List<CartProductResponse.Read> readAllResponse = new ArrayList<>();

        // FIXME: 쿼리 N번
        for (Product product : productList) {
            CartProduct cartProduct = cartProductRepository.findByCartIdAndProductId(cart.getId(), product.getId());
            readAllResponse.add(CartProductResponse.Read.fromCartProduct(product, cartProduct.getCount()));
        }

        return readAllResponse;
    }

    public CartProductResponse.Add addCart(CartProductRequest.Add request, Principal principal) {
        final Cart cart = check(principal);
        final Product product = productService.findByProductId(request.getProductId());

        CartProduct cartProduct = cartProductRepository.findByCartIdAndProductId(cart.getId(), product.getId());

        if (Objects.isNull(cartProduct)) {
            cartProduct = CartProduct.createCartProduct(product, cart, request.getCount());
        } else {
            cartProduct.addCount(request.getCount());
        }

        final CartProduct save = cartProductRepository.save(cartProduct);

        return CartProductResponse.Add.fromCartProduct(save);
    }

    private Cart check(Principal principal) {
        final Account account = accountService.findByPrincipal(principal);
        return this.findByAccountId(account.getId());
    }

    private Cart findByAccountId(long cartId) {
        return cartRepository.findByAccountId(cartId);
    }

    private CartProduct findById(long cartId) {
        return cartProductRepository.findById(cartId).get();
    }

    public int deleteCart(CartProductRequest.Clear request, Principal principal) {
        final Cart cart = check(principal);

        try {
            return cartProductRepository.deleteByCartIdAndProductId(cart.getId(), request.getProductId());
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
        return -1;
    }

    public int updateQuantity(CartProductRequest.Update request, Principal principal) {
        final Cart cart = check(principal);
        final Product product = productService.findByProductId(request.getProductId());
        final CartProduct cartProduct = cartProductRepository.findByCartIdAndProductId(cart.getId(), product.getId());
        final CartProduct save = CartProduct.createCartProduct(cartProduct.getId(), product, cart, request.getCount());

        cartProductRepository.save(save);
        return save.getCount();
    }


    private CartProduct findByCartIdAndProductId(Object request, Principal principal) {
        final Cart cart = check(principal);
        Product product = null;

        if (request instanceof CartProductRequest.Add)
            product = productService.findByProductId(((CartProductRequest.Add) request).getProductId());
        if (request instanceof CartProductRequest.Update)
            product = productService.findByProductId(((CartProductRequest.Update) request).getProductId());

        if (Objects.nonNull(product)) {
            final CartProduct cartProduct = cartProductRepository.findByCartIdAndProductId(cart.getId(), product.getId());
            return cartProduct;
        }

        return null;
    }

    @Transactional
    public  List<CartProductResponse.Buy> readBuyInfoCart(List<Long> productIdList, Principal principal) {
        final Cart cart = check(principal);

        final List<Product> productList = productService.findByIdIn(productIdList);
        final List<CartProductResponse.Buy> readAllResponse = new ArrayList<>();

        // FIXME: 쿼리 N번
        for (Product product : productList) {
            CartProduct cartProduct = cartProductRepository.findByCartIdAndProductId(cart.getId(), product.getId());
            readAllResponse.add(CartProductResponse.Buy.fromCartProduct(product, cartProduct.getCount()));
        }

        return readAllResponse;
    }
}