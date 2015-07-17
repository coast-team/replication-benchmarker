package jbenchmarker.rgaTreeSplitBalanced;

import jbenchmarker.core.Document;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.rgaTreeSplitBalanced.RgaSOperation;
import jbenchmarker.rgaTreeSplitBalanced.RgaSS3Vector;
import jbenchmarker.rgaTreeSplitBalanced.RgaSDocument.Position;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;




import java.util.NoSuchElementException;

import crdt.Operation;


public class RgaSDocument<T> implements Document {


	private HashMap<RgaSS3Vector, RgaSNode> hash;
	private RgaSNode head;
	private RgaSTree root;
	private int size = 0;
	private int nodeNumberInTree=0;
	private int nbOp=0;

	public RgaSDocument() {
		super();
		head = new RgaSNode();
		hash=(new HashMap<RgaSS3Vector, RgaSNode>());
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

		if (rgaop.getType() == SequenceOperation.OpType.delete) {
			remoteDelete(rgaop);
		} else {
			remoteInsert(rgaop);
		}		
	}

	private void remoteInsert(RgaSOperation op) {
		RgaSNode newnd = new RgaSNode(op.getS3vtms(), op.getContent());
		RgaSNode node, next=null;
		RgaSS3Vector s3v = op.getS3vtms();
		RgaSNode nodeTree = null;

		if (op.getS3vpos() == null) {
			node = head;

		} else {
			int offsetAbs = op.getOffset1()+op.getS3vpos().getOffset();
			node = hash.get(op.getS3vpos());
			node = findGoodNode(node, offsetAbs);
			remoteSplit(node, offsetAbs);
		}

		next = node.getNext();
		while (next!=null) {
			if (s3v.compareTo(next.getKey()) == RgaSS3Vector.AFTER) {
				break;
			}
			node = next;
			next = next.getNext();
		}

		nodeTree=node.getNextVisible();
		insertInLocalTree( nodeTree,newnd);

		newnd.setNext(next);
		node.setNext(newnd);
		hash.put(op.getS3vtms(), newnd);
		size+=newnd.size();
	}


	private void remoteDelete(RgaSOperation op) {
		int offsetAbs1 = op.getOffset1()+op.getS3vpos().getOffset()-1;
		int offsetRel1 = op.getOffset1()-1;
		int offsetAbs2 = op.getOffset2()+op.getS3vpos().getOffset();
		int offsetRel2 = op.getOffset2();

		RgaSNode node = hash.get(op.getS3vpos());
		node=findGoodNode(node, offsetAbs1);

		if (offsetRel1>0){
			remoteSplit(node,offsetAbs1);
			node=node.getLink();
		}

		while (node.getOffset() + node.size() < offsetAbs2){
			if (node.isVisible()){
				size-=node.size();
				deleteInLocalTree(node);
			}
			node.makeTombstone();
			node=node.getLink();
		}

		if (offsetRel2>0){
			remoteSplit(node,offsetAbs2);
			if (node.isVisible()){
				size-=node.size();
				deleteInLocalTree(node);	
			}
			node.makeTombstone();	
		}
	}

	public void remoteSplit(RgaSNode node, int offsetAbs) {
		RgaSNode end=null;
		if (offsetAbs-node.getOffset()>0 && node.size()-offsetAbs+node.getOffset()>0){

			List<T> a= null;
			List<T> b = null;

			if (node.isVisible()){
				a = node.getContent().subList(0,offsetAbs-node.getOffset());
				b = node.getContent().subList(offsetAbs-node.getOffset(),node.size());
			}

			end = new RgaSNode(node.clone(), b, offsetAbs);
			end.setSize(node.size()-offsetAbs+node.getOffset());
			end.setNext(node.getNext());

			node.setContent(a);
			node.setSize(offsetAbs-node.getOffset());
			node.setNext(end);
			node.setLink(end);

			hash.put(node.getKey(), node);			
			hash.put(end.getKey(), end);	

			if (node.isVisible()){
				RgaSTree treeEnd = new RgaSTree(end, null, node.getTree().getRightSon());
				node.getTree().setRoot(node);
				node.getTree().setRightSon(treeEnd);
				nodeNumberInTree++;
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
		int POS = pos;
		if (pos<=0 || root == null){
			return new Position(null, 0);
		} 
		else if (pos>=this.viewLength()){
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
		while (newTree!=null){ // add the size of the inserted node in all fathers and grandfathers
			newTree.setSize(newTree.size()+newnd.size());
			newTree=newTree.getFather();
		}
		nodeNumberInTree++;
		nbOp++;
		
		if (nbOp >(3*nodeNumberInTree+1)/(0.44*Math.log(nodeNumberInTree+1)/Math.log(2))){
			//System.out.println("I'm come in! " + nodeNumberInTree +", " + nbOp);
			nbOp=0;
			List<RgaSNode> content = createNodeList(new ArrayList(), getRoot());
			createBalancedTree(new RgaSTree(), content,  0, content.size());
			addGoodSize(getRoot());
		}
	}
/*
	public void insertInLocalTree(RgaSNode nodePos, RgaSNode newnd){
		RgaSTree tree = (nodePos== null) ? null : nodePos.getTree();
		RgaSTree newTree = new RgaSTree(newnd, null, null);

		if (root==null || (nodePos!=null && nodePos.equals(head))){
			if (root==null)	root=newTree;
			else findMostLeft(root, 0).setLeftSon(newTree);

		} else if (nodePos==null){
			findMostRight(root, 0).setRightSon(newTree);

		} else {
			if (tree.getRightSon()== null) tree.setRightSon(newTree);
			else findMostLeft(tree.getRightSon(),0).setLeftSon(newTree);
		}

		newTree=newTree.getFather();
		while (newTree!=null){ // add the size of the inserted node in all fathers and grandfathers
			newTree.setSize(newTree.size()+newnd.size());
			newTree=newTree.getFather();
		}
		nodeNumberInTree++;
		nbOp++;
		
		if (nbOp >(3*nodeNumberInTree+1)/(0.44*Math.log(nodeNumberInTree+1)/Math.log(2))){
			//System.out.println("I'm come in! " + nodeNumberInTree +", " + nbOp);
			nbOp=0;
			List<RgaSNode> content = createNodeList(new ArrayList(), getRoot());
			createBalancedTree(new RgaSTree(), content,  0, content.size());
			addGoodSize(getRoot());
		}
	}*/

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
		nodeNumberInTree--;

		while (father!=null){  // soutract the size of the deleted node in all fathers and grandfathers 
			father.setSize(father.size()-nodeDel.size());
			father=father.getFather();
		}
	}


	/* Methods to display the view of the document
	 * 
	 *  view(): normal view of the document, without separator between each node
	 *  viewWithSeparator(): view with separators between each node for debugging
	 *  treeView(): normal view of the local tree, without separator between each node
	 *  treeViewWithSeparator(): view of the local tree with separators between each node for debugging
	 */

	/*
	@Override
	public String view() {
		String s = new String();
		RgaSNode node = head.getNextVisible();
		while (node != null) {
			if (node.isVisible() && node.getContent()!=null) {
				for (int i=0; i<node.size();i++)
					s+=node.getContent().get(i);
			}
			node = node.getNextVisible();
		}
		return s.toString();
	}*/
	
	@Override
	public String view() {
		return treeView(new StringBuilder(),root);
	}

	public String viewWithSeparator() {
		StringBuilder s = new StringBuilder();
		StringBuilder a = new StringBuilder();
		RgaSNode node = head.getNext();
		while (node != null) {
			if (node.isVisible() && node.getContent()!=null) {
				a = new StringBuilder();
				for (int i=0; i<node.size();i++){
					a.append(node.getContent().get(i));
				}
				s.append("->|"+a+"|");
			}
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

	public RgaSNode findGoodNode(RgaSNode target, int off){

		while (target.getOffset() + target.size() < off){
			target=target.getLink();
		}
		return target;
	}


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
			node = node.getNextVisible();
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

	public HashMap<RgaSS3Vector, RgaSNode> getHash() {
		return hash;
	}


	public RgaSNode getHead(){
		return head;
	}

	@Override
	public int viewLength() {

		return size;
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
}