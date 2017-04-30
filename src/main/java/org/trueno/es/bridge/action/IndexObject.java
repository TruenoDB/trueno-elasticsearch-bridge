package org.trueno.es.bridge.action;

/**
 * Created by ebarsallo on 3/20/17.
 */
public class IndexObject {

    private String index;
    private Long shards;
    private Long replicas;

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

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

    public boolean isShardNotSet() {
        return shards == null;
    }

    public boolean isReplicasNotSet() {
        return replicas == null;
    }
}
