package com.spring_mvc_object_mapper.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @NotEmpty(message = "Name is required.")
    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @PositiveOrZero(message = "Price must be positive.")
    @Column(name = "price")
    private Double price;

    @PositiveOrZero(message = "Quantity in stock must be positive.")
    @Column(name = "quantity_in_stock")
    private Integer quantityInStock;
}

