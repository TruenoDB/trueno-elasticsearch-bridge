package org.trueno.core;

import org.apache.tinkerpop.gremlin.structure.Graph;

/**
 * @author Edgardo Barsallo Yi (ebarsallo)
 */
@Graph.OptIn(Graph.OptIn.SUITE_STRUCTURE_STANDARD)
@Graph.OptIn(Graph.OptIn.SUITE_STRUCTURE_PERFORMANCE)
@Graph.OptIn(Graph.OptIn.SUITE_PROCESS_STANDARD)
@Graph.OptIn(Graph.OptIn.SUITE_GROOVY_ENVIRONMENT)
public interface TruenoGraph extends Graph {


    // TODO: Implement this useful function: isOpen, isClosed, something for management?

    // TODO: Change the Exception for a customized TruenoException
    public void close() throws Exception;
}
