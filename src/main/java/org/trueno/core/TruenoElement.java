package org.trueno.core;

import org.apache.tinkerpop.gremlin.structure.Element;
import org.trueno.graphdb.internal.ElementType;

/**
 * @author Edgardo Barsallo Yi (ebarsallo)
 */
public interface TruenoElement extends Element {

    @Override
    public Object id();

    /**
     * Checks wether
     * @param prop the key of the property
     * @return true if the property key exists, false otherwise
     */
    boolean hasProperty(String prop);

    /**
     * Returns the type of the element.
     *
     * @return the type of the element
     */
    ElementType getType();
}
