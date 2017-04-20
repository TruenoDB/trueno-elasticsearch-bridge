package org.trueno.core;

import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.util.iterator.IteratorUtils;

import com.google.common.collect.ImmutableList;

import java.util.Iterator;
import java.util.List;

/**
 * @author Edgardo Barsallo Yi (ebarsallo)
 * @see Edge
 */
public interface TruenoEdge extends Edge {

    /**
     * Returns the vertex for the specified direction.
     * The direction cannot be Direction.BOTH
     *
     * @param dir the direction for the vertex (either the incomming or the outgoing vertex)
     * @return the vertex for the specified direction
     */
    public TruenoVertex vertex(Direction dir);

    @Override
    default Vertex outVertex() {
        return vertex(Direction.OUT);
    }

    @Override
    default Vertex inVertex() {
        return vertex(Direction.IN);
    }

    @Override
    default Iterator<Vertex> vertices(Direction direction) {
        List<Vertex> vertices;
        if  (direction == Direction.BOTH) {
            vertices = ImmutableList.of((Vertex) vertex(Direction.OUT), (Vertex) vertex(Direction.IN));
        } else {
            vertices = ImmutableList.of((Vertex) vertex(direction));
        }
        return  vertices.iterator();
    }

}
