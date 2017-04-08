import com.opencsv.CSVReader;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import org.json.JSONException;
import org.json.JSONObject;
import org.trueno.es.bridge.action.Callback;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


public class ReadSocketClientTest {

    static String data = "/Users/ebarsallo/Code/purdue.edu/699-research/truenodb/neo4j-benchmark/performance/data/films-50k.csv";

    private ArrayList<String> keys = new ArrayList<String>();

    private String hostname;
    private int port;

    private static String index = "films";

    private Socket socket;

    static int count = 0;
    static double sum = 0.0;

    /**
     *
     * @param hostname
     * @param port
     */
    ReadSocketClientTest(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    /**
     *
     * @param connCallback
     * @param discCallback
     * @throws URISyntaxException
     */
    public void connect(final Callback connCallback, final Callback discCallback)  {

        try {
            IO.Options opts = new IO.Options();
            opts.forceNew = true;
            opts.reconnection = true;
            opts.reconnectionDelay = 2000;
            opts.timeout = 1000000;

            this.socket = IO.socket(this.hostname + ":" + this.port, opts);
        } catch (URISyntaxException e) {
            throw new Error ("Invalid host and port specified");
        }

        System.out.println("Trying connection to " + this.hostname + ":" + this.port);

        /* Register callbacks to CONNECT and DISCONNECT events */
        this.socket
                .on(Socket.EVENT_CONNECT, args -> connCallback.method(this.socket))
                .on(Socket.EVENT_DISCONNECT, args -> discCallback.method(this.socket));


        this.socket.connect();
    }

    /**
     *
     * @param key
     */
    public void addKeys(String key) {
        keys.add(key);
    }

    /**
     *
     * @param key
     * @return
     */
    public CompletableFuture<Double> read(String key) {

        CompletableFuture<Double> completableFuture = new CompletableFuture<>();

            try {
                /* Create org.trueno.es.bridge.action.SearchObject instance from a JSON object */
                JSONObject obj = new JSONObject();
                obj.put("index", index);
                obj.put("type", "v");
                obj.put("size", 60);
                obj.put("query", "{\"term\":{\"prop.filmId\":\"" + key + "\"}}");

                this.socket
                        .emit("search", obj, (Ack) results -> {
                            /* get results */
                            for (Object elem:  results) {

                                try {
                                    Object control = ((JSONObject)((JSONObject)((JSONObject)elem)
                                            .get("_source"))
                                            .get("prop"))
                                            .get("control");
                                    count++;
                                    sum += Math.round(Double.parseDouble(control.toString())* 100000000.0) / 100000000.0;
                                    completableFuture.complete((Double)control);
                                    //System.out.println("result --> " + control);
                                } catch (JSONException e) {
                                    completableFuture.complete(0.0);
                                    e.printStackTrace();
                                }
                            }

                        });

            } catch (JSONException e) {
                completableFuture.complete(0.0);
                throw new Error("Error while trying to form the org.trueno.es.bridge.action.SearchObject");
            }

            return completableFuture;
    }

    /**
     *
     * @return
     */
    public double doTest() {

        System.out.println("--> start");
        /* A completablefuture for each async operation of reading to ES (thru the socket.io connection) */
        Collection<CompletableFuture<Double>> futures = new ArrayList<>();
        long startTime = System.currentTimeMillis();

        /* Read objets per each key registered, and keep track as each read operation as future */
        for (String key : keys) {
            futures.add(read(key));
        }


        /**
         *  based on:
         *  http://www.briandupreez.net/2014/04/playing-with-java-8-lambdas-and.html
         *  http://www.programcreek.com/java-api-examples/index.php?class=java.util.concurrent.CompletableFuture&method=allOf
         */

        final CompletableFuture<Void> allFuturesDone =
                CompletableFuture.allOf(futures.toArray(new CompletableFuture<?>[futures.size()]));

        final CompletableFuture<List<Double>> allDone =
            allFuturesDone.thenApply( v -> futures.stream()
                    .map(CompletableFuture<Double>::join)
                    .collect(Collectors.<Double>toList()));

        /* Report results when all the futures are completed */
        allDone.thenApply(values -> {
            long endTime = System.currentTimeMillis();

            /* report */
            System.out.println("--> " + (endTime - startTime) + " ms\t"
                    + count + " objects\t"
                    + (count*1.0)/(endTime - startTime)*1000 + " objets/sec\t"
                    + sum);

            return  (count*1.0)/(endTime - startTime)*1000;
        });

        return 0.0;
    }


    /* main */
    public static void main(String[] args) {

        ReadSocketClientTest client = new ReadSocketClientTest("http://localhost", 8009);
        client.connect(socket -> {
            System.out.println("Connected!");
        }, socket -> {
            System.out.println("Disconnected!");
        });

        /* Reading dataset */
        try {
            CSVReader reader = new CSVReader(new FileReader(data));
            String [] nextLine;
            reader.readNext();  // skip first line
            while ((nextLine = reader.readNext()) != null) {
                client.addKeys(nextLine[0]);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /* Run test */
        try {
            TimeUnit.SECONDS.sleep(3);
            client.doTest();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}