package jbenchmarker.RGALogootSplitTree;

import java.util.List;

public class LogootBaseFamily {
	@Override
	public String toString() {
		return ""+base ;
	}


	private List<Integer> base;
	private List<RgaSNode> nodeList;
	
	public List<Integer> getBase() {
		return base;
	}
	public void setBase(List<Integer> base) {
		this.base = base;
	}
	public List<RgaSNode> getNodeList() {
		return nodeList;
	}
	public void setNodeList(List<RgaSNode> nodeList) {
		this.nodeList = nodeList;
	}
	
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((base == null) ? 0 : base.hashCode());
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
		LogootBaseFamily other = (LogootBaseFamily) obj;
		if (base == null) {
			if (other.base != null)
				return false;
		} else if (!base.equals(other.base))
			return false;
		return true;
	}
	
	
	public LogootBaseFamily(List<Integer> base, List<RgaSNode> nodeList) {
		super();
		this.base = base;
		this.nodeList = nodeList;
	}
	
	
}
