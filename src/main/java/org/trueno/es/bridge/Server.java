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
 *
 * @author victor
 * @author ebarsallo
 *
 */
public class Server extends WebSocketServer {

    public static final Logger logger = LoggerFactory.getLogger(Server.class);

    /* Elasticsearch Client */
    private final ElasticClient client;

    /* Configuration keys */
    static final String CONFIG_CLUSTER_NAME = "elasticsearch.cluster.name";
    static final String CONFIG_CLUSTER_PORT = "elasticsearch.cluster.port";
    static final String CONFIG_PATH_HOME    = "elasticsearch.path.home";
    static final String CONFIG_PATH_CFG     = "elasticsearch.path.config";

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

        System.out.println("Starting ES server on {} "+ config.getInt("elasticsearch.cluster.port"));

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
        logger.info("onOpen");
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
        logger.info("onClose -> {}", reason);
    }

    /**
     *
     * @param conn
     * @param message
     */
    @Override
    public void onMessage(WebSocket conn, String message) {

        Message msg = new Gson().fromJson(message, Message.class);

        logger.info("onMessage -> {}", message);

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

                            System.out.println("CREATE - {} done."+ obj.getIndex());

                        }

                        @Override
                        public void onFailure(Throwable throwable) {

                            System.out.println("CREATE - error: {}"+ msg.getObject());
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
                        System.out.println("DROP - {} done."+ obj.getIndex());
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        System.out.println("DROP - error: {}"+ msg.getObject());
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

                            //System.out.println("PERSIST - {}."+ obj.getIndex());
                            Response resp = new Response();
                            resp.setCallbackIndex(msg.getCallbackIndex());
                            resp.setObject(new Map[0]);

                            conn.send( new Gson().toJson(resp) );

                        }

                        @Override
                        public void onFailure(Throwable throwable) {

                            System.out.println("PERSIST - error: {}"+ msg.getObject());
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
                            System.out.println("SEARCH - Average execution time: {} ms"+
                                    (totalTime * 0.000001) / totalRequest );
                            totalRequest = 0; totalTime = 0;
                        }

                        conn.send( new Gson().toJson(response) );

                    }

                    @Override
                    public void onFailure(Throwable throwable) {

                        System.out.println("SEARCH - error: {}"+ msg.getObject());
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

                        System.out.println("BULK - batch time: {} {} ms"+
                                estimatedTime * 0.000001 + " "+
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
                        System.out.println("BULK - error: {}"+ msg.getObject());
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

    @Override
    public void onStart() {
        System.out.println("Trueno Bridge server is up");
    }

    /* Main */
    public static void main(String args[]) throws UnknownHostException {

        PropertiesConfiguration configuration;

        /* Check args list, if there's no arg then load params from config file */
        if (args.length > 1) {

            String host = args[0];
            long   port = Integer.parseInt(args[1]);

            configuration = new PropertiesConfiguration();
            configuration.setProperty(CONFIG_CLUSTER_NAME, host);
            configuration.setProperty(CONFIG_CLUSTER_PORT, port);

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
                logger.error("Aborting initialization...");
                logger.error(e.getMessage());
                e.printStackTrace();
                return;
            }
        }

        System.out.println("cluster     [{}]"+ configuration.getString(CONFIG_CLUSTER_NAME));
        System.out.println("port        [{}]"+ configuration.getInt(CONFIG_CLUSTER_PORT));
        System.out.println("path home   [{}]"+ configuration.getString(CONFIG_PATH_HOME));
        System.out.println("path config [{}]"+ configuration.getString(CONFIG_PATH_CFG));

        /* Start the server */
        Server myserver = new Server(configuration, null);
        myserver.start();

    }

}
