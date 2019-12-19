package at.aau.algorithms;

import at.aau.abstractGraph.LabelType;
import at.aau.stnu.STNUNode;
import at.aau.stnu.NumericEdge;
import at.aau.stnu.STNU;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

public class RULSolver {
    /**
     * Copy of the initial STNU
     */
    private STNU derivedSTNU;
    /**
     * h must be a sufficient high number;
     * used for making STNU's upperbounded
     */
    private int h;

    private Logger logger;
    private STNUNode z;


    public RULSolver(STNU stnu, boolean logging, int deadline) throws CloneNotSupportedException {
        this.derivedSTNU = null;
        this.derivedSTNU = (STNU) stnu.clone();
        if(deadline!=Integer.MAX_VALUE){
            h = deadline;
        }else{
            h = derivedSTNU.getMostNegative() * (derivedSTNU.getNodeList().size()*-1);
        }
      //makePointedAndUpperbounded();
       // init();
        derivedSTNU.printSTNU();


    }

    public void apply(boolean rulPlus){
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
                      applyRules(e1,e2,rulPlus,status);
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

    private Status applyRules(NumericEdge e1, NumericEdge e2, boolean rulPlus, Status status){
        STNUNode source = e1.getSource();
        STNUNode target = e2.getTarget();
        /**
         * relax
         */
        int retVal = relaxApplicable(e1,e2,rulPlus);
        if(retVal!=Integer.MIN_VALUE){
            dcCheck(source,target,retVal, status);
            if(status.hasNegativeLoop){
                System.out.println("Negative Loop at: "+source.getName());
                return status;
            }
            NumericEdge newEdge = new NumericEdge(source, target,retVal);
            if(updateEdge(newEdge)) {
                status.hasDerived = true;
                System.out.println("RELAX derived: "+ e1.toString() +" + "+e2.toString()+ " = " +newEdge.toString());
            }
        }
        retVal = upperApplicable(e1,e2,rulPlus);
        if(retVal!=Integer.MIN_VALUE){
           dcCheck(source,target,retVal, status);
            if(status.hasNegativeLoop){
                System.out.println("Negative Loop at: "+source.getName());
                return status;
            }
            NumericEdge newEdge = new NumericEdge(source, target,retVal);
            if(updateEdge(newEdge)) {
                status.hasDerived = true;
                System.out.println("UPPER derived: "+ e1.toString() +" + "+e2.toString()+ " = " +newEdge.toString());
            }
        }
        retVal = lowerApplicable(e1,e2);
        if(retVal!=Integer.MIN_VALUE){
            dcCheck(source,target,retVal, status);
            if(status.hasNegativeLoop){
                System.out.println("Negative Loop at: "+source.getName());
                return status;
            }
            NumericEdge newEdge = new NumericEdge(source, target,retVal);
            if(updateEdge(newEdge)) {
                status.hasDerived = true;
                System.out.println("LOWER derived: "+ e1.toString() +" + "+e2.toString()+ " = " +newEdge.toString());
            }
        }
        return status;
    }

    private int relaxApplicable(NumericEdge e1, NumericEdge e2, boolean rulPlus) {

        STNUNode p = e1.getSource();
        STNUNode q = e1.getTarget();
        STNUNode r = e2.getTarget();
        int v = e1.getNonLabeledValue();
        int w = e2.getNonLabeledValue();
        //System.out.println("Checking RELAX "+p.getName() +"-- "+v+" --> "+q.getName()+" -- "+w+" -->"+r.getName());
        if (v != Integer.MAX_VALUE && w != Integer.MAX_VALUE && !p.equals(q) && !q.equals(r)) {
            if (rulPlus) {
                if ((p.isContingent() || p.getName().equals("Z")) && (!q.isContingent() && !q.getName().equals("Z")) && v > 0) {
                    return v+w;
                } else {
                    return Integer.MIN_VALUE;
                }
            }
            return v+w;
        }
        return Integer.MIN_VALUE;
    }

    private int upperApplicable (NumericEdge e1, NumericEdge e2, boolean rulPlus){
        STNUNode p = e1.getSource();
        STNUNode c = e1.getTarget();
        STNUNode ac = e2.getTarget();
        int v = e1.getNonLabeledValue();
        int uc = e2.getLabeledValue();
       // System.out.println("Checking UPPER "+p.getName() +"-- "+v+" --> "+c.getName()+" -- "+uc+" -->"+ac.getName());
        if(v!=Integer.MAX_VALUE && uc!=Integer.MAX_VALUE && e2.getLabel().getType().equals(LabelType.uC) && !p.equals(c)&&!c.equals(ac)){
            if(c.isContingent() && !ac.isContingent() && c.getActivationTimepoint().equals(ac)){
                if(rulPlus){
                    if((p.isContingent() || p.getName().equals("Z")) && v>uc){
                        return v+uc;
                    }
                }
                else{

                    int lc = derivedSTNU.getEdge(ac,c).getLabeledValue()*-1;

                   // System.out.println("max( "+v+" - "+uc+" , "+lc+") = "+Integer.max(v+uc,lc));
                    return Integer.max(v+uc,lc);
                }
            }
        }
        return Integer.MIN_VALUE;
    }

    private int lowerApplicable (NumericEdge e1, NumericEdge e2){
        STNUNode ac = e1.getSource();
        STNUNode c = e1.getTarget();
        STNUNode r = e2.getTarget();
        int lc = e1.getLabeledValue();
        int w = e2.getNonLabeledValue();
        //System.out.println("Checking LOWER "+ac.getName() +"-- "+lc+" --> "+c.getName()+" -- "+w+" -->"+r.getName());
        //System.out.println("   "+r.getName()+" is contingent? "+r.isContingent());
        if(lc!=Integer.MAX_VALUE && w!=Integer.MAX_VALUE && e1.getLabel().getType().equals(LabelType.lC)&&!ac.equals(c)&&!c.equals(r)){
            if(!ac.isContingent() && c.isContingent() && c.getActivationTimepoint().equals(ac)){
                //System.out.println("   "+w+" <= 0 ?");
                    if(!r.isContingent()&& w<=0){
                  //      System.out.println("   true");
                        return lc+w;
                    }
               // System.out.println(false);
                    if(r.isContingent() && !r.equals(c)){
                        int ur = derivedSTNU.getEdge(r,r.getActivationTimepoint()).getLabeledValue();
                     //   System.out.println("   "+r.getName()+"(R) -- "+ur+" --> "+r.getActivationTimepoint().getName());
                        if(w<=ur) {
                            return lc + w;
                        }
                    }
                }
            }
        return Integer.MIN_VALUE;
    }

    private void makePointedAndUpperbounded(){
        /**
         * Adding dummy time-point Z;
         * connecting Z to every node P with h (if P is in Tx)
         */
        z = new STNUNode("Z");
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

        NumericEdge old = derivedSTNU.getEdge(e.getSource(), e.getTarget());

        int newWeight = e.getNonLabeledValue();
        /**
         * Assumption: contingent edges wont get updated
         */
        if (old != null) {
            if (!old.isStatic() ) {
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
    private void init() {
        z = new STNUNode("Z");
        derivedSTNU.addNode(z);
        for (STNUNode source :
                derivedSTNU.getNodeList()) {
            if (!source.equals(z) && !source.isContingent()) {
                NumericEdge e = new NumericEdge(source, z, 0);
                derivedSTNU.addEdge(e);
            }
        }
    }
}
