package org.trueno.graphdb.tinkerpop.structure;

import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Utiliy class to deal with iterators.
 *
 * @author Edgardo Barsallo Yi (ebarsallo)
 */
public class TruenoIteratorUtils {


    /**
     * Converts an interator to a sequential {@code Stream}
     *
     * @param sourceIterator the iterator
     * @param <T>            the type of stream elements
     * @return the new sequential {@code stream}
     */
    public static <T> Stream<T> asStream(Iterator<T> sourceIterator) {
        return asStream(sourceIterator, false);
    }

    /**
     * Converts an interator to a sequential or parallel {@code Stream}
     *
     * @param sourceIterator the iterator
     * @param parallel        if true then the returned stream is a parallel stream, otherwise, the returned stream
     *                        is sequential
     * @param <T>             the type of stream elements
     * @return the new sequential or parallel {@code Stream}
     */
    public static <T> Stream<T> asStream(Iterator<T> sourceIterator, boolean parallel) {
        Iterable<T> iterable = () -> sourceIterator;
        return StreamSupport.stream(iterable.spliterator(), parallel);
    }

}
