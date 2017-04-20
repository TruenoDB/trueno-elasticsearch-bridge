package org.trueno.graphdb.tinkerpop.structure;

import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.apache.tinkerpop.gremlin.structure.util.StringFactory;
import org.trueno.core.TruenoVertex;

/**
 * A class that represent the capabilities supported by Trueno Graph. By default all methods of features in
 * Graph.Features {@link org.apache.tinkerpop.gremlin.structure.Graph.Features} return true. Thus, the implementation
 * only overrides the features not supported by Trueno.
 *
 * @author Edgardo Barsallo Yi (ebarsallo)
 */
public class TruenoFeatures implements Graph.Features {

    protected Graph.Features.GraphFeatures  graphFeatures  = new TruenoGraphFeatures();
    protected Graph.Features.VertexFeatures vertexFeatures = new TruenoVertexFeatures();
    protected Graph.Features.EdgeFeatures   edgesFeatures  = new TruenoEdgeFeatures();

    @Override
    public Graph.Features.GraphFeatures graph() {
        return graphFeatures;
    }

    @Override
    public Graph.Features.VertexFeatures vertex() {
        return vertexFeatures;
    }

    @Override
    public Graph.Features.EdgeFeatures edge() {
        return edgesFeatures;
    }

    @Override
    public String toString() {
        return StringFactory.featureString(this);
    }

    /**
     * Features specific to a operations of a Trueno Graph
     * {@link  org.trueno.core.TruenoGraph}.
     */
    public class TruenoGraphFeatures implements Graph.Features.GraphFeatures {

        private Graph.Features.VariableFeatures variableFeatures = new TruenoVariableFeatures();

        TruenoGraphFeatures() {
        }

        @Override
        public boolean supportsThreadedTransactions() {
            return false;
        }

        @Override
        public boolean supportsComputer() {
            return false;
        }

        @Override
        public boolean supportsConcurrentAccess() {
            return false;
        }

        @Override
        public boolean supportsTransactions() {
            return false;
        }

        @Override
        public Graph.Features.VariableFeatures variables() {
            return variableFeatures;
        }

        @Override
        public boolean supportsPersistence() {
            return true;
        }
    }

    /**
     * Features that are related to Vertex {@link TruenoVertex}
     * operations and supported by Trueno.
     */
    public class TruenoVertexFeatures implements Graph.Features.VertexFeatures {

        private final Graph.Features.VertexPropertyFeatures vertexPropertyFeatures = new TruenoVertexPropertyFeatures();

        TruenoVertexFeatures(){
        }

        @Override
        public Graph.Features.VertexPropertyFeatures properties() {
            return this.vertexPropertyFeatures;
        }

        // FIXME: This should be more dynamic, and not always returning Cardinality.single
        @Override
        public VertexProperty.Cardinality getCardinality(String key) {
            return VertexProperty.Cardinality.single;
        }

        // FIXME: Trueno actually supports meta properties.
        @Override
        public boolean supportsMetaProperties() {
            return false;
        }

        @Override
        public boolean supportsUserSuppliedIds() {
            return false;
        }

        @Override
        public boolean supportsAddVertices() {
            return true;
        }

        @Override
        public boolean supportsRemoveVertices() {
            return false;
        }

        @Override
        public boolean supportsAddProperty() {
            return false;
        }

        @Override
        public boolean supportsRemoveProperty() {
            return false;
        }

        @Override
        public boolean supportsNumericIds() {
            return false;
        }

        @Override
        public boolean supportsStringIds() {
            return false;
        }

        @Override
        public boolean supportsUuidIds() {
            return false;
        }

        @Override
        public boolean supportsCustomIds() {
            return false;
        }

        @Override
        public boolean supportsAnyIds() {
            return false;
        }

        @Override
        public boolean willAllowId(Object id) {
            return false;
        }
    }

    /**
     * Features that are related to Edges operations and supported by Trueno.
     */
    public class TruenoEdgeFeatures implements Graph.Features.EdgeFeatures {

        private final Graph.Features.EdgePropertyFeatures edgePropertyFeatures = new TruenoEdgePropertyFeatures();

        TruenoEdgeFeatures() {
        }

        @Override
        public Graph.Features.EdgePropertyFeatures properties() {
            return this.edgePropertyFeatures;
        }

        @Override
        public boolean supportsAddEdges() {
            return false;
        }

        @Override
        public boolean supportsRemoveEdges() {
            return false;
        }

        @Override
        public boolean supportsAddProperty() {
            return false;
        }

        @Override
        public boolean supportsRemoveProperty() {
            return false;
        }

        @Override
        public boolean supportsUserSuppliedIds() {
            return false;
        }

        @Override
        public boolean supportsNumericIds() {
            return false;
        }

        @Override
        public boolean supportsStringIds() {
            return false;
        }

        @Override
        public boolean supportsUuidIds() {
            return false;
        }

        @Override
        public boolean supportsCustomIds() {
            return false;
        }

        @Override
        public boolean supportsAnyIds() {
            return false;
        }

        @Override
        public boolean willAllowId(Object id) {
            return false;
        }
    }

    /**
     * Features for Graph.Variables and supported by Trueno.
     */
    public class TruenoVariableFeatures implements Graph.Features.VariableFeatures {

        @Override
        public boolean supportsByteValues() {
            return false;
        }

        @Override
        public boolean supportsMapValues() {
            return false;
        }

        @Override
        public boolean supportsMixedListValues() {
            return false;
        }

        @Override
        public boolean supportsBooleanArrayValues() {
            return false;
        }

        @Override
        public boolean supportsByteArrayValues() {
            return false;
        }

        @Override
        public boolean supportsDoubleArrayValues() {
            return false;
        }

        @Override
        public boolean supportsFloatArrayValues() {
            return false;
        }

        @Override
        public boolean supportsIntegerArrayValues() {
            return false;
        }

        @Override
        public boolean supportsStringArrayValues() {
            return false;
        }

        @Override
        public boolean supportsLongArrayValues() {
            return false;
        }

        @Override
        public boolean supportsSerializableValues() {
            return false;
        }

        @Override
        public boolean supportsStringValues() {
            return false;
        }

        @Override
        public boolean supportsUniformListValues() {
            return false;
        }

        @Override
        public boolean supportsVariables() {
            return true;
        }

        @Override
        public boolean supportsBooleanValues() {
            return true;
        }

        @Override
        public boolean supportsDoubleValues() {
            return true;
        }

        @Override
        public boolean supportsFloatValues() {
            return true;
        }

        @Override
        public boolean supportsIntegerValues() {
            return true;
        }

        @Override
        public boolean supportsLongValues() {
            return true;
        }
    }
    /**
     * Features that are related to Vertex Property objects and supported by Trueno.
     */
    public class TruenoVertexPropertyFeatures implements Graph.Features.VertexPropertyFeatures {

        TruenoVertexPropertyFeatures() {
        }

        @Override
        public boolean supportsUserSuppliedIds() {
            return true;
        }

        @Override
        public boolean supportsAnyIds() {
            return false;
        }

        @Override
        public boolean supportsMapValues() {
            return false;
        }

        @Override
        public boolean supportsMixedListValues() {
            return false;
        }

        @Override
        public boolean supportsSerializableValues() {
            return false;
        }

        @Override
        public boolean supportsUniformListValues() {
            return false;
        }

        @Override
        public boolean supportsRemoveProperty() {
            return true;
        }

        @Override
        public boolean supportsNumericIds() {
            return false;
        }

        @Override
        public boolean supportsStringIds() {
            return true;
        }

        @Override
        public boolean supportsUuidIds() {
            return false;
        }

        @Override
        public boolean supportsCustomIds() {
            return false;
        }

        @Override
        public boolean willAllowId(Object id) {
            return false;
        }

        @Override
        public boolean supportsProperties() {
            return true;
        }

        @Override
        public boolean supportsBooleanValues() {
            return true;
        }

        @Override
        public boolean supportsByteValues() {
            return false;
        }

        @Override
        public boolean supportsDoubleValues() {
            return true;
        }

        @Override
        public boolean supportsFloatValues() {
            return true;
        }

        @Override
        public boolean supportsIntegerValues() {
            return true;
        }

        @Override
        public boolean supportsLongValues() {
            return true;
        }

        @Override
        public boolean supportsBooleanArrayValues() {
            return false;
        }

        @Override
        public boolean supportsByteArrayValues() {
            return false;
        }

        @Override
        public boolean supportsDoubleArrayValues() {
            return false;
        }

        @Override
        public boolean supportsFloatArrayValues() {
            return false;
        }

        @Override
        public boolean supportsIntegerArrayValues() {
            return false;
        }

        @Override
        public boolean supportsStringArrayValues() {
            return false;
        }

        @Override
        public boolean supportsLongArrayValues() {
            return false;
        }

        @Override
        public boolean supportsStringValues() {
            return true;
        }
    }

    /**
     * Features that are related to Edge Property objects and supported by Trueno.
     */
    public class TruenoEdgePropertyFeatures implements Graph.Features.EdgePropertyFeatures {

        TruenoEdgePropertyFeatures() {
        }

        @Override
        public boolean supportsMapValues() {
            return false;
        }

        @Override
        public boolean supportsMixedListValues() {
            return false;
        }

        @Override
        public boolean supportsSerializableValues() {
            return false;
        }

        @Override
        public boolean supportsUniformListValues() {
            return false;
        }

        @Override
        public boolean supportsProperties() {
            return true;
        }

        @Override
        public boolean supportsBooleanValues() {
            return true;
        }

        @Override
        public boolean supportsByteValues() {
            return false;
        }

        @Override
        public boolean supportsDoubleValues() {
            return true;
        }

        @Override
        public boolean supportsFloatValues() {
            return true;
        }

        @Override
        public boolean supportsIntegerValues() {
            return true;
        }

        @Override
        public boolean supportsLongValues() {
            return true;
        }

        @Override
        public boolean supportsBooleanArrayValues() {
            return false;
        }

        @Override
        public boolean supportsByteArrayValues() {
            return false;
        }

        @Override
        public boolean supportsDoubleArrayValues() {
            return false;
        }

        @Override
        public boolean supportsFloatArrayValues() {
            return false;
        }

        @Override
        public boolean supportsIntegerArrayValues() {
            return false;
        }

        @Override
        public boolean supportsStringArrayValues() {
            return false;
        }

        @Override
        public boolean supportsLongArrayValues() {
            return false;
        }

        @Override
        public boolean supportsStringValues() {
            return true;
        }
    }

}
