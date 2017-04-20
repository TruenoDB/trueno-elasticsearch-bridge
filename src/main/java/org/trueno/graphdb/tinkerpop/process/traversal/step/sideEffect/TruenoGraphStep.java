package org.trueno.graphdb.tinkerpop.process.traversal.step.sideEffect;

import org.apache.tinkerpop.gremlin.process.traversal.step.HasContainerHolder;
import org.apache.tinkerpop.gremlin.process.traversal.step.map.GraphStep;
import org.apache.tinkerpop.gremlin.process.traversal.step.util.HasContainer;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.util.StringFactory;
import org.apache.tinkerpop.gremlin.util.iterator.IteratorUtils;

import java.util.*;
import java.util.stream.Stream;

/**
 * @author Edgardo Barsallo Yi (ebarsallo)
 *
 */
public final class TruenoGraphStep<S, E extends Element> extends GraphStep<S, E> implements HasContainerHolder {

    private final List<HasContainer> hasContainers = new ArrayList<>();

    public TruenoGraphStep(final GraphStep<S, E> originalGraphStep) {
        super(originalGraphStep.getTraversal(), originalGraphStep.getReturnClass(), originalGraphStep.isStartStep(), originalGraphStep.getIds());
        originalGraphStep.getLabels().forEach(this::addLabel);
        this.setIteratorSupplier(() -> (Iterator<E>) (
                Vertex.class.isAssignableFrom(this.returnClass) ?
                        this.vertices() :
                        this.edges()));
    }

    private Iterator<? extends Edge> edges() {
        return IteratorUtils.filter(
                this.getTraversal().getGraph().get().edges(this.ids),
                edge -> HasContainer.testAll((Edge) edge, this.hasContainers));
    }

//    Neo4j
//    final Neo4jGraph graph = (Neo4jGraph) this.getTraversal().getGraph().get();
//        return graph.getTrait().lookupVertices(graph, this.hasContainers, this.ids);

//    TinkerGraph
//final TinkerGraph graph = (TinkerGraph) this.getTraversal().getGraph().get();
//    final HasContainer indexedContainer = getIndexKey(Vertex.class);
//    // ids are present, filter on them first
//        if (this.ids != null && this.ids.length > 0)
//            return this.iteratorList(graph.vertices(this.ids));
//        else
//                return null == indexedContainer ?
//            this.iteratorList(graph.vertices()) :
//            IteratorUtils.filter(TinkerHelper.queryVertexIndex(graph, indexedContainer.getKey(), indexedContainer.getPredicate().getValue()).iterator(),
//    vertex -> HasContainer.testAll(vertex, this.hasContainers));

//     [TruenoGraphStep(vertex,[name.eq(CG14879)])]
//    gremlin> g.V().has('name','CG15472')
//    gremlin> g.V().has('name','CLV1B')
//    gremlin> t = g.V().has('name','CG14879');

    // TODO: Check this iterator, since Neo4j implementation does not used the Neo4jGraph vertices iterator (most probably this is not the best implementation)
    private Iterator<? extends Vertex> vertices() {
        TruenoGraph graph =  (TruenoGraph)this.getTraversal().getGraph().get();

//        System.out.println("TruenoGraphStep!");
//        for (int i=0; i<this.getIds().length; i++)
//            System.out.println("traversal --> " + i + this.getIds()[i]);

//        for (final HasContainer h : this.hasContainers)
//            System.out.println("traversal hasContainer --> " + h.getKey() + " " + h.getBiPredicate() + " " + h.getValue() );

        try {
            return TruenoHelper.lookupVertices(graph, hasContainers, ids);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return Collections.emptyIterator();
        }

    }

    // TODO: copy from TinkerGraph implementation
    private <E extends Element> Iterator<E> iteratorList(final Iterator<E> iterator) {
        final List<E> list = new ArrayList<>();
        while (iterator.hasNext()) {
            final E e = iterator.next();
            if (HasContainer.testAll(e, this.hasContainers))
                list.add(e);
        }
        return list.iterator();
    }

    @Override
    public String toString() {
        if (this.hasContainers.isEmpty())
            return super.toString();
        else
            return 0 == this.ids.length ?
                    StringFactory.stepString(this, this.returnClass.getSimpleName().toLowerCase(), this.hasContainers) :
                    StringFactory.stepString(this, this.returnClass.getSimpleName().toLowerCase(), Arrays.toString(this.ids), this.hasContainers);
    }

    @Override
    public List<HasContainer> getHasContainers() {
        return Collections.unmodifiableList(this.hasContainers);
    }

    @Override
    public void addHasContainer(HasContainer hasContainer) {
        this.hasContainers.add(hasContainer);
    }
}
