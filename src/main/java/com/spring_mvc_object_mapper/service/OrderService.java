package com.spring_mvc_object_mapper.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring_mvc_object_mapper.entity.Customer;
import com.spring_mvc_object_mapper.entity.Order;
import com.spring_mvc_object_mapper.entity.Product;
import com.spring_mvc_object_mapper.exception.*;
import com.spring_mvc_object_mapper.repository.CustomerRepository;
import com.spring_mvc_object_mapper.repository.OrderRepository;
import com.spring_mvc_object_mapper.repository.ProductRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;
    private final ObjectMapper objectMapper;

    public OrderService(OrderRepository orderRepository, CustomerRepository customerRepository,
                        @Lazy ProductRepository productRepository,
                        @Lazy ProductService productService, ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.productService = productService;
        this.objectMapper = objectMapper;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElseThrow(()
                -> new OrderNotFoundException("Order with ID: " + id + " not found"));
    }

    public String getOrderAsJson(Long id) {
        Order order = getOrderById(id);
        try {
            return objectMapper.writeValueAsString(order);
        } catch (Exception e) {
            throw new ConvertExceptionFromObject("Error converting from Object");
        }
    }

    public Order createOrderFromJson(String orderJson) {
        try {
            return objectMapper.readValue(orderJson, Order.class);
        } catch (Exception e) {
            throw new ConvertExceptionFromString("Error converting from String");
        }
    }

    public Order createOrder(Order order, Customer customer, List<Product> products) {
        Customer persistedCustomer = customerRepository.findById(customer.getCustomerId())
                .orElseThrow(() -> new CustomerNotFoundException("Customer with ID: " + customer.getCustomerId() + " not found"));
        order.setCustomer(persistedCustomer);

        List<Product> persistedProducts = products.stream()
                .map(product -> productRepository.findById(product.getProductId())
                        .orElseThrow(() -> new ProductNotFoundException("Product with ID: " + product.getProductId() + " not found")))
                .peek(product -> {
                    if (product.getQuantityInStock() < 1) {
                        throw new ProductOutOfStockException("Product with ID: " + product.getProductId() + " is out of stock");
                    }
                })
                .collect(Collectors.toList());

        order.setProducts(persistedProducts);
        for (Product product : persistedProducts) {
            product.setQuantityInStock(product.getQuantityInStock() - 1);
            productService.updateProduct(product.getProductId(), product);
            order.setTotalPrice(order.getTotalPrice() + product.getPrice());
        }
        return orderRepository.save(order);
    }

    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new OrderNotFoundException("Order with ID: " + id + " not found");
        }
        orderRepository.delete(getOrderById(id));
    }
}