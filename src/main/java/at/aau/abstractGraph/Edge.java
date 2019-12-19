package at.aau.abstractGraph;

import java.util.UUID;

/**
 * This class is the abstract representation of nodes in a graph. The specification depends on the graph type (STNU or CSTNU)
 * @author Josef Lubas
 */
public abstract class Edge {
    /**
     * generates an unique id
     */
    private String id = UUID.randomUUID().toString();
    public String getId(){
        return id;
    }
}
