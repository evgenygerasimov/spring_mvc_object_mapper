package com.spring_mvc_object_mapper.controller;

import com.spring_mvc_object_mapper.entity.Customer;
import com.spring_mvc_object_mapper.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @GetMapping("/{id}/json")
    public ResponseEntity<String> getCustomerAsJson(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomerAsJson(id));
    }

    @PostMapping("/from-json")
    public ResponseEntity<Customer> createCustomerFromJson(@RequestBody String customerJson) {
        return ResponseEntity.ok(customerService.createCustomerFromJson(customerJson));
    }

    @PostMapping
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.createCustomer(customer));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}