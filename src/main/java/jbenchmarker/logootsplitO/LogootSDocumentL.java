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

import java.util.LinkedList;
import java.util.List;
import jbenchmarker.core.Document;
import jbenchmarker.core.Operation;

/**
 *
 * @author Stephane Martin <stephane@stephanemartin.fr>
 */
public class LogootSDocumentL implements Document{
    List<ElementList> list=new LinkedList();
    class ElementList {
         int begin;
         int end;
         LogootSBlock block;
         public LogootSBlock getBlock(){
             return block;
         }
         /*public List<T> getValues(){
             return getBlock().getElements(begin, end);
         }*/

        public int getBegin() {
            return begin;
        }

        public void setBegin(int begin) {
            this.begin = begin;
        }

        public int getEnd() {
            return end;
        }

        public void setEnd(int end) {
            this.end = end;
        }
         
     }
    int size=0;
    @Override
    public String view() {
        //todo: improve the complexity with innerstate
        StringBuilder ret=new StringBuilder();
        for(ElementList e: list){
         //  ret.append(e.getValues());
        }
        return ret.toString();
    }
    

    @Override
    public int viewLength() {
        return size;
    }

    @Override
    public void apply(Operation op) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
