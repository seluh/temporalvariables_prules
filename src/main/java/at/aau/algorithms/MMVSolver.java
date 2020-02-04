package at.aau.algorithms;

import at.aau.abstractGraph.LabelType;
import at.aau.stnu.Label;
import at.aau.stnu.NumericEdge;
import at.aau.stnu.STNU;
import at.aau.stnu.STNUNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * MMVSolver implements the DC-checking algorithm based on the repeated application of the constraint propagation rules of the MMV system by Morris and Muscettola.
 * <br><img src="Step-by-Step introduction-files/mmv.png" alt="Application rules for MMV">
 * <br>For now logging is not implemented.
 * @author Marco Franceschetti
 * @author Department of Informatics-Systems, Alpen-Adria-Universitaet Klagenfurt
 * @author marco.franceschetti@aau.at
 * @version 1.0
 *
 */
public class MMVSolver {
    private STNU derivedSTNU;
    private STNUNode z;
    private int h;


    /**
     * The default constructor.
     * @param stnu      the instance of the STNU

     */
    public MMVSolver(STNU stnu, boolean logging, int deadline) throws CloneNotSupportedException {

        this.derivedSTNU = null;
        this.derivedSTNU = (STNU) stnu.clone();
        if(deadline!=Integer.MAX_VALUE){
            h = deadline;
        }else{
            h = derivedSTNU.getMostNegative() * (derivedSTNU.getNodeList().size()*-1);
        }
        z = new STNUNode("Z",false);
        derivedSTNU.addNode(z);
        for (STNUNode target:
                derivedSTNU.getNodeList()) {
            if(!target.equals(z)&&!target.isContingent()) {

                NumericEdge e = new NumericEdge(z, target, h);
                derivedSTNU.addEdge(e);
            }

        }
        /**
         * connecting every node P to Z with -uC-1 (if P is in Tc);
         * connecting every node P to Z with -1 (if P is in Tx);
         */
        for(STNUNode source:
                derivedSTNU.getNodeList()){
            if(source.isContingent()){
                int uc = derivedSTNU.getEdge(source,source.getActivationTimepoint()).getLabeledValue()+1;
                derivedSTNU.addEdge(new NumericEdge(source,z,uc));
            } else if(!source.equals(z)){
                derivedSTNU.addEdge(new NumericEdge(source,z,-1));
            }
        }
    }

    /**
     * Applies the MM DC-check algorithm by looping through the triangles until network quiescence. It does not (yet) check for dynamic controllability.
     */
    public void apply() {
        ArrayList<STNUNode> nodeList = derivedSTNU.getNodeList();
       Status status;
        int runs = 0;
        outerloop:
        do {
            runs++;
            status = new Status();
            for (STNUNode source :
                    nodeList) {
                ArrayList<NumericEdge> outgoing = getSuccessor(source);
                for (NumericEdge e1 :
                        outgoing) {
                    STNUNode interim = e1.getTarget();
                    ArrayList<NumericEdge> incomming = getSuccessor(interim);
                    for (NumericEdge e2 :
                            incomming) {
                        applyRules(e1,e2,status);
                        // System.out.println("hasDerived"+status.hasDerived);
                        if(status.hasNegativeLoop){
                            System.out.println("-------------------------negativeLoop---------------------------");
                            break outerloop;
                        }
                    }
                }
            }
            nodeList = derivedSTNU.getNodeList();
            System.out.println("---------------------------------------------------------------------run:"+runs);
        } while(status.hasDerived);
    }
    private Status applyRules(NumericEdge e1, NumericEdge e2, Status status) {
        STNUNode source = e1.getSource();
        STNUNode target = e2.getTarget();
        /**
         * relax
         */
        int retVal = noCaseApplicable(e1, e2);
        if (retVal != Integer.MIN_VALUE) {
            dcCheck(source, target, retVal, status);
            if (status.hasNegativeLoop) {
                System.out.println("Negative Loop at: " + source.getName());
                return status;
            }
            NumericEdge newEdge = new NumericEdge(source, target, retVal);
            if (updateEdge(newEdge)) {
                status.hasDerived = true;
                System.out.println("NoCase derived: " + e1.toString() + " + " + e2.toString() + " = " + newEdge.toString());
            }
        }
        retVal = upperCaseApplicable(e1, e2);
        if (retVal != Integer.MIN_VALUE) {
            NumericEdge newEdge = new NumericEdge(source, target, retVal, new Label(e2.getLabel().getLabelSTNUNode(), LabelType.uC));
            int retVal2 = labelRemovalApplicable(newEdge);
            if (retVal2 != Integer.MIN_VALUE) {
                newEdge = new NumericEdge(source, target, retVal);
            }
            dcCheck(source, target, retVal, status);
            if (status.hasNegativeLoop) {
                System.out.println("Negative Loop at: " + source.getName());
                return status;
            }

            if (updateEdge(newEdge)) {
                status.hasDerived = true;
                System.out.println("UpperCase derived: " + e1.toString() + " + " + e2.toString() + " = " + newEdge.toString());
            }


        }
        retVal = lowerCaseApplicable(e1,e2);
        if(retVal!=Integer.MIN_VALUE){
            dcCheck(source,target,retVal, status);
            if(status.hasNegativeLoop){
                System.out.println("Negative Loop at: "+source.getName());
                return status;
            }
            NumericEdge newEdge = new NumericEdge(source, target,retVal);
            if(updateEdge(newEdge)) {
                status.hasDerived = true;
                System.out.println("LowerCase derived: "+ e1.toString() +" + "+e2.toString()+ " = " +newEdge.toString());
            }
        }
        retVal = crossCaseApplicable(e1,e2);
        if(retVal!=Integer.MIN_VALUE){
            dcCheck(source,target,retVal, status);
            if(status.hasNegativeLoop){
                System.out.println("Negative Loop at: "+source.getName());
                return status;
            }
            NumericEdge newEdge = new NumericEdge(source, target,retVal);
            if(updateEdge(newEdge)) {
                status.hasDerived = true;
                System.out.println("CrossCase derived: "+ e1.toString() +" + "+e2.toString()+ " = " +newEdge.toString());
            }
        }
        return status;
    }




    private int noCaseApplicable(NumericEdge e1, NumericEdge e2) {
        STNUNode p = e1.getSource();
        STNUNode q = e1.getTarget();
        STNUNode r = e2.getTarget();
        int v = e1.getNonLabeledValue();
        int w = e2.getNonLabeledValue();
        //System.out.println("Checking RELAX "+p.getName() +"-- "+v+" --> "+q.getName()+" -- "+w+" -->"+r.getName());
        if (v != Integer.MAX_VALUE && w != Integer.MAX_VALUE && !p.equals(q) && !q.equals(r)) {
            return v+w;
        }
        return Integer.MIN_VALUE;
    }


    private int upperCaseApplicable(NumericEdge e1, NumericEdge e2) {
        STNUNode p = e1.getSource();
        STNUNode c = e1.getTarget();
        STNUNode ac = e2.getTarget();
        int v = e1.getNonLabeledValue();
        int uc = e2.getLabeledValue();

        // System.out.println("Checking UPPER "+p.getName() +"-- "+v+" --> "+c.getName()+" -- "+uc+" -->"+ac.getName());
        if(v!=Integer.MAX_VALUE && uc!=Integer.MAX_VALUE && e2.getLabel()!=null &&e2.getLabel().getType().equals(LabelType.uC)) {
            if (e2.getLabel().getType().equals(LabelType.uC) && !c.equals(ac) && !p.equals(ac)) {
                STNUNode contingent = e2.getLabel().getLabelSTNUNode();
                if (c.isContingent() && !ac.isContingent() && c.getActivationTimepoint().equals(ac) && !p.equals(contingent)) {

                    return v + uc;
                }

            }
        }
        return Integer.MIN_VALUE;
    }

    private int lowerCaseApplicable(NumericEdge e1, NumericEdge e2) {

        STNUNode ac = e1.getSource();
        STNUNode c = e1.getTarget();
        STNUNode r = e2.getTarget();
        int lc = e1.getLabeledValue();
        int w = e2.getNonLabeledValue();
        //System.out.println("Checking LOWER "+ac.getName() +"-- "+lc+" --> "+c.getName()+" -- "+w+" -->"+r.getName());
        //System.out.println("   "+r.getName()+" is contingent? "+r.isContingent());
        if (lc != Integer.MAX_VALUE && w != Integer.MAX_VALUE &&e1.getLabel()!=null&& e1.getLabel().getType().equals(LabelType.lC) && !ac.equals(c) && !c.equals(r) && !ac.equals(r)) {
            if (!ac.isContingent() && c.isContingent() && c.getActivationTimepoint().equals(ac) && w <= 0) {
                //System.out.println("   "+w+" <= 0 ?");

                return lc + w;
            }
        }
            return Integer.MIN_VALUE;

    }

    private int crossCaseApplicable(NumericEdge e1, NumericEdge e2) {

        STNUNode ac = e1.getSource();
        STNUNode c = e1.getTarget();
        STNUNode ad = e2.getTarget();
        int lc = e1.getLabeledValue();
        int w = e2.getLabeledValue();
        if (lc != Integer.MAX_VALUE && w!=Integer.MAX_VALUE &&c.getActivationTimepoint()!=null&& c.getActivationTimepoint().equals(ac) && c.isContingent()&&e2.getLabel()!=null&&e1.getLabel()!=null && e2.getLabel().getLabelSTNUNode().getActivationTimepoint().equals(ad) && e2.getLabel().getType().equals(LabelType.uC)&& e1.getLabel().getType().equals(LabelType.lC)) {
            if (w<=0 && !c.equals(e2.getLabel().getLabelSTNUNode())) {
                return lc+w;
            }
        }

        return Integer.MIN_VALUE;
    }


    private int labelRemovalApplicable(NumericEdge e1) {
       if(e1.getLabel().getType().equals(LabelType.uC)){
        STNUNode p = e1.getSource();
        STNUNode ac = e1.getTarget();
        int zC = e1.getLabeledValue();
        STNUNode c = e1.getLabel().getLabelSTNUNode();
         NumericEdge e2 = derivedSTNU.getEdge(ac,c);
         if(e2.getLabel().getType().equals(LabelType.lC)){
             int lC = e2.getLabeledValue()*-1;

             if (e1.getLabel().getLabelSTNUNode().getActivationTimepoint().equals(ac) &&zC>=lC) {
                 return zC;
             }
         }
        }

        return Integer.MIN_VALUE;
    }


    /**
     * Applies the No Case rule of MMV by introducing an unlabeled edge from a source node to a target node.
     * @param sourceNode The source node.
     * @param targetNode The target node.
     * @param weight The edge weight.
     */

    /**
     * Generates all the combinations of 3 distinct nodes in a given network of nodes.
     * @return An array of arrays, each representing a triangle (i.e. a combination of 3 different nodes).
     */
    private ArrayList<NumericEdge> getSuccessor(STNUNode source){
        HashMap<STNUNode, HashMap<STNUNode, NumericEdge>> network = derivedSTNU.getNetwork();
        HashMap<STNUNode, NumericEdge> successors = network.get(source);
        ArrayList<NumericEdge>out = new ArrayList<>();
        for (STNUNode interim:
                successors.keySet()) {
            NumericEdge e = successors.get(interim);
            out.add(e);
        }
        return out;
    }
    public class Status{
        private boolean hasNegativeLoop;
        private boolean hasDerived;

        public Status(){
            hasNegativeLoop = false;
            hasDerived = false;
        }
        public void setHasDerived(boolean b){
            hasDerived= b;
        }
        public void setHasNegativeLoop(){
            hasNegativeLoop = true;
        }
    }
    private boolean updateEdge(NumericEdge e) {
        //  System.out.println("   New Edge: "+e.toString());
        if (e.getLabeledValue()==Integer.MAX_VALUE) {
            NumericEdge old = derivedSTNU.getEdge(e.getSource(), e.getTarget());

            int newWeight = e.getNonLabeledValue();
            /**
             * Assumption: contingent edges wont get updated
             */
            if (old != null) {
                if (!old.isStatic()) {
                    //   System.out.println("   Updating old edge: "+old.toString());
                    int oldWeight = old.getNonLabeledValue();
                    if (newWeight < oldWeight) {
                        old.setNonLabeledValue(newWeight);
                        //    System.out.println("   updated edge: " + derivedSTNU.getEdge(e.getSource(),e.getTarget()).toString());
                        return true;
                    }
                    //   System.out.println("           Cannot update old weight "+old+" <=" + newWeight);

                }
                return false;
            }
            derivedSTNU.addEdge(e);
            //  System.out.println("   derived edge: " + e.toString());
            return true;
        }
        else{
            NumericEdge old = derivedSTNU.getEdge(e.getSource(), e.getTarget());

            int newWeight = e.getLabeledValue();
            /**
             * Assumption: contingent edges wont get updated
             */
            if (old != null) {
                if (!old.isStatic()) {
                    //   System.out.println("   Updating old edge: "+old.toString());
                    int oldWeight = old.getLabeledValue();
                    if (newWeight < oldWeight) {
                        old.setLabeledValue(newWeight);
                        //    System.out.println("   updated edge: " + derivedSTNU.getEdge(e.getSource(),e.getTarget()).toString());
                        return true;
                    }
                    //   System.out.println("           Cannot update old weight "+old+" <=" + newWeight);

                }
                return false;
            }
            e.setLabel(new Label(e.getLabel().getLabelSTNUNode(),LabelType.uC));
            derivedSTNU.addEdge(e);
            //  System.out.println("   derived edge: " + e.toString());
            return true;
        }
    }


    private Status dcCheck(STNUNode source, STNUNode target, int retVal, Status status){
        if(source.equals(target)&&retVal<0){
            status.hasNegativeLoop=true;
            System.out.println(new NumericEdge(source,target,retVal));
            return status;
        }
        return status;
    }

    public STNU getDerivedSTNU() {
        return derivedSTNU;
    }
}

