package com.shop.authservice.exception;

import java.io.Serial;

public class UserServiceException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 8213983657180288139L;

    public UserServiceException(String message) {
        super(message);
    }

    public UserServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserServiceException() {
    }

    public UserServiceException(Throwable cause) {
        super(cause);
    }

}
