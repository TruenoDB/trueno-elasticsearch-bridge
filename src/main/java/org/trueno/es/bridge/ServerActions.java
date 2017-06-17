package org.trueno.es.bridge;

import org.elasticsearch.action.Action;
import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.trueno.es.bridge.comm.Response;

/**
 * @author ebarsallo
 */
public interface ServerActions {

    Response create(ActionResponse response);
    Response drop(ActionResponse response);
    Response persist(ActionResponse response);
    Response bulk(ActionResponse response);
    Response search(ActionResponse response);
}
