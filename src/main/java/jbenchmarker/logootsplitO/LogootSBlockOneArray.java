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
import java.util.List;

/**
 *
 * @author Stephane Martin <stephane@stephanemartin.fr>
 */
public class LogootSBlockOneArray<T> extends LogootSBlock<T> {

    //ArrayList<T> data = new ArrayList<T>();
    int nbElement=0;
    public LogootSBlockOneArray(IdentifierInterval id, ArrayList list) {
        super(id);
       // this.data = list;
        nbElement=list.size();
    }

    public LogootSBlockOneArray(IdentifierInterval id) {
        super(id);
    }

    public LogootSBlockOneArray() {
    }

    @Override
    List<T> getElements(int begin, int end) {
        return null; // data.subList(begin, end);
    }

    @Override
    void addBlock(int pos, List<T> contains) {
        /*if (pos >= id.begin) {
            this.getId().end += contains.size();
            data.addAll(pos - getId().begin, contains);
        } else {
            this.getId().begin -= contains.size();
            data.addAll(0, contains);
        }*/
        nbElement+=contains.size();
    }
    

    @Override
    void delBlock(int begin, int end,int nbElement) {
       nbElement-=nbElement;
    }

    @Override
    T getElement(int i) {
        return null;//data.get(i);
    }

    @Override
    int numberOfElements() {
        return nbElement;
    }

    
}
