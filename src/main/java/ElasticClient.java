
import com.fasterxml.jackson.databind.ObjectWriter;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.fieldstats.FieldStats;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.mapper.object.ObjectMapper;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.SearchHit;

import java.io.File;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Map;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Created by Victor, Servio
 */
public class ElasticClient {

    /* Private properties */
    private TransportClient client;
    private String clusterName;
    private String[] addresses;

    public ElasticClient(String clusterName, String addresses) {
        /* set cluster name and addresses */
        this.clusterName = clusterName;
        this.addresses = addresses.split(",");
    }

    /* connect to elasticsearch using transport client */
    public void connect() {

        try{

            // System.out.println("clusterName: " + this.clusterName);
            // System.out.println("addresses: " + this.addresses[0]);

            /* prepare cluster settings */
            Settings settings = Settings.settingsBuilder()
                    .put("cluster.name", this.clusterName)
                    .build();
            /* instantiate transport build */
            this.client = TransportClient.builder().settings(settings).build();

            /* set addresses */
            for(String addr: this.addresses){
                this.client.addTransportAddress(new InetSocketTransportAddress(new InetSocketAddress( InetAddress.getByName(addr), 9300)));
            }

        }catch (Exception e){
           System.out.println(e);
        }
    }


    /* connect to elasticsearch using transport client */
    public Map<String,Object>[] search(SearchObject data) {

        /* collecting results */
        ArrayList<Map<String,Object>> sources = new ArrayList<>();

        try{

            /* build query */
            SearchResponse resp =  this.client.prepareSearch(data.getIndex())
                    .setTypes(data.getType())
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setSize(data.getSize())
                    .setQuery(data.getQuery()).get();

            SearchHit[] results = resp.getHits().getHits();

            /* for each hit result */
            for(SearchHit h: results){
            /* add map to array, note: a map is the equivalent of a JSON object */
                sources.add(h.getSource());
            }


            /* returning array of strings */
            return sources.toArray(new Map[sources.size()]);

        }catch (Exception e){
            e.printStackTrace(new PrintStream(System.out));
        }
        return null;
    }

    public String bulk(BulkObject bulkData) {
        try {

            String index = bulkData.getIndex();
            System.out.println("index: " + index);
            String[][] operations = bulkData.getOperations();

            long totalStartTime = System.currentTimeMillis();
            BulkRequestBuilder bulkRequest = this.client.prepareBulk();
            for (String[] info : operations) {

                if (info[0].equals("index")) {
                    /*
                    info[0] = index or delete
                    info[1] = type {v, e}
                    info[2] = id
                    info[3] = '{name:pedro,age:15}'
                     */
                    //bulkRequest.add(this.client.prepareIndex(index, info[1], info[2]).setSource(info[3]));
                    bulkRequest.add(this.client.prepareIndex(index, info[1], info[2])
                            .setSource(jsonBuilder()
                                    .startObject()
                                    .field("id", "id")
                                    //.field("testDate", new FieldStats.Date())
                                    .field("label", "movie1")
                                    .endObject()
                            )
                    );
                    //System.out.println(index + " " + info[0] + " " + info[1] + " " + info[2] + " " + info[3]);

                    continue;
                }//if

                if (!info[0].equals("delete")) continue;

                bulkRequest.add(this.client.prepareDelete(index, info[1], info[2]));

                //System.out.println(index + " " + info[0] + " " + info[1] + " " + info[2]);
            }//for

            BulkResponse bulkResponse = bulkRequest.get();//(BulkResponse)
            long totalEstimatedTime = System.currentTimeMillis() - totalStartTime;
            System.out.println("time ms: " + totalEstimatedTime);

            if (bulkResponse.hasFailures()) {
                return bulkResponse.buildFailureMessage();
            }
            return "";
        }
        catch (Exception e) {
            e.printStackTrace(new PrintStream(System.out));
            return null;
        }
    }//bulk

}//class

