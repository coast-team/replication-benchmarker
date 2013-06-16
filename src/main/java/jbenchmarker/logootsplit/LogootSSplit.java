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

import crdt.Operation;
import java.util.ArrayList;
import java.util.List;
import jbenchmarker.core.SequenceOperation;

public class LogootSSplit implements LogootSOperation{
    
    private LogootSElement element;
    private int offset;
    

    public LogootSSplit(LogootSElement el, int offset) {
        this.element=el;
        this.offset=offset;  
    }

    @Override
    public void apply(LogootSDocument doc) {
        List<Integer> list=doc.getAllLike(element);
        if(!list.isEmpty()){
            int index=0;
            int p=offset+this.element.getIdAt(this.element.size()-1).getOffset();
            LogootSElement el;//=doc.getEl(list.get(0));
            int o;//=el.getIdAt(el.size()-1).getOffset();
            while (index<list.size()){
                int length=doc.get(list.get(index)).size();
                el = doc.getEl(list.get(index));
                o=el.getIdAt(el.size()-1).getOffset();
                if(p>=o+length){//no split in this element
                    index++;
                }
                else{
                    if(p<=o){//no split
                        return;
                    }
                    else{
                        //String s=doc.get(list.get(index));
                        List s=doc.get(list.get(index));
                        //String ns=s.substring(p-o);
                        List ns=new ArrayList(s.subList(p-o, s.size()));
                        LogootSElement el2=new LogootSElement(el, p-o);
                        doc.delete(list.get(index), p-o, s.size());
                        doc.add(el2, ns);    
                    }
                }
            }
        }
        /*LogootSElement el = this.element.origin();
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
        }*/
    }

    @Override
    public Operation clone() {
        return new LogootSSplit(element.clone(), offset);
    }

    
}