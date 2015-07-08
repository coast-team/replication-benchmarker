package jbenchmarker.rgaTreeSplitBalanced;

public class RgaSTree {

	private RgaSNode root;
	private RgaSTree father;
	private RgaSTree leftSon;
	private RgaSTree rightSon;
	private int size;
	
	
	RgaSTree (){
		this.root=root;
		this.setLeftSon(leftSon);
		this.setRightSon(rightSon);
		this.size=size;
	}
	
	
	RgaSTree (RgaSNode root, RgaSTree leftSon, RgaSTree rightSon, int size){
		this.root=root;
		this.root.setTree(this);
		this.setLeftSon(leftSon);
		this.setRightSon(rightSon);
		this.size=size;

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

	public RgaSTree clone(){
		return new RgaSTree(root, leftSon, rightSon, size);
	}

	public RgaSTree getFather() {
		return father;
	}

	public void setFather(RgaSTree father) {
		this.father = father;
	}

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
		if (leftSon!=null) leftSon.setFather(this);
	}

	public RgaSTree getRightSon() {
		return rightSon;
	}

	public void setRightSon(RgaSTree rightSon) {
		this.rightSon = rightSon;
		if (rightSon!=null) rightSon.setFather(this);
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((leftSon == null) ? 0 : leftSon.hashCode());
		result = prime * result
				+ ((rightSon == null) ? 0 : rightSon.hashCode());
		result = prime * result + ((root == null) ? 0 : root.hashCode());
		return result;
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RgaSTree other = (RgaSTree) obj;
		if (leftSon == null) {
			if (other.leftSon != null)
				return false;
		} else if (!leftSon.equals(other.leftSon))
			return false;
		if (rightSon == null) {
			if (other.rightSon != null)
				return false;
		} else if (!rightSon.equals(other.rightSon))
			return false;
		if (root == null) {
			if (other.root != null)
				return false;
		} else if (!root.equals(other.root))
			return false;
		return true;
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
