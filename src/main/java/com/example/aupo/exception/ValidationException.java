package com.example.aupo.exception;

public class ValidationException extends RuntimeException{


    private String message;
    public ValidationException(String message) {
        this.message = message;
    }


}
