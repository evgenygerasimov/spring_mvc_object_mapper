package com.spring_mvc_object_mapper.repository;

import com.spring_mvc_object_mapper.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
