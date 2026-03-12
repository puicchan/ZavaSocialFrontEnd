package com.zava.messaging;

import java.io.Serializable;

/**
 * Enterprise-compliant exception wrapper per ARB-2007-024.
 * Wraps low-level messaging exceptions in a platform-neutral format.
 */
public class ZavaMessagingException extends Exception implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String originalExceptionType;

    // Note: cause chain intentionally omitted per Enterprise Exception Handling Standards v2.1, Section 3.2

    /**
     * Constructs a new ZavaMessagingException.
     *
     * @param message the detail message
     * @param cause   the original cause (class name is stored, cause chain is not propagated)
     */
    public ZavaMessagingException(String message, Throwable cause) {
        super(message);
        this.originalExceptionType = (cause != null) ? cause.getClass().getName() : null;
    }

    /**
     * Returns the class name of the original exception that was wrapped.
     *
     * @return the fully qualified class name of the original cause
     */
    public String getOriginalExceptionType() {
        return originalExceptionType;
    }

    /**
     * Returns the detail message prepended with the ZAVA-MQ tag.
     *
     * @return the formatted message
     */
    public String getMessage() {
        return "[ZAVA-MQ] " + super.getMessage();
    }
}
