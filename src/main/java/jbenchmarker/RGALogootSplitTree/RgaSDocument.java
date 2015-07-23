package jbenchmarker.RGALogootSplitTree;

import jbenchmarker.core.Document;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.RGALogootSplitTree.RgaSOperation;
import jbenchmarker.RGALogootSplitTree.RgaSS3Vector;
import jbenchmarker.RGALogootSplitTree.RgaSDocument.Position;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;




import java.util.NoSuchElementException;

import crdt.Operation;


public class RgaSDocument<T> implements Document {


	private HashMap<IdentifierInterval, RgaSNode> hash;
	public int getClock() {
		return clock;
	}


	public void setClock(int clock) {
		this.clock = clock;
	}

	public int incClock() {
		return this.clock++;
	}


	private int clock=0;
	private RgaOperations rga;
	private LogootOperations logoot;
	private RgaSNode head;
	private RgaSTree root;
	private int size = 0;
	private int nodeNumberInTree=0;
	private int nbOp=0;

	public RgaSDocument() {
		super();
		rga=new RgaOperations(this);
		logoot=new LogootOperations(this);
		head = new RgaSNode();
		hash=(new HashMap<IdentifierInterval, RgaSNode>());
	}


	/* Methods to apply each type of remote operations:
	 * 
	 * apply
	 * remoteInsert
	 * remoteDelete:
	 * remoteSplit
	 */

	public void apply(Operation op) {

		RgaSOperation rgaop = (RgaSOperation) op;
		boolean notFindInHashtable;
		if (rgaop.getType() == SequenceOperation.OpType.delete) {
			notFindInHashtable=rga.remoteDelete(rgaop);
			if (notFindInHashtable){
				logoot.remoteDelete(rgaop);
			}

		} else {
			notFindInHashtable=rga.remoteInsert(rgaop);
			if (notFindInHashtable){
				logoot.remoteInsert(rgaop);
			}
		}		
	}




	/* Methods to apply each type of local operations:
	 * 
	 * findPosInLocalTree
	 * insertInLocalTree
	 * deleteInLocalTree
	 */

	public Position findPosInLocalTree(int pos){	
		RgaSTree tree = root;
		//System.out.println("FIND :" + pos +", " +this.viewLength() );
		if (pos<=0 || root == null){
			return new Position(null, 0);
		} 
		else if (pos>this.viewLength()){

			tree=findMostRight(tree,0);
			return new Position(tree.getRoot(), tree.getRoot().size());
		}
		else {
			while (!(tree.size()-tree.getRightSize()-tree.getRoot().size()< pos && pos <= tree.size()-tree.getRightSize())){
				if (pos<=tree.size()-tree.getRightSize()-tree.getRoot().size()){
					tree=tree.getLeftSon();
				}

				else {
					pos-=tree.getLeftSize()+tree.getRoot().size();
					tree=tree.getRightSon();
				}
			}	
			return new Position(tree.getRoot(), pos-(tree.size()-tree.getRightSize()-tree.getRoot().size()));
		}
	}

	public RgaOperations getRga() {
		return rga;
	}


	public void insertInLocalTree(RgaSNode nodePos, RgaSNode newnd){
		RgaSTree tree = (nodePos== null) ? null : nodePos.getTree();
		RgaSTree newTree = new RgaSTree(newnd, null, null);

		if (root==null || (nodePos!=null && nodePos.equals(head))){
			if (root==null)	root=newTree;
			else if (root.getLeftSon()==null) root.setLeftSon(newTree);
			else findMostRight(root.getLeftSon(), 0).setRightSon(newTree);

		} else if (nodePos==null){
			findMostRight(root, 0).setRightSon(newTree);

		} else {
			if (tree.getLeftSon()== null) tree.setLeftSon(newTree);
			else findMostRight(tree.getLeftSon(),0).setRightSon(newTree);
		}

		newTree=newTree.getFather();
		int i=1;
		while (newTree!=null){ // add the size of the inserted node in all fathers and grandfathers
			newTree.setSize(newTree.size()+newnd.size());
			newTree=newTree.getFather();
			i++;
		}
		setNodeNumberInTree(getNodeNumberInTree() + 1);
		nbOp++;
		/*
		if (nbOp >(getNodeNumberInTree())/(0.14*Math.log(getNodeNumberInTree())/Math.log(2))){

		//if (sumHeight/ nodeNumberInTree > 2.44*Math.log(nodeNumberInTree+1)/Math.log(2)){
			//System.out.println("I'm come in! " + nodeNumberInTree +", " + nbOp);
			nbOp=0;
			//System.out.println("Before balanced: "+ (int) (checkTreeDepth(root,0)+1) + ", " + ((float)checkTreeAverageDepth(root,0)) / nodeNumberInTree+ ", "+ Math.log(nodeNumberInTree+1)/Math.log(2)+ ", "+ nodeNumberInTree);
			List<RgaSNode> content = createNodeList(new ArrayList(), getRoot());
			createBalancedTree(new RgaSTree(), content,  0, content.size());
			addGoodSize(getRoot());
			//System.out.println("After balanced: "+ (int) (checkTreeDepth(root,0)+1) + ", " + ((float)checkTreeAverageDepth(root,0)) / nodeNumberInTree+ ", "+ Math.log(nodeNumberInTree+1)/Math.log(2)+ ", "+ nodeNumberInTree);
			//System.out.println();
		}
		//}
		 */

	}


	public void deleteInLocalTree(RgaSNode nodeDel){
		RgaSTree tree = nodeDel.getTree(), father = null;
		boolean isRoot = (tree.equals(this.root)) ? true : false;
		boolean hasRightSon = (tree.getRightSon()!=null) ? true: false;
		boolean hasLeftSon = (tree.getLeftSon()!=null) ? true: false;
		boolean isLeaf = (!hasRightSon && !hasLeftSon) ? true: false;
		boolean isLeftSon = false;

		if (!isRoot){
			father = tree.getFather();
			if (father.getLeftSon()==null);
			else if (father.getLeftSon().equals(tree)) isLeftSon = true;
		}

		if (isRoot){    // if the tree is the root, so...
			if (isLeaf) root = null;
			else if (hasLeftSon && !hasRightSon) root=root.getLeftSon();
			else if (!hasLeftSon && hasRightSon) root=root.getRightSon();
			else {
				findMostLeft(tree.getRightSon(), root.getLeftSon().size()).setLeftSon(root.getLeftSon());
				root=root.getRightSon();
			}

		} else if (isLeaf){     // else if it is a leaf, so...
			if (isLeftSon)father.setLeftSon(null);
			else father.setRightSon(null);

		} else {   // else ...
			if (!hasRightSon){
				if (isLeftSon) father.setLeftSon(tree.getLeftSon());
				else father.setRightSon(tree.getLeftSon());

			} else if (!hasLeftSon){
				if (isLeftSon) father.setLeftSon(tree.getRightSon());
				else father.setRightSon(tree.getRightSon());

			} else {
				RgaSTree tree2 = findMostLeft(tree.getRightSon(), tree.getLeftSon().size());
				tree2.setLeftSon(tree.getLeftSon());
				if (isLeftSon) father.setLeftSon(tree.getRightSon());
				else father.setRightSon(tree.getRightSon());
			}
		}

		tree=null;
		setNodeNumberInTree(getNodeNumberInTree() - 1);
		int i =1;
		while (father!=null){  // soutract the size of the deleted node in all fathers and grandfathers 
			father.setSize(father.size()-nodeDel.size());
			father=father.getFather();
			i++;
		}
	}


	/* Methods to display the view of the document
	 * 
	 *  view(): normal view of the document, without separator between each node
	 *  viewWithSeparator(): view with separators between each node for debugging
	 *  treeView(): normal view of the local tree, without separator between each node
	 *  treeViewWithSeparator(): view of the local tree with separators between each node for debugging
	 */



	public String viewChain() {
		String s = new String();
		RgaSNode node = head.getNext();
		while (node != null) {

			for (int i=0; i<node.size();i++)
				s+=node.getContent().get(i);

			node = node.getNext();
		}
		return s.toString();
	}

	@Override
	public String view() {
		return treeView(new StringBuilder(),root);
	}

	public String viewChainWithSeparator() {
		StringBuilder s = new StringBuilder();
		StringBuilder a = new StringBuilder();
		RgaSNode node = head.getNext();
		while (node != null) {

			a = new StringBuilder();
			for (int i=0; i<node.size();i++){
				a.append(node.getContent().get(i));
			}
			s.append("->|"+a+"|");

			node = node.getNext();
		}
		return s.toString();
	}

	public void treeViewWithSeparator(RgaSTree tree, int profondeur){

		if (tree!=null){
			if (tree.getLeftSon()!=null) treeViewWithSeparator(tree.getLeftSon(),profondeur + 1);
			for (int i=0; i < profondeur; i++){
				System.out.print("   ");
			}

			System.out.println("-->"+ tree.getRoot().getContentAsString()+", " +tree.size());

			if (tree.getRightSon()!=null) treeViewWithSeparator(tree.getRightSon(),profondeur + 1);
		}
	}

	public String treeView(StringBuilder buf,RgaSTree tree){
		if (tree!=null){
			if (tree.getLeftSon()!=null) treeView(buf,tree.getLeftSon());
			buf.append(tree.getRoot().getContentAsString());
			if (tree.getRightSon()!=null) treeView(buf,tree.getRightSon());
		}
		return buf.toString();
	}


	/* 
	 * 
	 *  other methods used in local and remote operations
	 */




	public RgaSTree findMostLeft(RgaSTree tree, int i){

		while (tree.getLeftSon()!=null){
			tree.setSize(tree.size()+i);
			tree=tree.getLeftSon();
		}
		tree.setSize(tree.size()+i);
		return tree;
	}

	public RgaSTree findMostRight(RgaSTree tree, int i){

		while (tree.getRightSon()!=null){
			tree.setSize(tree.size()+i);
			tree=tree.getRightSon();
		}
		tree.setSize(tree.size()+i);
		return tree;
	}

	protected class Position {
		protected RgaSNode node;
		protected int offset;

		public Position(RgaSNode n, int offset) {
			this.node= n;
			this.offset = offset;

		}

		public String toString(){
			return "[" + node + "," + offset  +"]"; 
		}
	}

	public Position getPosition(RgaSNode node, int start){
		Position pos;
		int i = node.size();

		while (node != null && i <= start ) {
			node = node.getNext();
			if (node!=null) {
				i+=node.size();
			}
		}

		if (node!=null){
			return pos = new Position(node, node.size() - i + start);
		} else {
			return pos = new Position(null, 0);
		}
	}

	public HashMap<IdentifierInterval, RgaSNode> getHash() {
		return hash;
	}


	public RgaSNode getHead(){
		return head;
	}

	@Override
	public int viewLength() {

		return size;
	}

	public int getSize() {
		return size;
	}


	public void setSize(int size) {
		this.size = size;
	}


	public RgaSTree getRoot() {
		return root;
	}


	public List createNodeList(List list, RgaSTree tree){
		if (tree!=null){
			if (tree.getLeftSon()!=null){
				createNodeList(list, tree.getLeftSon());
			}

			list.add(tree.getRoot());


			if (tree.getRightSon()!=null){
				createNodeList(list, tree.getRightSon());
			}
			tree=null;
		}
		return list;

	}

	public void createBalancedTree(RgaSTree tree, final List<RgaSNode> content, final int begin, final int length) {

		if (tree!=null && !content.isEmpty()){

			final int leftSubtree = (length - 1) / 2 ;
			final int rightSubtree = length -1 - leftSubtree;

			if (leftSubtree > 0) {
				final RgaSTree leftChildren = new RgaSTree();
				tree.setLeftSon(leftChildren);
				createBalancedTree(leftChildren, content, begin, leftSubtree);
			}


			content.get(begin + leftSubtree).setTree(tree);
			tree.setRoot(content.get(begin + leftSubtree));
			tree.setSize(0);


			if (rightSubtree > 0) {
				final RgaSTree rightChildren = new RgaSTree();
				tree.setRightSon(rightChildren);
				createBalancedTree(rightChildren, content, begin + leftSubtree + 1, rightSubtree);	
			}
			root = tree;
		}

	}


	public void addGoodSize(RgaSTree tree){
		if (tree!=null){
			if (tree.getLeftSon()!=null){
				addGoodSize(tree.getLeftSon());
				tree.setSize(tree.size()+tree.getLeftSize());
			}

			tree.setSize(tree.size()+tree.getRootSize());

			if (tree.getRightSon()!=null){
				addGoodSize(tree.getRightSon());
				tree.setSize(tree.size()+tree.getRightSize());
			}
		}
	}

	private int checkTreeDepth(RgaSTree tree, int height) {
		int hright = (tree.getRightSon()== null) ? -1 : checkTreeDepth(tree.getRightSon(), height);
		int hleft = (tree.getLeftSon() == null ? -1 : checkTreeDepth(tree.getLeftSon(),height));
		height=Math.max(hright, hleft)+1;
		return height;
	}

	private int checkTreeAverageDepth(RgaSTree tree, int height) {
		if (tree!=null){
			if (tree.getLeftSon()!=null){
				height+=treeDepth(tree.getLeftSon());
				height=checkTreeAverageDepth(tree.getLeftSon(), height);

			}
			height++;
			if (tree.getRightSon()!=null){
				height+=treeDepth(tree.getRightSon());
				height=checkTreeAverageDepth(tree.getRightSon(),height);

			}
		}
		return height;
	}

	private int treeDepth(RgaSTree tree){
		int i=0;
		while (!tree.equals(root)){
			i++;
			tree=tree.getFather();
		}
		return i;
	}


	public int getNodeNumberInTree() {
		return nodeNumberInTree;
	}


	public void setNodeNumberInTree(int nodeNumberInTree) {
		this.nodeNumberInTree = nodeNumberInTree;
	}


	public void setHead(RgaSNode head) {
		this.head = head;
	}



}