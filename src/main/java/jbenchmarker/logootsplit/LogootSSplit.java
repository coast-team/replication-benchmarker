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
package jbenchmarker.logootsplit;

import jbenchmarker.core.SequenceMessage;
import jbenchmarker.core.SequenceOperation;

public class LogootSSplit extends SequenceMessage implements LogootSOperation{
    
    private LogootSElement element;
    private int offset;
    

    public LogootSSplit(SequenceOperation so, LogootSElement el, int offset) {
        super(so);
        this.element=el;
        this.offset=offset;
        
    }

    @Override
    public void apply(LogootSDocument doc) {
        LogootSElement el = this.element.origin();
        LogootSIdentifier id = el.getIdAt(el.size() - 1);
        int elOffset = this.element.getIdAt(this.element.size() - 1).getOffset();
        int maxpeer = this.offset + elOffset;
        int i = 0;
        while (i < maxpeer) {
            int index=doc.IndexOf(el, false);
            if (index==-1) {
                i++;
                id.setOffset(i);
            } else {
                if (i + doc.get(index).length() < maxpeer) {//split after this content
                    i = i + doc.get(index).length();
                    id.setOffset(i);
                } else {
                    if (i + doc.get(index).length() == maxpeer) {//split already done
                        i = maxpeer;
                    } else {//split in this content
                        String s=doc.get(index);
                        String ns=s.substring(maxpeer-i);
                        LogootSElement el2=new LogootSElement(el, maxpeer-i);
                        doc.delete(index, maxpeer-i, s.length());
                        doc.add(el2, ns);
                    }
                }
            }
        }
    }

    @Override
    public SequenceMessage clone() {
        return new LogootSSplit(this.getOriginalOp(), element.clone(), offset);
    }

    
}