package com.ecommerce.backend.service;

import com.ecommerce.backend.domain.entity.*;
import com.ecommerce.backend.domain.request.CartProductRequest;
import com.ecommerce.backend.domain.response.CartProductResponse;
import com.ecommerce.backend.repository.jpa.CartProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartProductService {
    private final CartProductRepository cartProductRepository;

    private final JwtService jwtService;
    private final ProductService productService;
    private final ProductImageService productImageService;
    private final CartService cartService;

    private List<CartProduct> readByCartId(Long cartId) {
        return cartProductRepository.findByCartId(cartId)
                .orElseThrow(EntityNotFoundException::new);
    }

    // FIXME: return entity로 변경해야 됨.
    public List<CartProductResponse.Read> readCart(Principal principal) {
        final Cart cart = check(principal);

        if (cart == null) return Collections.emptyList();

        final List<CartProduct> cartProductList = this.readByCartId(cart.getId());
        final List<Long> productIdList =
                cartProductList.stream().map(cp -> cp.getProduct().getId()).collect(Collectors.toList());

        final List<Product> productList = productService.readByIdList(productIdList);
        final int size = productList.size();

        final List<CartProduct> cartProductListInCart =
                this.readByCartIdListAndProductIdList(cart.getId(), productIdList);
        final List<ProductImage> productImageList = productImageService.readAllByProductId(productIdList);

        final List<CartProductResponse.Read> response = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            response.add(CartProductResponse.Read.from(productList.get(i), productImageList.get(i), cartProductListInCart.get(i).getCount()));
        }

        return response;
    }

    // FIXME: return entity로 변경해야 됨.
    public  List<CartProductResponse.Buy> readBuyInfoCart(List<Long> productIdList, Principal principal) {
        final Cart cart = check(principal);

        if (cart == null) return Collections.emptyList();

        final List<Product> productList = productService.readByIdList(productIdList);
        final int size = productList.size();

        final List<CartProduct> cartProductListInCart =
                this.readByCartIdListAndProductIdList(cart.getId(), productIdList);
        final List<ProductImage> productImageList = productImageService.readAllByProductId(productIdList);

        final List<CartProductResponse.Buy> response = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            response.add(CartProductResponse.Buy.from(productList.get(i), productImageList.get(i), cartProductListInCart.get(i).getCount()));
        }

        return response;
    }

    @Transactional
    public CartProductResponse.Create addCart(CartProductRequest.Add request, Principal principal) {
        final Cart cart = check(principal);

        if (cart == null) return new CartProductResponse.Create();

        final Product product = productService.readByProductId(request.getProductId());

        CartProduct cartProduct = readByCartIdAndProductId(cart.getId(), product.getId());

        if (Objects.isNull(cartProduct)) {
            cartProduct = CartProduct.createCartProduct(product, cart, request.getCount());
        } else {
            cartProduct.addCount(request.getCount());
        }

        final CartProduct save = cartProductRepository.save(cartProduct);

        return CartProductResponse.Create.of(save);
    }

    private Cart check(Principal principal) {
        final Account account = jwtService.readByPrincipal(principal);
        return cartService.readByAccountId(account.getId());
    }

    public Long removeCart(CartProductRequest.Clear request, Principal principal) {
        final Cart cart = check(principal);

        try {
            return cartProductRepository.deleteByCartIdAndProductId(cart.getId(), request.getProductId());
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
        
        // 실패했을 때
        return -1L;
    }

    public int updateQuantity(CartProductRequest.Update request, Principal principal) {
        final Cart cart = check(principal);
        final Product product = productService.readByProductId(request.getProductId());
        final CartProduct cartProduct = readByCartIdAndProductId(cart.getId(), product.getId());
        final CartProduct save = CartProduct.createCartProduct(cartProduct.getId(), product, cart, request.getCount());

        cartProductRepository.save(save);
        return save.getCount();
    }

    public List<CartProduct> readByCartIdListAndProductIdList(Long cartId, List<Long> productIdList){
        return cartProductRepository.findByCartIdAndProductIdIn(cartId, productIdList)
                .orElseThrow(EntityNotFoundException::new);
    }

    public CartProduct readByCartIdAndProductId(Long cartId, Long productId) {
        return cartProductRepository.findByCartIdAndProductId(cartId, productId);
    }
}