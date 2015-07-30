
package jbenchmarker;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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

	
	// 7 7000 0.8 0.15 100 1 0.1 5 1 10 "realized on coast-team's computer"
	// 4 5000 0.8 0.05 625 5 0.1 10 4 15 "Test realized on coast-team's computer"
	
	/*
	 * launch in command line
	 
	 cd git/replication-benchmarker/
	 mvn exec:java -Dexec.mainClass=jbenchmarker.MainResult -Dexec.args="jbenchmarker.factories.TraceFactory  jbenchmarker.factories.RgaTreeSplitFactory TraceTest  3 2 0 0 1 0 1 7 1000 0.8 0.15 10 1 0.1 5 1 10 'realized on Grid 5000'"
	
	 */
	
	
	public static void main(String[] args) throws Exception {

		
		/*
		 *  Check that list of arguments is correct
		 */
		if (args.length < 20) {
			
			System.err.println("Arguments : Factory Trace [nb_exec [thresold]]\n");
			
			System.err.println("- Factory to run trace main");
			System.err.println("- Factory : a jbenchmaker.core.ReplicaFactory implementation ");
			System.err.println("- Trace : a file of a trace ");
			System.err.println("- nb_exec : the number of execution by trace(default 1)");
			System.err.println("- thresold : the proportional thresold for not counting a result in times the average (default 2.0)");
			System.err.println("- number of serialization");
			System.err.println("- Save traces ? (0 don't save, else save)");
			System.err.println("- Calcule Time execution ? (0 don't calcul, else calcule)");
			System.err.println("- Serialization with overhead ? (0 don't store, else store)");
			System.err.println("- Compute size of messages ? (0 don't store, else store)");
			System.err.println("- Number of trace execution ?");

			
			System.err.println("\n\n Caracteristics of traces : \n");			
			
			System.err.println("- Duration ? ");
			System.err.println("- perIns ? ");
			System.err.println("- avgBlockSize ? ");
			System.err.println("- sdvBlockSize ? ");
			System.err.println("- probability ? ");
			System.err.println("- delay ? ");
			System.err.println("- sdv ? ");
			System.err.println("- replicas ? ");

			System.err.println("\n- Comments about the test (which computer is used, what is the target of the test...). It is a String.");

			System.exit(1);
		}
		

		/*
		 *  Check that the folder we want to create doesn't exist already. If it is the case, we throw an exception
		 */
		
		if(new File(System.getProperty("user.dir")+ File.separator+"ResultTest" + File.separator+args[2].toString()+File.separator).exists())
		{
			throw new Exception("Le dossier " + System.getProperty("user.dir")+ File.separator+"ResultTest" + File.separator+args[2].toString()+File.separator 
					+" existe deja. Veuiller changer le nom de la trace donne dans les arguments ou deplacer/renommer/supprimer le dossier existant.");	
		}
		

		/*
		 *  Parameterize each variable contained in the list of arguments 
		 */
		
		
		String traceName = args[2];
		int nbExec = Integer.parseInt(args[3]);
		int nbTraceExec = Integer.parseInt(args[10]);
		long duration = Long.parseLong(args[11]);
		double perIns = Double.parseDouble(args[12]);
		double perBlock = Double.parseDouble(args[13]);
		int avgBlockSize = Integer.parseInt(args[14]);
		double sdvBlockSize = Double.parseDouble(args[15]);
		double probability = Double.parseDouble(args[16]);
		long delay = Long.parseLong(args[17]);
		double sdv = Double.parseDouble(args[18]);
		int replicas = Integer.parseInt(args[19]);
		String comment = args[20];


		/*
		 *  Choose factories that you want to test
		 */
		
		ArrayList<String> factories = new ArrayList<String>();		
		//factories.add("jbenchmarker.factories.LogootFactory");
		factories.add("jbenchmarker.factories.LogootSplitAVLFactory");
		//factories.add("jbenchmarker.factories.RGAFactory");
		//factories.add("jbenchmarker.factories.RGAFFactory");
		//factories.add("jbenchmarker.factories.RGATreeListFactory");
		//factories.add("jbenchmarker.factories.RgaSFactory");
		factories.add("jbenchmarker.factories.RgaTreeSplitBalancedFactory");
		//factories.add("jbenchmarker.factories.TreedocFactory");
		//factories.add("jbenchmarker.factories.RgaTreeSplitFactory");
		//factories.add("jbenchmarker.factories.WootFactories$WootHFactory");
		
		

		
		
		/* 
		 * write result for all trace executions 
		 */
		
		for (int k=0; k<nbTraceExec; k++){
			String repPath = System.getProperty("user.dir")+ File.separator+"ResultTest" + File.separator;
			String repPath1 = repPath+traceName+File.separator+traceName+"-"+k+File.separator;				
			args[2]=repPath1+traceName+"-"+k;
			if(!new File(repPath1).exists())
			{
				new File(repPath1).mkdirs();
			}

			writeTofile(repPath1+traceName+"-"+k, "RESULT FOR : " + traceName+"-"+k + "\n\n");
			writeTofile(repPath1+traceName+"-"+k, "Comment : " + comment + "\n\n");
			writeTofile(repPath1+ traceName+"-"+k, "		Nb of generated traces :	" + nbTraceExec + "\n");
			writeTofile(repPath1+ traceName+"-"+k, "		Nb of executions by trace:	" + nbExec + "\n");
			writeTofile(repPath1+ traceName+"-"+k, "		Duration :	" + duration + "\n");
			writeTofile(repPath1+ traceName+"-"+k, "		% of insertions :	" + perIns+ "\n");
			writeTofile(repPath1+ traceName+"-"+k, "		% of Blocks :	" + perBlock+ "\n");
			writeTofile(repPath1+ traceName+"-"+k, "		Avg blockSize :	 " + avgBlockSize+ "\n");
			writeTofile(repPath1+ traceName+"-"+k, "		Sdv blockSize :	 " + sdvBlockSize+ "\n");
			writeTofile(repPath1+ traceName+"-"+k, "		Probability :	" + probability + "\n");
			writeTofile(repPath1+ traceName+"-"+k, "		Delay :	" + delay+ "\n");
			writeTofile(repPath1+ traceName+"-"+k, "		Sdv :	" + sdv+ "\n");
			writeTofile(repPath1+ traceName+"-"+k, "		Number of replicas :	" + replicas+ "\n");
			writeTofile(repPath1+ traceName+"-"+k,"\n\nName	Total execution time (ms)	Average local execution time (ns)	Average remote execution time (ns)	Bandwidth (o)	Memory (o)\n");


			Factory<CRDT> rf = (Factory<CRDT>) Class.forName(args[1]).newInstance();
			Trace trace = new RandomTrace(duration, RandomTrace.FLAT, 
					new StandardSeqOpProfile(perIns, perBlock, avgBlockSize, sdvBlockSize), probability, delay, sdv, replicas);

			CausalSimulator cd = new CausalSimulator(rf, false,  0, false);
			cd.setWriter(new TraceObjectWriter(repPath1+ traceName+"-"+k));
			cd.run(trace); //create Trace

			
			for (int i=0; i<factories.size() ; i++ ){
				args[1]=(String) factories.get(i);
				ExperienceFactory ef = (ExperienceFactory) Class.forName(args[0]).newInstance();
				ef.create(args);


				Scanner scanner=new Scanner(new File(repPath1+ traceName+"-"+k+".csv"));
				StringBuilder s1 = new StringBuilder();
				while (scanner.hasNextLine()) {
					String line = scanner.nextLine();
					line=line.replace(".",",");
					s1.append(line+"\n");
				}

				FileWriter local = new FileWriter(repPath1+ traceName+"-"+k+".csv", false);
				local.write(s1.toString());

				if (local != null) {
					local.close();
				}

				scanner.close();
			}
		}


		/* 
		 * write a resume of all trace execution 
		 */

		String repPath = System.getProperty("user.dir")+ File.separator+"ResultTest" + File.separator;
		String repPath1 = repPath+traceName+File.separator;	
		writeTofile(repPath+traceName+File.separator+traceName, "RESUME OF RESULTS FOR : " + traceName + "\n\n");
		writeTofile(repPath+traceName+File.separator+traceName, "Comment : " + comment + "\n\n");
		writeTofile(repPath+traceName+File.separator+traceName, "		Nb of generated traces :	" + nbTraceExec + "\n");
		writeTofile(repPath+traceName+File.separator+traceName, "		Nb of executions by trace :	" + nbExec + "\n");
		writeTofile(repPath+traceName+File.separator+traceName, "		Duration :	" + duration + "\n");
		writeTofile(repPath+traceName+File.separator+traceName, "		% of insertions :	" + perIns+ "\n");
		writeTofile(repPath+traceName+File.separator+traceName, "		% of Blocks :	" + perBlock+ "\n");
		writeTofile(repPath+traceName+File.separator+traceName, "		Avg blockSize :	 " + avgBlockSize+ "\n");
		writeTofile(repPath+traceName+File.separator+traceName, "		Sdv blockSize :	 " + sdvBlockSize+ "\n");
		writeTofile(repPath+traceName+File.separator+traceName, "		Probability :	" + probability + "\n");
		writeTofile(repPath+traceName+File.separator+traceName, "		Delay :	" + delay+ "\n");
		writeTofile(repPath+traceName+File.separator+traceName, "		Sdv :	" + sdv+ "\n");
		writeTofile(repPath+traceName+File.separator+traceName, "		Number of replicas :	" + replicas+ "\n");
		writeTofile(repPath+traceName+File.separator+traceName, "\n\n\nName	Total execution time (ms)	Average local execution time (ns)	Average remote execution time (ns)	Bandwidth (o)	Memory (o)\n");

		for (int f=0; f<factories.size(); f++){
			StringBuilder s = new StringBuilder();
			double execTime = 0;
			double localExecTime = 0;
			double remoteExecTime = 0;
			double bandwidth = 0;
			double memory = 0;
			String algoName ="";

			for (int k=0; k<nbTraceExec; k++){
				Scanner scanner=new Scanner(new File(repPath1+traceName+"-"+k+File.separator+traceName+"-"+k+".csv"));
				while (scanner.hasNextLine()) {
					String line = scanner.nextLine();
					StringTokenizer st = new StringTokenizer(line, "	"); 
					if (st.hasMoreTokens()) algoName = st.nextToken().toString();
					if (st.hasMoreTokens() && (("jbenchmarker.factories."+ algoName +"Factory").equals(factories.get(f)) || ("jbenchmarker.factories.WootFactories$"+algoName+"Factory").equals(factories.get(f)))){ 
						execTime += Double.parseDouble(st.nextToken().replace(",","."));
						localExecTime += Double.parseDouble(st.nextToken().replace(",","."));
						remoteExecTime += Double.parseDouble(st.nextToken().replace(",","."));
						bandwidth += Double.parseDouble(st.nextToken().replace(",","."));
						memory += Double.parseDouble(st.nextToken().replace(",","."));
						scanner.close();
						break;
					}
				}
			}

			s.append(algoName + "	");
			s.append((execTime / nbTraceExec)+"	");
			s.append((localExecTime / nbTraceExec)+"	");
			s.append((remoteExecTime / nbTraceExec)+"	");
			s.append((bandwidth / nbTraceExec)+"	");
			s.append((memory / nbTraceExec)+"\n");	
			writeTofile(repPath+traceName+File.separator+traceName, s.toString().replace(".",","));
		}
		

		Scanner scanner=new Scanner(new File(repPath+traceName+File.separator+traceName+".csv"));
		StringBuilder s1 = new StringBuilder();
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			line=line.replace(".",",");
			s1.append(line+"\n");
		}

		FileWriter local = new FileWriter(repPath+traceName+File.separator+traceName+".csv", false);
		local.write(s1.toString());

		if (local != null) {
			local.close();
		}
		scanner.close();
	}













	public static void writeTofile(String file, String s) throws IOException {
		FileWriter local = new FileWriter(file + ".csv", true);

		local.write(s);

		if (local != null) {
			local.close();
		}
	}

}
