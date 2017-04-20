package org.trueno.core;

import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;

/**
 * @author Edgardo Barsallo Yi (ebarsallo)
 */
public interface TruenoVertex extends TruenoElement, Vertex {

    /**
     * Add an outgoing edge on this vertex, with provided label and edge properties as key/value pairs.
     * These key/values must be provided in an even number where the odd numbered arguments are {@link String}
     * property keys and the even numbered arguments are the related property values.
     * <p/>
     * Automatically creates the edge label if the label is not provided and automatic creation is enabled.
     * Otherwise, this method will throw an {@link IllegalArgumentException}
     *
     * @param label     the label of the edge
     * @param inVertex  the vertex to receive an incoming edge from the current vertex
     * @param keyValues the key/value pairs to turn into edge properties
     * @return the newly created {@link TruenoEdge} edge
     */
    @Override
    public TruenoEdge addEdge(String label, Vertex inVertex, Object... keyValues);

    /**
     * Create a new property for this vertex given a key and the specified value.
     * <p/>
     * Creates and return a new {@link TruenoVertexProperty} for the given key (and the specified value)
     * on this vertex.
     *
     * @param key   the key of the vertex property
     * @param value the value of the vertex property
     * @return the newly created vertex property
     */
    @Override
    public default <V> TruenoVertexProperty<V> property(String key, V value) {
        return this.property(key, value, EMPTY_ARGS);
    }

    @Override
    public  <V> TruenoVertexProperty<V> property(final String key, final V value, final Object... keyValues);

    @Override
    public <V> TruenoVertexProperty<V> property(final VertexProperty.Cardinality cardinality, final String key,
                                         final V value,
                                         final Object... keyValues);
}
