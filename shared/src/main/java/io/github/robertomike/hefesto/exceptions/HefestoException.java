package io.github.robertomike.hefesto.exceptions;


/**
 * This class is for the exceptions that can be thrown
 */
public class HefestoException extends RuntimeException {
    /**
     * constructor
     * @param message the message
     */
    public HefestoException(String message) {
        super(message);
    }

    /**
     * constructor
     * @param message the message
     * @param cause the cause
     */
    public HefestoException(String message, Throwable cause) {
        super(message, cause);
    }
}
