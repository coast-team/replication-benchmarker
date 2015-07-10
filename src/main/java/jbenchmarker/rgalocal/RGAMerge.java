/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/ Copyright (C) 2013
 * LORIA / Inria / SCORE Team
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package jbenchmarker.rgalocal;

import crdt.CRDT;
import java.util.List;
import java.util.ArrayList;
import collect.VectorClock;
import jbenchmarker.core.Document;
import jbenchmarker.core.MergeAlgorithm;
import crdt.Operation;
import crdt.simulator.IncorrectTraceException;
import java.util.LinkedList;
import jbenchmarker.core.SequenceOperation;
import static jbenchmarker.rgalocal.RGADocument.MAX;

/**
 * RGA with local table of long position identifiers.
 * Converge but not efficient. 
 * 
 * @author Roh, urso
 */
public class RGAMerge extends MergeAlgorithm {

    private final VectorClock siteVC;
    public final static int MAGIC = 2;

    public RGAMerge(Document doc, int r) {
        super(doc, r);
        siteVC = new VectorClock();
    }

    @Override
    protected void integrateRemote(crdt.Operation message) throws IncorrectTraceException {
        RGAOperation rgaop = (RGAOperation) message;
        RGADocument rgadoc = (RGADocument) (this.getDoc());
        this.siteVC.inc(rgaop.getReplica()); // TO FIX
        rgadoc.apply(rgaop);
    }

    @Override
    protected List<Operation> localInsert(SequenceOperation opt) throws IncorrectTraceException {
        List<Operation> lop = new ArrayList<Operation>();
        RGADocument rgadoc = (RGADocument) (this.getDoc());
        RGAS2Vector s4vtms, first = null;
        RGANode before, node;
        long after;

        int p = opt.getPosition();
        int offset;

        offset = opt.getContent().size();	
        before = rgadoc.getVisibleNode(p);
        if (p == rgadoc.viewLength()) {
            after = MAX;
        } else {
            after = rgadoc.getVisibleNode(p+1).getPosition();
        }     
        
        long pos = before.getPosition(), step = (after - pos) / (offset * MAGIC);  
        node = before;
        List<RGANode> ln = new ArrayList();
        for (int i = 0; i < offset; i++) {
            this.siteVC.inc(this.getReplicaNumber());
            s4vtms = new RGAS2Vector(this.getReplicaNumber(), this.siteVC);
            if (first == null) {
                first = s4vtms;
            }
            node = rgadoc.remoteInsert(node, s4vtms, opt.getContent().get(i));
            pos += step;
            node.setPosition(pos);
            ln.add(node);
        }
        rgadoc.addLocal(p, ln);
        
        lop.add(new RGAOperation(before.getKey(), opt.getContent(), first));
        return lop;
    }

    @Override
    protected List<Operation> localDelete(SequenceOperation opt) throws IncorrectTraceException {
        List<Operation> lop = new ArrayList<Operation>();
        RGADocument rgadoc = (RGADocument) (this.getDoc());
        RGAS2Vector s4vtms;
        RGAOperation rgaop;
        RGANode target;

        int p = opt.getPosition();
        int offset;

        offset = opt.getLenghOfADel();
        target = rgadoc.getVisibleNode(p + 1);

        for (int i = 0; i < offset; i++) {
            rgaop = new RGAOperation(target.getKey());
            target = target.getNextVisible();
            lop.add(rgaop);
            rgadoc.remoteDelete(rgaop);
        }
        rgadoc.removeLocal(p,offset);
        
        return lop;
    }

    @Override
    public CRDT<String> create() {
        return new RGAMerge(new RGADocument(), 0);
    }
}
