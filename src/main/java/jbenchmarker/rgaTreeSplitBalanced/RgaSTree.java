package jbenchmarker.rgaTreeSplitBalanced;

import jbenchmarker.rgaTreeSplitBalanced.RgaSNode;

public class RgaSTree {


	private RgaSNode root;

	private RgaSTree father;
	private RgaSTree leftSon;
	private RgaSTree rightSon;

	private int size;



	/*
	 *		Constructors
	 */

	RgaSTree (){


		this.root=null;
		this.setLeftSon(null);
		this.setRightSon(null);
	}

	RgaSTree (RgaSNode root, RgaSTree leftSon, RgaSTree rightSon){
		int a = 0;
		int b = 0;

		this.root=root;
		this.root.setTree(this);
		this.setLeftSon(leftSon);
		this.setRightSon(rightSon);
		if (leftSon!=null) a=leftSon.size();
		if (rightSon!=null) b=rightSon.size();
		size= a+b+this.getRoot().size();
	}


	/*
	 *		Getters & Setters
	 */

	public int getRootSize(){
		if (!this.getRoot().isVisible()){
			return 0;
		} else {
			return this.getRoot().size();
		}
	}
	public RgaSNode getRoot() {
		return root;
	}

	public void setRoot(RgaSNode root) {
		this.root = root;
	}


	public RgaSTree getFather() {
		return father;
	}

	public void setFather(RgaSTree father) {
		this.father = father;
	}	

	public RgaSTree getLeftSon() {
		return leftSon;
	}

	public void setLeftSon(RgaSTree leftSon) {
		this.leftSon = leftSon;
		if (leftSon!=null) leftSon.setFather(this);
	}

	public RgaSTree getRightSon() {
		return rightSon;
	}

	public void setRightSon(RgaSTree rightSon) {
		this.rightSon = rightSon;
		if (rightSon!=null) rightSon.setFather(this);
	}

	public int size() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getRightSize(){
		if (this.getRightSon()==null) return 0;
		else return this.getRightSon().size();
	}

	public int getLeftSize(){
		if (this.getLeftSon()==null) return 0;
		else return this.getLeftSon().size();
	}

}
