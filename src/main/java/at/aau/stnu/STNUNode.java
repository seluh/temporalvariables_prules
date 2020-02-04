package at.aau.stnu;
import java.util.UUID;

/**
 * @author Josef Lubas
 */

public class STNUNode {

    private boolean isContingent;
    private STNUNode activationTimepoint;

    public boolean isParam() {
        return isParam;
    }

    private boolean isParam;
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
        this.isParam = false;
    }
    public STNUNode(String name, boolean isParam){
        this.name = name;
        this.isContingent=false;
        this.activationTimepoint=null;
        this.isParam = isParam;
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
