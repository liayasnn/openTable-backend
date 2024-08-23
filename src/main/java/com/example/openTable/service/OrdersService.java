package com.example.openTable.service;

import com.example.openTable.exception.ResourceNotFoundException;
import com.example.openTable.model.*;
import com.example.openTable.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;


@Service
public class OrdersService {

    private static final Logger logger = LoggerFactory.getLogger(OrdersService.class);

    @Autowired
    private OrdersRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private EmailService emailService;


    public Orders placeOrder(Long userId, String guestId, String email, String payment, Address address, String paypalResponseJson) {
        logger.info("PLACING ORDER...");

        User user = null;
        if (userId != null) {
            logger.info("FETCHING USER WITH USER ID: {}", userId);
            user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            email = user.getEmail(); // Use user's email if available
            logger.info("USER FOUND: {}, WITH EMAIL: {}", user.getName(), email);
        } else {
            logger.info("USER NOT LOGGED IN, GUEST CHECKOUT WITH EMAIL: {}", email);
        }

        // Retrieve the cart based on userId or guestId
        logger.info("RETRIEVING CART FOR USER: {} / GUEST: {}", userId, guestId);
        ShoppingCart cart = shoppingCartService.getCartByUserOrGuestId(userId, guestId);

        // Generate OrderNumber
        logger.info("GENERATING ORDER NUMBER...");
        String orderNumber = generateOrderNumber();
        logger.info("ORDER NUMBER....: {}", orderNumber);

        // Create the shipping address
        Address shippingAddress = new Address();
        shippingAddress.setStreet(address.getStreet());
        shippingAddress.setCity(address.getCity());
        shippingAddress.setState(address.getState());
        shippingAddress.setPostcode(address.getPostcode());
        shippingAddress.setCountry(address.getCountry());
        logger.info("SHIPPING ADDRESS CREATED: {}", shippingAddress);

        // Create the order
        Orders orders = new Orders();
        orders.setUser(user);  // Set the user (can be null for guests)
        orders.setGuestId(guestId);  // Set the guestId
        orders.setShippingAddress(shippingAddress);
        orders.setPaymentId(payment);
        orders.setStatus("PROCESSING");
        orders.setItems(new ArrayList<>());
        orders.setOrderNumber(orderNumber);
        logger.info("ORDER CREATED FOR USER: {} / GUEST: {}", userId, guestId);

        // Process each item in the cart
        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            logger.info("PROCESSING PRODUCT IN CART ITEM: {}", product.getName());

            if (product.getStock() < cartItem.getQuantity()) {
                logger.error("ITEM OUT OF INVENTORY/STOCK: {}", product.getName());
                throw new RuntimeException("Not enough stock for product: " + product.getName());
            }

            // Reduce the stock
            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);
            logger.info("STOCK LEVELS UPDATED FOR: {}, NEW STOCK: {}", product.getName(), product.getStock());

            // Create the order item
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(product.getPrice());
            orderItem = orderItemRepository.save(orderItem);

            orders.getItems().add(orderItem);
            logger.info("ORDER ITEM ADDED FOR PRODUCT: {}, QUANTITY: {}", product.getName(), cartItem.getQuantity());
        }

        // Save the order to the database
        orderRepository.save(orders);
        logger.info("ORDER SAVED IN DATABASE WITH ORDER NUMBER: {}", orderNumber);

        // Send confirmation email
        logger.info("PREPARING TO SEND EMAIL TO: {}", email);
        emailService.sendOrderConfirmation(email, orders.getOrderNumber());
        logger.info("ORDER CONFIRMATION EMAIL SENT TO: {}", email);

        // Clear the cart
        shoppingCartService.clearCart(userId, guestId);
        logger.info("SHOPPING CART CLEARED FOR USER: {} /  GUEST: {}", userId, guestId);
        logger.info("ORDER PLACED SUCCESSFULLY!");
        return orders;
    }


    //Helper method to generate a unique order number
    private String generateOrderNumber() {
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String uuid = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "ORD-" + timestamp + "-" + uuid;
    }

    public List<Orders> getOrdersByUserIdOrGuestId(Long userId, String guestId) {
        if (userId != null) {
            return getOrdersByUserId(userId);
        } else if (guestId != null) {
            return getOrdersByGuestId(guestId);
        } else {
            throw new IllegalArgumentException("Either userId or guestId must be provided.");
        }
    }

    private List<Orders> getOrdersByUserId(Long userId) {
        logger.info("FETCHING ORDERS FOR USER: {}", userId);
        List<Orders> orders = orderRepository.findByUserId(userId);
        logger.debug("FOUND {} ORDERS FOR USER: {}", orders.size(), userId);
        return orders;
    }

    private List<Orders> getOrdersByGuestId(String guestId) {
        logger.info("FETCHING ORDERS FOR GUEST {}", guestId);
        List<Orders> orders = orderRepository.findByGuestId(guestId);
        logger.debug("FOUND {} ORDERS FOR GUEST: {}", orders.size(), guestId);
        return orders;
    }

    public Orders updateOrderStatus(Long orderId, String status) {
        logger.info("UPDATING STATUS...");
        Orders orders = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));
        orders.setStatus(status);
        logger.info("ORDER {} STATUS UPDATED TO: {}", orderId, status);
        return orderRepository.save(orders);
    }

    public List<Orders> getAllOrdersForAdmin() {
        logger.info("FETCHING ALL ORDERS FOR ADMIN");
        return orderRepository.findAll();
    }

    public Orders updateOrderAddress(Long orderId, Address newAddress) {
        logger.info("FETCHING ALL ORDERS FOR ADMIN");
        Orders orders = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));
        orders.setShippingAddress(newAddress);
        logger.info("ALL ORDERS SUCCESSFULLY FETCHED!");
        return orderRepository.save(orders);
    }

    public void deleteOrder(Long orderId) {
        logger.info("DELETING ORDER {} ...",orderId);
        Orders orders = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));
        orderRepository.delete(orders);
        logger.info("ORDER DELETED!");
    }

    public List<Orders> getOrdersByStatus(String status) {
        logger.info("FETCHING ORDERS BY STATUS: {}", status);
        return orderRepository.findByStatus(status);
    }
}


