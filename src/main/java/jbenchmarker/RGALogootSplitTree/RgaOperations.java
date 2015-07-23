package jbenchmarker.RGALogootSplitTree;

import java.util.List;

public class RgaOperations {
	RgaSDocument doc;

	public RgaOperations(RgaSDocument doc){
		this.doc=doc;
	}


	public boolean remoteInsert(RgaSOperation op) {

		RgaSNode newnd = new RgaSNode(op.getNewNodeID(), op.getContent());

		RgaSNode node, next=null;
		RgaSNode nodeTree = null;

		if (op.getNodeIdPos() == null) {
			node = doc.getHead();

		} else {
			int offsetAbs = op.getOffset1()+op.getNodeIdPos().getBegin();
			node = (RgaSNode) doc.getHash().get(op.getNodeIdPos());

			if (node == null){
				return true;
			}
			
			/*if (node.getNext()!=null && new Identifier(node.getNext().getNodeID().getBase()).compareTo(new Identifier(newnd.getNodeID().getBase())) == -1){
				return true;
			}*/
			remoteSplit(node, offsetAbs);
		}

		nodeTree=node.getNext();
		doc.insertInLocalTree( nodeTree,newnd);

		next = node.getNext();
		newnd.setNext(next);
		node.setNext(newnd);

		doc.getHash().put(op.getNewNodeID(), newnd);
		newnd.getNodeID().getBaseFamily().getNodeList().add(newnd);
		doc.setSize(doc.getSize()+newnd.size());
		return false;
	}


	public boolean remoteDelete(RgaSOperation op) {
		int offsetAbs1 = op.getOffset1()+op.getNodeIdPos().getOffset()-1;
		int offsetRel1 = op.getOffset1()-1;
		int offsetAbs2 = op.getOffset2()+op.getNodeIdPos().getOffset();
		int offsetRel2 = op.getOffset2();

		RgaSNode node = (RgaSNode) doc.getHash().get(op.getNodeIdPos());
		if (node==null){
			return true;
		}

		if (offsetRel1>0){
			remoteSplit(node,offsetAbs1);
			node=node.getNext();
		}

		if (offsetRel2>0){
			remoteSplit(node,offsetAbs2);
			doc.setSize(doc.getSize()-node.size());
			doc.deleteInLocalTree(node);	
			makeTombstone(node);	
		}
		return false;
	}
	
	private void makeTombstone(RgaSNode node){
		node.getPrev().setNext(node.getNext());
		node.clone().getNodeID().getBaseFamily().getNodeList().remove(node);
		doc.getHash().remove(node);	
		node=null;
	}
	

	public void remoteSplit(RgaSNode node, int offsetAbs) {
		RgaSNode end=null;

		if (offsetAbs-node.getOffset()>0 && node.size()-offsetAbs+node.getOffset()>0){
			doc.getHash().remove(node);	
			List a = node.getContent().subList(0,offsetAbs-node.getOffset());
			List b = node.getContent().subList(offsetAbs-node.getOffset(),node.size());

			end = new RgaSNode(node.clone(), b, offsetAbs);
			end.setNext(node.getNext());


			node.setContent(a);
			node.getNodeID().setEnd(offsetAbs-1);
			node.setNext(end);
		
			node.clone().getNodeID().getBaseFamily().getNodeList().add(end);

			doc.getHash().put(node.getNodeID(), node);			
			doc.getHash().put(end.getNodeID(), end);	


			RgaSTree treeEnd = new RgaSTree(end, null, node.getTree().getRightSon());
			node.getTree().setRoot(node);
			node.getTree().setRightSon(treeEnd);

			RgaSTree newTree = treeEnd;
			doc.setNodeNumberInTree(doc.getNodeNumberInTree() + 1);
		}
	}

}
