package org.trueno.es.bridge.action;

/**
 * This class defines an input object for an action request which involve index management on Elasticsearch.
 * The object holds all the required information to perform a create or drop action on the Trueno database.
 *
 * @author Edgardo Barsallo Yi (ebarsallo)
 */
public class IndexObject extends AbstractObject {

    private Long shards;
    private Long replicas;

    /* ---------------------------------------------------------------------------
     * Getter and Setter methods
     * ---------------------------------------------------------------------------
     */

    public Long getShards() {
        return shards;
    }

    public void setShards(long shards) {
        this.shards = shards;
    }

    public Long getReplicas() {
        return replicas;
    }

    public void setReplicas(long replicas) {
        this.replicas = replicas;
    }

    /* ---------------------------------------------------------------------------
     * Operational methods
     * ---------------------------------------------------------------------------
     */

    /**
     * Indicates whether the {@code shard} value for the index has been set.
     *
     * @return {@code true} if the shard value has not been set, {@code false} otherwise.
     */
    public boolean isShardNotSet() {
        return shards == null;
    }

    /**
     * Indicates whether the {@code replicas} value for the index has been set.
     *
     * @return {@code true} if the replicas value has not been set, {@code false} otherwise.
     */
    public boolean isReplicasNotSet() {
        return replicas == null;
    }

}
