package com.spring_mvc_object_mapper.exception;

public class ProductOutOfStockException extends RuntimeException {
    public ProductOutOfStockException(String message) { super(message); }
}
