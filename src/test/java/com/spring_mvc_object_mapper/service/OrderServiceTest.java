package com.spring_mvc_object_mapper.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring_mvc_object_mapper.entity.Customer;
import com.spring_mvc_object_mapper.entity.Order;
import com.spring_mvc_object_mapper.entity.Product;
import com.spring_mvc_object_mapper.exception.ConvertExceptionFromObject;
import com.spring_mvc_object_mapper.exception.ConvertExceptionFromString;
import com.spring_mvc_object_mapper.exception.CustomerNotFoundException;
import com.spring_mvc_object_mapper.exception.OrderNotFoundException;
import com.spring_mvc_object_mapper.repository.CustomerRepository;
import com.spring_mvc_object_mapper.repository.OrderRepository;
import com.spring_mvc_object_mapper.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductService productService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private OrderService orderService;

    private Long orderId;
    private Long customerId;
    private Long productId;

    private Order order;
    private Customer customer;
    private Product product;
    private String orderJson;

    @BeforeEach
    void setUp() {
        orderId = 1L;
        customerId = 1L;
        productId = 1L;

        customer = new Customer();
        customer.setCustomerId(customerId);
        customer.setFirstName("John Doe");
        customer.setEmail("johndoe@gmail.com");

        product = new Product();
        product.setProductId(productId);
        product.setName("Product A");
        product.setPrice(100.0);
        product.setQuantityInStock(10);

        order = new Order();
        order.setOrderId(orderId);
        order.setCustomer(customer);
        order.setProducts(List.of(product));
        order.setTotalPrice(100.0);

        orderJson = "{\"orderId\":1,\"customer\":{\"customerId\":1,\"name\":\"John Doe\",\"email\":\"johndoe@gmail.com\"},\"products\":[{\"productId\":1,\"name\":\"Product A\",\"price\":100.0}],\"totalPrice\":100.0}";
    }

    @Test
    void shouldReturnAllOrdersTest() {
        when(orderRepository.findAll()).thenReturn(List.of(order));

        List<Order> result = orderService.getAllOrders();

        assertEquals(1, result.size());
        assertEquals(order, result.get(0));
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void shouldThrowExceptionWhenOrderNotFoundTest() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(OrderNotFoundException.class, () -> {
            orderService.getOrderById(orderId);
        });
        assertEquals("Order with ID: 1 not found", exception.getMessage());
        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    void shouldReturnOrderAsJsonTest() throws Exception {
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(objectMapper.writeValueAsString(order)).thenReturn(orderJson);

        String result = orderService.getOrderAsJson(orderId);

        assertEquals(orderJson, result);
        verify(orderRepository, times(1)).findById(orderId);
        verify(objectMapper, times(1)).writeValueAsString(order);
    }

    @Test
    void shouldThrowExceptionWhenConvertingOrderObjectToJsonFailsTest() throws Exception {
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(objectMapper.writeValueAsString(order)).thenThrow(new RuntimeException("Serialization error"));

        Exception exception = assertThrows(ConvertExceptionFromObject.class, () -> {
            orderService.getOrderAsJson(orderId);
        });
        assertEquals("Error converting from Object", exception.getMessage());
        verify(objectMapper, times(1)).writeValueAsString(order);
    }

    @Test
    void shouldCreateOrderFromJsonTest() throws Exception {
        when(objectMapper.readValue(orderJson, Order.class)).thenReturn(order);

        Order result = orderService.createOrderFromJson(orderJson);

        assertEquals(order, result);
        verify(objectMapper, times(1)).readValue(orderJson, Order.class);
    }

    @Test
    void shouldThrowExceptionWhenConvertingStringToOrderObjectFailsTest() throws Exception {
        when(objectMapper.readValue(orderJson, Order.class)).thenThrow(new RuntimeException("Deserialization error"));

        Exception exception = assertThrows(ConvertExceptionFromString.class, () -> {
            orderService.createOrderFromJson(orderJson);
        });
        assertEquals("Error converting from String", exception.getMessage());
        verify(objectMapper, times(1)).readValue(orderJson, Order.class);
    }

    @Test
    void shouldCreateOrderTest() {
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(orderRepository.save(order)).thenReturn(order);

        Order result = orderService.createOrder(order, customer, List.of(product));

        assertEquals(order, result);
        assertEquals(9, product.getQuantityInStock()); // Проверка уменьшения остатков
        verify(customerRepository, times(1)).findById(customerId);
        verify(productRepository, times(1)).findById(productId);
        verify(productService, times(1)).updateProduct(productId, product);
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void shouldThrowExceptionWhenCreatingOrderWithNonExistentCustomerTest() {
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(CustomerNotFoundException.class, () -> {
            orderService.createOrder(order, customer, List.of(product));
        });
        assertEquals("Customer with ID: 1 not found", exception.getMessage());
        verify(customerRepository, times(1)).findById(customerId);
    }

    @Test
    void shouldDeleteOrderTest() {
        when(orderRepository.existsById(orderId)).thenReturn(true);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        orderService.deleteOrder(orderId);

        verify(orderRepository, times(1)).existsById(orderId);
        verify(orderRepository, times(1)).delete(order);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentOrderTest() {
        when(orderRepository.existsById(orderId)).thenReturn(false);

        Exception exception = assertThrows(OrderNotFoundException.class, () -> {
            orderService.deleteOrder(orderId);
        });
        assertEquals("Order with ID: 1 not found", exception.getMessage());
        verify(orderRepository, times(1)).existsById(orderId);
    }
}
