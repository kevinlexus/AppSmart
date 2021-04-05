package com.ast.app.rest;

import com.ast.app.dao.CustomerService;
import com.ast.app.dao.ProductService;
import com.ast.app.dto.Customer;
import com.ast.app.dto.Product;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
public class WebController {

    private final CustomerService customerService;
    private final ProductService productService;

    public WebController(CustomerService customerService, ProductService productService) {
        this.customerService = customerService;
        this.productService = productService;
    }

    @PostMapping(value = "/customers")
    public UUID postCustomer(@RequestBody Customer customer) {
        return customerService.createCustomer(customer);
    }

    @PutMapping(value = "/customers/{customerId}")
    public void putCustomer(@RequestBody Customer customer, @PathVariable UUID customerId) {
        customerService.editCustomer(customerId, customer);
    }

    @GetMapping(value = "/customers/{customerId}")
    public ResponseEntity<Customer> getCustomer(@PathVariable UUID customerId) {
        Optional<Customer> customer = customerService.getCustomerById(customerId);
        return customer.map(value -> new ResponseEntity<>(value, new HttpHeaders(), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping(value = "/customers")
    public ResponseEntity<List<Customer>> getAllCustomers(
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        List<Customer> customers = customerService.getAllCustomers(pageNo, pageSize);

        return new ResponseEntity<>(customers, new HttpHeaders(), HttpStatus.OK);
    }


    @PostMapping(value = "/customers/{customerId}/products")
    public ResponseEntity<UUID> postProduct(@PathVariable UUID customerId,
                                            @RequestBody Product product) {
        return productService.createProduct(customerId, product)
                .map(value -> new ResponseEntity<>(value, new HttpHeaders(), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping(value = "/products/{productId}")
    public ResponseEntity<Product> getProduct(@PathVariable UUID productId) {
        Optional<Product> product = productService.getProductById(productId);
        return product.map(value -> new ResponseEntity<>(value, new HttpHeaders(), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping(value = "/products/{productId}")
    public void putProduct(@RequestBody Product product, @PathVariable UUID productId) {
        productService.editProduct(productId, product);
    }

    @GetMapping(value = "/customers/{customerId}/products")
    public ResponseEntity<List<Product>> getProductsByCustomer(@PathVariable UUID customerId,
                                                               @RequestParam(defaultValue = "0") Integer pageNo,
                                                               @RequestParam(defaultValue = "10") Integer pageSize) {
        List<Product> products = productService.getProductsByCustomerId(customerId, pageNo, pageSize);
        return new ResponseEntity<>(products, new HttpHeaders(), HttpStatus.OK);
    }

    @DeleteMapping(value = "/customers/{customerId}")
    public void deleteCustomer(@PathVariable UUID customerId) {
        customerService.deleteCustomer(customerId);
    }

    @DeleteMapping(value = "/products/{productId}")
    public void deleteProduct(@PathVariable UUID productId) {
        productService.deleteProduct(productId);
    }
}
