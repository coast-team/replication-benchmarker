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
package jbenchmarker.wootr;

import java.util.ArrayList;
import jbenchmarker.core.Document;
import jbenchmarker.core.Operation;
import jbenchmarker.core.SequenceOperation;

/**
 * WOOTR document
 * @author urso
 */
public abstract class WootRDocument implements Document {
    final protected ArrayList<WootRNode> elements;

    public WootRDocument(WootRNode CB, WootRNode CE) {
        super();
        elements = new ArrayList<WootRNode>();
        elements.add(CB);
        elements.add(CE);
    }

    public String view() {
        StringBuilder s = new StringBuilder();
        for (WootRNode w : elements) {
            s.append(w.getContent());
        }
        return s.toString();
    }
    

    public void apply(Operation op) {
        WootROperation wop = (WootROperation) op;
        WootRNode e = wop.getNode();
        if (wop.getType() == SequenceOperation.OpType.del) {
            elements.remove(e);
        } else {             
            elements.add(find(e), e);
        }        
    }
    
    /*
     * Returns the position where the node should be 
     */
    public int find(WootRNode e){
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
