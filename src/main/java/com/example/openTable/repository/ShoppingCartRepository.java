package com.example.openTable.repository;

import com.example.openTable.model.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    Optional<ShoppingCart> findByUserId(Long userId);

    Optional<ShoppingCart> findByGuestId(String guestId);
}
