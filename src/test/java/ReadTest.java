import com.opencsv.CSVReader;
import org.trueno.es.bridge.ElasticClient;
import org.trueno.es.bridge.action.SearchObject;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;


public class ReadTest {

    static String data = "/Users/ebarsallo/Code/purdue.edu/699-research/truenodb/neo4j-benchmark/performance/data/films-50k.csv";
    static final String PATH_HOME = "~/Code/purdue.edu/699-research/truenodb/trueno/lib/core/binaries/elasticsearch/bin";
    static final String PATH_CONFIG = "~/Code/purdue.edu/699-research/truenodb/trueno/lib/core/binaries/elasticsearch/config";

    private static ArrayList<String> keys = new ArrayList<String>();

    private static String hostname = "localhost";

    private static String index = "films";

    /* doTest */
    private static double doTest(ElasticClient client) {

        int count = 0;
        double sum = 0;

        long startTime = System.currentTimeMillis();

        for (String key : keys) {
            SearchObject obj = new SearchObject();
            obj.setIndex(index);
            obj.setType("v");
            obj.setSize(60);
            obj.setQuery("{\"term\":{\"prop.filmId\":\"" + key + "\"}}");

            /* get results */
            //Map<String,Object>[] results = client.search(obj);
            Map<String,Object>[] results = new Map[0];


            for (Map<String, Object> elem : results) {

                Object control = ((Map<String, Object>)((Map<String, Object>)elem.get("_source")).get("prop")).get("control");
                sum += Math.round(Double.parseDouble(control.toString())* 100000000.0) / 100000000.0;
                count++;
            }
        }

        long endTime = System.currentTimeMillis();

        /* report */
        System.out.println("--> " + (endTime - startTime) + " ms\t"
                + count + " objects\t"
                + (count*1.0)/(endTime - startTime)*1000 + " objets/sec\t"
                + sum);

        return  (count*1.0)/(endTime - startTime)*1000;
    }

    /* main */
    public static void main(String[] args) {

        ElasticClient client;

        /* Instantiate the ElasticSearch client and connect to org.trueno.es.bridge.Server */
        client = new ElasticClient("trueno", hostname, PATH_HOME, PATH_CONFIG);
        client.connect();

        /* Reading dataset */
        try {
            CSVReader reader = new CSVReader(new FileReader(data));
            String [] nextLine;
            reader.readNext();  // skip first line
            while ((nextLine = reader.readNext()) != null) {
                keys.add(nextLine[0]);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        double avg=0.0;
        for (int i=0; i<10; i++) {
            avg += doTest(client);
        }
        System.out.println("avg: " + avg/10);

    }
}