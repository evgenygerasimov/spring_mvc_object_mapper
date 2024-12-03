package com.spring_mvc_object_mapper.controller;

import com.spring_mvc_object_mapper.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
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
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private String customerJson;

    @BeforeEach
    void setUp() {
        customerJson = "{\"customerId\":1,\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"johndoe@example.com\",\"contactNumber\":\"1234567890\"}";
    }

    @Test
    void shouldGetAllCustomers() throws Exception {
        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerId").value(1L))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].email").value("johndoe@example.com"));
    }

    @Test
    void shouldGetCustomerById() throws Exception {
        mockMvc.perform(get("/api/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(1L))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.email").value("johndoe@example.com"));
    }

    @Test
    void shouldReturnNotFoundWhenCustomerDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/customers/100"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetCustomerAsJson() throws Exception {
        mockMvc.perform(get("/api/customers/1/json"))
                .andExpect(status().isOk())
                .andExpect(content().string(customerJson));
    }

    @Test
    void shouldCreateCustomerFromJson() throws Exception {
        mockMvc.perform(post("/api/customers/from-json")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(customerJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(1L))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.email").value("johndoe@example.com"));
    }

    @Test
    void shouldCreateCustomer() throws Exception {
        customerJson = "{\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"johndoe@example.com\",\"contactNumber\":\"1234567890\"}";
        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(customerJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerId").value(2L))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.email").value("johndoe@example.com"));
    }

    @Test
    void shouldDeleteCustomer() throws Exception {
        mockMvc.perform(delete("/api/customers/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistentCustomer() throws Exception {
        mockMvc.perform(delete("/api/customers/20"))
                .andExpect(status().isNotFound());
    }
}
