package com.ast.app.dao;

import com.ast.app.dto.Product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductService {

    Optional<UUID> createProduct(UUID customerId, Product product);

    void editProduct(UUID id, Product product);

    Optional<Product> getProductById(UUID id);

    List<Product> getProductsByCustomerId(UUID customerId, Integer pageNo, Integer pageSize);

    void deleteProduct(UUID productId);
}
