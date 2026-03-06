package org.example.authservice.exception;

import java.io.Serial;

public class PasswordMismatchException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = -4361489263641598930L;

    public PasswordMismatchException() {
    }

    public PasswordMismatchException(Throwable cause) {
        super(cause);
    }

    public PasswordMismatchException(String message) {
        super(message);
    }

    public PasswordMismatchException(String message, Throwable cause) {
        super(message, cause);
    }
}
