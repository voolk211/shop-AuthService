package org.example.authservice.exception;

import java.io.Serial;

public class UserServiceUnavailableException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = 2046462838880830846L;

    public UserServiceUnavailableException() {
    }

    public UserServiceUnavailableException(Throwable cause) {
        super(cause);
    }

    public UserServiceUnavailableException(String message) {
        super(message);
    }

    public UserServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
