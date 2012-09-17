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

public class LogootSDeletion extends SequenceMessage implements LogootSOperation{
    
    private LogootSElement element;
    private int start;
    private int end;


    public LogootSDeletion(SequenceOperation so, LogootSElement el, int start, int end) {
        super(so);
        this.start=start;
        this.end=end;
        this.element=el;
    }

    @Override
    public SequenceMessage clone() {
        return new LogootSDeletion(this.getOriginalOp(), element.clone(), start, end);
    }

    @Override
    public void apply(LogootSDocument doc) {
        LogootSElement el = this.element.origin();
        LogootSIdentifier id = el.getIdAt(el.size() - 1);
        int offset = this.element.getIdAt(this.element.size() - 1).getOffset();
        int maxpeer = offset + this.end;
        int i = 0;
        while (i < maxpeer) {
            int index=doc.IndexOf(el, false);
            if(index==-1){
                i++;
                id.setOffset(i);
            }
            else{
                if(i+doc.get(index).length()<=this.start + offset){//deletion after this content
                    i = i + doc.get(index).length();
                    id.setOffset(i);
                }
                else{//start of deletion in this content or before
                    if (i >= this.start + offset) {//before
                        if (i + doc.get(index).length() <= this.end + offset) {//end of deletion after this content
                            i = i + doc.get(index).length();
                            id.setOffset(i);
                            doc.remove(index);
                        }
                        else {//end of deletion in this content
                            doc.delete(index, 0, this.end + offset - i);
                            i=maxpeer;
                        }
                    }
                    else {//start of deletion in this content
                        if (i + doc.get(index).length() <= this.end + offset) {//end of deletion after this content
                            int l=doc.get(index).length();
                            doc.delete(index, this.start + offset - i, l);
                            i = i + l;
                            id.setOffset(i); 
                        }
                        else {//end of deletion in this content
                            doc.delete(index, this.start + offset - i, this.end + offset - i);
                            i=maxpeer;
                        }
                    }
                }
            }
        }
    }    
}