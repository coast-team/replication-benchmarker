/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/
 * Copyright (C) 2013 LORIA / Inria / SCORE Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package jbenchmarker;

import crdt.CRDT;
import crdt.Factory;
import crdt.simulator.CausalSimulator;
import crdt.simulator.Trace;
import crdt.simulator.tracestorage.TraceFromFile;
import crdt.simulator.tracestorage.TraceObjectWriter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Scanner;
import java.util.StringTokenizer;

import jbenchmarker.trace.TraceGenerator;

/**
 *
 * @author score
 */
public final class TraceMain extends Experience {

	static int baseSerializ = 1, base = 100;

	public TraceMain(String[] args) throws Exception {

		if (args.length < 10) {
			System.err.println("Arguments : Factory Trace [nb_exec [thresold]]");
			System.err.println("- Factory to run trace main");
			System.err.println("- Factory : a jbenchmaker.core.ReplicaFactory implementation ");
			System.err.println("- Trace : a file of a trace ");
			System.err.println("- nb_exec : the number of execution (default 1)");
			System.err.println("- thresold : the proportional thresold for not counting a result in times the average (default 2.0)");
			System.err.println("- number of serialization");
			System.err.println("- Save traces ? (0 don't save, else save)");
			System.err.println("- Calcule Time execution ? (0 don't calcul, else calcule)");//make it bool
			System.err.println("- Serialization with overhead ? (0 don't store, else store)");//make it bool
			System.err.println("- Compute size of messages ? (0 don't store, else store)");//make it bool
			System.exit(1);
		}

		
		Long sum = 0L;
		Factory<CRDT> rf = (Factory<CRDT>) Class.forName(args[1]).newInstance();
		int nbExec = (args.length > 2) ? Integer.valueOf(args[3]) : 1;
		int nb = (nbExec > 1) ? nbExec + 1 : nbExec;
		double thresold = (args.length > 3) ? Double.valueOf(args[4]) : 2.0;
		long ltime[][] = null, mem[][] = null, rtime[][] = null;
		int cop = 0, uop = 0, nbReplica = 0, mop = 0;
		int minCop = 0, minUop = 0, minMop = 0;

		boolean calculTimeEx = Integer.valueOf(args[7]) == 0 ? false : true;
		boolean overhead = Integer.valueOf(args[8]) == 0 ? false : true;
		boolean sizeMessage = Integer.valueOf(args[9]) == 0 ? false : true;
		int sizemsg = 0;

		System.out.println("\n\n" + createName(args));
		for (int ex = 0; ex < nbExec; ex++) {
			System.out.println("execution ::: " + ex);

			//Trace trace = TraceGenerator.traceFromXML(args[2], 1);
			Trace trace = new TraceFromFile(args[2]);
			CausalSimulator cd;




			if (ex == 0 || args[1].contains("Logoot")) {
				cd = new CausalSimulator(rf, calculTimeEx, Integer.valueOf(args[5]), overhead);
			} else {
				cd = new CausalSimulator(rf, calculTimeEx, 0, overhead);
			}
			cd.setWriter(Integer.valueOf(args[6]) == 1 ? new TraceObjectWriter("trace") : null);
			cd.run(trace);
			if (ltime == null) {
				cop = cd.getRemoteTimes().size();
				uop = cd.getGenerationTimes().size();
				mop = cd.getMemUsed().size();
				nbReplica = cd.replicas.size();
				ltime = new long[nb][uop];
				rtime = new long[nb][cop];
				mem = new long[nb][mop];
				minCop = cop;
				minUop = uop;
				minMop = mop;
			}

			minCop = minCop > cd.getRemoteTimes().size() ? cd.getRemoteTimes().size() : minCop;
			minUop = minUop > cd.getGenerationTimes().size() ? cd.getGenerationTimes().size() : minUop;
			minMop = minMop > cd.getMemUsed().size() ? cd.getMemUsed().size() : minMop;

			if (calculTimeEx) {
				toArrayLong(ltime[ex], cd.getGenerationTimes());
				toArrayLong(rtime[ex], cd.getRemoteTimes());
			}
			if (args[1].contains("Logoot") || ex == 0) {
				toArrayLong(mem[ex], cd.getMemUsed());
			}

			if (nbReplica > 2) {
				for (int i = 0; i < cop - 1; i++) {
					rtime[ex][i] /= nbReplica - 1;
				}
			}

			if (sizeMessage) {
				sizemsg += this.serializ(cd.getGenHistory());
			}

			sum += cd.getRemoteSum() + cd.getLocalTimeSum();
			cd = null;
			trace = null;
			System.gc();
		}
		sum = sum / nbReplica;
		sum = sum / nbExec;

		if (nbExec > 1) {
			computeAverage(ltime, thresold, minUop);
			computeAverage(rtime, thresold, minCop);
			if (args[1].contains("Logoot")) {
				computeAverage(mem, thresold, minMop);
			}
		}

		//String repPath_ResultTest = new File(args[2]).getParentFile().getParent()+File.separator;
		String repPath_RT_T = new File(args[2]).getParent()+File.separator;
		args[2]= new File(args[2]).getName();
		String fileName = createName(args);
		String repPath_RT_T_Tk = repPath_RT_T + fileName + File.separator;
		
	
		
		
		
		
		
		if(!new File(repPath_RT_T_Tk).exists())
		{
			new File(repPath_RT_T_Tk).mkdirs();
		}
		
		
		writeToFile(ltime, repPath_RT_T_Tk + fileName, "gen");
		writeToFile(rtime, repPath_RT_T_Tk + fileName, "usr");
		writeToFile(mem, repPath_RT_T_Tk + fileName, "mem");		


		
		
		writeTofile(repPath_RT_T+args[2],fileName.substring(0,fileName.length() - (args[2].length()+1)) +"	");
		
		System.out.println("Average execution time in : " + (sum / Math.pow(10, 6)) + " Mili-second");
		writeTofile(repPath_RT_T+args[2], sum / Math.pow(10, 6) +"	");

		System.out.println("Average local execution time in :   " + getAverage(repPath_RT_T_Tk+fileName +  "-gen.res")+ " Nano-second");
		writeTofile(repPath_RT_T+args[2], getAverage(repPath_RT_T_Tk+fileName+ "-gen.res") + "	");

		System.out.println("Average remote execution time in :   " + getAverage(repPath_RT_T_Tk+fileName+ "-usr.res")+ " Nano-second");
		writeTofile(repPath_RT_T+args[2], getAverage(repPath_RT_T_Tk+fileName+ "-usr.res")+ "	" );

		if (sizeMessage) {
			System.out.println("Bandwidth is :" + sizemsg / nbExec);
			writeTofile(repPath_RT_T+args[2], sizemsg / nbExec + "	");
		}

		System.out.println("Memory :   " + getAverageMem(repPath_RT_T_Tk+fileName + "-mem.res",1));
		writeTofile(repPath_RT_T+args[2], getAverageMem( repPath_RT_T_Tk + fileName + "-mem.res",1)+ "\n");

		
		args[2]= repPath_RT_T + args[2];
	}

	public TraceMain() {
		// TODO Auto-generated constructor stub
	}



	@Override
	String createName(String[] args) {
		int i = args[2].lastIndexOf('/'), j = args[2].lastIndexOf('.'),
				k = args[1].lastIndexOf('.'), l = args[1].lastIndexOf("Factory");

		if (l == -1) {
			l = args[1].lastIndexOf("Factories");
		}

		if (i == -1) {
			i = args[1].lastIndexOf('\\');
		}

		String n = args[1].substring(k + 1, l);

		String[] c;
		if (n.contains("$")) {
			c = n.split("\\$");
			n = c[1];
		}

		if (n.equals("TTF")) {
			String tab[] = args[1].split("\\$");
			n = n + "" + tab[tab.length - 1];
		}

		if (j < 0) {
			j = args[2].length();
		}

		return n + "-" + args[2].substring(i + 1, j);
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

}
