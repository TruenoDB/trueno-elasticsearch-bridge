package org.trueno.es.bridge.comm;

import com.google.gson.JsonObject;

/**
 * {@link Message} denotes an incoming message for an action request. The message wrap the input object,
 * which describe the requirement for the request.
 *
 * The action request can be:
 * <ul>
 *     <li><b>Create Graph</b></li>. Creates a graph in Trueno database.
 *     <li><b>Drop Graph</b></li>. Drops a graph in the Trueno database.
 *     <li><b>Bulk Operations</b></li>. Perform a bulk operation (either a group of inserts or delete) in the Trueno
 *     database.
 *     <li><b>Persist Document</b></li>. Persist a document (vertex or edge) in the Trueno database.
 *     <li><b>Search</b></b></li>. Search for documens (vertices or edges) that match a filter.
 * </ul>
 *
 * @author Edgardo Barsallo Yi (ebarsallo)
 */
public class Message {

    private String callbackIdOK;
    private String callbackIdError;
    private String action;
    private JsonObject object;

    public Message() {
    }

    /* ---------------------------------------------------------------------------
     * Getter and Setter methods
     * ---------------------------------------------------------------------------
     */

    public String getCallbackIdOK() {
        return callbackIdOK;
    }

    public void setCallbackIdOK(String callbackIdComplete) {
        this.callbackIdOK = callbackIdComplete;
    }

    public String getCallbackIdError() {
        return callbackIdError;
    }

    public void setCallbackIdError(String callbackIdError) {
        this.callbackIdError = callbackIdError;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public JsonObject getObject() {
        return object;
    }

    public void setObject(JsonObject object) {
        this.object = object;
    }
}
