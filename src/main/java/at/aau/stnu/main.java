package at.aau.stnu;
import at.aau.abstractGraph.LabelType;
import at.aau.algorithms.MMVSolver;
import at.aau.algorithms.RULSolver;
import at.aau.io.ProcessReader;
import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class main {
    public static void main(String[] args) throws CloneNotSupportedException {
       // ProcessReader pread = new ProcessReader(new File(".\\src\\main\\resources\\testprocesses\\1.swd"));
        ArrayList<NumericEdge>edgeList = new ArrayList<>();
        ArrayList<STNUNode>nodesList = new ArrayList<>();

        STNUNode bs = new STNUNode("b.s");
        STNUNode be = new STNUNode("b.e",bs);
        STNUNode as = new STNUNode("a.s");
        STNUNode ae = new STNUNode("a.e");
        //STNUNode z = new STNUNode("Z");
       STNUNode start = new STNUNode("start");
        STNUNode end = new STNUNode("end");

        NumericEdge e2 = new NumericEdge(as,ae,8);
        NumericEdge e22 = new NumericEdge(ae,as,-2);
        NumericEdge e8 = new NumericEdge(bs,be,3,new Label(be, LabelType.lC));
        NumericEdge e9 = new NumericEdge(be,bs,-5,new Label(be, LabelType.uC));
        NumericEdge e55 = new NumericEdge(be,ae,1, true);
        NumericEdge e66 = new NumericEdge(ae,be,3, true);
        //NumericEdge e0 = new NumericEdge(ps,z,0);
        //NumericEdge e00 = new NumericEdge(z,ps,0);

      //  NumericEdge e10 = new NumericEdge(cs,ps,2);
         NumericEdge e12= new NumericEdge(bs,as,-4);
        //NumericEdge e11 = new NumericEdge(ps,cs,-1,true);


        NumericEdge e13 = new NumericEdge(as,start,-1);
        NumericEdge e133 = new NumericEdge(start,as,1);
        NumericEdge e14 = new NumericEdge(bs,start,-1);
        NumericEdge e144 = new NumericEdge(start,bs,1);
        NumericEdge e15 = new NumericEdge(end,ae,0);
        NumericEdge e16 = new NumericEdge(end,be,0);

        NumericEdge deadline = new NumericEdge(start,end,20);
/*

*/
        nodesList.add(bs);
        nodesList.add(be);
        nodesList.add(as);
        nodesList.add(ae);
       // nodesList.add(z);
        nodesList.add(start);
      nodesList.add(end);

        edgeList.add(e2);
        edgeList.add(e22);
        edgeList.add(e8);
        edgeList.add(e9);
       edgeList.add(e55);
  //  edgeList.add(e66);
       //edgeList.add(e133);
       //edgeList.add(e144);
       // edgeList.add(e0);
        //edgeList.add(e00);
     //   edgeList.add(e10);
        //edgeList.add(e11);
        //edgeList.add(e12);


        edgeList.add(e13);
        edgeList.add(e14);
        edgeList.add(e15);
        edgeList.add(e16);
        edgeList.add(deadline);

        STNU stnu = new STNU("test",nodesList,edgeList);
        stnu.printSTNU();
        RULSolver solver = new RULSolver(stnu, false, 20);
        solver.apply(false);
        solver.getDerivedSTNU().printSTNU();
/*
        STNU stnu2 = new STNU("mmv", nodesList,edgeList);
        MMVSolver mmv = new MMVSolver(stnu2,false,20);
        mmv.apply();
        mmv.getDerivedSTNU().printSTNU();
        */
    }
}
