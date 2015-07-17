package jbenchmarker.rgaTreeSplitBalanced;

import java.io.Serializable;
import java.util.List;


public class RgaSNode<T> implements Serializable {

	private RgaSS3Vector key;		 
	private RgaSNode next;
	private RgaSNode link;
	private RgaSTree tree;
	
	public RgaSTree getTree() {
		return tree;
	}

	public void setTree(RgaSTree tree) {
		this.tree = tree;
	}

	private List<T> content;
	private int size;
	private boolean tomb;	//used for visible and tombstone purging if null, then not tombstone 




	/*
	 *		Constructors
	 */

	public RgaSNode(RgaSS3Vector key, RgaSNode next, RgaSNode link, List<T> c, boolean tomb, RgaSTree tree) {
		this.key = key;
		this.next = next;
		this.link = link;
		this.content = c;
		if (content!=null) this.size = c.size();
		else this.size=0;
		this.tomb = tomb;
		this.tree=tree;
	}

	public RgaSNode() {
		this(null, null, null, null, true,null);
	}

	public RgaSNode(RgaSS3Vector s3v, List<T> c) {
		this(s3v, null, null, c, false,null);
	}

	public RgaSNode(RgaSNode n, List<T> c, int offset) {
		this(n.key.clone(), n.next, n.link, c, n.tomb, n.tree);
		this.key.setOffset(offset);
	}

	public RgaSNode clone(){
		return new RgaSNode(key, next, link, content, tomb, tree);
	}



	
	/*
	 *		toString, getContentAsString, equals, makeTombstone, getNextVisible, getLinkVisible and hashCode
	 */

	@Override
	public String toString() {
		String Next = next == null ? "null" : next.getKey().toString();
		String Link = link == null ? "null" : link.getKey().toString();
		return "[" + key + "," + Next + ","+ Link + ","+ tomb +","  + content + "," + size + "]";  
	}

	public String getContentAsString() {
		StringBuilder s = new StringBuilder();
		if (content!=null){
			for (T t : content) {

				s.append(t.toString());
			}
		}
		return s.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final RgaSNode other = (RgaSNode) obj;
		if (this.key != other.key && (this.key == null || !this.key.equals(other.key))) {
			return false;
		}
		return true;
	}

	public void makeTombstone() {
		this.tomb = true;
		this.content = null;
		this.tree=null;
	}

	public RgaSNode getNextVisible() {
		RgaSNode node = next;
		while (node != null && !node.isVisible()) {
			node = node.getNext();
		}
		return node;
	}
	
	public RgaSNode getLinkVisible() {
		RgaSNode node = next;
		while (node != null && !node.isVisible()) {
			node = node.getLink();
		}
		return node;
		
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 89 * hash + (this.key != null ? this.key.hashCode() : 0);
		return hash;
	}



	/*
	 *		Getters || Setters
	 */

	public RgaSS3Vector getKey() {
		return key;
	}

	public void setKey(RgaSS3Vector key) {
		this.key = key;
	}
	
	public int getOffset() {
		if (this.key!=null)	return key.getOffset();
		else return 0;
	}
	
	public void setOffset(int off) {
		if (this.key!=null) this.key.setOffset(off);
	}

	public RgaSNode getNext() {
		return next;
	}

	public void setNext(RgaSNode next) {
		this.next = next;
	}

	public RgaSNode getLink() {
		return link;
	}

	public void setLink(RgaSNode link) {
		this.link = link;
	}

	public List<T> getContent() {
		return content;
	}

	public void setContent(List<T> content) {
		this.content = content;
	}

	public int size() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public boolean isVisible() {
		return !tomb;
	}

	public void setTomb(boolean tomb) {
		this.tomb = tomb;
	}

}
