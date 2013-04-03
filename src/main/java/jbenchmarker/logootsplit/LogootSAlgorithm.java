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

import crdt.CRDT;
import crdt.simulator.IncorrectTraceException;
import java.util.ArrayList;
import java.util.List;
import jbenchmarker.core.MergeAlgorithm;
import jbenchmarker.core.SequenceMessage;
import jbenchmarker.core.SequenceOperation;

public class LogootSAlgorithm<T> extends MergeAlgorithm{
    
    public LogootSAlgorithm(){
        super(null,0);
    }
    
    
    public LogootSAlgorithm(LogootSDocument doc, int siteId){
        super(doc,siteId);
        doc.setReplicaNumber(siteId);
    }
    
    @Override
    public void setReplicaNumber(int r){
        super.setReplicaNumber(r);
        ((LogootSDocument)getDoc()).setReplicaNumber(r);
        
    }
   

    @Override
    protected void integrateRemote(SequenceMessage op) throws IncorrectTraceException {
        this.getDoc().apply(op);
    }

    @Override
    protected List<SequenceMessage> localInsert(SequenceOperation opt) throws IncorrectTraceException {
        List<SequenceMessage> list=new ArrayList<SequenceMessage>();
        list= ((LogootSDocument)this.getDoc()).generateInsertion(opt);
        for(int i=0;i<list.size();i++){
            this.getDoc().apply(list.get(i));
        }
        return list;
    }

    @Override
    protected List<SequenceMessage> localDelete(SequenceOperation opt) throws IncorrectTraceException {
        List<SequenceMessage> list=new ArrayList<SequenceMessage>();
        list= ((LogootSDocument)this.getDoc()).generateDeletion(opt);
        for(int i=0;i<list.size();i++){
            this.getDoc().apply(list.get(i));
        }
        return list;
    }
    
    @Override
    protected List<SequenceMessage> localUpdate(SequenceOperation opt) throws IncorrectTraceException {
        List<SequenceMessage> list=new ArrayList<SequenceMessage>();
        list= ((LogootSDocument)this.getDoc()).generateDeletion(opt);
        list.addAll(((LogootSDocument)this.getDoc()).generateInsertion(opt));
        for(int i=0;i<list.size();i++){
            this.getDoc().apply(list.get(i));
        }
        return list;
    }     
    
    /*@Override
    protected List<SequenceMessage> generateLocal(SequenceOperation opt) {
        List<SequenceMessage> list=new ArrayList<SequenceMessage>();
        if(opt.getType()==SequenceOperation.OpType.ins){
            list= ((LogootSDocument)this.getDoc()).generateInsertion(opt);
        }
        else {
            if(opt.getType()==SequenceOperation.OpType.del){
                list =((LogootSDocument)this.getDoc()).generateDeletion(opt);
            }
            else{
                if(opt.getType()==SequenceOperation.OpType.update){
                    list =((LogootSDocument)this.getDoc()).generateDeletion(opt);
                    list.addAll(((LogootSDocument)this.getDoc()).generateInsertion(opt));
                }
            }
        }    
        for(int i=0;i<list.size();i++){
            this.getDoc().apply(list.get(i));
        }
        return list;
    }*/

    @Override
    public CRDT<String> create() {
        return new LogootSAlgorithm(new LogootSDocument<String>(100), 1);
    }
}