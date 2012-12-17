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

import java.util.ArrayList;
import java.util.List;
import jbenchmarker.core.SequenceMessage;
import jbenchmarker.core.SequenceOperation;

public class LogootSInsertion extends SequenceMessage implements LogootSOperation{
    
    private LogootSElement element;
    private List content;
    
    
    private LogootSInsertion(SequenceOperation so,LogootSElement el,List s){
        super(so);
        element=el.clone();
        content=s;
    } 
    
    public LogootSInsertion(SequenceOperation so, LogootSElement lower, LogootSElement greater, List content, LogootSDocument doc,int siteId) {
        super(so);
        this.content = content;
        List<LogootSIdentifier> idList = new ArrayList<LogootSIdentifier>();
        int i = 0;
        while (i < lower.size()
                && lower.getIdAt(i).equals(greater.getIdAt(i))) {
            idList.add(new LogootSIdentifier(lower.getIdAt(i)));
            i++;
        }
        LogootSIdentifier gId = greater.getIdAt(i);
        LogootSIdentifier lId;
        if (i == lower.size()) {
            lId = new LogootSIdentifier(0, 0);
            while(gId.getPosition() - lId.getPosition() == 0 ){
                idList.add(lId);
                gId=greater.getIdAt(++i);
            }
            
            int distance = gId.getPosition() - lId.getPosition();
            if (distance < 2) {
                idList.add(lId);
                idList.add(new LogootSIdentifier((int) (1 + ((doc.max()-1) * Math.random())),siteId));
            } else {
                idList.add(new LogootSIdentifier((int) (lId.getPosition() + 1 + (distance-1) * Math.random()), siteId));
            }
            
        } else {
            int distance = gId.getPosition() - lower.getIdAt(i).getPosition();
            if (distance < 2) {
                if(i==lower.size()-1){
                    idList.add(new LogootSIdentifier(lower.getIdAt(i), lower.getIdAt(i).getOffset() + doc.get(doc.IndexOf(lower, false)).size() - 1));
                }
                else{
                    idList.add(new LogootSIdentifier(lower.getIdAt(i)));
                }
                i++;
                while(i<lower.size() && lower.getIdAt(i).getPosition()==doc.max()-1){
                    idList.add(new LogootSIdentifier(lower.getIdAt(i)));
                    i++;
                }
                if(i == lower.size()){
                    idList.add(new LogootSIdentifier((int) (1 + ((doc.max()-1) * Math.random())), siteId));
                }
                else{
                    distance = doc.max() - lower.getIdAt(i).getPosition();
                    idList.add(new LogootSIdentifier((int) (lower.getIdAt(i).getPosition() + 1 + (distance-1) * Math.random()), siteId));
                    
                }
            } else {
                idList.add(new LogootSIdentifier((int) (lower.getIdAt(i).getPosition() + 1 + (distance-1) * Math.random()), siteId));
            }            
        }
        this.element = new LogootSElement(idList, doc.clockIncrement());

    }

    public LogootSInsertion(SequenceOperation so,LogootSElement el, int offset, List content, LogootSDocument doc,int siteId) {
        super(so);
        this.content=content;
        List<LogootSIdentifier> idList = new ArrayList<LogootSIdentifier>();
        int i = 0;
        while (i < el.size()-1){
            idList.add(el.getIdAt(i));
            i++;
        }
        idList.add(new LogootSIdentifier(el.getIdAt(i), el.getIdAt(i).getOffset()+offset-1));
        idList.add(new LogootSIdentifier((int) (1 + ((doc.max()-1) * Math.random())), siteId));
        this.element=new LogootSElement(idList, doc.clockIncrement());
    }

    @Override
    public void apply(LogootSDocument doc) {
        doc.add(element,content);
    }

    @Override
    public SequenceMessage clone() {
        return new LogootSInsertion(this.getOriginalOp(),this.element,this.content);
    }

    
}