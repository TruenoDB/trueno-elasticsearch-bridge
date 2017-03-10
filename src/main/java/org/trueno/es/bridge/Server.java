package org.trueno.es.bridge;


import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;

import java.util.Map;


/**
 * @author victor
 * @since  02/24/2017
 */
public class Server {


    protected Configuration config = new Configuration();

    private final ElasticClient eClient;
    private final SocketIOServer server;


    /**
     * Construct a {@link Server}
     * @param hostname
     * @param port
     */
    Server (String hostname, int port) {

        /* Set the listening hostname */
        this.config.setHostname(hostname);

        /* Set the listening port */
        this.config.setPort(port);

        /* Instantiate the ElasticSearch client and connect to Server */
        this.eClient = new ElasticClient("trueno", hostname);
        this.eClient.connect();

        /* Instantiate the server */
        this.server = new SocketIOServer(config);

        /* Set event listeners */
        /* Search */
        server.addEventListener("search", SearchObject.class, new DataListener<SearchObject>() {
            @Override
            public void onData(SocketIOClient client, SearchObject data, AckRequest ackRequest) {
                //System.out.println(data);

                /* get time */
//                long startTime = System.currentTimeMillis();

                /* get results */
                Map<String,Object>[] results = eClient.search(data);

                /* print time */
//                long estimatedTime = System.currentTimeMillis() - startTime;
//                System.out.println("Execution time: " + estimatedTime +"ms");

                /* send back result */
                ackRequest.sendAckData(results);
            }
        });

        /* Bulk Operations */
        server.addEventListener("bulk", BulkObject.class, new DataListener<BulkObject>() {
            @Override
            public void onData(SocketIOClient client, BulkObject data, AckRequest ackRequest) {

                /* get results */
                String result = eClient.bulk(data);

                /* sending Acknowledge to socket client */
                ackRequest.sendAckData(result);
            }
        });


        /* Starting Socket Server */
        server.startAsync().addListener(new FutureListener<Void>() {
            @Override
            public void operationComplete(Future<Void> future) throws Exception {
                if (future.isSuccess()) {
                    System.out.println("ES bridge-server started on " + port);
                } else {
                    System.out.println("ES bridge-server failure");
                }
            }
        });

    }


    /* Main */
    public static void main(String args[]) throws InterruptedException {

        String host;
        int port;

        if (args.length < 2) {
            System.out.println("Usage: Server hostname port");
            System.exit(1);
        }

        host = args[0];
        port = Integer.parseInt(args[1]);

        new Server(host, port);
    }

}
