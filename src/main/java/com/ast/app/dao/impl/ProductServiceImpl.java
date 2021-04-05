package com.ast.app.dao.impl;

import com.ast.app.dao.ProductService;
import com.ast.app.dto.Product;
import com.ast.app.model.CustomerEntity;
import com.ast.app.model.ProductEntity;
import com.ast.app.repository.CustomerRepository;
import com.ast.app.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;
    private final EntityManager em;

    public ProductServiceImpl(ProductRepository productRepository, CustomerRepository customerRepository,
                              ModelMapper modelMapper, EntityManager em) {
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
        this.modelMapper = modelMapper;
        this.em = em;
    }


    @Transactional
    @Override
    public Optional<UUID> createProduct(UUID customerId, Product product) {
        Optional<CustomerEntity> customer = customerRepository.findById(customerId);
        if (customer.isPresent()) {
            ProductEntity productEntity = new ProductEntity();
            productEntity.setCustomer(customer.get());
            productEntity.setIsDeleted(false);
            productEntity.setCreatedAt(new Date());
            productEntity.setModifiedAt(new Date());
            productEntity.setTitle(product.getTitle());
            productEntity.setDescription(product.getDescription());
            productEntity.setPrice(product.getPrice());

            customer.get().addProductEntity(productEntity);
            em.persist(customer.get());

            return Optional.of(productEntity.getId());
        } else {
            return Optional.empty();
        }
    }

    @Transactional
    @Override
    public void editProduct(UUID id, Product product) {
        Optional<ProductEntity> productEntity = productRepository.findById(id);
        productEntity.ifPresent(t -> {
            t.setTitle(product.getTitle());
            t.setPrice(product.getPrice());
            t.setDescription(product.getDescription());
            Optional<CustomerEntity> customer = customerRepository.findById(product.getCustomerId());
            customer.ifPresent(t::setCustomer);
            t.setModifiedAt(new Date());
            em.persist(t);
        });
    }

    @Transactional
    @Override
    public Optional<Product> getProductById(UUID id) {
        Optional<ProductEntity> productEntity = productRepository.findById(id);
        return productEntity.map(entity -> modelMapper.map(entity, Product.class));
    }

    @Transactional
    @Override
    public List<Product> getProductsByCustomerId(UUID customerId, Integer pageNo, Integer pageSize) {
        PageRequest paging = PageRequest.of(pageNo, pageSize, Sort.by("title"));

        Page<ProductEntity> pagedResult = productRepository.findAllByCustomerIdAndIsDeletedFalse(customerId, paging);

        if (pagedResult.hasContent()) {
            return pagedResult.stream()
                    .map(t -> modelMapper.map(t, Product.class))
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    @Transactional
    @Override
    public void deleteProduct(UUID productId) {
        Optional<ProductEntity> product = productRepository.findById(productId);
        product.ifPresent(t->t.setIsDeleted(true));
    }
}
