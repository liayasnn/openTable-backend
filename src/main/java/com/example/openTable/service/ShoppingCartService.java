package com.example.openTable.service;

import com.example.openTable.exception.ResourceNotFoundException;
import com.example.openTable.model.CartItem;
import com.example.openTable.model.Product;
import com.example.openTable.model.ShoppingCart;
import com.example.openTable.repository.CartItemRepository;
import com.example.openTable.repository.ProductRepository;
import com.example.openTable.repository.ShoppingCartRepository;
import com.example.openTable.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class ShoppingCartService {

    private static final Logger logger = LoggerFactory.getLogger(ShoppingCartService.class);

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    public ShoppingCart getCartByUserOrGuestId(Long userId, String guestId) {
        if (userId != null) {
            logger.info("Fetching shopping cart for userId: {}", userId);
            return getCartByUserId(userId);
        } else if (guestId != null) {
            logger.info("Fetching shopping cart for guestId: {}", guestId);
            return getCartByGuestId(guestId);
        } else {
            throw new IllegalArgumentException("Either userId or guestId must be provided.");
        }
    }

    private ShoppingCart getCartByUserId(Long userId) {
        ShoppingCart cart = shoppingCartRepository.findByUserId(userId)
                .orElseGet(() -> createNewCartForUser(userId));
        logger.info("Retrieved cart with id: {} for userId: {}", cart.getId(), userId);
        return cart;
    }

    private ShoppingCart getCartByGuestId(String guestId) {
        ShoppingCart cart = shoppingCartRepository.findByGuestId(guestId)
                .orElseGet(() -> createNewCartForGuest(guestId));
        logger.info("Retrieved cart with id: {} for guestId: {}", cart.getId(), guestId);
        return cart;
    }

    private ShoppingCart createNewCartForUser(Long userId) {
        ShoppingCart newCart = new ShoppingCart();
        newCart.setUser(userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found")));
        return shoppingCartRepository.save(newCart);
    }

    private ShoppingCart createNewCartForGuest(String guestId) {
        ShoppingCart newCart = new ShoppingCart();
        newCart.setGuestId(guestId);
        return shoppingCartRepository.save(newCart);
    }

    public ShoppingCart addItemToCart(Long userId, String guestId, Long productId, int quantity) {
        logger.info("Adding item to cart...");
        ShoppingCart cart = getCartByUserOrGuestId(userId, guestId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        CartItem existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

        //check if item exists in cart already
        if (existingItem != null) {
            if (quantity > 0) {
                existingItem.setQuantity(quantity);
                cartItemRepository.save(existingItem);
            } else {
                removeItemFromCart(userId, guestId, productId);
            }
        } else if (quantity > 0) {
            CartItem cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem = cartItemRepository.save(cartItem);
            cart.getItems().add(cartItem);
        }
        logger.info("Item added sucessfully!");
        return shoppingCartRepository.save(cart);
    }

    public void clearCart(Long userId, String guestId) {
        logger.info("Clearing cart...");
        ShoppingCart cart = getCartByUserOrGuestId(userId, guestId);
        cart.getItems().clear();
        shoppingCartRepository.save(cart);
        cartItemRepository.deleteAll(cart.getItems());
        logger.info("Cart cleared!");
    }


    public void removeItemFromCart(Long userId, String guestId, Long productId) {
        logger.info("Removing item from cart...");
        ShoppingCart cart = getCartByUserOrGuestId(userId, guestId);
        CartItem itemToRemove = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        cart.getItems().remove(itemToRemove);
        cartItemRepository.delete(itemToRemove);
        shoppingCartRepository.save(cart);
        logger.info("Item removed from cart successfully!");
    }

    public double calculateTotalPrice(Long userId, String guestId) {
        ShoppingCart cart = getCartByUserOrGuestId(userId, guestId);
        return cart.getItems().stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();
    }

    public int calculateTotalQuantity(Long userId, String guestId) {
        ShoppingCart cart = getCartByUserOrGuestId(userId, guestId);
        return cart.getItems().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }
    public ShoppingCart getCartForAdmin(Long cartId) {
        logger.info("Fetching cart for admin, cartId: {}", cartId);
        return shoppingCartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
    }
}

