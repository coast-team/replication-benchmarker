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

public class Plot {

	public static void main(String[] args) throws Exception {
		String fileName = "TraceSizeBlock";
		String abscisseTitle = "size of block";
		File repertory= new File(System.getProperty("user.dir")+ File.separator+"ResultTest");

		plotAWholeGraph(repertory, fileName, "Average execution time by " +abscisseTitle, abscisseTitle, "Average execution time (ms)", 1);
		plotAWholeGraph(repertory, fileName, "Local execution time by " +abscisseTitle, abscisseTitle, "Local execution time (ns)", 2);
		plotAWholeGraph(repertory, fileName, "Remote execution time by " +abscisseTitle, abscisseTitle, "Remote execution time (ns)", 3);
		plotAWholeGraph(repertory, fileName, "Bandwidth by " +abscisseTitle, abscisseTitle, "Bandwidth (bytes)", 4);
		plotAWholeGraph(repertory, fileName, "Memory by " +abscisseTitle, abscisseTitle, "Memory (bytes)", 5);

	}



	public static void plotAWholeGraph(File repertory, String fileName, String title, String xName, String yName, int k ) throws Exception{

		ArrayList<String> fileList = listRepertoryContent( repertory, fileName);
		ArrayList<Integer> sortList = new ArrayList<Integer>();
		ArrayList<String> fileListSorted= new ArrayList<String>();


		for (int i=0; i<fileList.size(); i++){
			sortList.add(Integer.parseInt(fileList.get(i).replace(fileName, "")));
		}

		Collections.sort(sortList);


		for (int j=0; j<sortList.size(); j++){
			for (int i=0; i<fileList.size(); i++){
				if (sortList.get(j)==Integer.parseInt(fileList.get(i).replace(fileName, ""))){
					fileListSorted.add(j, fileList.get(i));
				}
			}
		}

		fileList=fileListSorted;



		ArrayList<String> algoList = new ArrayList<String>();
		algoList.add("LogootSplitAVL");
		algoList.add("RGA");
		algoList.add("RGAF");
		algoList.add("RGATreeList");
		algoList.add("RgaS");
		algoList.add("RgaTreeSplitBalanced");
		algoList.add("Treedoc");
	


		ArrayList<double[][]> dataPlotList =new ArrayList<double[][]>();
		for (int i=0; i<algoList.size(); i++){
			double[][] tab = new double[fileList.size()][2];
			for (int j=0; j<fileList.size(); j++){
				double res = readFileToPlot(repertory.getAbsolutePath()+File.separator+
						fileList.get(j)+File.separator+fileList.get(j)+".csv", algoList.get(i), k);
				tab[j][0]= Double.parseDouble(fileList.get(j).replace(fileName,""));
				tab[j][1]= Math.log(res)/Math.log(10);

			}
			dataPlotList.add(tab);
		}
		System.out.println(fileList);
		System.out.println(dataPlotList.get(1)[1][0]);


		JavaPlot p = new JavaPlot();
		p.set("term", "x11 persist");
		//p.setPersist(false);
		/*ImageTerminal png = new ImageTerminal();
		p.setTerminal(png);*/

		for (int i=0; i<dataPlotList.size();i++){
			double[][] dataPlot = dataPlotList.get(i);
			p=plotOneGraph(p, title, xName, yName, dataPlot, algoList.get(i) );
		}



		p.plot();
		
/*
		File file = new File(repertory.getAbsoluteFile() + File.separator + title +"_"+fileName+".png" );
		try {
			file.createNewFile();
			png.processOutput(new FileInputStream(file));
		} catch (FileNotFoundException ex) {
			System.err.print(ex);
		} catch (IOException ex) {
			System.err.print(ex);
		}
*/
	
		
		
		
	/*	
		try {
			ImageIO.write(png.getImage(), "png", file);
		} catch (IOException ex) {
			System.err.print(ex);
		}*/
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

		p.setTitle(title, "Arial", 27);
		p.getAxis("x").setLabel(xName, "Arial", 18);
		p.getAxis("y").setLabel(yName, "Arial", 18);

		//p.getAxis("x").setBoundaries(0, xBound);
		//p.getAxis("y").setBoundaries(0, yBound);
		p.setKey(JavaPlot.Key.OUTSIDE);


		PlotStyle myPlotStyle = new PlotStyle();
		myPlotStyle.setStyle(Style.LINES);
		DataSetPlot s = new DataSetPlot(dataPlot);
		s.setTitle(dataName);
		myPlotStyle.setLineWidth(1);
		s.setPlotStyle(myPlotStyle);
		p.addPlot(s);
		return p;
	}
}
