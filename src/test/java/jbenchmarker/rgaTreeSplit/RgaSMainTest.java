package jbenchmarker.rgaTreeSplit;

import java.util.ArrayList;
import jbenchmarker.factories.RgaTreeSplitFactory;
import java.util.List;

import crdt.simulator.IncorrectTraceException;
import jbenchmarker.core.*;
import jbenchmarker.core.SequenceOperation.OpType;


public class RgaSMainTest {


	static List<String> input(String A){
		List<String> a= new ArrayList<String>();
		for (int i=0; i<A.length(); i++){
			a.add((A.substring(i,i+1)));	
		}
		return a;			
	}

	static void apply(RgaSDocument rgadoc, SequenceOperation so, RgaSMerge merge, boolean bool) throws IncorrectTraceException{
		if (so.getType()==OpType.insert){
			merge.localInsert(so);
		}
		else if (so.getType()==OpType.delete){
			merge.localDelete(so);
		}
		if (bool){
			//System.out.println("HASH : " +rgadoc.getHash());
			//System.out.println(rgadoc.viewWithSeparator());
			System.out.println(rgadoc.viewLength());
			System.out.println(rgadoc.view());
			rgadoc.viewTree(rgadoc.getRoot());
			System.out.println("\n");
			rgadoc.viewTreeWithSeparator(rgadoc.getRoot(),0);
			System.out.println("\n\n");
		}
	}

	public static void main(String[] args) throws IncorrectTraceException {
		RgaSDocument rgadoc = new RgaSDocument();
		RgaSMerge merge0 = new RgaSMerge(rgadoc,0); 
		RgaSMerge merge1 = new RgaSMerge(rgadoc,1);

		/*
		List<String> a= input("abcdefghij");
		List<String> b= input("28");
		List<String> c= input("73");
		List<String> d= input("test");


		SequenceOperation so1 = new SequenceOperation (OpType.insert, 0, 0, a);
		SequenceOperation so2 = new SequenceOperation (OpType.insert, 2, 0, b);
		SequenceOperation so3 = new SequenceOperation (OpType.insert, 10, 0, c);
		SequenceOperation so4 = new SequenceOperation (OpType.delete, 3, 8, c);

		
		apply(rgadoc, so1, merge0, true);
		apply(rgadoc, so2, merge0, true);
		apply(rgadoc, so3, merge0, true);
		apply(rgadoc, so4, merge0, true);
*/

		List<String> a= input("aaaaaaaaaaaaaaa");
		List<String> b= input("bbbbbbbbbbbbbb");
		List<String> c= input("ccccccccccccc");
		List<String> d= input("dddddddddddd");
		List<String> e= input("eeeeeeeeeee");
		List<String> f= input("ffffffffff");
		List<String> g= input("ggggggggg");
		List<String> h= input("hhhhhhhh");
		List<String> i= input("iiiiiii");
		List<String> j= input("jjjjjj");
		List<String> k= input("kkkkk");
		List<String> l= input("llll");
		List<String> m= input("mmm");
		List<String> n= input("nn");
		List<String> o= input("o");


		SequenceOperation so1  = new SequenceOperation (OpType.insert, 0, 0, a);
		SequenceOperation so2  = new SequenceOperation (OpType.insert, 2, 0, b);
		SequenceOperation so3  = new SequenceOperation (OpType.insert, 7, 0, c);
		SequenceOperation so4  = new SequenceOperation (OpType.insert, 0, 0, d);
		SequenceOperation so5  = new SequenceOperation (OpType.insert, 54, 0, e);
		SequenceOperation so6  = new SequenceOperation (OpType.insert, 7,0, f);
		SequenceOperation so7  = new SequenceOperation (OpType.insert, 30, 0, g);
		SequenceOperation so8  = new SequenceOperation (OpType.insert, 9,0, h);
		SequenceOperation so9  = new SequenceOperation (OpType.insert, 2, 0, i);
		SequenceOperation so10 = new SequenceOperation (OpType.insert, 7, 0, j);
		SequenceOperation so11 = new SequenceOperation (OpType.insert, 0, 0, k);
		SequenceOperation so12 = new SequenceOperation (OpType.insert, 90, 0, l);
		SequenceOperation so13 = new SequenceOperation (OpType.insert, 7,0, m);
		SequenceOperation so14 = new SequenceOperation (OpType.insert, 30, 0, n);
		SequenceOperation so15 = new SequenceOperation (OpType.insert, 9,0, o);
		SequenceOperation so16 = new SequenceOperation (OpType.delete, 0,3, null);
		SequenceOperation so17 = new SequenceOperation (OpType.delete, 0,10, null);
		SequenceOperation so18 = new SequenceOperation (OpType.delete, 10,20, null);
		SequenceOperation so19 = new SequenceOperation (OpType.delete, 11,1, null);
		SequenceOperation so20 = new SequenceOperation (OpType.delete, 10,20, null);
		SequenceOperation so21 = new SequenceOperation (OpType.delete, 11,1, null);
		SequenceOperation so22 = new SequenceOperation (OpType.delete, 1,1, null);
		SequenceOperation so23 = new SequenceOperation (OpType.delete, 0,9, null);
		SequenceOperation so24 = new SequenceOperation (OpType.delete, 40,3, null);
		SequenceOperation so25 = new SequenceOperation (OpType.delete, 20,32, null);



		apply(rgadoc, so1, merge0, true);
		apply(rgadoc, so2, merge0, true);
		apply(rgadoc, so3, merge0, true);
		apply(rgadoc, so4, merge0, true);
		apply(rgadoc, so5, merge0, true);
		apply(rgadoc, so6, merge0, true);
		apply(rgadoc, so7, merge0, true);
		apply(rgadoc, so8, merge0, true);
		apply(rgadoc, so9, merge0, true);
		apply(rgadoc, so10, merge0, true);
		apply(rgadoc, so11, merge0, true);
		apply(rgadoc, so12, merge0, true);
		apply(rgadoc, so13, merge0, true);
		apply(rgadoc, so14, merge0, true);
		apply(rgadoc, so15, merge0, true);
		apply(rgadoc, so16, merge0, true);
		apply(rgadoc, so17, merge0, true);
		apply(rgadoc, so18, merge0, true);
		apply(rgadoc, so19, merge0, true);
		apply(rgadoc, so20, merge0, true);
		apply(rgadoc, so21, merge0, true);
		apply(rgadoc, so22, merge0, true);
		apply(rgadoc, so23, merge0, true);
		apply(rgadoc, so24, merge0, true);
		apply(rgadoc, so25, merge0, true);

	}

}