import org.elasticsearch.action.ListenableActionFuture;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.collect.HppcMaps;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.search.SearchHit;
import org.msgpack.MessagePack;
import org.msgpack.template.Template;
import org.msgpack.template.Templates;
import org.msgpack.type.Value;
import org.msgpack.unpacker.Converter;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Victor, edited by Servio on 2017.02.18.
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

        try{

            /* build query */
           SearchResponse resp =  this.client.prepareSearch(data.getIndex())
                    .setTypes(data.getType())
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setSize(data.getSize())
                    .setQuery(data.getQuery()).get();

            SearchHit[] results = resp.getHits().getHits();

            /* collecting results */
            ArrayList<Map<String,Object>> sources = new ArrayList<>();
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

}

