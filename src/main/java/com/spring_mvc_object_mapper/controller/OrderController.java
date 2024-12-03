package com.spring_mvc_object_mapper.controller;

import com.spring_mvc_object_mapper.entity.Order;
import com.spring_mvc_object_mapper.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @GetMapping("/{id}/json")
    public ResponseEntity<String> getOrderAsJson(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderAsJson(id));
    }

    @PostMapping("/from-json")
    public ResponseEntity<Order> createOrderFromJson(@RequestBody String orderJson) {
        return ResponseEntity.ok(orderService.createOrderFromJson(orderJson));
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(
                order,
                order.getCustomer(),
                order.getProducts()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}