package at.aau.stnu;

import at.aau.abstractGraph.Graph;

import java.util.*;

/**
 * Represents a Simple Temporal Network with uncertainty. Tasks are timepoints (activation/contingent) and edges are
 * constraints(precedence/temporal). Used to perform several reasoning tasks.
 */
public class STNU extends Graph implements Cloneable {
    /**
     * Human readable name of the STNU
     */
    private String name;

    public int getMostNegative() {
        return mostNegative;
    }

    /**
     * initialize stnu
     */
    private int numberOfNodes;

    /**
     * Graph representation.
     */
    private ArrayList<STNUNode> nodeList = new ArrayList<>();
    private int mostNegative = Integer.MAX_VALUE;

    public int getDeadline() {
        return deadline;
    }

    private int deadline = Integer.MAX_VALUE;
    private HashMap<STNUNode, HashMap<STNUNode, NumericEdge>> network;

    /**
     * Constructor for parsing in a whole STNU
     *
     * @param name  name of the process instance
     * @param nodes list of nodes
     * @param edges list of edges
     */
    public STNU(String name, ArrayList<STNUNode> nodes, ArrayList<NumericEdge> edges) {
        this.network = new HashMap<>();
        this.numberOfNodes = nodes.size();
        this.name = name;
        for (STNUNode n :
                nodes) {
            addNode(n);
        }
        for (NumericEdge e :
                edges) {
            if(e.getLabeledValue() < mostNegative){
                mostNegative = e.getLabeledValue();
            }
            if(e.getNonLabeledValue() < mostNegative){
                mostNegative = e.getNonLabeledValue();
            }
            addEdge(e);
        }

    }
    public STNU(String name, ArrayList<STNUNode> nodes, ArrayList<NumericEdge> edges, int deadline) {
        this.network = new HashMap<>();
        this.numberOfNodes = nodes.size();
        this.name = name;
        for (STNUNode n :
                nodes) {
            addNode(n);
        }
        for (NumericEdge e :
                edges) {
            if(e.getLabeledValue() < mostNegative){
                mostNegative = e.getLabeledValue();
            }
            if(e.getNonLabeledValue() < mostNegative){
                mostNegative = e.getNonLabeledValue();
            }
            addEdge(e);
        }
            this.deadline = deadline;
    }

    /**
     * Constructor for building a STNU from scratch
     *
     * @param name name of the STNU
     */
    public STNU(String name) {
        this.network = new HashMap<>();
        this.name = name;
    }

    /**
     * Adding edges to the STNU; used by constraint propagation algorithms
     *
     * @param e edge to be added
     * @return true if added
     */
    public void addEdge(NumericEdge e) {
        STNUNode source = e.getSource();
        STNUNode target = e.getTarget();
        this.network.get(source).put(target, e);
    }

    /**
     * Adding map entry for a new node
     *
     * @param n the new node
     */
    public void addNode(STNUNode n) {
        network.put(n, new HashMap<>());
        nodeList.add(n);
    }

    /**
     * Retrieving edges from the graph
     *
     * @param source source
     * @param target target
     * @return edge / null if no such entry
     */
    public NumericEdge getEdge(STNUNode source, STNUNode target) {
        Map<STNUNode, NumericEdge> sourceMap = network.get(source);
        NumericEdge e = sourceMap.get(target);
        return e;
    }
    public STNUNode getNode(String name) {
        for (STNUNode node: nodeList
             ) {
            if (node.getName().equals(name)){
                return node;
            }
        }
        return null;
    }


    /**
     * prints stnu to console
     */
    public void printSTNU() {
        for (STNUNode source :
                network.keySet()) {
            System.out.print(source + " || ");
            Map<STNUNode, NumericEdge> smap = network.get(source);
            for (STNUNode target :
                    smap.keySet()) {
                System.out.print(smap.get(target).toString());
            }
            System.out.println();
        }

    }



    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public ArrayList<STNUNode> getNodeList() {
        return nodeList;
    }
    public HashMap<STNUNode, HashMap<STNUNode, NumericEdge>> getNetwork() {
        return network;
    }



}


