package org.trueno.core;

import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Property;
import org.apache.tinkerpop.gremlin.structure.util.ElementHelper;

import java.util.NoSuchElementException;

/**
 * @author Edgardo Barsallo Yi (ebarsallo)
 */
public class TruenoProperty<V> implements Property<V> {

    protected final Element element;
    protected final String key;
    protected V value;

    public TruenoProperty(final Element element, final String key, final V value) {
        this.element = element;
        this.key = key;
        this.value = value;
    }

    @Override
    public String key() {
        return key;
    }

    @Override
    public V value() throws NoSuchElementException {
        return this.value;
    }

    @Override
    public boolean isPresent() {
        return null != this.value;
    }

    @Override
    public Element element() {
        return this.element;
    }

    @Override
    public void remove() {
        // TODO: implement remove()
//        Component entity = ((TruenoElement)element).getBaseElement();
//        entity.removeProperty(this.key);
//        this.value = null;
    }

    @Override
    public boolean equals(final Object object) {
        return ElementHelper.areEqual(this, object);
    }

    @Override
    public int hashCode() {
        return ElementHelper.hashCode(this);
    }
}
