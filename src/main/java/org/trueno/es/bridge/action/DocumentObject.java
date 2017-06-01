package org.trueno.es.bridge.action;

import java.util.Map;


/**
 * This class defines an input object for action request which involve index management. The object holds all
 * the required information to perform a persist a document on the Trueno database.
 *
 * @author Edgardo Barsallo Yi (ebarsallo)
 */

public class DocumentObject extends AbstractObject {

    private String type;
    private String id;
    private Map<String,Object> source;

    /* ---------------------------------------------------------------------------
     * Getter and Setter methods
     * ---------------------------------------------------------------------------
     */

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Object> getSource() {
        return source;
    }

    public void setSource(Map<String, Object> source) {
        this.source = source;
    }
}
