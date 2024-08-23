package com.example.openTable.repository;

import com.example.openTable.model.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, Long> {
    List<Orders> findByUserId(Long userId);

    List<Orders> findByGuestId(String guestId);

    Optional<Orders> findByOrderNumber(String orderNumber);

    List<Orders> findByStatus(String status);
}
