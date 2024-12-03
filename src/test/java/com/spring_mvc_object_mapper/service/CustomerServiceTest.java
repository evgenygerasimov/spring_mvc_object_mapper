package com.spring_mvc_object_mapper.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring_mvc_object_mapper.entity.Customer;
import com.spring_mvc_object_mapper.entity.Order;
import com.spring_mvc_object_mapper.exception.ConvertExceptionFromObject;
import com.spring_mvc_object_mapper.exception.ConvertExceptionFromString;
import com.spring_mvc_object_mapper.exception.CustomerNotFoundException;
import com.spring_mvc_object_mapper.repository.CustomerRepository;
import com.spring_mvc_object_mapper.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private CustomerService customerService;

    private Customer customer;

    private String customerJson;

    private Order order1;
    private Order order2;
    List<Order> mockOrders;

    @BeforeEach
    void setUp() {
        customer = new Customer(); // Использование конструктора без аргументов
        customer.setCustomerId(1L);
        customer.setLastName("John Doe");
        customer.setEmail("johndoe@gmail.com");

        customerJson = "{\"id\":1,\"name\":\"John Doe\",\"email\":\"johndoe@gmail.com\"}";

        order1 = new Order();
        order1.setOrderId(101L);
        order1.setCustomer(customer);

        order2 = new Order();
        order2.setOrderId(102L);
        order2.setCustomer(customer);

        mockOrders = new ArrayList<>();
        mockOrders.add(order1);
        mockOrders.add(order2);
    }

    @Test
    void shouldReturnAllCustomersTest() {
        List<Customer> customersList = List.of(customer);
        when(customerRepository.findAll()).thenReturn(customersList);

        List<Customer> result = customerService.getAllCustomers();

        assertEquals(1, result.size());
        assertEquals(customer, result.get(0));
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void shouldThrowExceptionWhenCustomerIsNotFoundTest() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(CustomerNotFoundException.class, () -> {
            customerService.getCustomerById(1L);
        });

        assertEquals("Customer with id 1 not found", exception.getMessage());
        verify(customerRepository, times(1)).findById(1L);
    }

    @Test
    void shouldReturnCustomerAsJsonTest() throws Exception {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(objectMapper.writeValueAsString(customer)).thenReturn(customerJson);

        String result = customerService.getCustomerAsJson(1L);

        assertEquals(customerJson, result);
        verify(customerRepository, times(1)).findById(1L);
        verify(objectMapper, times(1)).writeValueAsString(customer);
    }

    @Test
    void shouldThrowExceptionWhenConvertingObjectToJsonFailsTest() throws Exception {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(objectMapper.writeValueAsString(customer)).thenThrow(new RuntimeException("Serialization error"));

        Exception exception = assertThrows(ConvertExceptionFromObject.class, () -> {
            customerService.getCustomerAsJson(1L);
        });
        assertEquals("Error converting from object", exception.getMessage());
        verify(objectMapper, times(1)).writeValueAsString(customer);
    }

    @Test
    void shouldCreateCustomerFromJsonTest() throws Exception {
        when(objectMapper.readValue(customerJson, Customer.class)).thenReturn(customer);

        Customer result = customerService.createCustomerFromJson(customerJson);

        assertEquals(customer, result);
        verify(objectMapper, times(1)).readValue(customerJson, Customer.class);
    }

    @Test
    void shouldThrowExceptionWhenConvertingStringToObjectFailsTest() throws Exception {
        when(objectMapper.readValue(customerJson, Customer.class)).thenThrow(new RuntimeException("Deserialization error"));

        Exception exception = assertThrows(ConvertExceptionFromString.class, () -> {

            customerService.createCustomerFromJson(customerJson);
        });

        assertEquals("Error converting from string", exception.getMessage());
        verify(objectMapper, times(1)).readValue(customerJson, Customer.class);
    }

    @Test
    void shouldCreateCustomerTest() {
        when(customerRepository.save(customer)).thenReturn(customer);

        Customer result = customerService.createCustomer(customer);

        assertEquals(customer, result);
        verify(customerRepository, times(1)).save(customer);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentCustomerTest() {
        when(customerRepository.existsById(1L)).thenReturn(false);

        Exception exception = assertThrows(CustomerNotFoundException.class, () -> {
            customerService.deleteCustomer(1L);
        });

        assertEquals("Customer with id 1 not found", exception.getMessage());
        verify(customerRepository, times(1)).existsById(1L);
    }

    @Test
    void shouldDeleteCustomerTest() {
        when(customerRepository.existsById(customer.getCustomerId())).thenReturn(true);
        when(orderRepository.findAll()).thenReturn(mockOrders);
        when(customerRepository.findById(customer.getCustomerId())).thenReturn(Optional.of(customer)); // getCustomerById

        customerService.deleteCustomer(customer.getCustomerId());

        verify(orderRepository, times(1)).findAll();
        verify(orderRepository, times(1)).delete(order1);
        verify(orderRepository, times(1)).delete(order2);
        verify(customerRepository, times(1)).delete(customer);
    }
}
