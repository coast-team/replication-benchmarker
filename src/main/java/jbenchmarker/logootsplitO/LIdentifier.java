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


public class LIdentifier implements Comparable, Iterable{
    
    private List<Triple> list;
    private int clock;
    private int defaultValue;
    
    public LIdentifier(List<Triple> l,int c){
        list=l;
        clock=c;
        defaultValue=Integer.MIN_VALUE;
    }

    LIdentifier(LIdentifier id, int i){
        list=new ArrayList<Triple>(id.list);
        Triple triple=list.get(list.size()-1);
        triple=new Triple(triple,i);
        list.set(list.size()-1, triple);
        this.clock=id.clock;
    }
    
    public List<Triple> getList(){
        return list;
    }
    
    public void setDefaultValue(int v){
        defaultValue=v;
    }
    
    public int getDefaultValue(){
        return defaultValue;
    }
    

    @Override
    public int compareTo(Object o) {
        if(o instanceof LIdentifier){
            LIdentifier other=(LIdentifier)o;
            Iterator it1=this.iterator();
            Iterator it2=other.iterator();
            while(it1.hasNext() && it2.hasNext()){
                Triple t1=(Triple)it1.next();
                Triple t2=(Triple)it2.next();
                int result=t1.compareTo(t2);
                if (result!=0){
                    return result; 
                }
            }
            if(it1.hasNext()){
                return 1;
            }
            if(it2.hasNext()){
                return -1;
            }
            int result=this.clock-other.clock;
            if(result==0){
                return result;
            }
            return result/Math.abs(result);
        }
        return 1;
    }

    @Override
    public Iterator iterator() {
        return new LIterator(this);
    }
    
    
}
class Triple implements Comparable{
    
    private int position;
    private int siteId;
    private int offset;
    
    Triple(int p,int s, int o){
        position=p;
        siteId=s;
        offset=o;
    }
    
    Triple(Triple other, int o){
        this.position=other.position;
        this.siteId=other.siteId;
        this.offset=o;   
    }
    
    public void setOffset(int o){
        offset=o;
    }
    
    public int getPosition(){
        return position;
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof Triple){
            Triple other=(Triple)o;
            if(this.position!=other.position){
                return (this.position-other.position)/Math.abs(this.position-other.position);
            }
            if(this.siteId!=other.siteId){
                return (this.siteId-other.siteId)/Math.abs(this.siteId-other.siteId);
            }
            if(this.offset!=other.offset){
                return (this.offset-other.offset)/Math.abs(this.offset-other.offset);
            }
            return 0;
        }
        return 1;
    }
}

class LIterator implements Iterator{
    
    private List<Triple> list;
    private int i;
    private int defaultValue;

    LIterator(LIdentifier id){
        list=id.getList();
        i=0;
        defaultValue=id.getDefaultValue();
    }
    
    @Override
    public boolean hasNext() {
        return (i<list.size());
    }

    @Override
    public Object next() {
        if (hasNext()){
            return list.get(i++);
        }
        return new Triple(defaultValue,0,0);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}