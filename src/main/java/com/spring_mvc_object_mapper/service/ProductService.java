package com.spring_mvc_object_mapper.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring_mvc_object_mapper.entity.Order;
import com.spring_mvc_object_mapper.entity.Product;
import com.spring_mvc_object_mapper.exception.ConvertExceptionFromObject;
import com.spring_mvc_object_mapper.exception.ProductNotFoundException;
import com.spring_mvc_object_mapper.repository.OrderRepository;
import com.spring_mvc_object_mapper.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ObjectMapper objectMapper;
    private final OrderRepository orderRepository;

    public ProductService(ProductRepository productRepository,
                          ObjectMapper objectMapper, OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.objectMapper = objectMapper;
        this.orderRepository = orderRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product with id " + id + " not found"));
    }

    public String getProductAsJson(Long id) {
        Product product = getProductById(id);
        try {
            return objectMapper.writeValueAsString(product);
        } catch (Exception e) {
            throw new ConvertExceptionFromObject("Error converting product from object");
        }
    }

    public Product createProductFromJson(String productJson) {
        try {
            return objectMapper.readValue(productJson, Product.class);
        } catch (Exception e) {
            throw new RuntimeException("Error creating product from string");
        }
    }

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product productDetails) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException("Product with id " + id + " not found");
        }
        Product existingProduct = getProductById(id);
        existingProduct.setName(productDetails.getName());
        existingProduct.setDescription(productDetails.getDescription());
        existingProduct.setPrice(productDetails.getPrice());
        existingProduct.setQuantityInStock(productDetails.getQuantityInStock());
        return productRepository.save(existingProduct);
    }

    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product with id " + id + " not found"));
        List<Order> orders = orderRepository.findAll();
        for (Order order : orders) {
            order.getProducts().remove(product);
            orderRepository.save(order);
        }
        productRepository.delete(getProductById(id));
    }
}