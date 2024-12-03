package com.spring_mvc_object_mapper.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ConvertExceptionFromObject.class)
    public ResponseEntity<String> convertExceptionFromJson(final ConvertExceptionFromObject convertExceptionFromObject) {
        return ResponseEntity.status(400).body(convertExceptionFromObject.getMessage());
    }

    @ExceptionHandler(ConvertExceptionFromString.class)
    public ResponseEntity<String> convertExceptionFromString(final ConvertExceptionFromString convertExceptionFromString) {
        return ResponseEntity.status(400).body(convertExceptionFromString.getMessage());
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<String> customerNotFoundException(final CustomerNotFoundException customerNotFoundException) {
        return ResponseEntity.status(404).body(customerNotFoundException.getMessage());
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<String> orderNotFoundException(final OrderNotFoundException orderNotFoundException) {
        return ResponseEntity.status(404).body(orderNotFoundException.getMessage());
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<String> productNotFoundException(final ProductNotFoundException productNotFoundException) {
        return ResponseEntity.status(404).body(productNotFoundException.getMessage());
    }

    @ExceptionHandler(ProductOutOfStockException.class)
    public ResponseEntity<String> productOutOfStockException(final ProductOutOfStockException productOutOfStockException) {
        return ResponseEntity.status(404).body(productOutOfStockException.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolationException(ConstraintViolationException ex) {
        List<String> errors = ex.getConstraintViolations().stream()
                .map(violation -> violation.getMessage())
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

}
