package org.trueno.es.bridge;


import com.google.common.collect.ImmutableMap;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.search.SearchHit;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Map;


/**
 * @author Victor
 * @author Servio
 * @author ebarsallo
 * Operations available include:
 * <ul>
 *     <li>connect</li>
 *     <li>search</li>
 *     <li>bulk</li>
 * </ul>
 */
public class ElasticClient {

    /* Private properties */
    private TransportClient client;
    private String clusterName;
    private String[] addresses;

    /**
     * Constructor
     * @param clusterName -> String
     * @param addresses -> String
     */
    public ElasticClient(String clusterName, String addresses) {
        /* set cluster name and addresses */
        this.clusterName = clusterName;
        this.addresses = addresses.split(",");
    }

    /**
     * connect to elasticsearch using transport client
     */
    public void connect() {

        try{
            /* prepare cluster settings */
//            Settings settings = Settings.settingsBuilder()
//                    .put("cluster.name", this.clusterName)
//                    .build();
            Settings settings = Settings.builder()
                    .put("cluster.name", this.clusterName)
                    .build();

            /* instantiate transport build */
//            this.client = TransportClient.builder().settings(settings).build();
            this.client = new PreBuiltTransportClient(settings);

            /* set addresses */
            for(String addr: this.addresses){
                this.client.addTransportAddress(
                        new InetSocketTransportAddress(new InetSocketAddress( InetAddress.getByName(addr), 9300))
                );
            }

        }catch (Exception e){
           System.out.println(e);
        }
    }

    /**
     * The search API allows you to execute a search query and get back search hits that match the query.
     * The query can either be provided using a simple query string as a parameter, or using a request body
     * @param data -> org.trueno.es.bridge.SearchObject
     * @return results -> ArrayList
     */
    public Map<String,Object>[] search(SearchObject data) {

        /* collecting results */
        ArrayList<Map<String,Object>> sources = new ArrayList<>();

        try{

            QueryBuilder qb;

//            qb = QueryBuilders.wrapperQuery(data.getQuery());
//            qb = QueryBuilders.termQuery("prop.filmId", data.getKey());
            qb = QueryBuilders.termQuery("filmId", data.getKey());

            /* build query */
            SearchResponse resp =  this.client.prepareSearch(data.getIndex())
                    .setTypes(data.getType())
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setSize(data.getSize())
                    .setQuery(qb).get();

            SearchHit[] results = resp.getHits().getHits();

            /* for each hit result */
            for(SearchHit h: results){

                /* add map to array, note: a map is the equivalent of a JSON object */
                //sources.add(h.getSource());
                sources.add(ImmutableMap.of("_source", h.getSource()));
            }

            return sources.toArray(new Map[sources.size()]);

        }catch (Exception e){
//            System.out.println("==> " + e);
            e.printStackTrace();
        }
        return new Map[0];
    }


    public String create(IndexObject indexData) {
        try {

            System.out.println("Create index: " + indexData.getIndex());

            this.client.admin().indices().prepareCreate(indexData.getIndex())
                    .setSettings(new java.util.HashMap<String, Integer>()
                            .put("number_of_shards", 1))
                    .addMapping("mappings", indexData.getMapping()).get();

            return "OK";
        } catch (Exception e) {
            e.printStackTrace(new PrintStream(System.out));
            return null;
        }
    }


    /**
     * The bulk API allows one to index and delete several documents in a single request.
     * @param bulkData -> org.trueno.es.bridge.BulkObject [Index, Operations[][]]
     * @return [batch finished] -> String
     */
    public String bulk(BulkObject bulkData) {
        try {
            /* we will use this index instance on ES */
            String index = bulkData.getIndex();

            /* requested batch operations from client */
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
//                    System.out.println("==> " + info[1] + " " + info[2] + " " + info[3]);
                    /* adding document to the batch */
                    bulkRequest.add(this.client.prepareIndex(index, info[1], info[2]).setSource(info[3]));

                    continue;
                }//if

                if (!info[0].equals("delete")) continue;

                /* adding document to the batch */
                bulkRequest.add(this.client.prepareDelete(index, info[1], info[2]));

            }//for

            BulkResponse bulkResponse = bulkRequest.get();

            long totalEstimatedTime = System.currentTimeMillis() - totalStartTime;

            System.out.println("[" + index + "] batch time ms: " + totalEstimatedTime
                    + " " + bulkResponse.getIngestTookInMillis()
                    + " " + bulkResponse.buildFailureMessage());

            if (bulkResponse.hasFailures()) {
                System.out.println("Fail: " + bulkResponse.buildFailureMessage());
                return bulkResponse.buildFailureMessage();
            }

            return "[]";
        }
        catch (Exception e) {
            e.printStackTrace(new PrintStream(System.out));
            return null;
        }
    }//bulk

}//class

