package org.trueno.graphdb.internal;

import org.apache.tinkerpop.gremlin.structure.Property;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.apache.tinkerpop.gremlin.structure.util.ElementHelper;
import org.trueno.core.TruenoElement;
import org.trueno.core.TruenoVertex;
import org.trueno.core.TruenoVertexProperty;
import org.trueno.graphdb.tinkerpop.structure.TruenoHelper;
import org.trueno.graphdb.tinkerpop.structure.TruenoIteratorUtils;

import java.util.Iterator;

/**
 * Created by ebarsallo on 4/12/17.
 */
public abstract class AbstractVertex extends AbstractElement implements TruenoVertex {

    public AbstractVertex(long id) {
        super(id, ElementType.VERTEX);
    }

    /* ---------------------------------------------------------------------------
     * Access, modify inner data
     * ---------------------------------------------------------------------------
     */

    @Override
    public <V> TruenoVertexProperty<V> property(final String key, final V value, final Object... keyValues) {

    }

    @Override
    public <V> TruenoVertexProperty<V> property(final VertexProperty.Cardinality cardinality, final String key,
                                                final V value,
                                                final Object... keyValues) {

        ElementHelper.validateProperty(key, value);
//        System.out.println(" >> " + key + " ==> " + cardinality);
        /* Only single cardinality supported for now */
        if (cardinality != VertexProperty.Cardinality.single)
            throw VertexProperty.Exceptions.multiPropertiesNotSupported();
        try {
            this.getBaseElement().setProperty(key, value);
            return new TruenoVertexProperty<>(this, key, value);
        } catch (final IllegalArgumentException iae) {
            throw Property.Exceptions.dataTypeOfPropertyValueNotSupported(value, iae);
        }

    }

    @Override
    public <V> Iterator<VertexProperty<V>> properties(String... propertyKeys) {
         Iterator<? extends Property> it = super.properties(propertyKeys)

        return TruenoIteratorUtils.asStream(it)
                .map(p -> (VertexProperty<V>)new TruenoVertexProperty<V>(
                        (TruenoVertex)p.element(), p.key(), (V)p.value() )
                )
                .iterator();
    }

    /* ---------------------------------------------------------------------------
     * Iterators
     * ---------------------------------------------------------------------------
     */

}
