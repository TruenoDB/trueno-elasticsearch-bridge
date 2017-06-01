package org.trueno.es.bridge.action;

/**
 * An abstraction which defines the base entity class for the input object wrapped in an action request.
 *
 * @author Edgardo Barsallo Yi (ebarsallo)
 */
public abstract class AbstractObject {

    String index;


    AbstractObject() {

    }

    /* ---------------------------------------------------------------------------
     * Getter and Setter methods
     * ---------------------------------------------------------------------------
     */

    /**
     * Returns the Elasticsearch index name.
     *
     * @return the {@code index} name.
     */
    public String getIndex() {
        return index;
    }

    /**
     * Set the Elasticsearch index name.
     *
     * @param index the {@code index} name.
     */
    public void setIndex(String index) {
        this.index = index;
    }
}
