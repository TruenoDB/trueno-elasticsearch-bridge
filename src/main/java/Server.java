
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.framing.FrameBuilder;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by victor on 2/24/17.
 */
public class Server extends WebSocketServer {

    /* ElasticSearch Client */
    private final ElasticClient client;

    static final String PATH_HOME = "~/Code/purdue.edu/699-research/truenodb/trueno/lib/core/binaries/elasticsearch/bin";
    static final String PATH_CONFIG = "~/Code/purdue.edu/699-research/truenodb/trueno/lib/core/binaries/elasticsearch/config";

    static final String ACTION_SEARCH = "search";
    static final String ACTION_BULK   = "bulk";

    /* Stats */
    long totalTime1 = 0;
    long totalTime2 = 0;
    long totalReq = 0;

    /**
     * Construct a {@link Server} instance.
     * @param hostname
     * @param port
     * @param draft
     * @throws UnknownHostException
     */
    public Server(String hostname, int port, Draft draft) throws UnknownHostException {
        super(new InetSocketAddress(port));

        System.out.println( "Starting Server on " + port );

        /* Instantiate the ElasticSearch client and connect to Server */
        this.client = new ElasticClient("trueno", hostname, PATH_HOME, PATH_CONFIG);
        this.client.connect();
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        System.out.println( "Connection Opened" );
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        System.out.println( "closed" );
    }

    @Override
    public void onMessage(WebSocket conn, String s) {
//        System.out.println( "Message from client:" + s );

        Message msg = new Gson().fromJson(s, Message.class);

        // FIXME. Change to something more sophisticated and efficient
        // FIXME. When the load is too high, there are chance that the connection collapse.
        // The following error was reported while running two test-readers (50K each) at the same time
        //{"query":"{\"query\":{\"bool\":{\"filter\":{\"term\":{\"prop.filmId\":\"m.02r1vk7\"}}}}}","index":"films","type":"v","size":1000}
        //org.java_websocket.exceptions.WebsocketNotConnectedException
        //Exception in thread "elasticsearch[Bella Donna][listener][T#932]" org.java_websocket.exceptions.WebsocketNotConnectedException
        //at org.java_websocket.WebSocketImpl.send(WebSocketImpl.java:566)
        //at org.java_websocket.WebSocketImpl.send(WebSocketImpl.java:543)
        //at Server$1.onFailure(Server.java:152)
        //at org.elasticsearch.action.support.ThreadedActionListener$1.onFailure(ThreadedActionListener.java:94)
        //at org.elasticsearch.common.util.concurrent.AbstractRunnable.run(AbstractRunnable.java:39)
        //at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1142)
        //at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:617)
        //at java.lang.Thread.run(Thread.java:745)
        //Exception in thread "elasticsearch[Bella Donna][listener][T#931]" org.java_websocket.exceptions.WebsocketNotConnectedException
        //at org.java_websocket.WebSocketImpl.send(WebSocketImpl.java:566)
        //at org.java_websocket.WebSocketImpl.send(WebSocketImpl.java:543)
        //at Server$1.onFailure(Server.java:152)
        //at org.elasticsearch.action.support.ThreadedActionListener$1.onFailure(ThreadedActionListener.java:94)
        //at org.elasticsearch.common.util.concurrent.AbstractRunnable.run(AbstractRunnable.java:39)
        //at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1142)
        //at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:617)
        //at java.lang.Thread.run(Thread.java:745)
        //Exception in thread "elasticsearch[Bella Donna][listener][T#933]" org.java_websocket.exceptions.WebsocketNotConnectedException
        //at org.java_websocket.WebSocketImpl.send(WebSocketImpl.java:566)
        //at org.java_websocket.WebSocketImpl.send(WebSocketImpl.java:543)
        //at Server$1.onFailure(Server.java:152)
        //at org.elasticsearch.action.support.ThreadedActionListener$1.onFailure(ThreadedActionListener.java:94)
        //at org.elasticsearch.common.util.concurrent.AbstractRunnable.run(AbstractRunnable.java:39)
        //at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1142)
        //at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:617)
        //at java.lang.Thread.run(Thread.java:745)

        switch (msg.getAction()) {
            case ACTION_SEARCH: {
                /* Start time */
                long startTime2 = System.nanoTime();

                //System.out.println( "[search] ... Message from client:" + msg.getObject() );
                SearchObject obj = new Gson().fromJson(msg.getObject(), SearchObject.class);

                /* Start time */
                long startTime = System.nanoTime();

                /* Get results */
                //Map<String,Object>[] results = client.search(obj);
                //Map<String,Object>[] results = new Map[0];

                client.search(obj).addListener(new ActionListener<SearchResponse>() {
                    @Override
                    public void onResponse(SearchResponse resp) {

                        /* Collecting results */
                        ArrayList<Map<String,Object>> sources = new ArrayList<>();

                        SearchHit[] results = resp.getHits().getHits();

                        //System.out.println("Hits are " + results.length);

                        /* for each hit result */
                        for(SearchHit h: results){

                            /* add map to array, note: a map is the equivalent of a JSON object */
                            //sources.add(h.getSource());
                            sources.add(ImmutableMap.of("_source", h.getSource()));

                            //System.out.println(h.getSource());
                        }

                        /* End time */
                        long estimatedTime = System.nanoTime() - startTime;

                        Response response = new Response();
                        response.setCallbackIndex(msg.getCallbackIndex());
                        response.setObject(sources.toArray(new Map[sources.size()]));

                /* End time */
                        long estimatedTime2 = System.nanoTime() - startTime2;

                /* Compute stats */
                        totalTime1 += estimatedTime;
                        totalTime2 += estimatedTime2;
                        totalReq++;

                        if (totalReq % 5000 == 0) {
                            System.out.println("Average execution time "
                                    + (totalTime1 * 0.000001) / totalReq + " ms "
                                    + (totalTime2 * 0.000001) / totalReq + " ms ");
                            totalReq = 0; totalTime1 = 0; totalTime2 = 0;
                        }

                        conn.send( new Gson().toJson(response) );

                    }

                    @Override
                    public void onFailure(Throwable throwable) {

                        System.out.println("Failed on search.\n"
                                + msg.getObject()
                                + "\n" + throwable);

                        Response response = new Response();
                        response.setCallbackIndex(msg.getCallbackIndex());
                        response.setObject(new Map[0]);

                        conn.send( new Gson().toJson(response) );

                    }
                });
                break;

            }

            case ACTION_BULK: {
                //System.out.println( "[bulk] ..... Message from client:" + msg.getObject() );
                BulkObject obj = new Gson().fromJson(msg.getObject(), BulkObject.class);

                /* Start time */
                long startTime = System.nanoTime();

                /* get results */
                client.bulk(obj).addListener(new ActionListener< BulkResponse>() {

                    @Override
                    public void onResponse(BulkResponse bulkItemResponses) {

                        /* End time */
                        long estimatedTime = System.nanoTime() - startTime;

                        System.out.println("batch time ms: " + estimatedTime * 0.000001 + " " + bulkItemResponses.getItems().length);

                        if (bulkItemResponses.hasFailures()) {
                            System.out.println("failures --> " + bulkItemResponses.buildFailureMessage());
                        }

                        Response response = new Response();
                        response.setCallbackIndex(msg.getCallbackIndex());
                        response.setObject(new Map[0]);

                        conn.send( new Gson().toJson(response) );
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        System.out.println("Failed on search.\n"
                                + msg.getCallbackIndex()
                                + "\n" + throwable);

                        Response response = new Response();
                        response.setCallbackIndex(msg.getCallbackIndex());
                        response.setObject(new Map[0]);

                        conn.send( new Gson().toJson(response) );
                    }
                });

                break;
            }

        }

//        try {
//            JSONObject msg = new JSONObject(s);
//            System.out.println( "Message from client:" + msg );
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        /* this is a ECHO */
//        conn.send( s );
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        System.out.println( "Error:" );
        e.printStackTrace();
    }


//    Server (String hostname, int port) {
//
//        /* Instantiate the server */
//        this.server = new SocketIOServer(config);
//
//        /* Set event listeners */
//        /* Search */
//        server.addEventListener("search", SearchObject.class, new DataListener<SearchObject>() {
//
//            @Override
//            public void onData(SocketIOClient client, SearchObject data, AckRequest ackRequest) {
//                //System.out.println(data);
//                // System.out.println("request");
//
//                /* get time */
//                long startTime = System.nanoTime();
//
//                /* get results */
//                Map<String,Object>[] results = eClient.search(data);
//
//                /* print time */
//                long estimatedTime = System.nanoTime() - startTime;
//
//                totalTime += estimatedTime;
//                totalReq++;
//
//                if (totalReq % 5000 == 0) {
//                    System.out.println("Average execution time " + (totalTime * 0.000001) / totalReq + " ms ");
//                    totalReq = 0; totalTime = 0;
//                }
//                //System.out.println("Execution time: " + estimatedTime +"ns");
//
//                /* send back result */
//                ackRequest.sendAckData(results);
//            }
//        });
//
//        /* Bulk Operations */
//        server.addEventListener("bulk", BulkObject.class, new DataListener<BulkObject>() {
//            @Override
//            public void onData(SocketIOClient client, BulkObject data, AckRequest ackRequest) {
//
//                /* get results */
//                String result = eClient.bulk(data);
//
//                /* sending Acknowledge to socket client */
//                ackRequest.sendAckData(result);
//            }
//        });
//
//
//        /* Starting Socket Server */
//        server.startAsync().addListener(new FutureListener<Void>() {
//            @Override
//            public void operationComplete(Future<Void> future) throws Exception {
//                if (future.isSuccess()) {
//                    System.out.println("Bridge Server Started");
//                } else {
//                    System.out.println("Bridge Server Failure");
//                }
//            }
//        });
//
//    }


    /* Main */
    public static void main(String args[]) throws UnknownHostException {

        String host;
        int port;

        if (args.length < 2) {
            System.out.println("Usage: Server hostname port");
            System.exit(1);
        }

        host = args[0];
        port = Integer.parseInt(args[1]);

        new Server(host, port, null).start();
    }

}
