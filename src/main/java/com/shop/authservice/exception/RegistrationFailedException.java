package com.shop.authservice.exception;

import java.io.Serial;

public class RegistrationFailedException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 5416543853567230128L;

    public RegistrationFailedException() {
    }

    public RegistrationFailedException(Throwable cause) {
      super(cause);
    }

    public RegistrationFailedException(String message) {
      super(message);
    }

    public RegistrationFailedException(String message, Throwable cause) {
      super(message, cause);
    }

}
