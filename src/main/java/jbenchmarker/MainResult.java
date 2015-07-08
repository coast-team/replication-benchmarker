
package jbenchmarker;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.StringTokenizer;

import crdt.CRDT;
import crdt.Factory;
import crdt.simulator.CausalSimulator;
import crdt.simulator.Trace;
import crdt.simulator.random.RandomTrace;
import crdt.simulator.random.StandardSeqOpProfile;
import crdt.simulator.tracestorage.TraceObjectWriter;
import jbenchmarker.factories.ExperienceFactory;

public class MainResult {

	public static void main(String[] args) throws Exception {

		long duration = 20000;
		double perIns = 0.8;
		double perBlock = 0.1;
		int avgBlockSize = 20;
		double sdvBlockSize = 1;
		double probability = 1;
		long delay = 1;
		double sdv = 1;
		int replicas = 7;
		
		
		writeTofile("result"+args[2], "RESULT FOR : " + args[2] + "\n\n");
		
		writeTofile("result"+args[2], "		Nb execution :	" + args[3] + "\n");
		writeTofile("result"+args[2], "		Duration :	" + duration + "\n");
		writeTofile("result"+args[2], "		% of insertions :	" + perIns+ "\n");
		writeTofile("result"+args[2], "		% of Blocks :	" + perBlock+ "\n");
		writeTofile("result"+args[2], "		Avg blockSize :	 " + avgBlockSize+ "\n");
		writeTofile("result"+args[2], "		Sdv blockSize :	 " + sdvBlockSize+ "\n");
		writeTofile("result"+args[2], "		Probability :	" + probability + "\n");
		writeTofile("result"+args[2], "		Delay :	" + delay+ "\n");
		writeTofile("result"+args[2], "		Sdv :	" + sdv+ "\n");
		writeTofile("result"+args[2], "		Number of replicas :	" + replicas+ "\n");


		

		Factory<CRDT> rf = (Factory<CRDT>) Class.forName(args[1]).newInstance();
		Trace trace = new RandomTrace(duration, RandomTrace.FLAT, 
				new StandardSeqOpProfile(perIns, perBlock, avgBlockSize, sdvBlockSize), probability, delay, sdv, replicas);
		CausalSimulator cd = new CausalSimulator(rf, false,  0, false);
		cd.setWriter(new TraceObjectWriter(args[2]));


		cd.run(trace); //create Trace


		if(args.length<1){
			System.err.println("Arguments for Git experiment :::::::::: ");
			System.err.println("- Factory to run git main");
			System.err.println("- git directory ");
			System.err.println("- file [optional] path or number (default : all files)");
			System.err.println("- --save [optional] Store traces");
			System.err.println("- --clean [optional] clean DB");
			System.err.println("- --stat [optional] compute execution time and memory");
			System.err.println("- i :  number of file [To begin]");
			System.err.println("- Number of execution");
			System.err.println("- Factory");

			System.err.println("Arguments for real time trace experiment :::::::::: ");
			System.err.println("- Factory to run trace main");
			System.err.println("- Factory : a jbenchmaker.core.ReplicaFactory implementation ");
			System.err.println("- Trace : a file of a trace ");
			System.err.println("- nb_exec : the number of execution (default 1)");
			System.err.println("- thresold : the proportional thresold for not counting a result in times the average (default 2.0)");
			System.err.println("- number of serialization");
			System.err.println("- Save traces ? (0 don't save, else save)");
		}

		String[] factories = new String[9];
		
		factories[0] = "jbenchmarker.factories.RgaTreeSplitFactory";
		factories[1] = "jbenchmarker.factories.RgaTreeSplitBalancedFactory";  // doesn't work
		factories[2] = "jbenchmarker.factories.RGAFactory";
		factories[3] = "jbenchmarker.factories.RGAFFactory";
		factories[4] = "jbenchmarker.factories.RgaSFactory";
		factories[5] = "jbenchmarker.factories.LogootSFactory";
		factories[6] = "jbenchmarker.factories.LogootSplitOFactory";
		factories[7] = "jbenchmarker.factories.TreedocFactory";
		factories[8] = "jbenchmarker.factories.WootFactories$WootHFactory";
		
		
		writeTofile("result"+args[2],"\n\nName	Bandwith (o)	Total execution time (ms)	Average local execution time (ns)	Average remote execution time (ns)	Memory (o)\n");
		
		for (int i=0; i<factories.length ; i++ ){
			args[1]=factories[i];

			TraceMain tracemain = new TraceMain();
			String fileName = tracemain.createName(args);

			System.out.println("\n\n-----------------------\n"+fileName+"\n");
			writeTofile("result"+args[2],fileName.substring(0,fileName.length() - (args[2].length()+1)) +"	");

			ExperienceFactory ef = (ExperienceFactory) Class.forName(args[0]).newInstance();
			ef.create(args);

			String filePath = System.getProperty("user.dir")+"\\";

			System.out.println("Average local execution time in :   " + getAverage(filePath+fileName+ "-gen.res")+ " Nano-second");
			writeTofile("result"+args[2], getAverage(filePath+fileName+ "-gen.res") + "	");

			System.out.println("Average remote execution time in :   " + getAverage(filePath+fileName+ "-usr.res")+ " Nano-second");
			writeTofile("result"+args[2], getAverage(filePath+fileName+ "-usr.res")+ "	" );

			System.out.println("Memory :   " + getAverageMem(filePath+fileName+ "-mem.res",10));
			writeTofile("result"+args[2], getAverageMem(filePath+fileName+ "-mem.res",10)+ "\n");
		}
		
		
		Scanner scanner=new Scanner(new File("result"+args[2]+".csv"));
		StringBuilder s = new StringBuilder();
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			line=line.replace(".",",");
			s.append(line+"\n");
		}
		
		FileWriter local = new FileWriter("result"+args[2]+ ".csv", false);

		local.write(s.toString());

		if (local != null) {
			local.close();
		}
		
	}



	public static double getAverage(String fileName) throws FileNotFoundException{

		double sum = 0;
		double nbLine = 0;
		double inter=0;
		Scanner scanner=new Scanner(new File(fileName));

		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			StringTokenizer st = new StringTokenizer(line, "	"); 

			while (st.hasMoreTokens()) { 
				inter= Double.parseDouble(st.nextToken()) ;
			} 

			nbLine++;
			sum= sum+inter;
		}

		double res = sum/nbLine;

		scanner.close();
		return res;
	}

	public static double getAverageMem(String fileName, int i) throws FileNotFoundException{

		double sum = 0;
		double nbLine = 0;
		double inter=0;
		Scanner scanner1=new Scanner(new File(fileName));
		Scanner scanner2=new Scanner(new File(fileName));

		while (scanner1.hasNextLine()) {
			String line = scanner1.nextLine();
			nbLine++;
		}

		int j=0;
		while (scanner2.hasNextLine()) {
			String line = scanner2.nextLine();
			j++;
			StringTokenizer st = new StringTokenizer(line, "	"); 

			while (st.hasMoreTokens()) { 
				inter= Double.parseDouble(st.nextToken()) ;
			} 
			if (nbLine-j<=i){
				sum= sum+inter;
			}
		}

		double res = sum/i;

		scanner1.close();
		scanner2.close();
		return res;
	}

	public static void writeTofile(String file, String s) throws IOException {
		FileWriter local = new FileWriter(file + ".csv", true);

		local.write(s);

		if (local != null) {
			local.close();
		}
	}

}
