package org.trueno.core;

import org.apache.tinkerpop.gremlin.structure.Property;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;

import org.apache.tinkerpop.gremlin.structure.util.ElementHelper;
import org.apache.tinkerpop.gremlin.util.iterator.IteratorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

/**
 * @author @author Edgardo Barsallo Yi (ebarsallo)
 */
public final class TruenoVertexProperty<V> extends TruenoProperty<V> implements VertexProperty<V> {

    private static final Logger logger = LoggerFactory.getLogger(TruenoVertexProperty.class);

    public TruenoVertexProperty(final TruenoVertex vertex, final String key, final V value) {
        super(vertex, key, value);
    }


    @Override
    public Object id() {
        logger.debug("{}", this.key);
        logger.debug("{}", this.value);
        System.out.println("**** " + this.key.hashCode() + " * " + this.value.hashCode());
        // FIXME. Using a similar technique than Neo4J plugin. Need for a better identifier.
        return (long) (this.key.hashCode() + this.value.hashCode());
    }

    @Override
    public <V> Property<V> property(String key, V value) {
        ElementHelper.validateProperty(key, value);
        return this.element.property(key, value);
    }

    @Override
    public <U> Iterator<Property<U>> properties(String... keys) {
        // FIXME: Most probably a Map (HashSet) will be needed).
//        Component elem = ((TruenoVertex)this.element()).getBaseElement();
//
//        return (Iterator) IteratorUtils.stream(elem.properties().keys())
//                .filter(key -> ElementHelper.keyExists(key, keys))
//                .map(key -> new TruenoProperty<>(this, key, (V) elem.getProperty(key))).iterator();
        return null;
    }

    @Override
    public Vertex element() {
        return (TruenoVertex) this.element;
    }

}
