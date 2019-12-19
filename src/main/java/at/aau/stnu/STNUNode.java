package at.aau.stnu;
import java.util.UUID;

/**
 * This class is the abstract representation of nodes in a graph. The specification depends on the graph type (STNU or CSTNU)
 * @author Josef Lubas
 */

public class STNUNode {

    private boolean isContingent;
    private STNUNode activationTimepoint;
    /**
     * generates an unique id
     */
    private String id = UUID.randomUUID().toString();
    /**
     * human readable name
     */
    private String name;

    public STNUNode(String name, STNUNode activation){
        this.name = name;
        this.isContingent=true;
        this.activationTimepoint = activation;
    }
    public STNUNode(String name){
        this.name = name;
        this.isContingent=false;
        this.activationTimepoint=null;
    }

    public String getId(){
        return id;
    }
    public String getName() {
        return name;
    }

    public boolean isContingent() {
        return isContingent;
    }

    public String toString(){
        return name;
    }
    public STNUNode getActivationTimepoint() {
        return activationTimepoint;
    }
}
