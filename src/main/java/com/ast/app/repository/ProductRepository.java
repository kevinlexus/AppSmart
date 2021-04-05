package com.ast.app.repository;

import com.ast.app.model.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<ProductEntity, UUID> {

    Page<ProductEntity> findAllByCustomerIdAndIsDeletedFalse(UUID customer_id,
                                                             org.springframework.data.domain.Pageable pageable);

}