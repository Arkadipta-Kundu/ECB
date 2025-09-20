package org.arkadipta.ecb.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arkadipta.ecb.dto.cart.AddToCartRequest;
import org.arkadipta.ecb.dto.cart.CartItemResponse;
import org.arkadipta.ecb.dto.cart.CartResponse;
import org.arkadipta.ecb.exception.ResourceNotFoundException;
import org.arkadipta.ecb.model.Cart;
import org.arkadipta.ecb.model.CartItem;
import org.arkadipta.ecb.model.Product;
import org.arkadipta.ecb.model.User;
import org.arkadipta.ecb.repository.CartItemRepository;
import org.arkadipta.ecb.repository.CartRepository;
import org.arkadipta.ecb.repository.ProductRepository;
import org.arkadipta.ecb.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartResponse getCart() {
        User currentUser = getCurrentUser();
        Cart cart = getOrCreateCart(currentUser);

        // Remove unavailable or out-of-stock items
        cart.getItems()
                .removeIf(item -> !item.getProduct().isActive() || item.getProduct().getStock() < item.getQuantity());

        return convertToResponse(cart);
    }

    @Transactional
    public CartResponse addToCart(AddToCartRequest request) {
        User currentUser = getCurrentUser();
        Cart cart = getOrCreateCart(currentUser);

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (!product.isActive()) {
            throw new RuntimeException("Product is not available");
        }

        if (product.getStock() < request.getQuantity()) {
            throw new RuntimeException("Insufficient stock available");
        }

        // Check if item already exists in cart
        CartItem existingItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId())
                .orElse(null);

        if (existingItem != null) {
            // Update quantity
            int newQuantity = existingItem.getQuantity() + request.getQuantity();
            if (product.getStock() < newQuantity) {
                throw new RuntimeException("Insufficient stock available");
            }
            existingItem.setQuantity(newQuantity);
            cartItemRepository.save(existingItem);
        } else {
            // Create new cart item
            CartItem cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(request.getQuantity());
            cartItemRepository.save(cartItem);
        }

        log.info("Added product {} to cart for user {}", product.getId(), currentUser.getId());
        return getCart();
    }

    @Transactional
    public CartResponse updateCartItem(Long itemId, Integer quantity) {
        User currentUser = getCurrentUser();
        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        // Verify the item belongs to current user's cart
        if (!cartItem.getCart().getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Unauthorized access to cart item");
        }

        if (quantity <= 0) {
            cartItemRepository.delete(cartItem);
            log.info("Removed cart item {} for user {}", itemId, currentUser.getId());
        } else {
            if (cartItem.getProduct().getStock() < quantity) {
                throw new RuntimeException("Insufficient stock available");
            }
            cartItem.setQuantity(quantity);
            cartItemRepository.save(cartItem);
            log.info("Updated cart item {} quantity to {} for user {}", itemId, quantity, currentUser.getId());
        }

        return getCart();
    }

    @Transactional
    public CartResponse removeFromCart(Long itemId) {
        User currentUser = getCurrentUser();
        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        // Verify the item belongs to current user's cart
        if (!cartItem.getCart().getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Unauthorized access to cart item");
        }

        cartItemRepository.delete(cartItem);
        log.info("Removed cart item {} for user {}", itemId, currentUser.getId());

        return getCart();
    }

    @Transactional
    public void clearCart() {
        User currentUser = getCurrentUser();
        Cart cart = getOrCreateCart(currentUser);
        cartItemRepository.deleteAll(cart.getItems());
        log.info("Cleared cart for user {}", currentUser.getId());
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private Cart getOrCreateCart(User user) {
        return cartRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUser(user);
                    cart.setItems(new ArrayList<>());
                    return cartRepository.save(cart);
                });
    }

    private CartResponse convertToResponse(Cart cart) {
        CartResponse response = new CartResponse();
        response.setId(cart.getId());

        List<CartItemResponse> itemResponses = cart.getItems().stream()
                .map(this::convertToItemResponse)
                .collect(Collectors.toList());

        response.setItems(itemResponses);
        response.setTotalPrice(cart.getTotalPrice());
        response.setTotalItems(cart.getTotalItems());

        return response;
    }

    private CartItemResponse convertToItemResponse(CartItem cartItem) {
        CartItemResponse response = new CartItemResponse();
        response.setId(cartItem.getId());
        response.setProductId(cartItem.getProduct().getId());
        response.setProductName(cartItem.getProduct().getName());
        response.setProductPrice(cartItem.getProduct().getPrice());
        response.setProductImageUrl(cartItem.getProduct().getImageUrl());
        response.setQuantity(cartItem.getQuantity());
        response.setSubtotal(cartItem.getSubtotal());
        response.setAvailable(cartItem.getProduct().isActive() &&
                cartItem.getProduct().getStock() >= cartItem.getQuantity());
        return response;
    }
}