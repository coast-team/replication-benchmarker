package jbenchmarker.rgaTreeSplit;

import jbenchmarker.core.Document;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.treedoc.TreedocNode;
import jbenchmarker.treedoc.TreedocIdentifier.EdgeDirection;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;














import crdt.Operation;


public class RgaSDocument<T> implements Document {

	private HashMap<RgaSS3Vector, RgaSNode> hash;
	private RgaSNode head;
	private RgaSTree root;
	private int size = 0;

	private int nodeNumberInTree=0;
	private int height = 0;




	public RgaSDocument() {
		super();
		head = new RgaSNode();
		hash=(new HashMap<RgaSS3Vector, RgaSNode>());
	}



	/* Methods to apply each type of remote operations:
	 * 
	 * Insertion, Deletion && Split
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
			int offsetAbs = op.getOffset1()+op.getS3vpos().getOffset();  // du au -1 du get position
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

		nodeTree=node;
		if(!node.equals(head) && !node.isVisible()) nodeTree=nodeTree.getNextVisible();
		insert(op.getPos(), nodeTree,newnd);


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
				delete(node);
				size-=node.size();
			}
			node.makeTombstone();
			node=node.getLink();
		}

		if (offsetRel2>0){
			remoteSplit(node,offsetAbs2);
			if (node.isVisible()){
				delete(node);
				size-=node.size();
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



	/* Methods to display the view of the document
	 * 
	 *  view(): normal view of the document, without separator between each node
	 *  viewWithSeparator(): view with separators between each node for debugging
	 */

	@Override
	public String view() {
		StringBuilder s = new StringBuilder();
		RgaSNode node = head.getNext();
		while (node != null) {
			if (node.isVisible() && node.getContent()!=null) {
				for (int i=0; i<node.size();i++)
					s.append(node.getContent().get(i));
			}
			node = node.getNext();
		}
		return s.toString();
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






	// Obtenir des objets de la classe Position

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




	public RgaSNode findGoodNode(RgaSNode target, int off){

		while (target.getOffset() + target.size() < off){
			target=target.getLink();
		}

		return target;
	}






	
	
	/*
	 * 
	 *   TREE
	 *   
	 */


	public Position find(int pos){
		
		RgaSTree tree = root;

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



	public void insert(int pos, RgaSNode nodePos, RgaSNode newnd){

		RgaSTree tree = null;
		RgaSTree newTree = null;
		int i =1;

		if (pos==0 || nodePos==null || nodePos.equals(head)){
			if (root==null){
				newTree = new RgaSTree(newnd, null, null);
				root=newTree;
			}
			else{
				tree=findMostLeft(root, 0);
				newTree = new RgaSTree(newnd, null, null);
				tree.setLeftSon(newTree);
			}
		}
		else if (pos == this.viewLength()){
			tree = nodePos.getTree();
			tree=findMostRight(root, 0);
			newTree = new RgaSTree(newnd, null, null);
			tree.setRightSon(newTree);
		}
		else {
			tree = nodePos.getTree();
			if (tree.getRightSon()== null){
				newTree = new RgaSTree(newnd, null, null);
				tree.setRightSon(newTree);

			} else {
				tree=tree.getRightSon();
				tree=findMostLeft(tree,0);
				newTree = new RgaSTree(newnd, null, null);
				tree.setLeftSon(newTree);
			}
		}


		while (newTree.getFather()!=null){
			i++;
			newTree=newTree.getFather();
			newTree.setSize(newTree.size()+newnd.size());
		}
		
		nodeNumberInTree++;
		if (i>height) height=i;
		
		if (-height>100* Math.log(nodeNumberInTree)){
			List<RgaSNode> content = createNodeList(new ArrayList(), getRoot());
			createBalancedTree(getRoot(), content,  0, content.size());
			addGoodSize(getRoot());
		}

	}

	public void delete(RgaSNode nodeDel){

		int sizeDel = nodeDel.size();
		boolean isLeftSon = false;
		boolean isLeaf = false;
		boolean isRoot = false;
		boolean hasRightSon = false;
		boolean hasLeftSon = false;

		RgaSTree tree = nodeDel.getTree();
		RgaSTree father = null;

		if (tree.equals(this.root)) isRoot = true;
		if (tree.getRightSon()!=null) hasRightSon = true;
		if (tree.getLeftSon()!=null) hasLeftSon = true;
		if (!hasLeftSon && ! hasRightSon) isLeaf=true;
		if (!isRoot){
			father = tree.getFather();
			if (father.getLeftSon()==null);
			else if (father.getLeftSon().equals(tree)) isLeftSon = true;
		}

		if (isRoot){
			if (isLeaf) root = null;
			else if (hasLeftSon && !hasRightSon) root=root.getLeftSon();
			else if (!hasLeftSon && hasRightSon) root=root.getRightSon();
			else {
				tree=findMostLeft(tree.getRightSon(), root.getLeftSon().size());
				tree.setLeftSon(root.getLeftSon());
				root=root.getRightSon();
			}
		}

		else if (isLeaf){
			if (isLeftSon)father.setLeftSon(null);
			else father.setRightSon(null);


		}else{

			if (!hasRightSon){
				if (isLeftSon) father.setLeftSon(tree.getLeftSon());
				else father.setRightSon(tree.getLeftSon());

			} else if (!hasLeftSon){
				if (isLeftSon) father.setLeftSon(tree.getRightSon());
				else father.setRightSon(tree.getRightSon());

			} else {

				RgaSTree Tree = findMostLeft(tree.getRightSon(), tree.getLeftSon().size());
				Tree.setLeftSon(tree.getLeftSon());
				if (isLeftSon) father.setLeftSon(tree.getRightSon());
				else father.setRightSon(tree.getRightSon());
			}
		}

		int i = 1;
		if (father!=null){
			while (father.getFather()!=null){
				i++;
				father.setSize(father.size()-sizeDel);
				father=father.getFather();
			}
			father.setSize(father.size()-sizeDel);
		}

		if (i==height) height--;
		nodeNumberInTree--;
	}

	
	public List createNodeList(List list, RgaSTree tree){

		if (tree.getLeftSon()!=null){
			createNodeList(list, tree.getLeftSon());
		}
		list.add(tree.getRoot());
		if (tree.getRightSon()!=null){
			createNodeList(list, tree.getRightSon());
		}
		return list;
	}

	public void createBalancedTree(RgaSTree tree, final List<RgaSNode> content, final int begin, final int length) {

		this.height=0;
		// Invariant: leftSubtree + rightSubtree + 1 = length
		final int leftSubtree = (length - 1) / 2;
		final int rightSubtree = length - 1 - leftSubtree;

		if (leftSubtree > 0) {
			final RgaSNode leftChild = new RgaSNode();
			final RgaSTree leftChildren = new RgaSTree(leftChild, null, null);
			tree.setLeftSon(leftChildren);
			createBalancedTree(leftChildren, content, begin, leftSubtree);
		}
		tree.setRoot(content.get(begin + leftSubtree));
		content.get(begin + leftSubtree).setTree(tree);
		tree.setSize(0);
		if (rightSubtree > 0) {
			final RgaSNode rightChild = new RgaSNode();
			final RgaSTree rightChildren = new RgaSTree(rightChild, null, null);
			tree.setRightSon(rightChildren);
			createBalancedTree(rightChildren, content, begin + leftSubtree + 1, rightSubtree);	
		}
		tree=root;
	}


	public void addGoodSize(RgaSTree tree){

		if (tree.getLeftSon()!=null){
			addGoodSize(tree.getLeftSon());
			tree.setSize(tree.size()+tree.getLeftSon().size());
		}
		tree.setSize(tree.size()+tree.getRoot().size());

		if (tree.getRightSon()!=null){
			addGoodSize( tree.getRightSon());
			tree.setSize(tree.size()+tree.getRightSon().size());
		}

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

	public void viewTreeWithSeparator(RgaSTree tree, int profondeur){

		if (tree!=null){
			if (tree.getLeftSon()!=null) viewTreeWithSeparator(tree.getLeftSon(),profondeur + 1);
			for (int i=0; i < profondeur; i++){
				System.out.print("   ");
			}

			System.out.println("-->"+ tree.getRoot().getContentAsString()+", " +tree.size());

			if (tree.getRightSon()!=null) viewTreeWithSeparator(tree.getRightSon(),profondeur + 1);
		}
	}



	public void viewTree(RgaSTree tree){
		if (tree!=null){
			if (tree.getLeftSon()!=null) viewTree(tree.getLeftSon());
			System.out.print(tree.getRoot().getContentAsString());
			if (tree.getRightSon()!=null) viewTree(tree.getRightSon());
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

	public void setRoot(RgaSTree root) {
		this.root = root;
	}
	

	public int getNodeNumberInTree() {
		return nodeNumberInTree;
	}






	public void setNodeNumberInTree(int nodeNumberInTree) {
		this.nodeNumberInTree = nodeNumberInTree;
	}






	public int getHeight() {
		return height;
	}






	public void setHeight(int height) {
		this.height = height;
	}






}