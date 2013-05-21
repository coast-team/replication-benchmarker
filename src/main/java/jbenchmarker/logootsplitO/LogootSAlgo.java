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

import crdt.CRDT;
import crdt.RemoteOperation;
import crdt.simulator.IncorrectTraceException;
import java.util.LinkedList;
import java.util.List;
import jbenchmarker.core.Document;
import jbenchmarker.core.MergeAlgorithm;
import jbenchmarker.core.SequenceOperation;

/**
 *
 * @author Stephane Martin <stephane@stephanemartin.fr>
 */
public class LogootSAlgo extends MergeAlgorithm {

    
    public LogootSAlgo(LogootSDoc doc, int siteId) {
        super(doc, siteId);
        
    }

    public LogootSAlgo(LogootSDoc doc) {
        super(doc);
        
    }

    @Override
    public void setReplicaNumber(int replicaNumber) {
        super.setReplicaNumber(replicaNumber); //To change body of generated methods, choose Tools | Templates.
        this.getLDoc().setReplicaNumber(replicaNumber);
    }

    LogootSDoc getLDoc() {
        return (LogootSDoc) this.getDoc();
    }

    @Override
    protected void integrateRemote(RemoteOperation message) throws IncorrectTraceException {
       
        ((LogootSOp) message).apply((LogootSDoc) this.getDoc());
        
    }

    @Override
    protected List<RemoteOperation> localInsert(SequenceOperation opt) throws IncorrectTraceException {
       
        List ret = new LinkedList();
        ret.add(getLDoc().insertLocal(opt.getPosition(), opt.getContent()));
       
        return ret;
    }

    @Override
    protected List<RemoteOperation> localDelete(SequenceOperation opt) throws IncorrectTraceException {
        
        List ret = new LinkedList();
        ret.add(getLDoc().delLocal(opt.getPosition(), opt.getLenghOfADel()+opt.getPosition()-1));
        
        return ret;
    }

    
    @Override
    public CRDT<String> create() {
        return new LogootSAlgo(getLDoc().create());
    }
    
}
