package io.github.robertomike.hefesto.exceptions

/**
 * Exception thrown when errors occur during query construction or validation.
 * 
 * This includes issues with invalid field names, unsupported operations,
 * or malformed query syntax.
 */
class QueryException : RuntimeException {
    /**
     * Creates a new QueryException with the specified message.
     * @param message the error message describing the query problem
     */
    constructor(message: String) : super(message)

    /**
     * constructor
     * @param message the message
     * @param cause the cause
     */
    constructor(message: String, cause: Throwable) : super(message, cause)
}
