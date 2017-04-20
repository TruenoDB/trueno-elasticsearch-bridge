package org.trueno.graphdb.internal;

import com.google.common.collect.ImmutableMap;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Property;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.apache.tinkerpop.gremlin.structure.util.ElementHelper;
import org.apache.tinkerpop.gremlin.util.iterator.IteratorUtils;
import org.trueno.core.TruenoElement;
import org.trueno.core.TruenoGraph;
import org.trueno.core.TruenoProperty;

import java.util.Iterator;
import java.util.Map;

/**
 * @author Edgardo Barsallo Yi (ebarsallo)
 */
public abstract class AbstractElement implements TruenoElement {

    private static final Map<String, Object> EMPTY_PROPERTIES = ImmutableMap.of();

    protected final long id;
    protected final ElementType type;

    String label;

    private Map<String, Object> prop = EMPTY_PROPERTIES;


    public AbstractElement(long id, ElementType type) {
        this.id   = id;
        this.type = type;
    }

    @Override
    public Object id() {
        return this.id;
    }

    // FIXME: Not sure if the graph property is needed after all.
    @Override
    public Graph graph() {
        return null;
    }

    @Override
    public <V> Property<V> property(String key, V value) {
        /* Sanity checks */
        ElementHelper.validateProperty(key, value);
        /* Set property to Trueno base element */
        this.prop.put(key, value);
        return new TruenoProperty<V>(this, key, value);
    }

    @Override
    public <V> Iterator<? extends Property<V>> properties(final String... propertyKeys) {
        return (Iterator) IteratorUtils.stream(this.prop.keySet())
                .filter(key -> ElementHelper.keyExists(key, propertyKeys))
                .map(key -> new TruenoProperty<>(this, key, (V)this.prop.get(key))).iterator();
    }

    @Override
    public boolean hasProperty(String prop) {
        return false;
    }

    @Override
    public ElementType getType() {
        return type;
    }

    @Override
    public String label() {
        return label;
    }

}
