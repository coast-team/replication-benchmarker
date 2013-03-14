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
package jbenchmarker.twotier;

import crdt.CRDT;
import crdt.CRDTMessage;
import crdt.Factory;
import crdt.PreconditionException;
import crdt.simulator.TraceOperation;
import java.util.List;
import jbenchmarker.core.LocalOperation;
import jbenchmarker.jupiter.JupiterClient;
import jbenchmarker.jupiter.OTModel;
import jbenchmarker.jupiter.OTOperation;



/**
 * A two tier replication client. 
 * Works as a Jupiter client. Knows its core replica.
 * @author oster
 */
public class Client<T> extends CRDT<T> {

    final private JupiterClient<T> doc;
    final private CoreReplica replica;

    transient private Factory<Client> cf;
    
    /**
     * Makes a new client.
     */
    Client(OTModel<T> doc, CoreReplica replica) {
        this.doc = new JupiterClient(doc);
        this.replica = replica;
    }

    @Override
    public CRDTMessage applyLocal(LocalOperation local) throws PreconditionException {
        doc.applyLocal(local);
        return null;        
    }

    public CRDTMessage emitNext() {
        return replica.applyLocal(doc.nextOperation());
    }



    @Override
    public void applyOneRemote(CRDTMessage msg) {
        List<OTOperation> ops = replica.applyRemote(msg);
        for (OTOperation op : ops) {
            doc.applyRemote(op);
        }
    }

    @Override
    public T lookup() {
        return doc.lookup();
    }

    @Override
    public Client create() {
        return cf.create();
    }

    @Override
    public void setReplicaNumber(int replicaNumber) {
        super.setReplicaNumber(replicaNumber);
        doc.setReplicaNumber(replicaNumber);
    }
}
