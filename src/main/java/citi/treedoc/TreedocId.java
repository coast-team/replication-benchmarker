/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/
 * Copyright (C) 2011 UNL / INRIA
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */ 
package citi.treedoc;

import java.util.*;
import java.io.*;

public class TreedocId
	implements IIdentifier, Serializable, Comparable<IIdentifier>
{
	BitSet bs;
	TreedocDisambiguator last;
	Disambiguators[] l;
	
	TreedocId( TreedocDisambiguator last) {
		bs = new BitSet();
		bs.set( 0);
		this.last = last;
		l = null;
	}
	
	public TreedocId( TreedocId id) {
		bs = new BitSet();
		bs.or( id.bs);
		this.last = id.last;
		if( id.l == null)
			l = id.l;
		else {
			l = new Disambiguators[id.l.length];
			System.arraycopy( id.l, 0, l, 0, l.length);
		}
	}

	TreedocId( TreedocId id, TreedocDisambiguator last) {
		bs = new BitSet();
		bs.or( id.bs);
		this.last = last;
		if( id.l == null)
			l = id.l;
		else {
			l = new Disambiguators[id.l.length];
			System.arraycopy( id.l, 0, l, 0, l.length);
		}
	}
	
	
	@Override
	public int size() {
		int acum = bs.length() + last.size();
		if( l != null)
		for( int i = 0 ; i < l.length; i++ ) 
			acum = acum + l[i].size();
		return acum;
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append( bs);
		buffer.append("-");
		if( l != null)
		for( int i = 0; i < l.length; i++) {
			TreedocDisambiguator e = l[i].d;
			buffer.append( "(" + e.p + "," + e.s + ")");
		}
		buffer.append( "(" + last.p + "," + last.s + ")");
		return buffer.toString();
	}

	@Override
	public int compareTo( IIdentifier elem0) {
		//TODO: consider disambiguators
		TreedocId elem = (TreedocId)elem0;
		int lenThis = bs.length() - 1;
		int lenElem = elem.bs.length() - 1;
		for( int i = 0; ; i++) {
			if( i == lenThis && i == lenElem) {
				if( last.p < elem.last.p || (last.p == elem.last.p && last.s < elem.last.s))
					return -1;
				else if( last.p == elem.last.p && last.s == elem.last.s)
					return 0;
				else
					return 1;
			}
			if( i == lenThis) {
				if( elem.bs.get( i))
					return -1;
				else
					return 1;
			}
			if( i == lenElem) {
				if( bs.get( i))
					return 1;
				else
					return -1;
			}
			if( elem.bs.get( i) == bs.get( i))
				continue;
			if( bs.get( i))
				return 1;
			else
				return -1;
		}
	}
}

