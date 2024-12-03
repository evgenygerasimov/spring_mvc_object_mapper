package com.spring_mvc_object_mapper.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Long customerId;

    @NotEmpty(message = "First name is required.")
    @Column(name = "first_name")
    private String firstName;

    @NotEmpty(message = "Last name is required.")
    @Column(name = "last_name")
    private String lastName;

    @Email(message = "Invalid email format.")
    @Column(name = "email")
    private String email;

    @NotEmpty(message = "Contact number is required.")
    @Column(name = "contact_number")
    private String contactNumber;
}