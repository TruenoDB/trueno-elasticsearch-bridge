package org.trueno.es.client;

import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.action.ListenableActionFuture;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.query.QueryBuilders;

import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.trueno.es.bridge.action.BulkObject;
import org.trueno.es.bridge.action.DocumentObject;
import org.trueno.es.bridge.action.IndexObject;
import org.trueno.es.bridge.action.SearchObject;
import org.trueno.es.bridge.exception.NoMappingFoundException;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.io.*;

/**
 * @author Victor Santos U.
 * @author maverick-zhn (Servio Palacios)
 * @author Edgardo Barsallo Yi (ebarsallo)
 *
 * Elasticsearch Transport Client.
 * Includes the following operations:
 * <ul>
 *     <li>Search</li>
 *     <li>Bulk insert/delete</li>
 * </ul>
 */
public class ElasticTransportClient {

    public static final Logger logger = LoggerFactory.getLogger(ElasticTransportClient.class);

    /* Transport client */
    private TransportClient client;

    /* Cluster name */
    private String clusterName;
    /* Host address that conforms the cluster */
    private String[] addresses;

    /**
     * Construct a new instance of the {@link ElasticTransportClient}
     *
     * @param name       the name of the Elasticsearch cluster.
     * @param addresses  the
     */
    public ElasticTransportClient(String name, String addresses) {
        /* set cluster name and addresses */
        this.clusterName = name;
        this.addresses   = addresses.split(",");
    }

    /**
     * connect to elasticsearch using transport client
     */
    public void connect() {

        /* Instantiate service node client */
//        Settings.Builder settings = NodeBuilder.nodeBuilder().settings();
//        settings.put("path.home", this.pathHome );
//        settings.put("path.conf", this.pathConf);
//        settings.put("node.name", "localhost");

        /* Build node client */
//        this.node = NodeBuilder.nodeBuilder()
//                .settings(settings)
//                .clusterName(this.clusterName)
//                .node();
//
//        /* Instantiate node client */
//        this.client = node.client();


        try {

            /* Prepare settings */
            Settings settings = Settings.builder()
                    .put("cluster.name", this.clusterName)
                    .build();

            /* Instantiate transport client */
            this.client = new PreBuiltTransportClient(settings);

            for (String addr : this.addresses) {
                this.client.addTransportAddress(
                        new InetSocketTransportAddress(
                                new InetSocketAddress(InetAddress.getByName(addr), 9300))
                );
            }
            
            System.out.println("Transport client setup complete");

        } catch (UnknownHostException ex) {
            logger.error("{}", ex);
            System.out.println("Error while connecting transport client: " + ex);
        }


        /* Wait until everything is OK. */
//        client.admin()
//            .cluster()
//            .prepareHealth()
//            .setWaitForGreenStatus()
//            .execute().actionGet();

        logger.info("Trueno Bridge server is up");
    }

    /**
     *
     * @param fileName
     * @return
     */
    private String getResourceContent(String fileName) throws NoMappingFoundException {
        
        String content = "";

        try{

            InputStream in = getClass().getClassLoader().getResourceAsStream(fileName);
            System.out.println("Input stream for resource "+ fileName + " is "+ in);

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            content = new String();
            for (String line; (line = reader.readLine()) != null; content += line);

        } catch (IOException ex) {
            System.out.println("Error reading resource content: " + ex.toString());
            ex.printStackTrace();

            new NoMappingFoundException(fileName, "Mapping not found", ex);
        }

        return content;
    }

    /**
     *
     * @param data
     * @return
     * @throws Exception
     */
    public ListenableActionFuture<CreateIndexResponse> create(IndexObject data) throws NoMappingFoundException
    {

//        try {

            /* mappings */
             String mappingG = getResourceContent("templates/mappings-graph.json");
            // String mappingG = new String(Files.readAllBytes(
            //         Paths.get(getClass()
            //                 .getClassLoader()
            //                 .getResource("templates/mappings-graph.json").toURI()))
            // );
             String mappingV = getResourceContent("templates/mappings-vertices.json");
            // String mappingV = new String(Files.readAllBytes(
            //         Paths.get(getClass()
            //                 .getClassLoader()
            //                 .getResource("templates/mappings-vertices.json").toURI()))
            // );
             String mappingE = getResourceContent("templates/mappings-edges.json");
            // String mappingE = new String(Files.readAllBytes(
            //         Paths.get(getClass()
            //                 .getClassLoader()
            //                 .getResource("templates/mappings-edges.json").toURI()))
            // );


            return this.client.admin().indices().prepareCreate(data.getIndex())
                    .setSettings(Settings.builder()
                            .put("index.number_of_shards", data.getShards())
                            .put("index.number_of_replicas", data.getReplicas())
                            .put("index.requests.cache.enable", true))
                    .addMapping("e", mappingE, XContentType.JSON)
                    .addMapping("v", mappingV, XContentType.JSON)
                    .addMapping("g", mappingG, XContentType.JSON)
                    .execute();
//        } catch (Exception ex) {
//            // FIXME. Use a custom Exception class
//            logger.error("{}", ex);
//            throw ex;
//        }

    }

    /**
     *
     * @param data
     * @return
     */
    public ListenableActionFuture<DeleteIndexResponse> drop(IndexObject data) {

        return this.client.admin().indices().prepareDelete(data.getIndex())
                .execute();
    }

    /**
     *
     * @param data
     * @return
     */
    public ListenableActionFuture<IndexResponse> persist(DocumentObject data) {

        return this.client.prepareIndex(data.getIndex(), data.getType(), data.getId())
                .setSource(data.getSource())
                .execute();
    }

    /**
     * The search API allows you to execute a search query and get back search hits that match the query.
     * The query can either be provided using a simple query string as a parameter, or using a request body
     * @param data -> org.trueno.es.bridge.action.SearchObject
     * @return results -> ArrayList
     */
    public ListenableActionFuture<SearchResponse> search(SearchObject data) {

        SearchRequestBuilder builder = this.client
            .prepareSearch(data.getIndex())
            .setTypes(data.getType())
            .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
            .setSize(data.getSize())
            .setQuery(QueryBuilders.wrapperQuery(data.getQuery()));

            return builder.execute();

    }

    /**
     * The bulk API allows one to index and delete several documents in a single request.
     * @param bulkData -> org.trueno.es.bridge.action.BulkObject [Index, Operations[][]]
     * @return [batch finished] -> String
     */
    public ListenableActionFuture<BulkResponse> bulk(BulkObject bulkData) {

            /* we will use this index instance on ES */
            String index = bulkData.getIndex();

            String[][] operations = bulkData.getOperations();
            BulkRequestBuilder bulkRequest = this.client.prepareBulk();

            for (String[] info : operations) {

                if (info[0].equals("index")) {
                    /*
                     * Example:
                     * info[0] = index or delete
                     * info[1] = type {v, e}
                     * info[2] = id
                     * info[3] = '{name:pedro,age:15}'
                     */

                    /* adding document to the batch */
                    bulkRequest.add(this.client.prepareIndex(index, info[1], info[2]).setSource(info[3]));
                    continue;
                }

                if (!info[0].equals("delete")) continue;

                /* adding document to the batch */
                bulkRequest.add(this.client.prepareDelete(index, info[1], info[2]));

            }

            return bulkRequest.execute();
    }

}
