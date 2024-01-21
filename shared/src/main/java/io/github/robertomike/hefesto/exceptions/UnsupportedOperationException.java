package io.github.robertomike.hefesto.exceptions;

/**
 * This class is for the exceptions that can be thrown
 */
public class UnsupportedOperationException extends RuntimeException {
    /**
     * constructor
     * @param message the message
     */
    public UnsupportedOperationException(String message) {
        super(message);
    }

    /**
     * constructor
     * @param message the message
     * @param cause the cause
     */
    public UnsupportedOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
