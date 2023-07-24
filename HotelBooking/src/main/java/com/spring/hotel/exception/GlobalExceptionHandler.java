package com.spring.hotel.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@RestController
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleCustomException(Exception ex, WebRequest request) {
        ApiError error = new ApiError();
        error.setTimestamp(LocalDateTime.now());
        error.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.setError(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        error.setMessage(ex.getMessage());
        error.setTrace(getStackTraceAsString(ex));
        error.setPath(request.getDescription(false));

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiError> handleResponseStatusException(NoSuchElementException ex, WebRequest request) {
        ApiError error = new ApiError();
        error.setTimestamp(LocalDateTime.now());
        error.setStatus(HttpStatus.NOT_FOUND.value());
        error.setError(HttpStatus.NOT_FOUND.getReasonPhrase());
        error.setMessage(ex.getMessage());
        error.setTrace(getStackTraceAsString(ex));
        error.setPath(request.getDescription(false));

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }



    private String getStackTraceAsString(Exception ex) {
        StringWriter sw = new StringWriter();
        return sw.toString();
    }
}
