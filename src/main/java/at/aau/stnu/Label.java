package at.aau.stnu;
import at.aau.abstractGraph.LabelType;

/**
 * Contingent edges in STNU's have labels.
 */
public class Label {
    private STNUNode labelSTNUNode;
    private LabelType type;

    public Label(STNUNode n, LabelType t){
        this.labelSTNUNode = n;
        this.type = t;
    }

    public STNUNode getLabelSTNUNode() {
        return labelSTNUNode;
    }

    public LabelType getType() {
        return type;
    }
    public String toString(){
        if(type.equals(LabelType.lC)){
            return labelSTNUNode.getName().toLowerCase()+":";
        } else if(type.equals(LabelType.uC)){
            return labelSTNUNode.getName().toUpperCase()+":";
        } else{
            return "";
        }
    }
}
