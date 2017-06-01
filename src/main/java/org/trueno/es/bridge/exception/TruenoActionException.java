package org.trueno.es.bridge.exception;

import org.trueno.es.bridge.comm.Message;

/**
 * @author Edgardo Barsallo Yi (ebarsallo)
 */
public abstract class TruenoActionException extends Exception {

    Message action;

    /**
     * Constructs a new exception with {@code null} as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     */
    TruenoActionException() {
        super();
    }

    /**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    TruenoActionException(String message) {
        super(message);
    }

    /**
     * Construct a new exception with the specified detail message and the
     * input data which originated the request.
     * @param message  the detail message. The detail message is saved for
     *                 later retrieval by the {@link #getMessage()} method.
     * @param action
     */
    TruenoActionException(String message, Message action) {
        super(message);
        this.action = action;
    }

    /**
     * Constructs a new exception with the specified detail message and
     * cause.  <p>Note that the detail message associated with
     * {@code cause} is <i>not</i> automatically incorporated in
     * this exception's detail message.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     * @param cause   the cause (which is saved for later retrieval by the
     *                {@link #getCause()} method).  (A <tt>null</tt> value is
     *                permitted, and indicates that the cause is nonexistent or
     *                unknown.)
     */
    public TruenoActionException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Construct a new exception with the specified detail message, the
     * input data which originated the request and the cause. <p>Note that
     * the detail message associated with {@code cause} is <i>not</i>
     * automatically incorporated in this exception's detail message.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     * @param action
     * @param cause   the cause (which is saved for later retrieval by the
     *                {@link #getCause()} method).  (A <tt>null</tt> value is
     *                permitted, and indicates that the cause is nonexistent or
     *                unknown.)
     */
    TruenoActionException(String message, Message action, Throwable cause) {
        super(message, cause);
        this.action = action;
    }

    /**
     * Returns the detail message string of this throwable.
     *
     * @return the detail message string of this {@code Throwable} instance
     * (which may be {@code null}).
     */
    @Override
    public String getMessage() {
        return super.getMessage()
                + "\n Action: " + action;
    }
}
