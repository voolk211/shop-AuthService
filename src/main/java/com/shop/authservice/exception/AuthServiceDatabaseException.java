package com.shop.authservice.exception;

import java.io.Serial;

public class AuthServiceDatabaseException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -8505172015275702806L;

    public AuthServiceDatabaseException() {
    }

    public AuthServiceDatabaseException(Throwable cause) {
        super(cause);
    }

    public AuthServiceDatabaseException(String message) {
        super(message);
    }

    public AuthServiceDatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
