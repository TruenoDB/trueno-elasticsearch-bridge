package org.trueno.es.bridge.exception;

import org.trueno.es.bridge.comm.Message;

/**
 * @author Edgardo Barsallo Yi (ebarsallo)
 */
public final class NoMappingFoundException extends TruenoActionException {

    String mapping;

    /**
     * Constructs a new exception with {@code null} as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     */
    public NoMappingFoundException() {
    }

    /**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public NoMappingFoundException(String message) {
        super(message);
    }


    /**
     * Construct a new exception with the specified detail message, the
     * input data which originated the request and the cause. <p>Note that
     * the detail message associated with {@code cause} is <i>not</i>
     * automatically incorporated in this exception's detail message.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     * @param cause   the cause (which is saved for later retrieval by the
     *                {@link #getCause()} method).  (A <tt>null</tt> value is
     *                permitted, and indicates that the cause is nonexistent or
     */
    public NoMappingFoundException(String message, Throwable cause) {
        super(message, cause);
    }


    public NoMappingFoundException(String mapping, String message, Throwable cause) {
        super(message, cause);
        this.mapping = mapping;
    }

}
