package org.trueno.graphdb.internal;

import org.apache.tinkerpop.gremlin.structure.Element;
import org.trueno.core.TruenoEdge;
import org.trueno.core.TruenoVertex;

/**
 * @author Edgardo Barsallo Yi (ebarsallo)
 *
 * check: com.thinkaurelius.titan.graphdb.internal.ElementCategory
 */
public enum ElementType {
    VERTEX, EDGE, GRAPH;

    public Class<? extends Element> getElementType() {
        switch (this) {
            case VERTEX:
                return TruenoVertex.class;
            case EDGE:
                return TruenoEdge.class;
            default:
                throw new IllegalArgumentException();
        }
    }


}
