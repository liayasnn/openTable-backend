package com.example.openTable.service;

import com.example.openTable.exception.ResourceNotFoundException;
import com.example.openTable.model.Product;
import com.example.openTable.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    private ProductRepository productRepository;

    public Product addProduct(Product product) {
        logger.info("Adding new product with name: {}", product.getName());
        Product savedProduct = productRepository.save(product);
        logger.info("Product added with ID: {}", savedProduct.getId());
        return savedProduct;
    }

    public List<Product> getAllProducts() {
        logger.info("Fetching all products");
        List<Product> products = productRepository.findAll();
        logger.info("Total products found: {}", products.size());
        return products;
    }

    public Product getProductById(Long id) {
        logger.info("Fetching product with ID: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Product not found with ID: {}", id);
                    return new ResourceNotFoundException("Product not found");
                });
        logger.info("Product found: {}", product.getName());
        return product;
    }

    public void deleteProduct(Long id) {
        logger.info("Deleting product with ID: {}", id);
        try {
            productRepository.deleteById(id);
            logger.info("Product with ID: {} has been deleted", id);
        } catch (Exception e) {
            logger.error("Error deleting product with ID: {}", id, e);
            throw e;
        }
    }

    public void updateStock(Long id, Integer quantity) {
        logger.info("Updating stock for product with ID: {} to quantity: {}", id, quantity);
        Product product = productRepository.getReferenceById(id);
        product.setStock(quantity);
        productRepository.save(product);
        logger.info("Stock updated for product with ID: {}", id);
    }
    public Product updateProductDetails(Long id, Product updatedProduct) {
        logger.info("Updating product details for ID: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        product.setName(updatedProduct.getName());
        product.setDescription(updatedProduct.getDescription());
        product.setPrice(updatedProduct.getPrice());
        product.setStock(updatedProduct.getStock());
        product.setProductGroup(updatedProduct.getProductGroup());

        Product savedProduct = productRepository.save(product);
        logger.info("Product details updated for ID: {}", id);
        return savedProduct;
    }

}
