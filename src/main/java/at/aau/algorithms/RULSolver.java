package at.aau.algorithms;

import at.aau.abstractGraph.LabelType;
import at.aau.stnu.Label;
import at.aau.stnu.STNUNode;
import at.aau.stnu.NumericEdge;
import at.aau.stnu.STNU;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
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
    private int deadline;


    public RULSolver(STNU stnu, boolean logging) throws CloneNotSupportedException {
        this.derivedSTNU = null;
        this.derivedSTNU = new STNU(stnu.getName(), stnu.getNodeList(), stnu.getEdgeList(), stnu.getDeadline(), stnu.getNumberOfConstraints());
        this.deadline = stnu.getDeadline();
        init();

    }

    public Status apply(boolean rulPlus, boolean computeParam, boolean semicont) {
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
                        applyRules(e1, e2, rulPlus, computeParam, semicont, status);
                        // System.out.println("hasDerived"+status.hasDerived);
                        if (status.hasNegativeLoop) {
                            System.out.println("-------------------------negativeLoop---------------------------");
                            break outerloop;
                        }
                    }
                }
            }
            nodeList = derivedSTNU.getNodeList();
            //System.out.println("---------------------------------------------------------------------run:"+runs);
        } while (status.hasDerived);

        return status;
    }

    private Status applyRules(NumericEdge e1, NumericEdge e2, boolean rulPlus, boolean computeParams,boolean semicont, Status status) {
        STNUNode source = e1.getSource();
        STNUNode target = e2.getTarget();
        /**
         * relax
         */

        int retVal = relaxApplicable(e1, e2, rulPlus);
        if (retVal != Integer.MIN_VALUE) {
            dcCheck(source, target, retVal, status);
            if (status.hasNegativeLoop) {
                System.out.println("Negative Loop at: "+source.getName());
                return status;
            }
            NumericEdge newEdge = new NumericEdge(source, target, retVal);
            if (updateEdge(newEdge)) {
                status.hasDerived = true;
                //System.out.println("RELAX derived: "+ e1.toString() +" + "+e2.toString()+ " = " +newEdge.toString());
            }
        }
        if (!computeParams) {
            retVal = upperApplicable(e1, e2, rulPlus);
            if (retVal != Integer.MIN_VALUE) {
                dcCheck(source, target, retVal, status);
                if (status.hasNegativeLoop) {
                     System.out.println("Negative Loop at: " + source.getName());
                    return status;
                }
                NumericEdge newEdge = new NumericEdge(source, target, retVal);
                if (updateEdge(newEdge)) {
                    status.hasDerived = true;
                   //  System.out.println("UPPER derived: " + e1.toString() + " + " + e2.toString() + " = " + newEdge.toString());
                }
            }
            retVal = lowerApplicable(e1, e2);
            if (retVal != Integer.MIN_VALUE) {
                dcCheck(source, target, retVal, status);
                if (status.hasNegativeLoop) {
                    System.out.println("Negative Loop at: " + source.getName());
                    return status;
                }
                NumericEdge newEdge = new NumericEdge(source, target, retVal);
                if (updateEdge(newEdge)) {
                    status.hasDerived = true;
                   // System.out.println("LOWER derived: " + e1.toString() + " + " + e2.toString() + " = " + newEdge.toString());
                }
            }
        }


         if(computeParams || semicont){

            retVal = uUpperApplicable(e1, e2, rulPlus);
            if (retVal != Integer.MIN_VALUE) {
                dcCheck(source, target, retVal, status);
                if (status.hasNegativeLoop) {
                    // System.out.println("Negative Loop at: " + source.getName());
                    return status;
                }
                NumericEdge newEdge = new NumericEdge(source, target, retVal);
                if (updateEdge(newEdge)) {
                    status.hasDerived = true;
                    // System.out.println("UPPER derived: " + e1.toString() + " + " + e2.toString() + " = " + newEdge.toString());
                }
            }
            retVal = lLowerApplicable(e1, e2);
            if (retVal != Integer.MIN_VALUE) {
                dcCheck(source, target, retVal, status);
                if (status.hasNegativeLoop) {
                    // System.out.println("Negative Loop at: " + source.getName());
                    return status;
                }
                NumericEdge newEdge = new NumericEdge(source, target, retVal);
                if (updateEdge(newEdge)) {
                    status.hasDerived = true;
                    // System.out.println("LOWER derived: " + e1.toString() + " + " + e2.toString() + " = " + newEdge.toString());
                }
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
                    return v + w;
                } else {
                    return Integer.MIN_VALUE;
                }
            }
            return v + w;
        }
        return Integer.MIN_VALUE;
    }

    private int upperApplicable(NumericEdge e1, NumericEdge e2, boolean rulPlus) {
        STNUNode p = e1.getSource();
        STNUNode c = e1.getTarget();
        STNUNode ac = e2.getTarget();
        int v = e1.getNonLabeledValue();
        int uc = e2.getLabeledValue();
        // System.out.println("Checking UPPER "+p.getName() +"-- "+v+" --> "+c.getName()+" -- "+uc+" -->"+ac.getName());
        if (v != Integer.MAX_VALUE && uc != Integer.MAX_VALUE && e2.getLabel().getType().equals(LabelType.uC) && !p.equals(c) && !c.equals(ac)) {
            if (c.isContingent() && !ac.isContingent() && c.getActivationTimepoint().equals(ac)) {
                if (rulPlus) {
                    if ((p.isContingent() || p.getName().equals("Z")) && v > uc) {
                        return v + uc;
                    }
                } else {

                    int lc = derivedSTNU.getEdge(ac, c).getLabeledValue() * -1;

                    // System.out.println("max( "+v+" - "+uc+" , "+lc+") = "+Integer.max(v+uc,lc));
                    return Integer.max(v + uc, lc);
                }
            }
        }
        return Integer.MIN_VALUE;
    }

    private int lowerApplicable(NumericEdge e1, NumericEdge e2) {
        STNUNode ac = e1.getSource();
        STNUNode c = e1.getTarget();
        STNUNode r = e2.getTarget();
        int lc = e1.getLabeledValue();
        int w = e2.getNonLabeledValue();
        //System.out.println("Checking LOWER "+ac.getName() +"-- "+lc+" --> "+c.getName()+" -- "+w+" -->"+r.getName());
        //System.out.println("   "+r.getName()+" is contingent? "+r.isContingent());
        if (lc != Integer.MAX_VALUE && w != Integer.MAX_VALUE && e1.getLabel().getType().equals(LabelType.lC) && !ac.equals(c) && !c.equals(r)) {
            if (!ac.isContingent() && c.isContingent() && c.getActivationTimepoint().equals(ac)) {
                //System.out.println("   "+w+" <= 0 ?");
                if (!r.isContingent() && w <= 0) {
                    //      System.out.println("   true");
                    return lc + w;
                }
                // System.out.println(false);
                if (r.isContingent() && !r.equals(c)) {
                    int ur = derivedSTNU.getEdge(r, r.getActivationTimepoint()).getLabeledValue();
                    //   System.out.println("   "+r.getName()+"(R) -- "+ur+" --> "+r.getActivationTimepoint().getName());
                    if (w <= (ur*-1)) {
                        return lc + w;
                    }
                }
            }
        }
        return Integer.MIN_VALUE;
    }

    private void makePointedAndUpperbounded() {
        /**
         * Adding dummy time-point Z;
         * connecting Z to every node P with h (if P is in Tx)
         */
        z = new STNUNode("Z", false);
        derivedSTNU.addNode(z);
        for (STNUNode target :
                derivedSTNU.getNodeList()) {
            if (!target.equals(z) && !target.isContingent()) {

                NumericEdge e = new NumericEdge(z, target, h);
                derivedSTNU.addEdge(e);
            }

        }
        /**
         * connecting every node P to Z with -uC-1 (if P is in Tc);
         * connecting every node P to Z with -1 (if P is in Tx);
         */
        for (STNUNode source :
                derivedSTNU.getNodeList()) {
            if (source.isContingent()) {
                int uc = derivedSTNU.getEdge(source, source.getActivationTimepoint()).getLabeledValue() + 1;
                derivedSTNU.addEdge(new NumericEdge(source, z, uc));
            } else if (!source.equals(z)) {
                derivedSTNU.addEdge(new NumericEdge(source, z, -1));
            }
        }
    }


    private ArrayList<NumericEdge> getSuccessor(STNUNode source) {
        HashMap<STNUNode, HashMap<STNUNode, NumericEdge>> network = derivedSTNU.getNetwork();
        HashMap<STNUNode, NumericEdge> successors = network.get(source);
        ArrayList<NumericEdge> out = new ArrayList<>();
        for (STNUNode interim :
                successors.keySet()) {
            NumericEdge e = successors.get(interim);
            out.add(e);
        }
        return out;
    }

    public static class Status {
        public boolean getHasNegativeLoop() {
            return hasNegativeLoop;
        }

        public boolean getHasDerived() {
            return hasDerived;
        }

        private boolean hasNegativeLoop;
        private boolean hasDerived;

        public Status() {
            hasNegativeLoop = false;
            hasDerived = false;
        }

        public void setHasDerived(boolean b) {
            hasDerived = b;
        }

        public void setHasNegativeLoop() {
            hasNegativeLoop = true;
        }
    }

    public boolean updateEdge(NumericEdge e) {
        // System.out.println("   New Edge: "+e.toString());

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

    private Status dcCheck(STNUNode source, STNUNode target, int retVal, Status status) {
        if (source.equals(target) && retVal < 0) {
            status.hasNegativeLoop = true;
            //System.out.println(new NumericEdge(source,target,retVal));
            return status;
        }
        return status;
    }

    public STNU getDerivedSTNU() {
        return derivedSTNU;
    }

    private void init() {
        z = new STNUNode("Z", false);
        derivedSTNU.addNode(z);
        for (STNUNode source :
                derivedSTNU.getNodeList()) {
            if (!source.equals(z) && !source.isContingent()) {
                NumericEdge e = new NumericEdge(source, z, 0);
                derivedSTNU.addEdge(e);
            }
        }
        if (deadline != Integer.MAX_VALUE) {
            STNUNode end = derivedSTNU.getNode("end");
            NumericEdge e = new NumericEdge(z, end, deadline);
            derivedSTNU.addEdge(e);
        }
    }

    private int uUpperApplicable(NumericEdge e1, NumericEdge e2, boolean rulPlus) {
        STNUNode p = e1.getSource();
        if (!p.isParam()) {
            return Integer.MIN_VALUE;
        }
        STNUNode c = e1.getTarget();
        STNUNode ac = e2.getTarget();
        int v = e1.getNonLabeledValue();
        int uc = e2.getLabeledValue();
        // System.out.println("Checking UPPER "+p.getName() +"-- "+v+" --> "+c.getName()+" -- "+uc+" -->"+ac.getName());
        if (v != Integer.MAX_VALUE && uc != Integer.MAX_VALUE && e2.getLabel().getType().equals(LabelType.uC) && !p.equals(c) && !c.equals(ac)) {
            if (c.isContingent() && !ac.isContingent() && c.getActivationTimepoint().equals(ac)) {

                // System.out.println("max( "+v+" - "+uc+" , "+lc+") = "+Integer.max(v+uc,lc));
                return v + uc;
            }
        }

        return Integer.MIN_VALUE;
    }

    private int lLowerApplicable(NumericEdge e1, NumericEdge e2) {
        STNUNode ac = e1.getSource();
        STNUNode c = e1.getTarget();
        STNUNode p = e2.getTarget();
        if (!p.isParam()) {
            return Integer.MIN_VALUE;
        }
        int lc = e1.getLabeledValue();
        int w = e2.getNonLabeledValue();
        //System.out.println("Checking LOWER "+ac.getName() +"-- "+lc+" --> "+c.getName()+" -- "+w+" -->"+r.getName());
        //System.out.println("   "+r.getName()+" is contingent? "+r.isContingent());
        if (lc != Integer.MAX_VALUE && w != Integer.MAX_VALUE && e1.getLabel().getType().equals(LabelType.lC) && !ac.equals(c) && !c.equals(p)) {
            if (!ac.isContingent() && c.isContingent() && c.getActivationTimepoint().equals(ac)) {
                //System.out.println("   "+w+" <= 0 ?");

                //      System.out.println("   true");
                return lc + w;

                // System.out.println(false);

            }

        }
        return Integer.MIN_VALUE;
    }


    public String computeComplexity(boolean deriveParam, boolean rulPlus, int bucketSize, int id) throws IOException, ExecutionException, InterruptedException, CloneNotSupportedException {


        Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        logger.setLevel(Level.OFF);
        StringBuilder sb = new StringBuilder();


        //number of activities of last bucket


        //number of parameters
        int param = 0;
        String pName = bucketSize + "N" + id;
        System.out.println("computing complexity for " + pName);
        long transStartTime = System.currentTimeMillis();
        long transEndTime = System.currentTimeMillis();
        long transTime = transEndTime - transStartTime;
        long dcStartTime = System.currentTimeMillis();
        Status flag = apply(false, false, false);
        long dcEndTime = System.currentTimeMillis();
        long dcTime = dcEndTime - dcStartTime;


        sb.append(pName);
        //System.out.println(pName);
        sb.append(',');
        sb.append(derivedSTNU.getNodeList().size());
        sb.append(',');
        sb.append(param);
        sb.append(',');
        sb.append(derivedSTNU.getNumberOfConstraints());
        sb.append(',');
        sb.append(transTime);
        sb.append(',');
        sb.append(dcTime);
        sb.append(',');
        sb.append(!flag.hasNegativeLoop);
        if (flag.hasNegativeLoop) {
            sb.append("\n");
            return sb.toString();
        }
        long paramStartTime = System.currentTimeMillis();
        Status flag2 = applyParam();
        long paramEndTime = System.currentTimeMillis();
        long paramTime = paramEndTime - paramStartTime;
        sb.append(',');
        sb.append(paramTime);
        sb.append(',');
        sb.append(!flag2.hasNegativeLoop);
        sb.append(',');
        sb.append('\n');


        return sb.toString();
    }

    public Status applyParam() throws CloneNotSupportedException {
        ArrayList<STNUNode> nodeList = derivedSTNU.getNodeList();

        Status status;
        Status paramStatus;
        int runs = 0;
        outerloop:
        do {
            runs++;
            status = new Status();
            paramStatus = new Status();
            for (STNUNode source :
                    nodeList) {
                ArrayList<NumericEdge> outgoing = getSuccessor(source);
                for (NumericEdge e1 :
                        outgoing) {
                    STNUNode interim = e1.getTarget();
                    ArrayList<NumericEdge> incomming = getSuccessor(interim);
                    for (NumericEdge e2 :
                            incomming) {
                        paramStatus = applyRules(e1, e2, false, true, false,status);
                        if (paramStatus.hasDerived) {
                            RULSolver solverTest = new RULSolver(new STNU(derivedSTNU.getName(), derivedSTNU.getNodeList(), derivedSTNU.getEdgeList(), derivedSTNU.getDeadline(), derivedSTNU.getNumberOfConstraints()), false);
                            status = solverTest.apply(false, false, false);
                            if (status.hasNegativeLoop) {
                                break outerloop;
                            }
                        }
                    }
                }
            }
            nodeList = derivedSTNU.getNodeList();
            //System.out.println("---------------------------------------------------------------------run:"+runs);
        } while (paramStatus.hasDerived);

        return status;
    }

    public boolean checkSDP(NumericEdge e1, NumericEdge e2) {

        STNUNode cs = e1.getSource();
        STNUNode ce = e1.getTarget();
        STNUNode se = e2.getTarget();


        if (cs != ce && ce != se && cs != se) {

            if (e1.getLabeledValue() != Integer.MAX_VALUE && e1.getLabel().getType().equals(LabelType.lC)) {
                int cmin = e1.getLabeledValue();

                NumericEdge e3 = derivedSTNU.getEdge(ce, cs);

                if (e3.getLabeledValue() != Integer.MAX_VALUE && e3.getLabel().getType().equals(LabelType.uC)) {
                    int cmax = e3.getLabeledValue()*-1;


                    if (se.getActivationTimepoint() != null) {
                        STNUNode ss = se.getActivationTimepoint();
                        NumericEdge l = derivedSTNU.getEdge(se,ss);
                        int smin = l.getNonLabeledValue()*-1;

                        NumericEdge e5 = derivedSTNU.getEdge(ss, se);
                        NumericEdge parallel1= derivedSTNU.getEdge(ss,cs); //checking if ce -- (<=0) --> ss  indicating that c and s are in parallel
                        NumericEdge parallel11= derivedSTNU.getEdge(ce,ss);
                        NumericEdge parallel2= derivedSTNU.getEdge(cs,ss);
                        NumericEdge parallel22= derivedSTNU.getEdge(se,cs);
                        if (e5.getNonLabeledValue() != Integer.MAX_VALUE ) {

                            int smax = e5.getNonLabeledValue();
                            return applyPattern(cs, ce, ss, se, cmax, cmin, smax,smin);
                        }
                    }

                }

            }


        }
        return false;

    }

    public boolean applyPattern(STNUNode cs, STNUNode ce, STNUNode ss, STNUNode se, int cmax, int cmin, int smax,int smin) {

        if(applyCase6(cs, ce, ss, se,cmax,cmin,smax,smin)){ //ubc(ce,se,w) ubc(ss,cs,v)
            return true;
        }
       else  if (applyCase5(cs, ce, ss, se,cmax,cmin,smax,smin)){ //ubc(ce,se,w) ubc(ss,ce,v)
            return true;
        }
        else if (applyCase4(cs, ce, ss, se,cmax,cmin,smax,smin)){ //ubc(ce,se,w) lbc(cs,ss,v)
            return true;
        }
        else if (applyCase3(cs, ce, ss, se,cmax,cmin,smax,smin)){ //ubc(cs,se,w) lbc(ce,se,v)
            return true;
        }
        else if (applyCase2(cs, ce, ss, se,cmax,cmin,smax,smin)){ //ubc(ce,se,w) lbc(cs,se,v)
            return true;
        }
      else   if (applyCase1(cs, ce, ss, se,cmax,cmin,smax,smin)){ //ubc(ce,se,w) lbc(ce,se,v)
            return true;
        }

        return false;
    }
     public boolean applyCase6(STNUNode cs, STNUNode ce, STNUNode ss, STNUNode se, int cmax, int cmin, int smax, int smin){

        NumericEdge ubc = derivedSTNU.getEdge(ce,se);
        int w = ubc.getNonLabeledValue();

        if(w!=Integer.MAX_VALUE&&w>0){

            NumericEdge ubc2 = derivedSTNU.getEdge(ss,cs);
            int v = ubc2.getNonLabeledValue();

            if(v>0){
                if(smin-v<cmin+w) {
                    return false;
                }
                int value = smin-v;
                if(value>0){
                    value*=-1;
                }

                    NumericEdge y = new NumericEdge(se,cs,value);
                    NumericEdge x = new NumericEdge(cs, se, cmin+w);



                    boolean c1 = updateEdge(y);
                    boolean c2 = updateEdge(x);

                    if(c1 || c2) {

                        return true;
                    }

                }
            }

        return false;
     }
    public boolean applyCase5(STNUNode cs, STNUNode ce, STNUNode ss, STNUNode se, int cmax, int cmin, int smax, int smin){

        NumericEdge ubc = derivedSTNU.getEdge(ce,se);
        int w = ubc.getNonLabeledValue();

        if(w!=Integer.MAX_VALUE&&w>0){

            NumericEdge ubc2 = derivedSTNU.getEdge(ss,ce);
            int v = ubc2.getNonLabeledValue();

            if(v>0){

                if(cmax+smin-v<cmin+w) {
                    return false;
                }

                int value = cmax+smin-v;
                if (value >= 0) {
                    value = value*-1;
                }


                NumericEdge y = new NumericEdge(se,cs,value);
                NumericEdge x = new NumericEdge(cs, se, cmin+w);

                boolean c1 = updateEdge(y);
                boolean c2 = updateEdge(x);

                if(c1 || c2) {

                    return true;
                }

            }
        }

        return false;
    }
    public boolean applyCase4(STNUNode cs, STNUNode ce, STNUNode ss, STNUNode se, int cmax, int cmin, int smax, int smin){

        NumericEdge ubc = derivedSTNU.getEdge(cs,se);
        int w = ubc.getNonLabeledValue();

        if(w!=Integer.MAX_VALUE&&w>0){

            NumericEdge lbc = derivedSTNU.getEdge(ss,cs);
            int v = lbc.getNonLabeledValue();

            if(v<=0){
                v=v*-1;

                if(smin+v<cmin+w) {
                    return false;
                }

                NumericEdge y = new NumericEdge(ss,cs,(smin+v)*-1);
                NumericEdge x = new NumericEdge(cs,se,cmin+w);



                boolean c1 = updateEdge(y);
                boolean c2 = updateEdge(x);

                if(c1||c2) {

                    return true;
                }

            }
        }

        return false;
    }
    public boolean applyCase3(STNUNode cs, STNUNode ce, STNUNode ss, STNUNode se, int cmax, int cmin, int smax, int smin){

        NumericEdge ubc = derivedSTNU.getEdge(cs,se);
        int w = ubc.getNonLabeledValue();

        if(w!=Integer.MAX_VALUE&&w>0){

            NumericEdge lbc = derivedSTNU.getEdge(se,ce);
            int v = lbc.getNonLabeledValue();

            if(v<=0){
            v=v*-1;

                if(cmax+v<w) {
                    return false;
                }

                NumericEdge y = new NumericEdge(se,cs,(cmax+v)*-1);



                boolean c1 = updateEdge(y);


                if(c1) {

                    return true;
                }

            }
        }

        return false;
    }
    public boolean applyCase2(STNUNode cs, STNUNode ce, STNUNode ss, STNUNode se, int cmax, int cmin, int smax, int smin){

        NumericEdge ubc = derivedSTNU.getEdge(ce,se);
        int w = ubc.getNonLabeledValue();

        if(w!=Integer.MAX_VALUE&&w>0){

            NumericEdge lbc = derivedSTNU.getEdge(se,cs);
            int v = lbc.getNonLabeledValue();

            if(v<=0){
                v = v*-1;

                if(v<cmin+w) {
                    return false;
                }

                NumericEdge y = new NumericEdge(se,cs,(v*-1));
                NumericEdge x = new NumericEdge(cs, se, cmin+w);



                boolean c1 = updateEdge(y);
                boolean c2 = updateEdge(x);

                if(c1 || c2) {

                    return true;
                }

            }
        }

        return false;
    }
    public boolean applyCase1(STNUNode cs, STNUNode ce, STNUNode ss, STNUNode se, int cmax, int cmin, int smax, int smin){
    NumericEdge s1 = derivedSTNU.getEdge(ss,ce);
    NumericEdge s2 = derivedSTNU.getEdge(cs,se);
    int parallel1=s1.getNonLabeledValue();
    int parallel2 = s2.getNonLabeledValue();
    if(parallel1<=0 || parallel2 <= 0){
        return false;
    }
        System.out.println(cs.getName());
        System.out.println(ss.getName());
        NumericEdge ubc = derivedSTNU.getEdge(ce,se);
        int w = ubc.getNonLabeledValue();

        if(w!=Integer.MAX_VALUE&&w>0){

            NumericEdge lbc = derivedSTNU.getEdge(se,ce);
            int v = lbc.getNonLabeledValue();

            if(v<=0){
                v = v*-1;


                if(cmax+v<=cmin+w) {



                NumericEdge y = new NumericEdge(se,cs,(cmax+v)*-1);
                NumericEdge x = new NumericEdge(cs, se, cmin+w);

                boolean c1 = updateEdge(y);
                boolean c2 = updateEdge(x);

                if(c1 || c2) {

                    return true;
                }

            }

        }
    }
        return false;
    }

    public Status applySDP() throws CloneNotSupportedException {
        ArrayList<STNUNode> nodeList = derivedSTNU.getNodeList();
        int times = 0;
        Status status;
        Status sdpStatus;
        boolean hasDerived;
        outerloop:
        do {
            hasDerived = false;
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
                        if (checkSDP(e1,e2)) {

                            hasDerived = true;
                            RULSolver solverTest = new RULSolver(new STNU(derivedSTNU.getName(), derivedSTNU.getNodeList(), derivedSTNU.getEdgeList(), derivedSTNU.getDeadline(), derivedSTNU.getNumberOfConstraints()), false);
                            status = solverTest.apply(false, false, true);
                            if (status.hasNegativeLoop) {
                                break outerloop;
                            }
                        }
                    }
                }
            }
            nodeList = derivedSTNU.getNodeList();
            //System.out.println("---------------------------------------------------------------------run:"+runs);
        } while (hasDerived);

        return status;
    }

    public String computeComplexitySDP(int bucketSize, int id) throws IOException, ExecutionException, InterruptedException, CloneNotSupportedException {


        Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        logger.setLevel(Level.OFF);
        StringBuilder sb = new StringBuilder();


        //number of activities of last bucket


        //number of parameters
        String pName = bucketSize + "N" + id;
        System.out.println("computing complexity for " + pName);
        long transStartTime = System.currentTimeMillis();
        long transEndTime = System.currentTimeMillis();
        long transTime = transEndTime - transStartTime;
        long dcStartTime = System.currentTimeMillis();
        Status flag = apply(false, false, true);
        System.out.println("-------------------"+pName+"..........................");

        long dcEndTime = System.currentTimeMillis();
        long dcTime = dcEndTime - dcStartTime;


        sb.append(pName);
        //System.out.println(pName);
        sb.append(',');
        sb.append(derivedSTNU.getNodeList().size());
        sb.append(',');
        sb.append(derivedSTNU.getNumberOfConstraints());
        sb.append(',');
        sb.append(transTime);
        sb.append(',');
        sb.append(dcTime);
        sb.append(',');
        sb.append(!flag.hasNegativeLoop);
        if (flag.hasNegativeLoop) {
            sb.append("\n");
            return sb.toString();
        }
        long paramStartTime = System.currentTimeMillis();
        Status flag2 = applySDP();
        long paramEndTime = System.currentTimeMillis();
        long paramTime = paramEndTime - paramStartTime;
        sb.append(',');
        sb.append(paramTime);
        sb.append(',');
        sb.append(!flag2.hasNegativeLoop);
        sb.append(',');
        sb.append('\n');


        return sb.toString();
    }

}
