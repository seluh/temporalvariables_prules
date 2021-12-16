package at.aau.io;

import at.aau.abstractGraph.LabelType;
import at.aau.stnu.STNUNode;
import at.aau.stnu.*;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;


/**
 * This class is used to parse in a processinstance from Manuel Ebners tool.
 */
public final class ProcessReader {
    private String processName;
    private ArrayList<STNUNode> processNodes;
    private ArrayList<NumericEdge> processEdges;
    private File file;
    private int processdeadline;
    int numberOfConstraints = 0;
    int params = 0;

    public ProcessReader(File file, int params) {
        processNodes = new ArrayList<>();
        processEdges = new ArrayList<>();
        this.file=file;

        this.params = params;
        read();
    }

    public ProcessReader(File file) {
        processNodes = new ArrayList<>();
        processEdges = new ArrayList<>();
        this.file=file;
        read();
    }

    public void read(){
        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {

                // basically just going through the text file and generating node and edge objects
                switch (line) {

                    case "[graph]":
                        String graphinstance = br.readLine();
                        String graphtype = graphinstance.substring(graphinstance.indexOf("=")+2);
                        graphinstance = br.readLine();
                        String graphname = graphinstance.substring(graphinstance.indexOf("=")+2);
                        graphinstance = br.readLine();
                        String deadline = graphinstance.substring(graphinstance.indexOf("=")+2);
                        processName = graphname;
                        processdeadline= Integer.parseInt(deadline);
                        break;

                    case "[nodes]":
                        String instance;
                        while ((instance = br.readLine()) != null && !instance.isEmpty() && !instance.equals("[edges]")) {

                            try {

                                String nodetype = "";
                                String id = instance.substring(0, instance.indexOf(" "));
                                instance = instance.substring(instance.indexOf(" ") + 1);

                                if(instance.contains(" ")) {
                                    nodetype = instance.substring(0, instance.indexOf(" "));
                                } else{
                                    nodetype = instance;
                                }
                                instance = instance.substring(instance.indexOf(" ") + 1);

                                if(nodetype.equals("Task")) {

                                    STNUNode actStart = new STNUNode(id+".s",false);
                                    processNodes.add(actStart);
                                    STNUNode actEnd = new STNUNode(id+".e",actStart);
                                    processNodes.add(actEnd);
                                    String nodemin = instance.substring(0, instance.indexOf(","));

                                    int lc = Integer.parseInt(nodemin);

                                    instance = instance.substring(instance.indexOf(" ") + 1);

                                    String nodemax = instance.substring(0,instance.indexOf(" "));


                                    String contingent = "none";


                                    contingent = instance.substring(instance.indexOf(" ")+1);

                                    int uc = Integer.parseInt(nodemax);

                                    if(contingent.equals("n")){

                                        processEdges.add(new NumericEdge(actStart,actEnd,uc));
                                        processEdges.add(new NumericEdge(actEnd,actStart,lc*-1));
                                    }

                                    else {
                                        processEdges.add(new NumericEdge(actStart, actEnd, lc, new Label(actEnd, LabelType.lC)));
                                        processEdges.add(new NumericEdge(actEnd, actStart, uc * -1, new Label(actEnd, LabelType.uC)));
                                    }
                                }
                                // start nodes are not defined as start and end
                                else if(nodetype.equals("Parameter")){
                                    processNodes.add(new STNUNode(id,true));
                                }
                                else{
                                    processNodes.add(new STNUNode(id,false));
                                }
                            } catch (StringIndexOutOfBoundsException ioobe) {
                                //graph.nodes.add(instance.toLowerCase());
                                //  br.skip(instance.length());
                                //   br.readLine();
                            }
                        }
                       // processEdges.add(new NumericEdge(getNode("start"),getNode("end"),processdeadline, new Label(getNode("start"),LabelType.nC)));
                        break;

                    case "[edges]":
                        String edgeinstance;
                        while ((edgeinstance = br.readLine()) != null && !edgeinstance.isEmpty()) {
                            String source = edgeinstance.substring(0, edgeinstance.indexOf(" "));
                            edgeinstance = edgeinstance.substring(edgeinstance.indexOf(" ") + 1);
                            String target = edgeinstance;

                            if ((!source.equals("start")) && (!source.contains("PAR_"))) {
                                source += ".e";
                            }
                            if (!target.equals("end") && !target.contains("PAR_")) {
                                target += ".s";
                            }
                            STNUNode s = getNode(source);
                            STNUNode t = getNode(target);

                            processEdges.add(new NumericEdge(t,s,0));
                        }
                        break;

                    case "[constraints]":

                        //constraints are handled like edges since in cstnu every constraint is defined by an edge
                        String constraintinstance;

                        while ((constraintinstance = br.readLine()) != null && !constraintinstance.isEmpty()) {
                            numberOfConstraints++;
                            String type = constraintinstance.substring(0, constraintinstance.indexOf(" "));
                            constraintinstance = constraintinstance.substring(constraintinstance.indexOf(" ") + 1);
                            String source = constraintinstance.substring(0, constraintinstance.indexOf(" "));
                            constraintinstance = constraintinstance.substring(constraintinstance.indexOf(" ") + 1);
                            String target = constraintinstance.substring(0, constraintinstance.indexOf(" "));
                            constraintinstance = constraintinstance.substring(constraintinstance.indexOf(" ") + 1);
                            String value = constraintinstance;
                            /*
                            if(!source.equals("start")&&!source.contains("PAR_")){
                                source+=".e";
                            }
                            if(!target.equals("end")&&!target.contains("PAR_")){
                                target+=".e";
                            }*/
                            int nvalue = Integer.parseInt(value);
                            STNUNode s = getNode(source);
                            STNUNode t = getNode(target);

                            if (type.equals("UBC")) {
                                processEdges.add(new NumericEdge(s,t,nvalue));
                            }
                            else if(type.equals("LBC")) { //lbc

                                processEdges.add(new NumericEdge(t,s,nvalue*-1));

                            }

                        }

                        break;

                    default:
                        continue;
                }

            }

        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        if (this.params>0){
            for (int i = 1; i<=params; i++){
                STNUNode pNode = new STNUNode("p"+i, true);
                processNodes.add(pNode);
                if(i<=params/4){
                    NumericEdge e = new NumericEdge(pNode,getRandomNode(pNode),getNumber(true));
                    processEdges.add(e);
                } else if(i<=params/3){
                    NumericEdge e = new NumericEdge(pNode,getRandomNode(pNode),getNumber(false));
                    processEdges.add(e);
                } else if(i<=params/2){
                    NumericEdge e = new NumericEdge(getRandomNode(pNode), pNode, getNumber(false));
                    processEdges.add(e);
                } else if (i<=params){
                    NumericEdge e = new NumericEdge(getRandomNode(pNode), pNode, getNumber(true));
                    processEdges.add(e);
                }
            }
        }
    }
    public STNU getSTNU(){
        return new STNU(processName,processNodes,processEdges, processdeadline, numberOfConstraints);
    }

    private STNUNode getNode(String name){
        Iterator<STNUNode> i = processNodes.iterator();
        while (i.hasNext()) {
            STNUNode node = i.next();
            if (node.getName().equals(name)) {
                return node;
            }
        }
        return null;
    }
    public void printEdges(){
        for (NumericEdge e:
             processEdges) {
            System.out.println(e.toString());
        }
    }
    public STNUNode getRandomNode(STNUNode n){
        Random rand = new Random();
        int sourceNode = processNodes.indexOf(n);
        int node = rand.nextInt(processNodes.size());
        if(node==0 || node == sourceNode) {
            return getRandomNode(n);
        }

        return processNodes.get(node);
    }
    public int getNumber(boolean positive){
        Random rand = new Random();
        if(positive){
             return rand.nextInt(processdeadline);
        }

        return rand.nextInt(5)*-1;
    }
   }


