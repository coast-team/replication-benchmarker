/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/
 * Copyright (C) 2012 LORIA / Inria / SCORE Team
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

public class Treedoc<Id extends IIdentifier,X>
{
	protected List<Id> ids;
	protected List<X> data;
	protected IdFactory<Id> factory;
	
	public Treedoc( IdFactory<Id> factory) {
		ids = new ArrayList<Id>();
		data = new ArrayList<X>(); 
		this.factory = factory; 
	}
	
	public int size() {
		return ids.size(); 
	}
	
	/**
	 * Inserts list of atoms d into the position pos of the data array
	 */
	public List<Id> insert( int pos, int n, List<X> d, int siteId) {
		List<Id> newId = null;
		if( ids.size() == 0) {
			newId = factory.createNew( null, null, n, siteId);
		} else if( pos == 0) {
			newId = factory.createNew( null, ids.get( pos), n, siteId);
		} else if( pos == ids.size()) {
			newId = factory.createNew( ids.get( pos-1), null, n, siteId);
		} else
			newId = factory.createNew( ids.get(pos-1), ids.get( pos), n, siteId);
		ids.addAll( pos, newId);
		data.addAll( pos, d);
		return newId;
	}

	/**
	 * Inserts atom d into the position pos of the data array
	 */
	public Id insert( int pos, X d, int siteId) {
		Id newId = null;
		if( ids.size() == 0) {
			newId = factory.createNew( null, null, siteId);
		} else if( pos == 0) {
			newId = factory.createNew( null, ids.get( pos), siteId);
		} else if( pos == ids.size()) {
			newId = factory.createNew( ids.get( pos-1), null, siteId);
		} else
			newId = factory.createNew( ids.get(pos-1), ids.get( pos), siteId);
		ids.add( pos, newId);
		data.add( pos, d);
		return newId;
	}
	
	/**
	 * Inserts atom d with Id id
	 */
	public int insert( Id newId, X d) {
		int pos = - Collections.binarySearch( ids, newId) - 1;
		if( pos < 0)
			throw new RuntimeException( "Inserting with an existing identifier");
		ids.add( pos, newId);
		data.add( pos, d);
		return pos;
	}
	
	/**
	 * Deletes atom at position pos
	 */
	public Id remove( int pos) {
		Id id = ids.remove( pos);
		data.remove( pos);
		return id;
	}
	
	public void printIdStatistics() {
		long idsize = 0;
		int minid = Integer.MAX_VALUE;
		int maxid = Integer.MIN_VALUE;
		long textsize = 0;

		for( int i = 0; i < ids.size(); i++) {
			Id id = ids.get( i);
			int s = id.size();
			idsize += s;
			if( minid > s)
				minid = s;
			if( maxid < s)
				maxid = s;
			
			String text = (String)data.get( i);
			textsize = textsize + (text == null ? 0 : text.length());
		}
		
		System.out.println( "Text size (bytes) = " + textsize);
		System.out.println( "Text size assumes data are strings");
		System.out.println( "minid (bits) = " + minid);
		System.out.println( "maxid (bits) = " + maxid);
		System.out.println( "avgid (bits) = " + (idsize / ids.size()));
		
		System.out.println( "total overhead (bits) = " + idsize);
	}
	
	public void printIds() {
		for( int i = 0; i < ids.size(); i++) {
			Id id = ids.get( i);
			System.out.println( id);
		}
	}
	
}
