package org.trueno.graphdb.internal;

import org.apache.commons.lang.NotImplementedException;
import org.apache.tinkerpop.gremlin.process.computer.GraphComputer;
import org.trueno.core.TruenoGraph;

/**
 * @author Edgardo Barsallo Yi (ebarsallo)
 */
public abstract class AbstractGraph implements TruenoGraph {

    /* Config settings for standalone installation */
    public static final String CONFIG_DEFAULT_DB = "trueno";

    public static final String CONFIG_SERVER   = "trueno.storage.server";
    public static final String CONFIG_PORT     = "trueno.storage.port";
    public static final String CONFIG_DATABASE = "trueno.storage.database";
    public static final String CONFIG_ASYNC    = "trueno.storage.async";
    public static final String CONFIG_CONF     = "trueno.storage.conf";


    @Override
    public <C extends GraphComputer> C compute(Class<C> aClass) throws IllegalArgumentException {
        throw new NotImplementedException();
    }

    @Override
    public GraphComputer compute() throws IllegalArgumentException {
        throw new NotImplementedException();
    }
}
