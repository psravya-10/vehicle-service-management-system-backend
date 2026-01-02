package com.vsms.servicerequest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

	 @ExceptionHandler(MethodArgumentNotValidException.class)
	    public ResponseEntity<String> handleValidation(
	            MethodArgumentNotValidException ex) {

	        String message = ex.getBindingResult()
	                .getFieldError()
	                .getDefaultMessage();

	        return ResponseEntity
	                .status(HttpStatus.BAD_REQUEST)
	                .body(message);
	    }
}
