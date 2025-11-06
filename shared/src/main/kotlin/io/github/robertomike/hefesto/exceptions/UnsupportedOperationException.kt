package io.github.robertomike.hefesto.exceptions

/**
 * This class is for the exceptions that can be thrown
 */
class UnsupportedOperationException : RuntimeException {
    /**
     * constructor
     * @param message the message
     */
    constructor(message: String) : super(message)

    /**
     * constructor
     * @param message the message
     * @param cause the cause
     */
    constructor(message: String, cause: Throwable) : super(message, cause)
}
