/*
 *  Replication Benchmarker
 *  https://github.com/score-team/replication-benchmarker/
 *  Copyright (C) 2012 LORIA / Inria / SCORE Team
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
package crdt;

import java.util.logging.Level;
import java.util.logging.Logger;
import jbenchmarker.core.LocalOperation;

/**
 *
 * @author Stephane Martin <stephane@stephanemartin.fr>
 */
public class CRDTMockTime<L> extends CRDT<L> {

    CRDT<L> crdt;
    int remote, local;
    L blockedLookup;

    public CRDTMockTime(CRDT<L> crdt, int remote, int local) {
        this.crdt = crdt;
        this.remote = remote;
        this.local = local;
    }

    @Override
    public CRDTMessage applyLocal(LocalOperation op) throws PreconditionException {
        waitASecond(local);
        return crdt.applyLocal(op);

    }

    @Override
    public void applyOneRemote(CRDTMessage op) {
        waitASecond(remote);
        crdt.applyOneRemote(op);
    }

    @Override
    public L lookup() {
        if (blockedLookup == null) {
            return crdt.lookup();
        } else {
            return blockedLookup;
        }

    }

    @Override
    public CRDT<L> create() {
       return new CRDTMockTime(crdt.create(),remote,local);
    }

    @Override
    public void setReplicaNumber(int replicaNumber) {
        crdt.setReplicaNumber(replicaNumber);
    }

    @Override
    public int getReplicaNumber() {
        return crdt.getReplicaNumber();
    }

    
    public void waitASecond(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException ex) {
            Logger.getLogger(CRDTMockTime.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
