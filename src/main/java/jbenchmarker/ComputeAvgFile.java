package jbenchmarker;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


/*
 * given all files result in parameter
 * result : one file ".final"
 */
public class ComputeAvgFile{
    
    public static void main(String[] args) throws FileNotFoundException, IOException {
        
        if (args.length != 1) {
            System.err.println("Arguments : ");
            System.err.println("- 1 ----> Bloc");
            System.err.println("- 2 ----> Ins");
            System.err.println("- 3 ----> Replica");
            System.err.println("- 4 ----> Operation");
            System.exit(1);
        }
        int arg = Integer.parseInt(args[0]);
        int nbFilebloc = 10, nbFileIns = 7, ins = 80, Replica = 10,
                operation = 25000, nbFileReplic = 1, nbFileOp = 4;
        long tabUsr[] = null, tabGen[] = null,tabMem[]  = null;
        String[] algo = new String[]{"TTFWithoutGC"};
        //String[] algo = new String[]{"Logoot","RGA", "Treedoc", "WootH", "WootO", "TTFWithoutGC"};
        String file = "";
        
        for (int j = 0; j < algo.length; j++) {
            if (arg == 1) { //Bloc
                tabUsr = new long[nbFilebloc];
                tabGen = new long[nbFilebloc];
                tabMem = new long[nbFilebloc];
                for (int i = 0; i < nbFilebloc; i++) {
                    if (i < 10) {
                        file = "trace00";
                    } else {
                        file = "trace0";
                    }
                    tabUsr[i] = keepAvgFile(algo[j] + "-" + file + i + "-usr.res");
                    tabGen[i] = keepAvgFile(algo[j] + "-" + file + i + "-gen.res");
                    tabMem[i] = keepAvgFile(algo[j] + "-" + file + i + "-mem.res");
                }
            } else if (arg == 2) { //Ins
                tabUsr = new long[nbFileIns];
                tabGen = new long[nbFileIns];
                tabMem = new long[nbFileIns];
                for (int i = 50, k=0; i <= ins; i += 5, k++) {
                    tabUsr[k] = keepAvgFile(algo[j] + "-trace" + i + "-usr.res");
                    tabGen[k] = keepAvgFile(algo[j] + "-trace" + i + "-gen.res");
                    tabMem[k] = keepAvgFile(algo[j] + "-trace" + i + "-mem.res");
                }
            } else if (arg == 3) { //Replica
                tabUsr = new long[nbFileReplic];
                tabGen = new long[nbFileReplic];
                tabMem = new long[nbFileReplic];
                for (int i = 10, k=0; i <= Replica; i += 5, k++) {
                    tabUsr[k] = keepAvgFile(algo[j] + "-trace" + i + "-usr.res");
                    tabGen[k] = keepAvgFile(algo[j] + "-trace" + i + "-gen.res");
                    tabMem[k] = keepAvgFile(algo[j] + "-trace" + i + "-mem.res");
                }
            } else { //Operation
                tabUsr = new long[nbFileOp];
                tabGen = new long[nbFileOp];
                tabMem = new long[nbFileOp];
                for (int i = 10000, k=0; i <= operation; i += 5000, k++) {
                    tabUsr[k] = keepAvgFile(algo[j] + "-trace" + i + "-usr.res");
                    tabGen[k] = keepAvgFile(algo[j] + "-trace" + i + "-gen.res");
                    tabMem[k] = keepAvgFile(algo[j] + "-trace" + i + "-mem.res");
            }
            }
            String chaine="";
            if(arg == 1) chaine= "bloc";
            if(arg == 2) chaine= "ins";
            if(arg == 3) chaine= "rep";
            if(arg == 4) chaine= "op";
            writeToFile(tabUsr, "../"+algo[j]+"-usr-"+chaine+".final");
            writeToFile(tabGen, "../"+algo[j]+"-gen-"+chaine+".final");
            writeToFile(tabMem, "../"+algo[j]+"-mem-"+chaine+".final");
        }
        
    }

    private static String writeToFile(long[] data, String nameFile) throws IOException {
        BufferedWriter out = new BufferedWriter(new FileWriter(nameFile));
        for (int op = 0; op < data.length; ++op) {
            {
                //double d = (double)data[op]/1000;//MicroSecond
                
                out.append(data[op] + "\n");
            }
        }
        out.close();
        return nameFile;
    }
    
    private static long keepAvgFile(String file) throws FileNotFoundException
    {
        long avg = 0L;
        String line;
        InputStream ips1 = new FileInputStream(file);
        InputStreamReader ipsr1 = new InputStreamReader(ips1);
        BufferedReader br1 = new BufferedReader(ipsr1);
        int nbLine = 0;
        try {
            line = br1.readLine();
            while (line != null) {
                nbLine++;
                String[] tab = line.split("\t");
                
                avg += (long)Double.parseDouble(tab[tab.length-1]);
                line = br1.readLine();
            }
            br1.close();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return avg/nbLine;
    }
}
