package jbenchmarker.rgaTreeSplit;

public class RgaSTree {
	
	private RgaSNode root;
	private RgaSTree leftSon;
	private RgaSTree rigthSon;
	private int size;
	
	RgaSTree (RgaSNode root, RgaSTree leftSon, RgaSTree rigthSon){
		this.root=root;
		this.leftSon=leftSon;
		this.rigthSon=rigthSon;
		
	}
	
	public RgaSTree findMostLeft(RgaSTree tree){
		while (tree.leftSon!=null){
			tree=tree.leftSon;
		}
		return tree;
	}
	
	public RgaSTree findMostRigth(RgaSTree tree){
		while (tree.rigthSon!=null){
			tree=tree.rigthSon;
		}
		return tree;
	}
	
	public String 
	
	public RgaSNode getRoot() {
		return root;
	}
	
	public void setRoot(RgaSNode root) {
		this.root = root;
	}
	
	public RgaSTree getLeftSon() {
		return leftSon;
	}
	
	public void setLeftSon(RgaSTree leftSon) {
		this.leftSon = leftSon;
	}
	
	public RgaSTree getRigthSon() {
		return rigthSon;
	}
	
	public void setRigthSon(RgaSTree rigthSon) {
		this.rigthSon = rigthSon;
	}
	
	public int size() {
		return size;
	}
	
	public void setSize(int size) {
		this.size = size;
	}
	
}
