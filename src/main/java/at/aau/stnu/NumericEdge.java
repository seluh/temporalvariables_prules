package at.aau.stnu;
import at.aau.abstractGraph.Edge;

/**
 * Edge weights are either 0 (precedence) or some integer (temporal constraint)
 */
public class NumericEdge extends Edge {
    private STNUNode source;
    private STNUNode target;

    public void setNonLabeledValue(int nonLabeledValue) {
        this.nonLabeledValue = nonLabeledValue;
    }
    public void setLabeledValue(int labeledValue) {
        this.labeledValue = labeledValue;
    }

    private int nonLabeledValue = Integer.MAX_VALUE;

    public void setLabel(Label label) {
        this.label = label;
    }

    private Label label;
    private int labeledValue = Integer.MAX_VALUE;


    public void setStatic(boolean aStatic) {
        isStatic = aStatic;
    }

    private boolean isStatic = false;

    public NumericEdge(STNUNode source, STNUNode target, int labeledValue, Label label){
        this.source = source;
        this.target = target;
        this.labeledValue = labeledValue;
        this.label = label;
        isStatic = true;
    }
    public NumericEdge(STNUNode source, STNUNode target, int nonLabeledValue){
        this.source = source;
        this.target = target;
        this.nonLabeledValue = nonLabeledValue;
        this.label = null;
    }
    public NumericEdge(STNUNode source, STNUNode target, int nonLabeledValue, boolean isStatic){
        this.source = source;
        this.target = target;
        this.nonLabeledValue = nonLabeledValue;
        this.label = null;
        this.isStatic = isStatic;
    }

    public boolean isStatic() {
        return isStatic;
    }
    public STNUNode getSource() {
        return source;
    }

    public STNUNode getTarget() {
        return target;
    }



    public Label getLabel() {


        return label;
    }

    public String toString() {

            StringBuilder sb = new StringBuilder();
            sb.append("[" + source.getName() + "-- ");
            if (label != null) {
                sb.append(label.toString() + labeledValue);
                if (nonLabeledValue != Integer.MAX_VALUE) {
                    sb.append("; (" + nonLabeledValue + ")");
                }
            } else if (nonLabeledValue != Integer.MAX_VALUE) {
                sb.append(nonLabeledValue);
            }
            sb.append(" --> " + target.getName() + "]");
            return sb.toString();
    }

    public int getNonLabeledValue() {
        return nonLabeledValue;
    }

    public int getLabeledValue() {
        return labeledValue;
    }


}
