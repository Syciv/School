package com.example.aupo.exception;

import com.example.aupo.util.LogUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class StudyExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(
            final Exception e,
            final WebRequest request
    ) {
        StringBuilder stackTrace = new StringBuilder("Error: " + e.getMessage());
        for (StackTraceElement stackTraceElement : e.getStackTrace()) {
            stackTrace.append("\n").append(stackTraceElement.toString());
        }
        LogUtil.error(stackTrace.toString());

        String responseMessage = "Произошла ошибка на сервере";

        return handleExceptionInternal(
                e,
                responseMessage,
                new HttpHeaders(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                request
        );
    }

}
