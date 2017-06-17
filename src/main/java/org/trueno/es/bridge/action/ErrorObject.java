package org.trueno.es.bridge.action;

import org.trueno.es.bridge.TruenoActions;

/**
 * This class defines an error occurred while executing an action. The object holds all the relevant information
 * that describes the error.
 *
 * @author Edgardo Barsallo Yi (ebarsallo)

 */
public class ErrorObject extends AbstractObject{
    private TruenoActions action;


    public ErrorObject(String index, TruenoActions action) {
        this.index  = index;
        this.action = action;
    }


    /* ---------------------------------------------------------------------------
     * Getter and Setter methods
     * ---------------------------------------------------------------------------
     */

    public TruenoActions getAction() {
        return action;
    }

    public void setAction(TruenoActions action) {
        this.action = action;
    }
}
