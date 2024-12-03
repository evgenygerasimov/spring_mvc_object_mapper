package com.spring_mvc_object_mapper.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring_mvc_object_mapper.entity.Order;
import com.spring_mvc_object_mapper.entity.Product;
import com.spring_mvc_object_mapper.exception.ConvertExceptionFromObject;
import com.spring_mvc_object_mapper.exception.ProductNotFoundException;
import com.spring_mvc_object_mapper.repository.OrderRepository;
import com.spring_mvc_object_mapper.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private Order order;
    private Long productId;
    private String productJson;

    @BeforeEach
    void setUp() {
        productId = 1L;

        product = new Product();
        product.setProductId(productId);
        product.setName("Product A");
        product.setDescription("Description of Product A");
        product.setPrice(100.0);
        product.setQuantityInStock(10);

        order = new Order();
        order.setOrderId(1L);
        order.setProducts(List.of(product));

        productJson = "{\"productId\":1,\"name\":\"Product A\",\"description\":\"Description of Product A\",\"price\":100.0,\"quantityInStock\":10}";
    }

    @Test
    void shouldReturnAllProductsTest() {
        when(productRepository.findAll()).thenReturn(List.of(product));

        List<Product> result = productService.getAllProducts();

        assertEquals(1, result.size());
        assertEquals(product, result.get(0));
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void shouldReturnProductByIdTest() {
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        Product result = productService.getProductById(productId);

        assertEquals(product, result);
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    void shouldThrowExceptionWhenProductNotFoundByIdTest() {
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ProductNotFoundException.class, () -> {
            productService.getProductById(productId);
        });
        assertEquals("Product with id 1 not found", exception.getMessage());
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    void shouldReturnProductAsJsonTest() throws Exception {
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(objectMapper.writeValueAsString(product)).thenReturn(productJson);

        String result = productService.getProductAsJson(productId);

        assertEquals(productJson, result);
        verify(productRepository, times(1)).findById(productId);
        verify(objectMapper, times(1)).writeValueAsString(product);
    }

    @Test
    void shouldThrowExceptionWhenConvertingProductToJsonFailsTest() throws Exception {
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(objectMapper.writeValueAsString(product)).thenThrow(new RuntimeException("Serialization error"));

        Exception exception = assertThrows(ConvertExceptionFromObject.class, () -> {
            productService.getProductAsJson(productId);
        });
        assertEquals("Error converting product from object", exception.getMessage());
        verify(objectMapper, times(1)).writeValueAsString(product);
    }

    @Test
    void shouldCreateProductFromJsonTest() throws Exception {
        when(objectMapper.readValue(productJson, Product.class)).thenReturn(product);

        Product result = productService.createProductFromJson(productJson);

        assertEquals(product, result);
        verify(objectMapper, times(1)).readValue(productJson, Product.class);
    }

    @Test
    void shouldThrowExceptionWhenConvertingJsonToProductFailsTest() throws Exception {
        when(objectMapper.readValue(productJson, Product.class)).thenThrow(new RuntimeException("Deserialization error"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            productService.createProductFromJson(productJson);
        });
        assertEquals("Error creating product from string", exception.getMessage());
        verify(objectMapper, times(1)).readValue(productJson, Product.class);
    }

    @Test
    void shouldCreateProductTest() {
        when(productRepository.save(product)).thenReturn(product);

        Product result = productService.createProduct(product);

        assertEquals(product, result);
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void shouldUpdateProductTest() {
        Product updatedDetails = new Product();
        updatedDetails.setName("Updated Product A");
        updatedDetails.setDescription("Updated description");
        updatedDetails.setPrice(150.0);
        updatedDetails.setQuantityInStock(20);

        when(productRepository.existsById(productId)).thenReturn(true);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);

        Product result = productService.updateProduct(productId, updatedDetails);

        assertEquals("Updated Product A", result.getName());
        assertEquals("Updated description", result.getDescription());
        assertEquals(150.0, result.getPrice());
        assertEquals(20, result.getQuantityInStock());
        verify(productRepository, times(1)).existsById(productId);
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentProductTest() {
        when(productRepository.existsById(productId)).thenReturn(false);

        Exception exception = assertThrows(ProductNotFoundException.class, () -> {
            productService.updateProduct(productId, product);
        });
        assertEquals("Product with id 1 not found", exception.getMessage());
        verify(productRepository, times(1)).existsById(productId);
    }

    @Test
    void shouldDeleteProductTest() {
        order.setProducts(new ArrayList<>(List.of(product))); // Сделать список изменяемым
        Mockito.when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        Mockito.when(orderRepository.findAll()).thenReturn(List.of(order));

        productService.deleteProduct(productId);

        Mockito.verify(productRepository, Mockito.times(2)).findById(productId);
        Mockito.verify(orderRepository, Mockito.times(1)).findAll();
        Mockito.verify(orderRepository, Mockito.times(1)).save(order);
        Mockito.verify(productRepository, Mockito.times(1)).delete(product);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentProductTest() {
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ProductNotFoundException.class, () -> {
            productService.deleteProduct(productId);
        });
        assertEquals("Product with id 1 not found", exception.getMessage());
        verify(productRepository, times(1)).findById(productId);
    }
}
