package org.trueno.es.bridge;

import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.trueno.es.bridge.action.DocumentObject;
import org.trueno.es.bridge.action.IndexObject;
import org.trueno.es.bridge.comm.Message;
import org.trueno.es.bridge.comm.Response;
import org.trueno.es.bridge.action.BulkObject;
import org.trueno.es.bridge.action.SearchObject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;

import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created on 2/24/17.
 * @author victor
 *
 */
public class Server extends WebSocketServer {

    public static final Logger logger = LoggerFactory.getLogger(Server.class);

    /* Elasticsearch Client */
    private final ElasticClient client;

    /* Allowed actions/methods */
    static final String ACTION_SEARCH  = "SEARCH";
    static final String ACTION_BULK    = "BULK";
    static final String ACTION_PERSIST = "PERSIST";
    static final String ACTION_CREATE_GRAPH  = "CREATE";
    static final String ACTION_OPEN_GRAPH    = "OPEN";
    static final String ACTION_DROP_GRAPH    = "DROP";

    /* Stats */
    static final long TOTAL_REQUEST_REPORT = 5000;
    long totalTime = 0;
    long totalRequest = 0;

    /**
     * Construct a {@link Server} instance.
     * @param config
     * @param draft
     * @throws UnknownHostException
     */
    public Server(PropertiesConfiguration config, Draft draft) throws UnknownHostException {
        super(new InetSocketAddress(config.getInt("elasticsearch.cluster.port")));

        logger.info("Starting ES server on {} ", config.getInt("elasticsearch.cluster.port"));

        String name = config.getString("elasticsearch.cluster.name");
        String home = config.getString("elasticsearch.path.home");
        String conf = config.getString("elasticsearch.path.config");

        /* Instantiate the ElasticSearch client and connect to org.trueno.es.bridge.Server */
        this.client = new ElasticClient("trueno", name, home, conf);
        this.client.connect();
    }

    /**
     *
     * @param conn
     * @param handshake
     */
    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
//        System.out.println( "Connection Opened" );
    }

    /**
     *
     * @param conn
     * @param code
     * @param reason
     * @param remote
     */
    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
//        System.out.println( "closed" );
    }

    /**
     *
     * @param conn
     * @param message
     */
    @Override
    public void onMessage(WebSocket conn, String message) {

        Message msg = new Gson().fromJson(message, Message.class);

        // TODO. Define a basic structure/contract for handling the message.
        // For instance, define an abstract class/interface. Basic events can be defined as function (which has
        // to been override). The treatment for error is similar for all cases.

//        if (msg.getAction().equalsIgnoreCase(ACTION_SEARCH)) {
//
//        } else if (msg.getAction().equalsIgnoreCase(ACTION_BULK)) {
//
//        } else if (msg.getAction().equalsIgnoreCase(ACTION_CREATE_GRAPH)) {
//
//        } else if (msg.getAction().equalsIgnoreCase(ACTION_DROP_GRAPH)) {
//
//        }

        switch (msg.getAction().toUpperCase()) {

            case ACTION_CREATE_GRAPH: {

                IndexObject obj = new Gson().fromJson(msg.getObject(), IndexObject.class);

                try {
                    client.create(obj).addListener(new ActionListener<CreateIndexResponse>() {
                        @Override
                        public void onResponse(CreateIndexResponse createIndexResponse) {

                            logger.info("CREATE - {} done.", obj.getIndex());

                        }

                        @Override
                        public void onFailure(Throwable throwable) {

                            logger.info("CREATE - error: {}", msg.getObject());
                            logger.error("{}", throwable);

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Response response = new Response();
                response.setCallbackIndex(msg.getCallbackIndex());
                response.setObject(new Map[0]);

                conn.send( new Gson().toJson(response) );

                break;
            }

            case ACTION_DROP_GRAPH: {

                IndexObject obj = new Gson().fromJson(msg.getObject(), IndexObject.class);

                client.drop(obj).addListener(new ActionListener<DeleteIndexResponse>() {
                    @Override
                    public void onResponse(DeleteIndexResponse deleteIndexResponse) {
                        logger.info("DROP - {} done.", obj.getIndex());
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        logger.info("DROP - error: {}", msg.getObject());
                        logger.error("{}", throwable);
                    }
                });

                // FIXME Define a functino to send response
                Response response = new Response();
                response.setCallbackIndex(msg.getCallbackIndex());
                response.setObject(new Map[0]);

                conn.send( new Gson().toJson(response) );
                break;
            }

            case ACTION_PERSIST: {

                DocumentObject obj = new Gson().fromJson(msg.getObject(), DocumentObject.class);

                try {
                    client.persist(obj).addListener(new ActionListener<IndexResponse>() {
                        @Override
                        public void onResponse(IndexResponse response) {

                            //logger.info("PERSIST - {}.", obj.getIndex());
                            Response resp = new Response();
                            resp.setCallbackIndex(msg.getCallbackIndex());
                            resp.setObject(new Map[0]);

                            conn.send( new Gson().toJson(resp) );

                        }

                        @Override
                        public void onFailure(Throwable throwable) {

                            logger.info("PERSIST - error: {}", msg.getObject());
                            logger.error("{}", throwable);

                            Response response = new Response();
                            response.setCallbackIndex(msg.getCallbackIndex());
                            response.setObject(new Map[0]);

                            conn.send( new Gson().toJson(response) );

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            }

            case ACTION_SEARCH: {
                /* Start time */
                long startTime = System.nanoTime();

                SearchObject obj = new Gson().fromJson(msg.getObject(), SearchObject.class);

                /* Get results */
                client.search(obj).addListener(new ActionListener<SearchResponse>() {
                    @Override
                    public void onResponse(SearchResponse resp) {

                        /* Collecting results */
                        ArrayList<Map<String,Object>> sources = new ArrayList<>();

                        SearchHit[] results = resp.getHits().getHits();

                        /* for each hit result */
                        for(SearchHit h: results){
                            /* add map to array, note: a map is the equivalent of a JSON object */
                            sources.add(ImmutableMap.of("_source", h.getSource()));
                        }

                        Response response = new Response();
                        response.setCallbackIndex(msg.getCallbackIndex());
                        response.setObject(sources.toArray(new Map[sources.size()]));

                        /* End time */
                        long estimatedTime = System.nanoTime() - startTime;

                        /* Compute stats */
                        totalTime += estimatedTime;
                        totalRequest++;

                        if (totalRequest % TOTAL_REQUEST_REPORT == 0) {
                            logger.info("SEARCH - Average execution time: {} ms",
                                    (totalTime * 0.000001) / totalRequest );
                            totalRequest = 0; totalTime = 0;
                        }

                        conn.send( new Gson().toJson(response) );

                    }

                    @Override
                    public void onFailure(Throwable throwable) {

                        logger.info("SEARCH - error: {}", msg.getObject());
                        logger.error("{}", throwable);

                        Response response = new Response();
                        response.setCallbackIndex(msg.getCallbackIndex());
                        response.setObject(new Map[0]);

                        conn.send( new Gson().toJson(response) );

                    }
                });
                break;

            }

            case ACTION_BULK: {
                BulkObject obj = new Gson().fromJson(msg.getObject(), BulkObject.class);

                /* Start time */
                long startTime = System.nanoTime();

                /* get results */
                client.bulk(obj).addListener(new ActionListener< BulkResponse>() {

                    @Override
                    public void onResponse(BulkResponse bulkItemResponses) {

                        /* End time */
                        long estimatedTime = System.nanoTime() - startTime;

                        logger.info("BULK - batch time: {} {} ms",
                                estimatedTime * 0.000001,
                                bulkItemResponses.getItems().length);

                        if (bulkItemResponses.hasFailures()) {
                            logger.error("{}", bulkItemResponses.buildFailureMessage());
                        }

                        Response response = new Response();
                        response.setCallbackIndex(msg.getCallbackIndex());
                        response.setObject(new Map[0]);

                        conn.send( new Gson().toJson(response) );
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        logger.info("BULK - error: {}", msg.getObject());
                        logger.error("{}", throwable);

                        Response response = new Response();
                        response.setCallbackIndex(msg.getCallbackIndex());
                        response.setObject(new Map[0]);

                        conn.send( new Gson().toJson(response) );
                    }
                });

                break;
            }

        }
    }

    /**
     *
     * @param webSocket
     * @param exception
     */
    @Override
    public void onError(WebSocket webSocket, Exception exception) {
        logger.error(exception.getMessage());
        System.out.println( "Error:" );
        exception.printStackTrace();
    }


    /* Main */
    public static void main(String args[]) throws UnknownHostException {

        /* Load configuration */
        FileBasedConfigurationBuilder<PropertiesConfiguration> builder =
            new FileBasedConfigurationBuilder<PropertiesConfiguration>(PropertiesConfiguration.class)
            .configure(new Parameters().properties()
            .setFileName("trueno.config")
            .setListDelimiterHandler(new DefaultListDelimiterHandler(','))
            .setThrowExceptionOnMissing(true));

        try {
            PropertiesConfiguration configuration = builder.getConfiguration();

            logger.info("cluster     [{}]", configuration.getString("elasticsearch.cluster.name"));
            logger.info("port        [{}]", configuration.getInt("elasticsearch.cluster.port"));
            logger.info("path home   [{}]", configuration.getString("elasticsearch.path.home"));
            logger.info("path config [{}]", configuration.getString("elasticsearch.path.config"));

            Server myserver = new Server(configuration, null);
            myserver.start();
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }

    }

}
