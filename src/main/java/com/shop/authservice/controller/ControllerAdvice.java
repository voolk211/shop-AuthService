package com.shop.authservice.controller;

import com.shop.authservice.exception.PasswordMismatchException;
import com.shop.authservice.exception.AuthServiceDatabaseException;
import com.shop.authservice.exception.RegistrationFailedException;
import com.shop.authservice.exception.ResourceNotFoundException;
import com.shop.authservice.exception.UserServiceException;
import com.shop.authservice.exception.UserServiceUnavailableException;
import com.shop.authservice.exception.ExceptionBody;
import io.jsonwebtoken.JwtException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(UserServiceUnavailableException.class)
    public ResponseEntity<ExceptionBody> handleUserServiceUnavailable(UserServiceUnavailableException e){
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new ExceptionBody(e.getMessage()));
    }

    @ExceptionHandler(AuthServiceDatabaseException.class)
    public ResponseEntity<ExceptionBody> handleAuthServiceDatabase(AuthServiceDatabaseException e){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ExceptionBody(e.getMessage()));
    }

    @ExceptionHandler(UserServiceException.class)
    public ResponseEntity<ExceptionBody> handleUserService(UserServiceException e){
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(new ExceptionBody(e.getMessage()));
    }

    @ExceptionHandler(RegistrationFailedException.class)
    public ResponseEntity<ExceptionBody> handleRegistrationFailed(RegistrationFailedException e){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ExceptionBody(e.getMessage()));
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ExceptionBody> handleJwt(JwtException e){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ExceptionBody(e.getMessage()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ExceptionBody> handleResourceNotFound(ResourceNotFoundException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ExceptionBody(e.getMessage()));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ExceptionBody> handleUsernameNotFound(UsernameNotFoundException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ExceptionBody(e.getMessage()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionBody> handleBadCredentials(BadCredentialsException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ExceptionBody("Invalid username or password."));
    }

    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<ExceptionBody> handlePasswordMismatch(PasswordMismatchException e){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ExceptionBody(e.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ExceptionBody> handleIllegalState(IllegalStateException e){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ExceptionBody(e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionBody> handleIllegalArgument(IllegalArgumentException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionBody(e.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionBody> handleAccessDenied(AccessDeniedException e){
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ExceptionBody(e.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionBody> handleHttpMessageNotReadable(HttpMessageNotReadableException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionBody(e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionBody> handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        ExceptionBody exceptionBody = new ExceptionBody("Validation failed");
        List<FieldError> errors = e.getBindingResult().getFieldErrors();
        exceptionBody.setErrors(errors.stream()
                .collect(Collectors.toMap(FieldError::getField,
                        fieldError ->
                                Optional.ofNullable(fieldError.getDefaultMessage())
                                        .orElse("Empty message")
                )));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionBody);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionBody> handleConstraintViolationException(ConstraintViolationException e) {
        ExceptionBody exceptionBody = new ExceptionBody("Validation failed");
        exceptionBody.setErrors(e.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString(),
                        violation -> Optional.ofNullable(violation.getMessage()).orElse("Empty message")
                )));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionBody);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionBody> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ExceptionBody(e.getMessage()));
    }

}

