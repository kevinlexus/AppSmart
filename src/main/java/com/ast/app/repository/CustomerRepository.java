package com.ast.app.repository;

import com.ast.app.model.CustomerEntity;
import com.ast.app.model.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CustomerRepository extends JpaRepository<CustomerEntity, UUID> {

    Page<CustomerEntity> findAllByIsDeletedFalse(org.springframework.data.domain.Pageable pageable);

}