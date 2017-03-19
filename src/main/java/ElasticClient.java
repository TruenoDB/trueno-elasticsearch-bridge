
import org.elasticsearch.action.ListenableActionFuture;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author victor
 * @author servio
 * @author ebarsallo
 *
 * Elasticsearch client.
 * Includes the following operations:
 * <ul>
 *     <li>Search</li>
 *     <li>Bulk insert/delete</li>
 * </ul>
 */
public class ElasticClient {

    public static final Logger logger = LoggerFactory.getLogger(ElasticClient.class);

    /* Private properties */
    private Client client;
    private String clusterName;
    private String pathHome;
    private String pathConf;
    private String[] addresses;
    private Node node;

    /**
     * Constructor
     * @param clusterName -> String
     * @param addresses -> String
     */
    public ElasticClient(String clusterName, String addresses, String pathHome, String pathConf) {
        /* set cluster name and addresses */
        this.clusterName = clusterName;
        this.addresses = addresses.split(",");
        this.pathHome = pathHome;
        this.pathConf = pathConf;
    }

    /**
     * connect to elasticsearch using transport client
     */
    public void connect() {

        /* Instantiate service node client */
        Settings.Builder settings = NodeBuilder.nodeBuilder().settings();
        settings.put("path.home", this.pathHome );
        settings.put("path.conf", this.pathConf);
        settings.put("node.name", "localhost");

        /* Build node client */
        this.node = NodeBuilder.nodeBuilder()
                .settings(settings)
                .clusterName(this.clusterName)
                .node();

        /* Instantiate client */
        this.client = node.client();

        /* Wait until everything is OK. */
        client.admin()
            .cluster()
            .prepareHealth()
            .setWaitForGreenStatus()
            .execute().actionGet();

        logger.info("Trueno Bridge server is up");
    }

    /**
     * The search API allows you to execute a search query and get back search hits that match the query.
     * The query can either be provided using a simple query string as a parameter, or using a request body
     * @param data -> SearchObject
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
     * @param bulkData -> BulkObject [Index, Operations[][]]
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
