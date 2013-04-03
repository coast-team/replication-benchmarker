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
/*
 * Jupiterclient with acknowledgement.
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.jupiter;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import jbenchmarker.core.LocalOperation;

/**
 * A client in jupiter system.
 * @author urso
 */
public class JupiterClient<T> {
    private int replicaNumber;
    final private Deque<List<OTOperation>> outGoing; 
    final private OTModel<T> document;

    public JupiterClient(OTModel<T> document) {
        this.document = document;
        this.outGoing = new LinkedList<List<OTOperation>>();
    }
    
    /**
     * Applies a user operation. Add corresponding OT operation to outgoing buffer.
     */
    public void applyLocal(LocalOperation local) {
        List<OTOperation> ops = document.generate(local);
        for (OTOperation op : ops) {
            op.setReplicaNumber(replicaNumber);
        }
        outGoing.add(ops);
    }

    /**
     * Applies server operations. 
     * Transforms this operation against concurrent pending local operations.
     */
    public void applyRemote(OTOperation msg) {
        Iterator<List<OTOperation>> it = outGoing.iterator();
        while (it.hasNext()) {
            for (OTOperation op : it.next()) {
                msg = document.transform(msg, op);
            }
        }
        document.apply(msg);
    }

    public T lookup() {
        return document.lookup();
    }

    public void setReplicaNumber(int replicaNumber) {
        this.replicaNumber = replicaNumber;
    }

    /**
     * Returns and remove first pending local operation.
     */
    public List<OTOperation> nextOperation() {
        return outGoing.pollFirst();
    }
}
