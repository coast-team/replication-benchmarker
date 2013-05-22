/*
 *  Replication Benchmarker
 *  https://github.com/score-team/replication-benchmarker/
 *  Copyright (C) 2013 LORIA / Inria / SCORE Team
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package jbenchmarker.logootsplitO;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LIDFactory{
    
    
    static public LIdentifier createBetweenPosition(LIdentifier l1,LIdentifier l2, int siteId, int clock){
        
        
        l1.setDefaultValue(Integer.MIN_VALUE);
        l2.setDefaultValue(Integer.MAX_VALUE);
        
        Iterator<Triple> it1=l1.iterator();
        Iterator<Triple> it2=l2.iterator();
        
        List<Triple> l=new ArrayList<Triple>();
        
        Triple t1=it1.next();
        Triple t2=it2.next();
        
        long d = t2.getPosition()-t1.getPosition();
        while(d<=1){
            l.add(t1);
            t1=it1.next();
            t2=it2.next();
            d=t2.getPosition()-t1.getPosition();
        }
        int p =(int)(Math.random()*(d-1))+1+t1.getPosition();
        l.add(new Triple(p, siteId, 0));
        
        return new LIdentifier(l, clock);
    }
}