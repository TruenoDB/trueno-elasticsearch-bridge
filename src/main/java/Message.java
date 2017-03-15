import com.google.gson.JsonObject;

/**
 * Created by ebarsallo on 3/14/17.
 */
public class Message {

    private String callbackIndex;
    private String action;
    private JsonObject object;

    Message() {

    }

    public String getCallbackIndex() {
        return callbackIndex;
    }

    public void setCallbackIndex(String callbackIndex) {
        this.callbackIndex = callbackIndex;
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
