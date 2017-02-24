
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;

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
                /* get results */
                Map<String,Object>[] results = eClient.search(data);
                /* send back result */
                ackRequest.sendAckData(results);
            }
        });
        /* set bulk event listener */
        server.addEventListener("bulk", BulkObject.class, new DataListener<BulkObject>() {
            @Override
            public void onData(SocketIOClient client, BulkObject data, AckRequest ackRequest) {


            }
        });

        server.start();
    }
}
