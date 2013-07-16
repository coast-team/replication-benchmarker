package jbenchmarker.ot.soct4.common;

import java.io.Serializable;

public class Atom<V extends Serializable & Comparable<V>> implements
		Serializable, Comparable<Atom<V>> {

	private static final long serialVersionUID = 1L;
	private V atom;

	public Atom() {
	}

	public Atom(V atom) {
		this.atom = atom;
	}

	@Override
	public String toString() {
		return atom.toString();
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Atom)
			return this.atom.equals(((Atom) other).atom);
		else
			return false;
	}

	@Override
	public int compareTo(Atom<V> arg0) {
		return this.atom.compareTo(arg0.atom);
	}

}
