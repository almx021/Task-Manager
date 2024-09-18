package com.mendes.task_manager.exception;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseError responseError(String message, HttpStatus statusCode) {
        ResponseError responseError = new ResponseError(
                                        LocalDateTime.now(), 
                                        statusCode.value(), 
                                        statusCode.getReasonPhrase(),
                                        List.of(message));
        return responseError;
    }

    private ResponseError responseError(List<String> messageList, HttpStatus statusCode) {
        ResponseError responseError = new ResponseError(
                                        LocalDateTime.now(), 
                                        statusCode.value(), 
                                        statusCode.getReasonPhrase(),
                                        messageList);
        return responseError;
    }

    @ExceptionHandler(Exception.class)
    private ResponseEntity<Object> handleGeneralException(Exception ex) throws Exception {
        ResponseError error = responseError(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    private ResponseEntity<Object> handleNoResourceFoundException(NoResourceFoundException ex) {
        ResponseError error = responseError(ex.getMessage(), HttpStatus.NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(TaskNotFoundException.class)
    private ResponseEntity<Object> handleTaskNotFoundException(TaskNotFoundException ex) {
        ResponseError error = responseError(ex.getMessage(), HttpStatus.NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    private ResponseEntity<Object> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        String errorMessage = "ID must be a positive Integer.";
        ResponseError error = responseError(errorMessage, HttpStatus.BAD_REQUEST);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    private ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<String> errorMessages = ex.getBindingResult().getFieldErrors()
                                        .stream().map(FieldError::getDefaultMessage)
                                        .collect(Collectors.toList());
        
        ResponseError error = responseError(errorMessages, HttpStatus.BAD_REQUEST);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    private ResponseEntity<Object> HandleHandlerMethodValidationException(HandlerMethodValidationException ex) {
        List<String> errorMessages = ex.getParameterValidationResults()
                                        .stream().map(ParameterValidationResult::getResolvableErrors)
                                        .flatMap(List::stream).map(MessageSourceResolvable::getDefaultMessage)
                                        .collect(Collectors.toList());

        ResponseError error = responseError(errorMessages, HttpStatus.BAD_REQUEST);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
