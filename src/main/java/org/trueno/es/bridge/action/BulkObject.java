package org.trueno.es.bridge.action;

/**
 * This class defines an input object for bulk action request. The object holds all the required information to
 * perform a bulk (insert or delete) action on the Trueno database.
 *
 * @author Victor O. Santos Uceta
 * @author Edgardo Barsallo Yi (ebarsallo)
 */
public class BulkObject extends AbstractObject {

    private String[][] operations;

    /* ---------------------------------------------------------------------------
     * Getter and Setter methods
     * ---------------------------------------------------------------------------
     */

    public String[][] getOperations() {
        return operations;
    }

    public void setOperations(String[][] operations) {
        this.operations = operations;
    }

}
