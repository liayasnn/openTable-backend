package com.example.openTable.controller;

import com.example.openTable.model.ShoppingCart;
import com.example.openTable.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shopping-cart")
@CrossOrigin
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @GetMapping
    public ResponseEntity<ShoppingCart> getCart(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String guestId) {
        ShoppingCart cart = shoppingCartService.getCartByUserOrGuestId(userId, guestId);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/items")
    public ResponseEntity<ShoppingCart> addItemToCart(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String guestId,
            @RequestParam Long productId,
            @RequestParam int quantity) {
        ShoppingCart cart = shoppingCartService.addItemToCart(userId, guestId, productId, quantity);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/items")
    public ResponseEntity<Void> clearCart(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String guestId) {
        shoppingCartService.clearCart(userId, guestId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/total-price")
    public ResponseEntity<Double> getTotalPrice(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String guestId) {
        double totalPrice = shoppingCartService.calculateTotalPrice(userId, guestId);
        return ResponseEntity.ok(totalPrice);
    }

    @GetMapping("/total-quantity")
    public ResponseEntity<Integer> getTotalQuantity(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String guestId) {
        int totalQuantity = shoppingCartService.calculateTotalQuantity(userId, guestId);
        return ResponseEntity.ok(totalQuantity);
    }

    @DeleteMapping("/item")
    public ResponseEntity<Void> removeItemFromCart(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String guestId,
            @RequestParam Long productId) {
        shoppingCartService.removeItemFromCart(userId, guestId, productId);
        return ResponseEntity.noContent().build();
    }
}
