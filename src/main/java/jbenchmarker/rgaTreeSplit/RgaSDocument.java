package jbenchmarker.rgaTreeSplit;

import jbenchmarker.core.Document;
import jbenchmarker.core.SequenceOperation;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;





import crdt.Operation;


public class RgaSDocument<T> implements Document {

	private HashMap<RgaSS3Vector, RgaSNode> hash;
	private RgaSNode head;
	private RgaSTree root;
	private int size = 0;



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

		if (op.getS3vpos() == null) {
			node = head;

		} else {
			int offsetAbs = op.getOffset1()+op.getS3vpos().getOffset()+1;  // du au -1 du get position
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

		newnd.setNext(next);
		node.setNext(newnd);
		hash.put(op.getS3vtms(), newnd);
		size+=newnd.size();
	}

	private void remoteDelete(RgaSOperation op) {

		int offsetAbs1 = op.getOffset1()+op.getS3vpos().getOffset();
		int offsetRel1 = op.getOffset1();

		int offsetAbs2 = op.getOffset2()+op.getS3vpos().getOffset();
		int offsetRel2 = op.getOffset2();

		RgaSNode node = hash.get(op.getS3vpos());
		node=findGoodNode(node, offsetAbs1);

		if (offsetRel1>0){
			remoteSplit(node,offsetAbs1);
			node=node.getLink();
		}

		while (node.getOffset() + node.size() < offsetAbs2){
			if (node.isVisible()) size-=node.size();
			node.makeTombstone();
			node=node.getLink();
		}

		if (offsetRel2>0){
			remoteSplit(node,offsetAbs2);
			if (node.isVisible()) size-=node.size();
			node.makeTombstone();	
		}
	}

	public void remoteSplit(RgaSNode node, int offsetAbs) {

		if (offsetAbs-node.getOffset()>0 && node.size()-offsetAbs+node.getOffset()>0){

			List<T> a= null;
			List<T> b = null;
			
			if (node.isVisible()){
				a = node.getContent().subList(0,offsetAbs-node.getOffset());
				b = node.getContent().subList(offsetAbs-node.getOffset(),node.size());
			}
			
			RgaSNode end = new RgaSNode(node.clone(), b, offsetAbs);
			end.setSize(node.size()-offsetAbs+node.getOffset());
			end.setNext(node.getNext());

			node.setContent(a);
			node.setSize(offsetAbs-node.getOffset());
			node.setNext(end);
			node.setLink(end);

			hash.put(node.getKey(), node);			
			hash.put(end.getKey(), end);	
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




}