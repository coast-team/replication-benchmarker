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
 * This block kind contain no elements only ids.
 * The elements are on view
 * @author Stephane Martin <stephane@stephanemartin.fr>
 */
public class LogootSBlockLight<T> extends LogootSBlock<T> implements Serializable{

    int nbElement=0;
    public LogootSBlockLight(IdentifierInterval id, int list) {
        super(id);
        nbElement=list;
    }

    public LogootSBlockLight(IdentifierInterval id) {
        super(id);
    }

    public LogootSBlockLight() {
    }

    @Override
    List<T> getElements(int begin, int end) {
        throw new UnsupportedOperationException("Version light contains no data");
        
    }

    @Override
    void addBlock(int pos, List<T> contains) {
        nbElement+=contains.size();
        this.getId().begin=Math.min(this.getId().begin, pos);
        this.getId().end=Math.max(this.getId().end, pos+contains.size()-1);
        
    }
    

    @Override
    void delBlock(int begin, int end,int nbElement) {
       this.nbElement-=nbElement;
    }

    @Override
    T getElement(int i) {
        throw new UnsupportedOperationException("Version light contains no data");
    }

    @Override
    int numberOfElements() {
        return nbElement;
    }

    
}
