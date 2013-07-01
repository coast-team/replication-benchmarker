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
import java.util.List;

/**
 *
 * @author Stephane Martin <stephane@stephanemartin.fr>
 */
public abstract class LogootSBlock<T> implements Serializable{
    // List<ElementList> elements=new ArrayList();
     IdentifierInterval id;

     boolean mine=false;

    public boolean isMine() {
        return mine;
    }

    public void setMine(boolean mine) {
        this.mine = mine;
    }
     
    public LogootSBlock(IdentifierInterval id) {
        this.id = id;
    }

    public LogootSBlock() {
    }
   
    abstract int numberOfElements();
     
     abstract List<T> getElements(int begin,int end);
     abstract T getElement(int i);
     abstract void addBlock (int pos, List<T> contains);
     abstract void delBlock (int begin, int end,int nbElement);
     IdentifierInterval getId(){
         return id;
     }
}
