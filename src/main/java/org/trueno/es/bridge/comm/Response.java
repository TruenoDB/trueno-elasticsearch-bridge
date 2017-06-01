package org.trueno.es.bridge.comm;

import java.util.ArrayList;
import java.util.Map;

/**
 * {@link Response} denotes an outgoing or response message after perform an action request. The outgoing message
 * wrap the final status of the request, the results (a collection of JSON objects represented by a {@link Map}, and
 * an {@link Exception} class if it is the case.
 *
 * @author Edgardo Barsallo Yi (ebarsallo)
 */
public class Response {

    private String callbackId;
    private int status;
    private ArrayList<Map<String,Object>> resultSet;
    private Exception exception;

    /* ---------------------------------------------------------------------------
     * Constructors
     * ---------------------------------------------------------------------------
     */

    public Response() {
    }

    /**
     * Construct a {@link Response} instance by specifying a callback id (to return the message to the client),
     * the status of the request and a result set (JSON objects).
     *
     * @param callbackId    the callback identifier.
     * @param status        the resulting status of the request.
     * @param objects       the result set (after executing the request).
     */
    public Response(String callbackId, int status, ArrayList<Map<String, Object>> objects) {

        setCallbackId(callbackId);
        setResultSet(objects);
        setStatus(status);
    }

    /**
     * Construct a {@link Response} instance by specifying a callback id (to return the message to the client),
     * the status of the request, a result set (JSON objects) and a exception (in case of an error).
     *
     * @param callbackId    the callback identifier.
     * @param status        the resulting status of the request.
     * @param objects       the result set (after executing the request).
     * @param ex            the exception reported
     */
    public Response(String callbackId, int status, ArrayList<Map<String, Object>> objects, Exception ex) {
        this(callbackId, status, objects);

        /* Set exception */
        setException(ex);
    }

    /* ---------------------------------------------------------------------------
     * Getter and Setter methods
     * ---------------------------------------------------------------------------
     */

    public String getCallbackId() {
        return callbackId;
    }

    public void setCallbackId(String callbackId) {
        this.callbackId = callbackId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ArrayList<Map<String, Object>> getResultSet() {
        return resultSet;
    }

    public void setResultSet(ArrayList<Map<String, Object>> object) {
        this.resultSet = object;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

}
