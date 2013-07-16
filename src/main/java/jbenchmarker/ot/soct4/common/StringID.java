package jbenchmarker.ot.soct4.common;

//TODO: Abstract Identifier

public class StringID implements Comparable<StringID>, ID {

	private String siteID;

	public StringID() {
	}

	public StringID(String siteID) {
		this.siteID = siteID;
	}

	public StringID clone() {
		return new StringID(this.siteID);
	}

	@Override
	public String toString() {
		return siteID;
	}

	@Override
	public int compareTo(StringID o) {
		return this.siteID.compareTo(o.siteID);
	}

	@Override
	public boolean equals(Object o) {
		return this.siteID.compareTo(((StringID) o).siteID) == 0;
	}

}
