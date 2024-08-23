package com.example.openTable.service;

import com.example.openTable.exception.ResourceNotFoundException;
import com.example.openTable.model.Product;
import com.example.openTable.model.Review;
import com.example.openTable.model.User;
import com.example.openTable.repository.ProductRepository;
import com.example.openTable.repository.ReviewRepository;
import com.example.openTable.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {

    private static final Logger logger = LoggerFactory.getLogger(ReviewService.class);

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    public Review addReview(Long userId, Long productId, Review review) {
        logger.info("Adding review for product...");

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    logger.error("Product not found with ID: {}", productId);
                    return new ResourceNotFoundException("Product not found");
                });

        if (userId != null) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> {
                        logger.error("User not found with ID: {}", userId);
                        return new ResourceNotFoundException("User not found");
                    });
            review.setUser(user);
            if (review.isAnonymous()) {
                review.setGuestName("Anonymous");
            } else {
                review.setGuestName(user.getName());
            }
        } else {
            review.setUser(null); // No user linked for guest review
        }

        review.setProduct(product);
        Review savedReview = reviewRepository.save(review);

        logger.info("Review added with ID: {}", savedReview.getId());
        return savedReview;
    }

    public List<Review> getReviewsByProductId(Long productId) {
        logger.info("Fetching reviews for product ID: {}", productId);
        List<Review> reviews = reviewRepository.findByProductId(productId);
        logger.info("Found {} reviews for product ID: {}", reviews.size(), productId);
        return reviews;
    }

    public void deleteReview(Long reviewId) {
        logger.info("Deleting review with ID: {}", reviewId);
        reviewRepository.deleteById(reviewId);
        logger.info("Review with ID: {} has been deleted", reviewId);
    }


}
