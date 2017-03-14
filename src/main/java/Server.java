
import com.corundumstudio.socketio.*;
import com.corundumstudio.socketio.listener.DataListener;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;

import java.util.Arrays;
import java.util.Map;

/**
 * Created by victor on 2/24/17.
 */
public class Server {

    /* Server settings */
    private Configuration config = new Configuration();

    /* ElasticSearch Client */
    private final ElasticClient eClient;

    /* Socket.io Server */
    private final SocketIOServer server;

    static final String PATH_HOME = "~/Code/purdue.edu/699-research/truenodb/trueno/lib/core/binaries/elasticsearch/bin";
    static final String PATH_CONFIG = "~/Code/purdue.edu/699-research/truenodb/trueno/lib/core/binaries/elasticsearch/config";

    long totalTime = 0;
    long totalReq = 0;

    /**
     * Construct a {@link Server} instance.
     * @param hostname
     * @param port
     */
    Server (String hostname, int port) {

        /* Set the listening hostname */
        this.config.setHostname(hostname);
        /* Set the listening port */
        this.config.setPort(port);
        /* Settings threads (current_processors_amount * 2) */
        this.config.setWorkerThreads(2);
        this.config.setBossThreads(2);
        /* Set ack mode to manual */
        this.config.setAckMode(AckMode.MANUAL);

        /* Instantiate the ElasticSearch client and connect to Server */
        this.eClient = new ElasticClient("trueno", hostname, PATH_HOME, PATH_CONFIG);
        this.eClient.connect();

        /* Instantiate the server */
        this.server = new SocketIOServer(config);

        /* Set event listeners */
        /* Search */
        server.addEventListener("search", SearchObject.class, new DataListener<SearchObject>() {

            @Override
            public void onData(SocketIOClient client, SearchObject data, AckRequest ackRequest) {
                //System.out.println(data);
                // System.out.println("request");

                /* get time */
                long startTime = System.nanoTime();

                /* get results */
                Map<String,Object>[] results = eClient.search(data);
//                Map<String,Object>[] results = new Map[0];

                /* print time */
                long estimatedTime = System.nanoTime() - startTime;

                totalTime += estimatedTime;
                totalReq++;

                if (totalReq % 5000 == 0) {
                    System.out.println("Average execution time " + (totalTime * 0.000001) / totalReq + " ms ");
                    totalReq = 0; totalTime = 0;
                }
//                System.out.println("Execution time: " + estimatedTime +"ns");

                /* send back result */
                ackRequest.sendAckData(results);
//                client.sendEvent("data", results);
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
                    System.out.println("Bridge Server Started");
                } else {
                    System.out.println("Bridge Server Failure");
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
