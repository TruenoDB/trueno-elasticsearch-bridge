package org.trueno.es.bridge;

import com.google.gson.Gson;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;

import org.java_websocket.WebSocket;

import com.google.common.collect.ImmutableMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trueno.es.bridge.comm.Message;
import org.trueno.es.bridge.comm.Response;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Victor Santos U.
 * @author Edgardo Barsallo Yi (ebarsallo)
 */
public class Server extends AbstractServer {

    public static final Logger logger = LoggerFactory.getLogger(Server.class);

    /* Configuration keys */
    static final String CONFIG_CLUSTER_NAME     = "elasticsearch.cluster.name";
    static final String CONFIG_CLUSTER_PORT     = "elasticsearch.cluster.port";
    static final String CONFIG_PATH_HOME        = "elasticsearch.path.home";
    static final String CONFIG_PATH_CFG         = "elasticsearch.path.config";
    static final String CONFIG_DEFAULT_SHARDS   = "elasticsearch.default.shards";
    static final String CONFIG_DEFAULT_REPLICAS = "elasticsearch.default.replicas";


    /**
     * Construct an instance of the {@link Server} that will handle trueno requests.
     *
     * @param configuration  the {@link PropertiesConfiguration} instance with all the params needed.
     */
    public Server(PropertiesConfiguration configuration)  {
        super(configuration);
    }

    /* ---------------------------------------------------------------------------
     * Interface (w/client)
     * ---------------------------------------------------------------------------
     */

    @Override
    protected void doFailure(WebSocket conn, String callback, Exception ex) {
        /* Wrap exception message on result set*/
        ArrayList<Map<String, Object>> err = new ArrayList<>();
        err.add(ImmutableMap.of("Exception", ex.getMessage()));

        if (ex.getCause() != null) {
            err.add(ImmutableMap.of("Exception", ex.getCause().getMessage()));
        }

        Response out = new Response(callback, -1, err, ex);
        logger.error("{}", ex);
        logger.error("{}", ex.getCause());


        /* Send outgoing message to client */
        conn.send( new Gson().toJson(out) );
    }

    @Override
    protected void doOK(WebSocket conn, String callback, Response out) {
        out.setCallbackId(callback);
        logger.debug("");

        /* Send outgoing message to client */
        conn.send( new Gson().toJson(out) );
    }

    /* ---------------------------------------------------------------------------
     * WebSocket Server
     * ---------------------------------------------------------------------------
     */

    @Override
    public void onStart() {

    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        super.onError(conn, ex);

        logger.error("{}", ex);
    }

    /* ---------------------------------------------------------------------------
     * Actions
     * ---------------------------------------------------------------------------
     */

    @Override
    public Response create(ActionResponse response) {

        CreateIndexResponse create = (CreateIndexResponse) response;

        /* Return response message */
        return TypeHelper.getEmptyResponse();
    }

    @Override
    public Response drop(ActionResponse response) {

        DeleteIndexResponse delete = (DeleteIndexResponse) response;

        /* Return response message */
        return TypeHelper.getEmptyResponse();
    }

    @Override
    public Response search(ActionResponse response) {

        SearchResponse search = (SearchResponse) response;

        /* Collecting results */
        ArrayList<Map<String,Object>> sources = new ArrayList<>();
        SearchHit[] results = search.getHits().getHits();

        /* Iterate thru results */
        for(SearchHit h: results){
            sources.add(ImmutableMap.of("_source", h.getSource()));
        }

        /* Return response message */
        return TypeHelper.arrayList2Response(sources);
    }

    @Override
    public Response persist(ActionResponse response) {

        IndexResponse persist = (IndexResponse)response;

        /* Return response message */
        return TypeHelper.getEmptyResponse();
    }

    @Override
    public Response bulk(ActionResponse response) {

        BulkResponse bulk = (BulkResponse) response;

        /* Check for errors */
        if (bulk.hasFailures()) {
            logger.error("{}", bulk.buildFailureMessage());
        }

        /* Return response message */
        return TypeHelper.getEmptyResponse();
    }

    /* ---------------------------------------------------------------------------
     * Main
     * ---------------------------------------------------------------------------
     */
    public static void main(String args[]) throws UnknownHostException {
        PropertiesConfiguration configuration;

        /* Check args list, if there's no arg then load params from config file */
        if (args.length > 0) {

            /**
             * arg 1: hostname
             * arg 2: port
             * arg 3: default shards (index creation)
             * arg 4: default replicas (index creation)
             */

            String host = args[0];
            long port   = Integer.parseInt(args[1]);

            configuration = new PropertiesConfiguration();
            configuration.setProperty(CONFIG_CLUSTER_NAME, host);
            configuration.setProperty(CONFIG_CLUSTER_PORT, port);

            if (args.length > 2) {
                configuration.setProperty(CONFIG_DEFAULT_SHARDS, Integer.parseInt(args[2]));
            }

            if (args.length > 3) {
                configuration.setProperty(CONFIG_DEFAULT_REPLICAS, Integer.parseInt(args[3]));
            }


        } else {

            /* Load configuration */
            FileBasedConfigurationBuilder<PropertiesConfiguration> builder =
                    new FileBasedConfigurationBuilder<PropertiesConfiguration>(PropertiesConfiguration.class)
                            .configure(new Parameters().properties()
                                    .setFileName("trueno.config")
                                    .setListDelimiterHandler(new DefaultListDelimiterHandler(','))
                                    .setThrowExceptionOnMissing(true));

            try {
                configuration = builder.getConfiguration();
            } catch (ConfigurationException e) {
                /* If it's not possible to load the configuration file, then abort */
                System.out.println("Aborting initialization: " + e.getMessage());
                logger.error("Aborting initialization...");
                logger.error(e.getMessage());
                e.printStackTrace();
                return;
            }
        }

        /* Start the server */
        Server myserver = new Server(configuration);
        myserver.start();
    }

}
