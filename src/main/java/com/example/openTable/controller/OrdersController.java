package com.example.openTable.controller;

import com.example.openTable.dto.OrderRequest;
import com.example.openTable.model.Address;
import com.example.openTable.model.Orders;
import com.example.openTable.service.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin
public class OrdersController {

    @Autowired
    private OrdersService orderService;

    @PostMapping
    public ResponseEntity<Orders> placeOrder(
            @RequestBody OrderRequest orderRequest) {
        Orders orders = orderService.placeOrder(
                orderRequest.getUserId(),
                orderRequest.getGuestId(),
                orderRequest.getEmail(),
                orderRequest.getPaymentId(),
                orderRequest.getAddress(),
                orderRequest.getPaypalResponseJson());
        return ResponseEntity.status(201).body(orders);
    }

    @GetMapping
    public ResponseEntity<List<Orders>> getOrders(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String guestId) {
        List<Orders> orders = orderService.getOrdersByUserIdOrGuestId(userId, guestId);
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<Orders> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam String status) {
        Orders updatedOrder = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(updatedOrder);
    }

    @GetMapping("/admin")
    public ResponseEntity<List<Orders>> getAllOrdersForAdmin() {
        List<Orders> orders = orderService.getAllOrdersForAdmin();
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/admin/{orderId}/address")
    public ResponseEntity<Orders> updateOrderAddress(
            @PathVariable Long orderId,
            @RequestBody Address newAddress) {
        Orders updatedOrder = orderService.updateOrderAddress(orderId, newAddress);
        return ResponseEntity.ok(updatedOrder);
    }

    @DeleteMapping("/admin/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/admin/status")
    public ResponseEntity<List<Orders>> getOrdersByStatus(@RequestParam String status) {
        List<Orders> orders = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(orders);
    }
}
