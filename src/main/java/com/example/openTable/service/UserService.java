package com.example.openTable.service;

import com.example.openTable.Enum.Role;
import com.example.openTable.exception.DuplicateResourceException;
import com.example.openTable.exception.ResourceNotFoundException;
import com.example.openTable.model.Review;
import com.example.openTable.model.ShoppingCart;
import com.example.openTable.model.User;
import com.example.openTable.repository.ReviewRepository;
import com.example.openTable.repository.ShoppingCartRepository;
import com.example.openTable.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService  {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public User createUser(User user) {
        logger.info("Creating a new user with email: {}", user.getEmail());
        //Encoding password for security
        String encodePassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodePassword);
        //Set by default new users to customer
        if (user.getRole() == null) {
            user.setRole(Role.CUSTOMER);
        }
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            logger.warn("Email already exists: {}", user.getEmail());
            throw new DuplicateResourceException("Email already exists: " + user.getEmail());
        }
        User createdUser = userRepository.save(user);
        logger.info("User created with ID: {}", createdUser.getId());
        return createdUser;
    }

    public User getUserById(Long id) {
        logger.info("Fetching user with ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        logger.info("User found: {}", user.getEmail());
        return user;
    }

    public List<User> getAllUsers() {
        logger.info("Fetching all users excluding ADMIN");
        return userRepository.findAll()
                .stream()
                .filter(user -> !Role.ADMIN.equals(user.getRole()))
                .collect(Collectors.toList());
    }
    public void updateUser(Long id, User updatedUser) {
        logger.info("Updating user with ID: {}", id);
        User user = getUserById(id);
        user.setName(updatedUser.getName());
        user.setEmail(updatedUser.getEmail());
        user.setPhoneNumber(updatedUser.getPhoneNumber());
        userRepository.save(user);
        logger.info("User with ID: {} has been updated", id);
    }

    public void deleteUser(Long id) {
        logger.info("Deleting user with ID: {}", id);

        if (!userRepository.existsById(id)) {
            logger.warn("Attempted to delete non-existing user with ID: {}", id);
            throw new EntityNotFoundException("User with ID: " + id + " does not exist");
        }

        //delete any cart associated with users for safe delete
        Optional<ShoppingCart> cartDelete = shoppingCartRepository.findByUserId(id);

        cartDelete.ifPresent(cart -> {
            shoppingCartRepository.delete(cart);
            logger.info("Deleted shopping cart for user with ID: {}", id);
        });
        //Delete any reviews associated with users for safe delete

        Optional<Review> reviewDelete = reviewRepository.findByUserId(id);

        reviewDelete.ifPresent(review -> {
            reviewRepository.delete(review);
            logger.info("Deleted shopping cart for user with ID: {}", id);
        });
        userRepository.deleteById(id);
        logger.info("User with ID: {} has been deleted", id);
    }

    public User loginUser(String email, String password) {
        logger.info("Login attempt with email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));


        logger.debug("Retrieved user: {}", user.getEmail());

        if (!passwordEncoder.matches(password, user.getPassword())) {
            logger.warn("Password mismatch for user: {}", email);
            throw new IllegalArgumentException("Invalid credentials");
        }

        logger.info("Login successful for user: {}", email);
        return user;
    }


}
