package com.ast.app.dao;

import com.ast.app.dto.Customer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerService {

    UUID createCustomer(Customer customer);

    void editCustomer(UUID id, Customer customer);

    Optional<Customer> getCustomerById(UUID id);

    List<Customer> getAllCustomers(Integer pageNo, Integer pageSize);

    void deleteCustomer(UUID customerId);
}
