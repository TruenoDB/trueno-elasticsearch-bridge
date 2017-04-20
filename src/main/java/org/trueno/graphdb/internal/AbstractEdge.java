package org.trueno.graphdb.internal;

import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Property;
import org.apache.tinkerpop.gremlin.structure.util.ElementHelper;
import org.apache.tinkerpop.gremlin.structure.util.StringFactory;
import org.apache.tinkerpop.gremlin.util.iterator.IteratorUtils;
import org.trueno.core.TruenoEdge;
import org.trueno.core.TruenoElement;
import org.trueno.core.TruenoProperty;
import org.trueno.core.TruenoVertex;
import org.trueno.graphdb.tinkerpop.structure.TruenoIteratorUtils;

import javax.lang.model.element.TypeElement;
import java.util.Iterator;

/**
 * @author Edgardo Barsallo Yi (ebarsallo)
 */
public abstract class AbstractEdge extends AbstractElement implements TruenoElement, TruenoEdge {

    private TruenoVertex start;
    private TruenoVertex end;


    public AbstractEdge(long id, String label, TruenoVertex start, TruenoVertex end) {
        super(id, ElementType.EDGE);
        this.label = label;

        assert start != null && end != null;
        this.start = start;
        this.end   = end;
    }

    /**
     * Returns the vertex for the specified direction.
     * The direction cannot be Direction.BOTH
     *
     * @param dir the direction for the vertex (either the incomming or the outgoing vertex)
     * @return the vertex for the specified direction
     */
    @Override
    public TruenoVertex vertex(Direction dir) {
        if (dir == Direction.IN)
            return start;
        else
            return end;
    }

    @Override
    public String toString() {
        return StringFactory.edgeString(this);
    }

    /* ---------------------------------------------------------------------------
     * Access, modify inner data
     * ---------------------------------------------------------------------------
     */

    @Override
    public <V> Iterator<Property<V>> properties(String... propertyKeys) {
        Iterator<? extends Property> it = super.properties(propertyKeys);

        return TruenoIteratorUtils.asStream(it)
                .map(p -> (Property<V>)p).iterator();
    }

    /* ---------------------------------------------------------------------------
     * Iterators
     * ---------------------------------------------------------------------------
     */

}
