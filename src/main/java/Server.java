
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;

import java.util.Map;

/**
 * Created by victor on 2/24/17.
 */
public class Server {

    public static void main(String args[]) throws InterruptedException{

        /* instantiate the configuration */
        Configuration config = new Configuration();

        /* set the listening hostname */
        config.setHostname(args[0]);

        /* set the listening port */
        config.setPort(Integer.parseInt(args[1]));

        /* instantiate the elasticsearch client */
        final ElasticClient eClient = new ElasticClient("trueno", args[0]);

        /* connect to elasticSearch server */
        eClient.connect();

        /* instantiate the server */
        final SocketIOServer server = new SocketIOServer(config);

        /* set search event listener */
        server.addEventListener("search", SearchObject.class, new DataListener<SearchObject>() {
            @Override
            public void onData(SocketIOClient client, SearchObject data, AckRequest ackRequest) {
                //System.out.println(data);

                /* get time */
                //long startTime = System.currentTimeMillis();

                /* get results */
                Map<String,Object>[] results = eClient.search(data);

                /* print time */
                //long estimatedTime = System.currentTimeMillis() - startTime;

                //System.out.println("Execution time: " + estimatedTime +"ms");

                /* send back result */
                ackRequest.sendAckData(results);
            }
        });//search event listener

        /* set bulk event listener */
        server.addEventListener("bulk", BulkObject.class, new DataListener<BulkObject>() {
            @Override
            public void onData(SocketIOClient client, BulkObject data, AckRequest ackRequest) {

                /* get results */
                String result = eClient.bulk(data);

                /* sending Acknowledge to socket client */
                ackRequest.sendAckData(result);
            }
        });


        /* starting socket server */
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

    }//main

}//Class
