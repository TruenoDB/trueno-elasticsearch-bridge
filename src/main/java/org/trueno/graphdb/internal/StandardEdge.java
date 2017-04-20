package org.trueno.graphdb.internal;

import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Property;
import org.trueno.core.TruenoVertex;

import java.util.Iterator;

/**
 * @author Edgardo Barsallo Yi (ebarsallo)
 */
public class StandardEdge extends AbstractEdge {

    public StandardEdge(long id, String label, TruenoVertex start, TruenoVertex end) {
        super(id, label, start, end);
    }

    @Override
    public void remove() {
        // TODO: implement remove()
//        this.getBaseElement().destroy();
//        TruenoHelper.destroy(this);
    }

    @Override
    public <V> Iterator<Property<V>> properties(String... propertyKeys) {
        return null;
    }
}
