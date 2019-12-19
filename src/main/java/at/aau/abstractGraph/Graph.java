package at.aau.abstractGraph;

import java.util.UUID;

/**
 * Abstract representation of a graph<N,E>.
 */
public abstract class Graph {
    /**
     * generates an unique id
     */
    private String id = UUID.randomUUID().toString();
    public String getId(){
        return id;
    }
}
