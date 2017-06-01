package org.trueno.es.bridge;

import com.google.gson.Gson;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import org.trueno.es.bridge.action.BulkObject;
import org.trueno.es.bridge.action.DocumentObject;
import org.trueno.es.bridge.action.IndexObject;
import org.trueno.es.bridge.action.SearchObject;
import org.trueno.es.bridge.comm.Message;
import org.trueno.es.bridge.comm.Response;
import org.trueno.es.bridge.exception.NoMappingFoundException;
import org.trueno.es.client.ElasticTransportClient;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * @author ebarsallo
 */
public abstract class AbstractServer extends WebSocketServer implements TruenoActions {

    /* Configuration keys */
    static final String CONFIG_CLUSTER_NAME  = "cluster.name";
    static final String CONFIG_NODE_HOST     = "node.host";
    static final String CONFIG_NODE_PORT     = "node.port";
    static final String CONFIG_NODE_ADDR     = "node.listen_address";

    static final String CONFIG_PATH_HOME        = "elasticsearch.path.home";
    static final String CONFIG_PATH_CFG         = "elasticsearch.path.config";
    static final String CONFIG_DEFAULT_SHARDS   = "elasticsearch.default.shards";
    static final String CONFIG_DEFAULT_REPLICAS = "elasticsearch.default.replicas";

    /* Defaults values */
    static final String DEFAULT_NODE_ADDR    = "0.0.0.0";
    static final String DEFAULT_CLUSTER_NAME = "trueno";
    static final long   DEFAULT_ES_SHARDS    = 1;
    static final long   DEFAULT_ES_REPLICAS  = 1;

    /* Allowed action request */
    static final String ACTION_SEARCH        = "SEARCH";
    static final String ACTION_BULK          = "BULK";
    static final String ACTION_PERSIST       = "PERSIST";
    static final String ACTION_CREATE_GRAPH  = "CREATE";
    static final String ACTION_OPEN_GRAPH    = "OPEN";
    static final String ACTION_DROP_GRAPH    = "DROP";


    /* Elasticsearch Client */
    private final ElasticTransportClient client;
    /* Configuration */
    private PropertiesConfiguration config;



    /**
     * Construct an instance of {@link AbstractServer} with the specified listen address and a port. In case that
     * the listen address is not provided, a default value will be used instead ({@code DEFAULT_NODE_ADDR}). Once
     * the {@link WebSocketServer} is instantiated, the Elasticsearch client is initialized.
     *
     * The Elasticsearch client will handle the Trueno action requests.
     *
     * @param config  the {@link PropertiesConfiguration} instance with all the params needed.
     */
    AbstractServer(PropertiesConfiguration config) {

        /* Construct instance */
        super(new InetSocketAddress(
                config.getString(CONFIG_NODE_ADDR, DEFAULT_NODE_ADDR),
                config.getInt(CONFIG_NODE_PORT)
        ));

        String cluster = config.getString(CONFIG_CLUSTER_NAME, DEFAULT_CLUSTER_NAME);
        String host    = config.getString(CONFIG_NODE_ADDR);
        client = new ElasticTransportClient(cluster, host);
        client.connect();
    }

    /* ---------------------------------------------------------------------------
     * Interface (w/client)
     * ---------------------------------------------------------------------------
     */

    protected void doFailure(WebSocket conn, String callback, Exception ex) {
        Response out = new Response(callback, -1, TypeHelper.emptySet(), ex);

        /* Send outgoing message to client */
        conn.send( new Gson().toJson(out) );

    }

    protected void doOK(WebSocket conn, String callback, Response out) {
        out.setCallbackId(callback);

        /* Send outgoing message to client */
        conn.send( new Gson().toJson(out) );

    }


    /* ---------------------------------------------------------------------------
     * ActionListener for each events
     * ---------------------------------------------------------------------------
     */

    /**
     * Set the index shards and replicas to a specified {@link IndexObject} instance with defaults value in case
     * that those values were not set.
     *
     * @param index  the index object
     * @return  the {@link IndexObject} instance with the default values set if needed.
     */
    private IndexObject setIndexDefaults(IndexObject index) {

        /* default index shards */
        if ( index.isShardNotSet() )
            index.setShards(config.getLong(CONFIG_DEFAULT_SHARDS, DEFAULT_ES_SHARDS));

        /* default index replicas */
        if ( index.isReplicasNotSet() )
            index.setReplicas(config.getLong(CONFIG_DEFAULT_REPLICAS, DEFAULT_ES_REPLICAS));

        return index;
    }

    private void doCreate(WebSocket conn, Message in) {

        IndexObject obj = new Gson().fromJson(in.getObject(), IndexObject.class);

        try {

            this.client.create(setIndexDefaults(obj))
                    .addListener(new ActionListener<CreateIndexResponse>() {
                        @Override
                        public void onResponse(CreateIndexResponse response) {
                            Response msg = create(response);
                            doOK(conn, in.getCallbackIdOK(), msg);
                        }

                        @Override
                        public void onFailure(Exception ex) {
                            doFailure(conn, in.getCallbackIdError(), ex);
                        }
                    });

        } catch (NoMappingFoundException ex) {
            doFailure(conn, in.getCallbackIdError(), ex);
        } catch (Exception ex) {
            doFailure(conn, in.getCallbackIdError(), ex);
        }

    }

    private void doDrop(WebSocket conn, Message in) {

        IndexObject obj = new Gson().fromJson(in.getObject(), IndexObject.class);

        try {
            this.client.drop(obj)
                    .addListener(new ActionListener<DeleteIndexResponse>() {

                        @Override
                        public void onResponse(DeleteIndexResponse response) {
                            Response msg = drop(response);
                            doOK(conn, in.getCallbackIdOK(), msg);
                        }

                        @Override
                        public void onFailure(Exception ex) {
                            doFailure(conn, in.getCallbackIdError(), ex);
                        }
                    });
        } catch (Exception ex) {
            doFailure(conn, in.getCallbackIdError(), ex);
        }

    }

    private void doBulk(WebSocket conn, Message in) {

        BulkObject obj = new Gson().fromJson(in.getObject(), BulkObject.class);

        try {
            this.client
                    .bulk(obj).addListener(new ActionListener<BulkResponse>() {

                @Override
                public void onResponse(BulkResponse response) {
                    Response msg = bulk(response);
                    doOK(conn, in.getCallbackIdOK(), msg);
                }

                @Override
                public void onFailure(Exception ex) {
                    doFailure(conn, in.getCallbackIdError(), ex);
                }
            });
        } catch (Exception ex) {
            doFailure(conn, in.getCallbackIdError(), ex);
        }

    }

    private void doPersist(WebSocket conn, Message in) {

        DocumentObject obj = new Gson().fromJson(in.getObject(), DocumentObject.class);

        try {
            this.client
                    .persist(obj).addListener(new ActionListener<IndexResponse>() {

                @Override
                public void onResponse(IndexResponse response) {
                    Response msg = persist(response);
                    doOK(conn, in.getCallbackIdOK(), msg);

                }

                @Override
                public void onFailure(Exception ex) {
                    doFailure(conn, in.getCallbackIdError(), ex);
                }
            });
        } catch (Exception ex) {
            doFailure(conn, in.getCallbackIdError(), ex);
        }

    }

    private void doSearch(WebSocket conn, Message in) {

        SearchObject obj = new Gson().fromJson(in.getObject(), SearchObject.class);

        try {
            this.client.search(obj)
                    .addListener(new ActionListener<SearchResponse>() {
                        @Override
                        public void onResponse(SearchResponse response) {
                            Response msg = search(response);
                            doOK(conn, in.getCallbackIdOK(), msg);
                        }

                        @Override
                        public void onFailure(Exception ex) {
                            doFailure(conn, in.getCallbackIdError(), ex);
                        }
                    });
        } catch (Exception ex) {
            doFailure(conn, in.getCallbackIdError(), ex);
        }

    }


    /* ---------------------------------------------------------------------------
     * WebSocketServer methods
     * ---------------------------------------------------------------------------
     */

    @Override
    public void onMessage(WebSocket conn, String message) {

        Message incoming = new Gson().fromJson(message, Message.class);

        switch (incoming.getAction().toUpperCase()) {

            case ACTION_CREATE_GRAPH: {
                doCreate(conn, incoming);
                break;
            }

            case ACTION_DROP_GRAPH: {
                doDrop(conn, incoming);
                break;
            }

            case ACTION_BULK: {
                doBulk(conn, incoming);
                break;
            }

            case ACTION_PERSIST: {
                doPersist(conn, incoming);
                break;
            }

            case ACTION_SEARCH: {
                doSearch(conn, incoming);
                break;
            }

        }

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {

    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {

    }

    @Override
    public void onError(WebSocket conn, Exception ex) {

    }

}