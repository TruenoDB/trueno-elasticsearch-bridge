package org.trueno.es.bridge.action;

/**
 * This class defines an input object for search action request. The object holds all the required information to
 * perform a search action on the Trueno database.
 *
 * @author Victor O. Santos Uceta
 * @author Edgardo Barsallo Yi (ebarsallo)
 */
public class SearchObject extends AbstractObject{

    private String query;
    private String type;
    private int size;

    /* ---------------------------------------------------------------------------
     * Getter and Setter methods
     * ---------------------------------------------------------------------------
     */

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

}
