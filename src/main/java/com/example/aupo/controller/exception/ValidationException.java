package com.example.aupo.controller.exception;

public class ValidationException extends RuntimeException{


    private String message;
    public ValidationException(String message) {
        this.message = message;
    }


}
