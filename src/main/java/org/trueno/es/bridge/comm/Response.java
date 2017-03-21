package org.trueno.es.bridge.comm;

import com.google.gson.JsonObject;

import java.util.Map;

/**
 * Created by ebarsallo on 3/14/17.
 */
public class Response {

    private String callbackIndex;
    private Map<String,Object>[] object;

    public Response() {

    }

    public String getCallbackIndex() {
        return callbackIndex;
    }

    public void setCallbackIndex(String callbackIndex) {
        this.callbackIndex = callbackIndex;
    }

    public Map<String, Object>[] getObject() {
        return object;
    }

    public void setObject(Map<String, Object>[] object) {
        this.object = object;
    }
}
