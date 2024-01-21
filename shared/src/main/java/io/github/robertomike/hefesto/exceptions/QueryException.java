package io.github.robertomike.hefesto.exceptions;


/**
 * This class is for the exceptions that can be thrown
 */
public class QueryException extends RuntimeException {
    /**
     * constructor
     * @param message the message
     */
    public QueryException(String message) {
        super(message);
    }

    /**
     * constructor
     * @param message the message
     * @param cause the cause
     */
    public QueryException(String message, Throwable cause) {
        super(message, cause);
    }
}
