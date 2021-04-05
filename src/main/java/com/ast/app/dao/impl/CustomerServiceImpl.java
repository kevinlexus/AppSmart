package com.ast.app.dao.impl;

import com.ast.app.dao.CustomerService;
import com.ast.app.dto.Customer;
import com.ast.app.model.CustomerEntity;
import com.ast.app.repository.CustomerRepository;
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
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;
    private final EntityManager em;

    public CustomerServiceImpl(CustomerRepository customerRepository, ModelMapper modelMapper, EntityManager em) {
        this.customerRepository = customerRepository;
        this.modelMapper = modelMapper;
        this.em = em;
    }


    @Transactional
    @Override
    public UUID createCustomer(Customer customer) {
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setIsDeleted(false);
        customerEntity.setCreatedAt(new Date());
        customerEntity.setModifiedAt(new Date());
        customerEntity.setTitle(customer.getTitle());
        em.persist(customerEntity);
        return customerEntity.getId();
    }

    @Transactional
    @Override
    public void editCustomer(UUID id, Customer customer) {
        Optional<CustomerEntity> customerEntity = customerRepository.findById(id);
        customerEntity.ifPresent(t -> {
            t.setTitle(customer.getTitle());
            t.setModifiedAt(new Date());
            em.persist(t);
        });
    }

    @Transactional
    @Override
    public Optional<Customer> getCustomerById(UUID id) {
        Optional<CustomerEntity> customerEntity = customerRepository.findById(id);
        return customerEntity.map(entity -> modelMapper.map(entity, Customer.class));
    }

    @Transactional
    @Override
    public List<Customer> getAllCustomers(Integer pageNo, Integer pageSize) {
        PageRequest paging = PageRequest.of(pageNo, pageSize, Sort.by("title"));

        Page<CustomerEntity> pagedResult = customerRepository.findAllByIsDeletedFalse(paging);

        if (pagedResult.hasContent()) {
            return pagedResult.stream()
                    .map(t -> modelMapper.map(t, Customer.class))
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    @Transactional
    @Override
    public void deleteCustomer(UUID customerId) {
        Optional<CustomerEntity> customer = customerRepository.findById(customerId);
        customer.ifPresent(t -> {
            t.setIsDeleted(true);
            t.getProducts().forEach(p -> System.out.println("****** p="+p.getTitle()));
            t.getProducts().forEach(p -> p.setIsDeleted(true));
        });
    }
}
