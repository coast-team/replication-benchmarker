/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/
 * Copyright (C) 2013 LORIA / Inria / SCORE Team
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

import java.util.List;
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

    /*@Override
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
    }*/
    
    @Override
    public void apply(LogootSDocument doc) {
        List<Integer> list=doc.getAllLike(element);
        if(!list.isEmpty()){
            int index=0;
            int d=0;
            int s=start+this.element.getIdAt(this.element.size()-1).getOffset();
            int e=end+this.element.getIdAt(this.element.size()-1).getOffset();
            LogootSElement el;//=doc.getEl(list.get(0));
            int offset;//=el.getIdAt(el.size()-1).getOffset();
            while (index<list.size()){
                int length=doc.get(list.get(index)-d).size();
                el = doc.getEl(list.get(index)-d);
                offset=el.getIdAt(el.size()-1).getOffset();
                if(s>=offset+length){//no deletion in this element
                    index++;
                }
                else{
                    if(e<=offset){//no more deletions
                        return;
                    }
                    else{
                        if(s<=offset){//start of deletion before this element
                            if(e>=offset+length){//end of deletion after this element
                                doc.remove(list.get(index)-d);
                                index++;
                                d++;
                            }
                            else{//end of deletion in this content
                                doc.delete(list.get(index)-d, 0, e-offset);
                                return;
                            }
                        }
                        else{//start of deletion in this element
                            if(e>=offset+length){//end of deletion after this element
                                doc.delete(list.get(index)-d, s-offset, length);
                                index++;
                            }
                            else{//end of deletion in this content
                                doc.delete(list.get(index)-d, s-offset, e-offset);
                                return;
                            }
                        }
                    }
                }
            }
        }
    }
}