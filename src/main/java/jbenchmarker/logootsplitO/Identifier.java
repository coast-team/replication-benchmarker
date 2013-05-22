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

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Stephane Martin <stephane@stephanemartin.fr>
 */
public class Identifier implements Comparable,Iterable, Serializable {

    List<Integer> base;
    Integer last;
    /**
     * -1 this< t 0 this=t 1 this > t 
     * @param t
     * @return 
     */
    public Identifier(List<Integer> base) {
        this.base = base;
    }

    public Identifier(List<Integer> base, Integer u) {
        this.base = base;
        this.last = u;
    }

    
    @Override
    public int compareTo(Object t) {
        if(t instanceof Identifier){
            return compareTo(this.iterator(),((Identifier)t).iterator());
        }
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    /**
     * -1 this.last< t.i, 0 this.last=t.i 1, this.last > t.i
     * @param last
     * @param t
     * @param i
     * @return 
     */
    /* public int compareTo(Integer u, Identifier t,Integer i){
        Iterator <Integer> s1=new Iterator_a(this.base.iterator(),u);
        Iterator <Integer> s2=new Iterator_a(t.base.iterator(),i);
        return compareTo(s1,s2);
    }
     public int compareTo(Integer u,List<Integer> t,Integer i){
        Iterator <Integer> s1=new Iterator_a(this.base.iterator(),u);
        Iterator <Integer> s2=new Iterator_a(t.iterator(),i);
        return compareTo(s1,s2);
    }
    public static int compareTo(List <Integer>l1,Integer u1,List <Integer> l2,Integer u2){
        Iterator <Integer> s1=new Iterator_a(l1.iterator(),u1);
        Iterator <Integer> s2=new Iterator_a(l2.iterator(),u2);
        return compareTo(s1,s2);
    }
    
    public int compareTo(Identifier t,Integer i){
        Iterator <Integer> s1=this.base.iterator();
        Iterator <Integer> s2=new Iterator_a(t.base.iterator(),i);
        return compareTo(s1,s2);
    }*/
    
    /**
     * -1 this< t.i 0 this=t.i 1 this > t.i
     * @param i
     * @param offset
     * @return 
     */
    public static int compareTo(Iterator <Integer> s1,Iterator <Integer> s2){
         
        
        while (s1.hasNext() && s2.hasNext()) {
            int b1 = s1.next();
            int b2 = s2.next();
            if (b1 < b2) {
                return -1;
            }
            if (b1 > b2) {
                return 1;
            }
        }
        /* S1 is longer than s2*/
        if (s1.hasNext()) {
            return 1;
        }
        /* S2 is longer than s1*/
        if (s2.hasNext()) {
            return -1;
        }
        // Both have same size
        return 0;
        
    }

    public List<Integer> getBase() {
        return base;
    }

   

    public Integer getLast() {
        return last;
    }

    

    @Override
    public Iterator iterator() {
       return new Iterator_a(base.iterator(),this.last);
    }
    static class Iterator_a implements Iterator {

        public Iterator_a(Iterator it, Object more) {
            this.it = it;
            this.more = more;
            loadNext();
        }
        
        Iterator it;
        Object more;
        Object nexte;
        
        private void loadNext(){
            if(it.hasNext()){
                nexte=it.next();
            }else{
                nexte=more;
                more=null;
            }
        }
        
        @Override
        public boolean hasNext() {
           return nexte!=null;
        }

        @Override
        public Object next() {
            Object ret=nexte;
            loadNext();
            return ret;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported");
        }
    }

    @Override
    public String toString() {
        return "Identifiant{" + base + "," + last + '}';
    }
    
}
