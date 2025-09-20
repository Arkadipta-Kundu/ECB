package org.arkadipta.ecb.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arkadipta.ecb.dto.product.ProductRequest;
import org.arkadipta.ecb.dto.product.ProductResponse;
import org.arkadipta.ecb.exception.ResourceNotFoundException;
import org.arkadipta.ecb.model.Product;
import org.arkadipta.ecb.repository.ProductRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    @Cacheable(value = "products", key = "#pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        return productRepository.findByActiveTrue(pageable)
                .map(this::convertToResponse);
    }

    @Cacheable(value = "product", key = "#id")
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        if (!product.isActive()) {
            throw new ResourceNotFoundException("Product not available");
        }

        return convertToResponse(product);
    }

    @Cacheable(value = "productSearch")
    public Page<ProductResponse> searchProducts(
            String category, String name, BigDecimal minPrice,
            BigDecimal maxPrice, BigDecimal minRating, Pageable pageable) {

        return productRepository.findProductsWithFilters(
                category, name, minPrice, maxPrice, minRating, pageable)
                .map(this::convertToResponse);
    }

    @Cacheable(value = "categories")
    public List<String> getAllCategories() {
        return productRepository.findDistinctCategories();
    }

    @Transactional
    @CacheEvict(value = { "products", "productSearch", "categories" }, allEntries = true)
    public ProductResponse createProduct(ProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setCategory(request.getCategory());
        product.setImageUrl(request.getImageUrl());
        product.setRating(BigDecimal.ZERO);
        product.setActive(true);

        Product savedProduct = productRepository.save(product);
        log.info("Created new product with id: {}", savedProduct.getId());

        return convertToResponse(savedProduct);
    }

    @Transactional
    @CacheEvict(value = { "products", "productSearch", "categories", "product" }, allEntries = true)
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setCategory(request.getCategory());
        product.setImageUrl(request.getImageUrl());

        Product updatedProduct = productRepository.save(product);
        log.info("Updated product with id: {}", updatedProduct.getId());

        return convertToResponse(updatedProduct);
    }

    @Transactional
    @CacheEvict(value = { "products", "productSearch", "categories", "product" }, allEntries = true)
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        product.setActive(false);
        productRepository.save(product);
        log.info("Soft deleted product with id: {}", id);
    }

    @Transactional
    @CacheEvict(value = { "products", "productSearch", "product" }, allEntries = true)
    public ProductResponse updateStock(Long id, Integer stock) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        product.setStock(stock);
        Product updatedProduct = productRepository.save(product);
        log.info("Updated stock for product id: {} to {}", id, stock);

        return convertToResponse(updatedProduct);
    }

    private ProductResponse convertToResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setStock(product.getStock());
        response.setCategory(product.getCategory());
        response.setRating(product.getRating());
        response.setImageUrl(product.getImageUrl());
        response.setActive(product.isActive());
        response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedAt(product.getUpdatedAt());
        return response;
    }
}