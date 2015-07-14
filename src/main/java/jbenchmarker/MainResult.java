
package jbenchmarker;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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

		
		/*
		 *  Check that list of argument is correct
		 */
		if (args.length < 19) {
			System.err.println("Arguments : Factory Trace [nb_exec [thresold]]");
			System.err.println("- Factory to run trace main");
			System.err.println("- Factory : a jbenchmaker.core.ReplicaFactory implementation ");
			System.err.println("- Trace : a file of a trace ");
			System.err.println("- nb_exec : the number of execution by trace(default 1)");
			System.err.println("- thresold : the proportional thresold for not counting a result in times the average (default 2.0)");
			System.err.println("- number of serialization");
			System.err.println("- Save traces ? (0 don't save, else save)");
			System.err.println("- Calcule Time execution ? (0 don't calcul, else calcule)");//make it bool
			System.err.println("- Serialization with overhead ? (0 don't store, else store)");//make it bool
			System.err.println("- Compute size of messages ? (0 don't store, else store)");//make it bool
			System.err.println("- Number of trace execution ?");

			System.err.println("\n Caracteristics of traces : \n");			
			System.err.println("- Duration ? ");
			System.err.println("- perIns ? ");
			System.err.println("- avgBlockSize ? ");
			System.err.println("- sdvBlockSize ? ");
			System.err.println("- probability ? ");
			System.err.println("- delay ? ");
			System.err.println("- sdv ? ");
			System.err.println("- replicas ? ");

			System.err.println("- Comments about the test (which computer is used, what is the target of the test...). It is a String.");

			System.exit(1);
		}
		

		/*
		 *  Check that the folder we want to create doesn't exist already. If it is the case, we throw an exception
		 */
		
		if(new File(System.getProperty("user.dir")+ File.separator+"ResultTest" + File.separator+args[2].toString()+File.separator).exists())
		{
			throw new Exception("Le dossier " + System.getProperty("user.dir")+ File.separator+"ResultTest" + File.separator+args[2].toString()+File.separator 
					+" existe déjà. Veuiller changer le nom de la trace donné dans les arguments ou déplacer/renommer/supprimer le dossier éxistant.");	
		}
		

		/*
		 *  Parameterize each variable contains in the list of arguments 
		 */
		
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

		
		ArrayList factories = new ArrayList<String>();		
		factories.add("jbenchmarker.factories.LogootSplitOFactory");
		factories.add("jbenchmarker.factories.TreedocFactory");
		//factories.add("jbenchmarker.factories.RGAFactory");
		factories.add("jbenchmarker.factories.RgaSFactory");
		//factories.add("jbenchmarker.factories.RgaTreeSplitFactory");
		factories.add("jbenchmarker.factories.RgaTreeSplitBalancedFactory");
		//factories.add("jbenchmarker.factories.RGAFFactory");
		// factories.add("jbenchmarker.factories.LogootSFactory");
		//factories.add("jbenchmarker.factories.WootFactories$WootHFactory");

		
		String name=args[2].toString();
		String name1=name;
		for (int k=0; k<nbTraceExec; k++){

			args[2]=name1+File.separator+name1+"-"+k;
			name=name1+"-"+k;
			String repPath = System.getProperty("user.dir")+ File.separator+"ResultTest" + File.separator;
			String repPath1 = repPath+args[2]+File.separator;				
			args[2]=repPath1+name;
			if(!new File(repPath1).exists())
			{
				new File(repPath1).mkdirs();
			}

			writeTofile(repPath1+name, "RESULT FOR : " + name1 + "\n\n");
			if (args.length>=20) writeTofile(repPath1+name, "Comment : " + args[20] + "\n\n");
			writeTofile(repPath1+ name, "		Nb of generated traces :	" + nbTraceExec + "\n");
			writeTofile(repPath1+ name, "		Nb of executions by trace:	" + nbExec + "\n");
			writeTofile(repPath1+ name, "		Duration :	" + duration + "\n");
			writeTofile(repPath1+ name, "		% of insertions :	" + perIns+ "\n");
			writeTofile(repPath1+ name, "		% of Blocks :	" + perBlock+ "\n");
			writeTofile(repPath1+ name, "		Avg blockSize :	 " + avgBlockSize+ "\n");
			writeTofile(repPath1+ name, "		Sdv blockSize :	 " + sdvBlockSize+ "\n");
			writeTofile(repPath1+ name, "		Probability :	" + probability + "\n");
			writeTofile(repPath1+ name, "		Delay :	" + delay+ "\n");
			writeTofile(repPath1+ name, "		Sdv :	" + sdv+ "\n");
			writeTofile(repPath1+ name, "		Number of replicas :	" + replicas+ "\n");


			Factory<CRDT> rf = (Factory<CRDT>) Class.forName(args[1]).newInstance();
			Trace trace = new RandomTrace(duration, RandomTrace.FLAT, 
					new StandardSeqOpProfile(perIns, perBlock, avgBlockSize, sdvBlockSize), probability, delay, sdv, replicas);

			CausalSimulator cd = new CausalSimulator(rf, false,  0, false);
			cd.setWriter(new TraceObjectWriter(repPath1+ name));
			cd.run(trace); //create Trace

			writeTofile(repPath1+ name,"\n\nName	Total execution time (ms)	Average local execution time (ns)	Average remote execution time (ns)	Bandwidth (o)	Memory (o)\n");

			for (int i=0; i<factories.size() ; i++ ){
				args[1]=(String) factories.get(i);
				ExperienceFactory ef = (ExperienceFactory) Class.forName(args[0]).newInstance();
				ef.create(args);


				Scanner scanner=new Scanner(new File(repPath1+ name+".csv"));
				StringBuilder s1 = new StringBuilder();
				while (scanner.hasNextLine()) {
					String line = scanner.nextLine();
					line=line.replace(".",",");
					s1.append(line+"\n");
				}

				FileWriter local = new FileWriter(repPath1+ name+".csv", false);
				local.write(s1.toString());

				if (local != null) {
					local.close();
				}

			}
		}


		/* 
		 * write a resume of all trace execution 
		 */

		String repPath = System.getProperty("user.dir")+ File.separator+"ResultTest" + File.separator;
		String repPath1 = repPath+name1+File.separator;	
		writeTofile(repPath+name1+File.separator+name1, "RESULT FOR : " + name1 + "\n\n");
		if (args.length>=20) writeTofile(repPath+name1+File.separator+name1, "Comment : " + args[20] + "\n\n");
		writeTofile(repPath+name1+File.separator+name1, "		Nb of generated traces :	" + nbTraceExec + "\n");
		writeTofile(repPath+name1+File.separator+name1, "		Nb of executions by trace :	" + nbExec + "\n");
		writeTofile(repPath+name1+File.separator+name1, "		Duration :	" + duration + "\n");
		writeTofile(repPath+name1+File.separator+name1, "		% of insertions :	" + perIns+ "\n");
		writeTofile(repPath+name1+File.separator+name1, "		% of Blocks :	" + perBlock+ "\n");
		writeTofile(repPath+name1+File.separator+name1, "		Avg blockSize :	 " + avgBlockSize+ "\n");
		writeTofile(repPath+name1+File.separator+name1, "		Sdv blockSize :	 " + sdvBlockSize+ "\n");
		writeTofile(repPath+name1+File.separator+name1, "		Probability :	" + probability + "\n");
		writeTofile(repPath+name1+File.separator+name1, "		Delay :	" + delay+ "\n");
		writeTofile(repPath+name1+File.separator+name1, "		Sdv :	" + sdv+ "\n");
		writeTofile(repPath+name1+File.separator+name1, "		Number of replicas :	" + replicas+ "\n");
		writeTofile(repPath+name1+File.separator+name1, "\n\n\nName	Total execution time (ms)	Average local execution time (ns)	Average remote execution time (ns)	Bandwidth (o)	Memory (o)\n");

		for (int f=0; f<factories.size(); f++){
			StringBuilder s = new StringBuilder();
			double a=0;
			double b=0;
			double c=0;
			double d=0;
			double e=0;
			String aa ="";

			for (int k=0; k<nbTraceExec; k++){
				Scanner scanner=new Scanner(new File(repPath1+name1+"-"+k+File.separator+name1+"-"+k+".csv"));
				while (scanner.hasNextLine()) {
					String line = scanner.nextLine();
					StringTokenizer st = new StringTokenizer(line, "	"); 

					if (st.hasMoreTokens()) aa = st.nextToken().toString();
					if (st.hasMoreTokens() && ("jbenchmarker.factories."+ aa +"Factory").equals(factories.get(f))){ 
						a+= Double.parseDouble(st.nextToken().replace(",","."));
						b+= Double.parseDouble(st.nextToken().replace(",","."));
						c+= Double.parseDouble(st.nextToken().replace(",","."));
						d+= Double.parseDouble(st.nextToken().replace(",","."));
						e+= Double.parseDouble(st.nextToken().replace(",","."));
						scanner.close();
						break;
					}
				}
			}

			s.append(aa+"	");
			s.append((a/nbTraceExec)+"	");
			s.append((b/nbTraceExec)+"	");
			s.append((c/nbTraceExec)+"	");
			s.append((d/nbTraceExec)+"	");
			s.append((e/nbTraceExec)+"\n");	
			writeTofile(repPath+name1+File.separator+name1, s.toString().replace(".",","));

		}
	}













	public static void writeTofile(String file, String s) throws IOException {
		FileWriter local = new FileWriter(file + ".csv", true);

		local.write(s);

		if (local != null) {
			local.close();
		}
	}

}
