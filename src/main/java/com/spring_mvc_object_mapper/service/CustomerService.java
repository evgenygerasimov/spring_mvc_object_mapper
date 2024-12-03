package com.spring_mvc_object_mapper.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring_mvc_object_mapper.entity.Customer;
import com.spring_mvc_object_mapper.entity.Order;
import com.spring_mvc_object_mapper.exception.ConvertExceptionFromObject;
import com.spring_mvc_object_mapper.exception.ConvertExceptionFromString;
import com.spring_mvc_object_mapper.exception.CustomerNotFoundException;
import com.spring_mvc_object_mapper.repository.CustomerRepository;
import com.spring_mvc_object_mapper.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final ObjectMapper objectMapper;
    private final OrderRepository orderRepository;

    public CustomerService(CustomerRepository customerRepository, ObjectMapper objectMapper, OrderRepository orderRepository) {
        this.customerRepository = customerRepository;
        this.objectMapper = objectMapper;
        this.orderRepository = orderRepository;
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id).orElseThrow(()
                -> new CustomerNotFoundException("Customer with id " + id + " not found"));
    }

    public String getCustomerAsJson(Long id) {
        Customer customer = getCustomerById(id);
        try {
            return objectMapper.writeValueAsString(customer);
        } catch (Exception e) {
            throw new ConvertExceptionFromObject("Error converting from object");
        }
    }

    public Customer createCustomerFromJson(String customerJson) {
        try {
            return objectMapper.readValue(customerJson, Customer.class);
        } catch (Exception e) {
            throw new ConvertExceptionFromString("Error converting from string");
        }
    }

    public Customer createCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new CustomerNotFoundException("Customer with id " + id + " not found");
        }
        for(Order order : orderRepository.findAll()) {
            if(order.getCustomer().getCustomerId().equals(id)) {
                orderRepository.delete(order);
            }
        }
        customerRepository.delete(getCustomerById(id));
    }
}