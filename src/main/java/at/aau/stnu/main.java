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

       ProcessReader pread = new ProcessReader(new File("C:\\Users\\josef\\Documents\\TemporalVariables\\src\\main\\resources\\testprocesses\\1.swd"));


        STNU stnu = pread.getSTNU();
        RULSolver solver = new RULSolver(stnu, false);
        solver.apply(false, false);

        if(!solver.apply(false,false).getHasNegativeLoop()){

            System.out.println("------------------------------------------------------------RUL derived that the network is DC");
            solver.apply(false,true);
        }
        solver.getDerivedSTNU().printSTNU();
    }
}
