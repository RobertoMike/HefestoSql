package io.github.robertomike.hefesto.exceptions

/**
 * Base exception class for all Hefesto-related errors.
 * 
 * Thrown when general errors occur during query building or execution.
 */
class HefestoException : RuntimeException {
    /**
     * Creates a new HefestoException with the specified message.
     * @param message the error message
     */
    constructor(message: String) : super(message)

    /**
     * constructor
     * @param message the message
     * @param cause the cause
     */
    constructor(message: String, cause: Throwable) : super(message, cause)
}
