package com.spring_mvc_object_mapper.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Sql(scripts = "/schema.sql")
@Sql(scripts = "/data.sql")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private String orderJson;

    @BeforeEach
    void setUp() {
        orderJson = """
                    {
                         "orderId": 2,
                         "customer": {
                             "customerId": 2,
                             "firstName": "John",
                             "lastName": "Doe",
                             "email": "johndoe@example.com",
                             "contactNumber": "1234567890"
                         },
                         "products": [
                             {
                                 "productId": 5,
                                 "name": "Product 1",
                                 "description": "This is a test product",
                                 "price": 10.0,
                                 "quantityInStock": 99
                             }
                         ],
                         "orderDate": "2021-01-01",
                         "shippingAddress": "123 Main St, Anytown USA",
                         "totalPrice": 10.0,
                         "orderStatus": "Pending"
                     }
                """;
    }

    @Test
    void shouldGetAllOrders() throws Exception {
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderId").value(1L))
                .andExpect(jsonPath("$[0].customer.firstName").value("John"))
                .andExpect(jsonPath("$[0].products[0].name").value("Product 1"));
    }

    @Test
    void shouldGetOrderById() throws Exception {
        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1L))
                .andExpect(jsonPath("$.customer.email").value("johndoe@example.com"))
                .andExpect(jsonPath("$.products[0].name").value("Product 1"));
    }

    @Test
    void shouldReturnNotFoundWhenOrderDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/orders/100"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetOrderAsJson() throws Exception {
        mockMvc.perform(get("/api/orders/1/json"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldCreateOrderFromJson() throws Exception {
        mockMvc.perform(post("/api/orders/from-json")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(2L))
                .andExpect(jsonPath("$.customer.firstName").value("John"))
                .andExpect(jsonPath("$.products[0].name").value("Product 1"));
    }

    @Test
    void shouldCreateOrder() throws Exception {
        orderJson = """
                    {
                          "orderDate": "2021-01-01",
                          "orderStatus": "Pending",
                          "shippingAddress": "123 Main St, Anytown USA",
                          "totalPrice": 0,
                          "customer": {
                              "customerId": 1,
                              "firstName": "John",
                              "lastName": "Doe",
                              "email": "johndoe@example.com",
                              "contactNumber": "1234567890"
                          },
                          "products": [
                              {
                                  "productId": 1,
                                  "name": "Product 1",
                                  "description": "This is a test product",
                                  "price": 10.0,
                                  "quantityInStock": 100
                              }
                          ]
                      }
                """;
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value(2L))
                .andExpect(jsonPath("$.customer.firstName").value("John"))
                .andExpect(jsonPath("$.products[0].name").value("Product 1"));
    }

    @Test
    void shouldDeleteOrder() throws Exception {
        mockMvc.perform(delete("/api/orders/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistentOrder() throws Exception {
        mockMvc.perform(delete("/api/orders/100"))
                .andExpect(status().isNotFound());
    }
}
