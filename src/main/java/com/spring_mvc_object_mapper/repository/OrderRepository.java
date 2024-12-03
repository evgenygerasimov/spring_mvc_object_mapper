package com.spring_mvc_object_mapper.repository;

import com.spring_mvc_object_mapper.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
