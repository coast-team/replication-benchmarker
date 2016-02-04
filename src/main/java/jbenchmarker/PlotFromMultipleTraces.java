package jbenchmarker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;

import com.panayotis.gnuplot.JavaPlot;
import com.panayotis.gnuplot.plot.DataSetPlot;
import com.panayotis.gnuplot.style.NamedPlotColor;
import com.panayotis.gnuplot.style.PlotStyle;
import com.panayotis.gnuplot.style.Style;
import com.panayotis.gnuplot.terminal.ImageTerminal;
import com.panayotis.gnuplot.utils.Debug;

public class PlotFromMultipleTraces {

	public static void main(String[] args) throws Exception {
		
		ArrayList<String> algoList = new ArrayList<String>();
		//algoList.add("Logoot");
		//algoList.add("WootH");		
		algoList.add("RGA");
		algoList.add("RGASplit");
		algoList.add("RGATreeList");
		algoList.add("RGATreeSplitBalanced");
		algoList.add("LogootS");
		algoList.add("LogootSplitAVL");
		algoList.add("Treedoc");
		
		String fileName = "TraceBPB";
		File repertory= new File(System.getProperty("user.dir")+ File.separator+"ResultTest/");
		String abscisseTitle = "Number of operations";
		
		//plotAWholeGraph(repertory, fileName, "Average execution time by " +abscisseTitle, abscisseTitle, "Average execution time (ms)", 1);
		plotAWholeGraph(algoList, repertory, fileName, "Local performance   -log scale-", abscisseTitle, "Local execution time (ns)", 2);
		plotAWholeGraph(algoList, repertory, fileName, "Remote performance   -log scale- ", abscisseTitle, "Remote execution time (ns)", 3);
		plotAWholeGraph(algoList, repertory, fileName, "Bandwidth    -log scale-", abscisseTitle, "Bandwidth (bytes)", 4);
		plotAWholeGraph(algoList, repertory, fileName, "Memory -log scale-", abscisseTitle, "Memory (bytes)", 5);
	}


	
	
	public static void plotAWholeGraph(ArrayList<String> algoList, File repertory, String fileName, String title, String xName, String yName, int k ) throws Exception{

		/*
		 * Trier les dossiers dans le bon ordre (que l'on ait bien 1000, 2000, 10000 et non pas 1000, 10000, 2000)
		 */
		ArrayList<String> fileList = listRepertoryContent( repertory, fileName);
		ArrayList<String> fileWithBadConvention = new ArrayList();
		ArrayList<Integer> sortList = new ArrayList<Integer>();
		ArrayList<String> fileListSorted= new ArrayList<String>();
		for (int i=0; i<fileList.size(); i++){
			try{
				sortList.add(Integer.parseInt(fileList.get(i).replace(fileName, "")));
			} catch(NumberFormatException e){
				fileWithBadConvention.add(fileList.get(i));
			} finally {
				
			}
		} 
		fileList.removeAll(fileWithBadConvention);
		Collections.sort(sortList);
		for (int j=0; j<sortList.size(); j++){
			for (int i=0; i<fileList.size(); i++){
				if (sortList.get(j)==Integer.parseInt(fileList.get(i).replace(fileName, ""))){
					fileListSorted.add(j, fileList.get(i));
				}
			}
		}
		fileList=fileListSorted;

		/*
		 * On trace les courbes
		 */
		ArrayList<double[][]> dataPlotList =new ArrayList<double[][]>();
		for (int i=0; i<algoList.size(); i++){
			double[][] tab = new double[fileList.size()][2];
			for (int j=0; j<fileList.size(); j++){
				double res = readFileToPlot(repertory.getAbsolutePath()+File.separator+
						fileList.get(j)+File.separator+fileList.get(j)+".csv", algoList.get(i), k);
				tab[j][0]= Double.parseDouble(fileList.get(j).replace(fileName,""));
				tab[j][1]= res;

			}
			dataPlotList.add(tab);
		}
		JavaPlot p = new JavaPlot();
		//p.set("term", "x11 persist");

		for (int i=0; i<dataPlotList.size();i++){
			double[][] dataPlot = dataPlotList.get(i);
			p=plotOneGraph(p, title, xName, yName, dataPlot, algoList.get(i) );
		}
		p.plot();

	}


	public static double readFileToPlot(String filePath, String targetLine, int targetRow) throws Exception{
		double res= -10;
		Scanner scanner=new Scanner(new File(filePath));
		String algoName="";
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			StringTokenizer st = new StringTokenizer(line, "	"); 

			if (st.hasMoreTokens()) algoName = st.nextToken().toString();
			if (st.hasMoreTokens() && algoName.equals(targetLine)){ 
				int i = 1;
				while (st.hasMoreTokens() && i!=targetRow){
					i++;
					st.nextToken();
				}
				res = Double.parseDouble(st.nextToken().replace(",","."));
				scanner.close();
				break;
			}
		}

		if (res==-10){
			throw new Exception("Value not find for :" + targetLine);
		}
		return res; 
	}


	public static ArrayList<String> listRepertoryContent(File repertoire , String nameFile){ 

		String [] listefichiers;
		ArrayList<String> list = new ArrayList<String>(); 
		int i; 
		listefichiers=repertoire.list(); 
		for(i=0;i<listefichiers.length;i++){ 
			if(listefichiers[i].startsWith(nameFile)){ 
				list.add(listefichiers[i]); 
			} 
		} 
		return list;
	}


	private static JavaPlot plotOneGraph(JavaPlot p, String title, String xName, String yName, double[][] dataPlot, String dataName){

		p.setTitle(title, "Arial", 14);
		p.getAxis("x").setLabel(xName, "Arial", 13);
		p.getAxis("y").setLabel(yName, "Arial", 13);
		p.getAxis("y").setLogScale(true);
		//p.getAxis("x").setBoundaries(0, xBound);
		//p.getAxis("y").setBoundaries(2.5,5.7);
		p.setKey(JavaPlot.Key.OUTSIDE);

		PlotStyle myPlotStyle = new PlotStyle();
		myPlotStyle.setStyle(Style.LINES);
		DataSetPlot s = new DataSetPlot(dataPlot);
		if (dataName=="RGATreeSplitBalanced") dataName = "RGATreeSplit";
		s.setTitle(dataName);
		myPlotStyle.setLineWidth(2);
		s.setPlotStyle(myPlotStyle);
		p.addPlot(s);
		return p;
	}
}
