package io.github.robertomike.hefesto.exceptions

/**
 * Exception thrown when an operation is not supported by the current implementation.
 * 
 * Different Hefesto implementations (Criteria Builder vs HQL) support different features.
 * This exception is thrown when attempting to use unsupported features.
 */
class UnsupportedOperationException : RuntimeException {
    /**
     * Creates a new UnsupportedOperationException with the specified message.
     * @param message the error message describing the unsupported operation
     */
    constructor(message: String) : super(message)

    /**
     * constructor
     * @param message the message
     * @param cause the cause
     */
    constructor(message: String, cause: Throwable) : super(message, cause)
}
