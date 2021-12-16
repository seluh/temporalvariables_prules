package at.aau.stnu;
import at.aau.abstractGraph.LabelType;
import at.aau.algorithms.BinarySearch;
import at.aau.algorithms.ConstraintGenerator;
import at.aau.algorithms.MMVSolver;
import at.aau.algorithms.RULSolver;
import at.aau.io.ProcessReader;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class main {
    public static void main(String[] args) throws CloneNotSupportedException, InterruptedException, ExecutionException, IOException {
/*
        ProcessReader pread = new ProcessReader(new File("C:\\Users\\josef\\Documents\\TemporalVariables\\src\\main\\resources\\testprocesses\\samples\\testcase1.swd"),0);

        STNU stnu = pread.getSTNU();
        RULSolver solver = new RULSolver(stnu, false);
        solver.apply(false,false);
        solver.getDerivedSTNU().printSTNU();
*/
        experimentSDP();
       // ConstraintGenerator generator = new ConstraintGenerator(stnu,1);
        //stnu =  generator.compute();

       // binaryTest();


    }
    public static void binaryTest () throws CloneNotSupportedException, FileNotFoundException, UnsupportedEncodingException {
        String fileTemplate = "C:\\Users\\josef\\Desktop\\ISYS\\TemporalVariables\\src\\main\\resources\\testprocesses\\";
        List<Double> ret= new ArrayList<>();
        int bucket1 = 10;
        int bucket2 = 20;
        int bucket3 = 30;
        int bucket4 = 40;
        int bucket5 = 50;

        String filename = "BinarySearch_"+bucket1;

        StringBuilder sb = new StringBuilder();
        sb.append("ProcessID:" + ',' + "Total Nodes:" + ',' + "Constraints:" + ',' + "Time computation (ms)");
        sb.append('\n');

        for (int i = 1; i <=1; i++) {

            for (int j = 1; j <= 5; j++) {
                sb.append(i);
                sb.append(',');
                ProcessReader pread = new ProcessReader(new File(fileTemplate + "\\n" + bucket1 + "\\1.swd"));
                STNU stnu = pread.getSTNU();
                sb.append(stnu.getNodeList().size());
                sb.append(',');
                sb.append(j);
                sb.append(',');
                double start = System.currentTimeMillis();
                ConstraintGenerator generator = new ConstraintGenerator(stnu, j);
                generator.compute();
                double end = System.currentTimeMillis();
                sb.append(end-start);
                sb.append(',');
                sb.append('\n');
            }
            sb.append('\n');
        }
        LogToCSV(sb,filename);



        filename = "BinarySearch_"+bucket2;

        sb.delete(0,sb.length()-1);
        sb.append("ProcessID:" + ',' + "Total Nodes:" + ',' + "Constraints:" + ',' + "Time computation (ms)");
        sb.append('\n');

        for (int i = 1; i <=1; i++) {

            for (int j = 1; j <= 10; j++) {
                sb.append(i);
                sb.append(',');
                ProcessReader pread = new ProcessReader(new File(fileTemplate + "\\n" + bucket2 + "\\1.swd"));
                STNU stnu = pread.getSTNU();
                sb.append(stnu.getNodeList().size());
                sb.append(',');
                sb.append(j);
                sb.append(',');
                double start = System.currentTimeMillis();
                ConstraintGenerator generator = new ConstraintGenerator(stnu, j);
                generator.compute();
                double end = System.currentTimeMillis();
                sb.append(end-start);
                sb.append(',');
                sb.append('\n');
            }
            sb.append('\n');
        }
        LogToCSV(sb,filename);


        filename = "BinarySearch_"+bucket3;

        sb.delete(0,sb.length()-1);
        sb.append("ProcessID:" + ',' + "Total Nodes:" + ',' + "Constraints:" + ',' + "Time computation (ms)");
        sb.append('\n');

        for (int i = 1; i <=1; i++) {

            for (int j = 1; j <= 15; j++) {
                sb.append(i);
                sb.append(',');
                ProcessReader pread = new ProcessReader(new File(fileTemplate + "\\n" + bucket3 + "\\1.swd"));
                STNU stnu = pread.getSTNU();
                sb.append(stnu.getNodeList().size());
                sb.append(',');
                sb.append(j);
                sb.append(',');
                double start = System.currentTimeMillis();
                ConstraintGenerator generator = new ConstraintGenerator(stnu, j);
                generator.compute();
                double end = System.currentTimeMillis();
                sb.append(end-start);
                sb.append(',');
                sb.append('\n');
            }
            sb.append('\n');
        }
        LogToCSV(sb,filename);

        filename = "BinarySearch_"+bucket4;

        sb.delete(0,sb.length()-1);
        sb.append("ProcessID:" + ',' + "Total Nodes:" + ',' + "Constraints:" + ',' + "Time computation (ms)");
        sb.append('\n');

        for (int i = 1; i <=1; i++) {

            for (int j = 1; j <= 20; j++) {
                sb.append(i);
                sb.append(',');
                ProcessReader pread = new ProcessReader(new File(fileTemplate + "\\n" + bucket4+ "\\1.swd"));
                STNU stnu = pread.getSTNU();
                sb.append(stnu.getNodeList().size());
                sb.append(',');
                sb.append(j);
                sb.append(',');
                double start = System.currentTimeMillis();
                ConstraintGenerator generator = new ConstraintGenerator(stnu, j);
                generator.compute();
                double end = System.currentTimeMillis();
                sb.append(end-start);
                sb.append(',');
                sb.append('\n');
            }
            sb.append('\n');
        }
        LogToCSV(sb,filename);


        filename = "BinarySearch_"+bucket5;

        sb.delete(0,sb.length()-1);
        sb.append("ProcessID:" + ',' + "Total Nodes:" + ',' + "Constraints:" + ',' + "Time computation (ms)");
        sb.append('\n');

        for (int i = 1; i <=1; i++) {

            for (int j = 1; j <= 25; j++) {
                sb.append(i);
                sb.append(',');
                ProcessReader pread = new ProcessReader(new File(fileTemplate + "\\n" + bucket5+ "\\1.swd"));
                STNU stnu = pread.getSTNU();
                sb.append(stnu.getNodeList().size());
                sb.append(',');
                sb.append(j);
                sb.append(',');
                double start = System.currentTimeMillis();
                ConstraintGenerator generator = new ConstraintGenerator(stnu, j);
                generator.compute();
                double end = System.currentTimeMillis();
                sb.append(end-start);
                sb.append(',');
                sb.append('\n');
            }
            sb.append('\n');
        }
        LogToCSV(sb,filename);
    }


    public static void experiment() throws CloneNotSupportedException, InterruptedException, ExecutionException, IOException {
        String fileString = "C:\\Users\\josef\\Documents\\TemporalVariables\\src\\main\\resources\\testprocesses\\";
        int bucket1 = 5;
        int bucket2 = 10;
        int bucket3 = 20;
        int bucket4 = 30;
      //  int bucket5 = 40;
        //int bucket6 = 50;
        int instances = 5;

        String filename = "ComplexityLog_N_3P"+bucket1;
        /*
        StringBuilder sb = new StringBuilder();
        sb.append("ProcessID:" + ',' + "Total Nodes:" + ',' + "ParameterNodes:" + ',' + "Constraints:" + ',' + "Time STNU-Transformation (ms)" + ',' + "Time DC-Check (ms)" + ',' + "DC: " + ',' + "Time Parameter Ranges:"+','+ "DC after ParamCalc:");
        sb.append('\n');
        for (int i = 1; i<=instances;i++){
            ProcessReader pread = new ProcessReader(new File(fileString+"n"+bucket1+"\\"+i+".swd"),3);
            STNU stnu = pread.getSTNU();
            RULSolver solver = new RULSolver(stnu,false);
            sb.append(solver.computeComplexity(true,false, bucket1,i));

        }
        LogToCSV(sb,filename);


        filename = "ComplexityLog_N_6P"+bucket2;
        StringBuilder sb2 = new StringBuilder();
        sb2.append("ProcessID:" + ',' + "Total Nodes:" + ',' + "ParameterNodes:" + ',' + "Constraints:" + ',' + "Time STNU-Transformation (ms)" + ',' + "Time DC-Check (ms)" + ',' + "DC: " + ',' + "Time Parameter Ranges:"+','+ "DC after ParamCalc:");
        sb2.append('\n');
        for (int i = 1; i<=instances;i++){
            ProcessReader pread = new ProcessReader(new File(fileString+"n"+bucket2+"\\"+i+".swd"),6);
            STNU stnu = pread.getSTNU();
            RULSolver solver = new RULSolver(stnu,false);
            sb2.append(solver.computeComplexity(false,false, bucket2,i));

        }
        LogToCSV(sb2,filename);


\*
        filename = "ComplexityLog_N_9"+bucket3;
        StringBuilder sb3 = new StringBuilder();
        sb3.append("ProcessID:" + ',' + "Total Nodes:" + ',' + "ParameterNodes:" + ',' + "Constraints:" + ',' + "Time STNU-Transformation (ms)" + ',' + "Time DC-Check (ms)" + ',' + "DC: " + ',' + "Time Parameter Ranges:"+','+ "DC after ParamCalc:");
        sb3.append('\n');
        for (int i = 1; i<=instances;i++){
            ProcessReader pread = new ProcessReader(new File(fileString+"n"+bucket3+"\\"+i+".swd"),9);
            STNU stnu = pread.getSTNU();
            RULSolver solver = new RULSolver(stnu,false);
            sb3.append(solver.computeComplexity(true,false, bucket3,i));

        }
        LogToCSV(sb3,filename);



        filename = "ComplexityLog_N_12P"+bucket4;
        StringBuilder sb4 = new StringBuilder();
        sb4.append("ProcessID:" + ',' + "Total Nodes:" + ',' + "ParameterNodes:" + ',' + "Constraints:" + ',' + "Time STNU-Transformation (ms)" + ',' + "Time DC-Check (ms)" + ',' + "DC: " + ',' + "Time Parameter Ranges:"+','+ "DC after ParamCalc:");
        sb4.append('\n');
        for (int i = 1; i<=instances;i++){
            ProcessReader pread = new ProcessReader(new File(fileString+"n"+bucket4+"\\"+i+".swd"),12);
            STNU stnu = pread.getSTNU();
            RULSolver solver = new RULSolver(stnu,false);
            sb4.append(solver.computeComplexity(true,false, bucket4,i));

        }
        LogToCSV(sb4,filename);


/*
        filename = "ComplexityLog_N"+bucket5;
        StringBuilder sb5 = new StringBuilder();
        sb5.append("ProcessID:" + ',' + "Total Nodes:" + ',' + "ParameterNodes:" + ',' + "Constraints:" + ',' + "Time STNU-Transformation (ms)" + ',' + "Time DC-Check (ms)" + ',' + "DC: " + ',' + "Time Parameter Ranges:"+','+ "DC after ParamCalc:");
        sb5.append('\n');

        for (int i = 1; i<=instances;i++){
            ProcessReader pread = new ProcessReader(new File(fileString+"n"+bucket5+"\\"+i+".swd"),12);
            STNU stnu = pread.getSTNU();
            RULSolver solver = new RULSolver(stnu,false);
            sb5.append(solver.computeComplexity(true,false, bucket5,i));
        }
        LogToCSV(sb5,filename);



        filename = "ComplexityLog_N"+bucket6;
        StringBuilder sb6 = new StringBuilder();
        sb6.append("ProcessID:" + ',' + "Total Nodes:" + ',' + "ParameterNodes:" + ',' + "Constraints:" + ',' + "Time STNU-Transformation (ms)" + ',' + "Time DC-Check (ms)" + ',' + "DC: " + ',' + "Time Parameter Ranges:"+','+ "DC after ParamCalc:");
        sb6.append('\n');

        for (int i = 1; i<=instances;i++){
            ProcessReader pread = new ProcessReader(new File(fileString+"n"+bucket5+"\\"+i+".swd"),15);
            STNU stnu = pread.getSTNU();
            RULSolver solver = new RULSolver(stnu,false);
            sb6.append(solver.computeComplexity(true,false, bucket6,i));
        }
        LogToCSV(sb6,filename);

*/

    }
    public static void experimentSDP() throws CloneNotSupportedException, InterruptedException, ExecutionException, IOException {
        String fileString = "C:\\Users\\josef\\IdeaProjects\\TemporalVariables\\src\\main\\resources\\testprocesses\\";
       // C:\Users\josef\IdeaProjects\TemporalVariables\src\main\resources\testprocesses
        int bucket1 = 5;
        int bucket2 = 10;
        int bucket3 = 20;
        int bucket4 = 30;
        int bucket5 = 40;
        int bucket6 = 50;
        int instances = 10;
        String fileprefix = "ComplexityLogSDP";

        String filename = fileprefix;
/*
       filename=fileprefix +bucket1;
        StringBuilder sb1 = new StringBuilder();
        sb1.append("ProcessID:" + ',' + "Total Nodes:" + ',' + "Constraints:" + ',' + "Time STNU-Transformation (ms)" + ',' + "Time DC-Check (ms)" + ',' + "DC: " + ',' + "Time SDP:"+','+ "DC after SDPCalc:");
        sb1.append('\n');
        for (int i = 1; i<=instances;i++){
            ProcessReader pread = new ProcessReader(new File(fileString+"n"+bucket1+"\\"+i+".swd"),0);
            STNU stnu = pread.getSTNU();

            RULSolver solver = new RULSolver(stnu,false);
            sb1.append(solver.computeComplexitySDP(bucket1,i));

        }
        LogToCSV(sb1,filename);

        filename = fileprefix+bucket2;

        StringBuilder sb2 = new StringBuilder();
        sb2.append("ProcessID:" + ',' + "Total Nodes:" + ',' + "Constraints:" + ',' + "Time STNU-Transformation (ms)" + ',' + "Time DC-Check (ms)" + ',' + "DC: " + ',' + "Time SDP:"+','+ "DC after SDPCalc:");
        sb2.append('\n');
        for (int i = 1; i<=instances;i++){
            ProcessReader pread = new ProcessReader(new File(fileString+"n"+bucket2+"\\"+i+".swd"),0);
            STNU stnu = pread.getSTNU();

            RULSolver solver = new RULSolver(stnu,false);
            sb2.append(solver.computeComplexitySDP(bucket2,i));

        }
        LogToCSV(sb2,filename);


        filename = fileprefix+bucket3;
        StringBuilder sb3 = new StringBuilder();
        sb3.append("ProcessID:" + ',' + "Total Nodes:" + ',' + "Constraints:" + ',' + "Time STNU-Transformation (ms)" + ',' + "Time DC-Check (ms)" + ',' + "DC: " + ',' + "Time SDP:"+','+ "DC after SDPCalc:");        sb3.append('\n');
        for (int i = 1; i<=instances;i++){
            ProcessReader pread = new ProcessReader(new File(fileString+"n"+bucket3+"\\"+i+".swd"),0);
            STNU stnu = pread.getSTNU();
            RULSolver solver = new RULSolver(stnu,false);
            sb3.append(solver.computeComplexitySDP(bucket3,i));

        }
        LogToCSV(sb3,filename);


        filename = fileprefix+bucket4;
        StringBuilder sb4 = new StringBuilder();
        sb4.append("ProcessID:" + ',' + "Total Nodes:" + ',' + "Constraints:" + ',' + "Time STNU-Transformation (ms)" + ',' + "Time DC-Check (ms)" + ',' + "DC: " + ',' + "Time SDP:"+','+ "DC after SDPCalc:");        sb4.append('\n');
        for (int i = 1; i<=instances;i++){
            ProcessReader pread = new ProcessReader(new File(fileString+"n"+bucket4+"\\"+i+".swd"),0);
            STNU stnu = pread.getSTNU();
            RULSolver solver = new RULSolver(stnu,false);
            sb4.append(solver.computeComplexitySDP(bucket4,i));

        }
        LogToCSV(sb4,filename);



        filename = fileprefix+bucket5;
        StringBuilder sb5 = new StringBuilder();
        sb5.append("ProcessID:" + ',' + "Total Nodes:" + ',' + "Constraints:" + ',' + "Time STNU-Transformation (ms)" + ',' + "Time DC-Check (ms)" + ',' + "DC: " + ',' + "Time SDP:"+','+ "DC after SDPCalc:");        sb5.append('\n');

        for (int i = 1; i<=instances;i++){
            ProcessReader pread = new ProcessReader(new File(fileString+"n"+bucket5+"\\"+i+".swd"),0);
            STNU stnu = pread.getSTNU();
            RULSolver solver = new RULSolver(stnu,false);
            sb5.append(solver.computeComplexitySDP(bucket5,i));
        }
        LogToCSV(sb5,filename);


*/
        filename = fileprefix+bucket6;
        StringBuilder sb6 = new StringBuilder();
        sb6.append("ProcessID:" + ',' + "Total Nodes:" + ',' + "Constraints:" + ',' + "Time STNU-Transformation (ms)" + ',' + "Time DC-Check (ms)" + ',' + "DC: " + ',' + "Time SDP:"+','+ "DC after SDPCalc:");        sb6.append('\n');

        for (int i = 1; i<=instances;i++){
            ProcessReader pread = new ProcessReader(new File(fileString+"n"+bucket6+"\\"+i+".swd"),0);
            STNU stnu = pread.getSTNU();
            RULSolver solver = new RULSolver(stnu,false);
            sb6.append(solver.computeComplexitySDP(bucket6,i));
        }
        LogToCSV(sb6,filename);



    }

    public static void LogToCSV(StringBuilder sb, String filename) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter(filename+".csv", "UTF-8");
        //seperator
        writer.write("sep=,");
        writer.write('\n');
        writer.write(sb.toString());
        writer.close();
    }
}
