package com.example.aupo.exception;

import lombok.AllArgsConstructor;

public class ValidationException extends RuntimeException{

    private final String message;

    public ValidationException(String message) {
        this.message = message;
    }

    public String getMessage(){
        return this.message;
    }


}
