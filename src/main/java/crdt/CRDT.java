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
package crdt;

import java.io.Serializable;
import java.util.Observable;
import jbenchmarker.core.LocalOperation;

/**
 * A CRDT is a factory. create() returns a new CRDT with the same behavior.
 *
 * @author urso
 */
public abstract class CRDT<L> extends Observable implements Factory<CRDT<L>>, Serializable, Replica<L> {

    private int replicaNumber;

    public CRDT(int replicaNumber) {
        this.replicaNumber = replicaNumber;
    }

    public CRDT() {
    }

    @Override
    public void setReplicaNumber(int replicaNumber) {
        this.replicaNumber = replicaNumber;
    }

    @Override
    public int getReplicaNumber() {
        return replicaNumber;
    }

    @Override
    final public void applyRemote(CRDTMessage msg) {
        msg.execute(this);
    }

    abstract public void applyOneRemote(CRDTMessage op);

    @Deprecated
    public Long lastExecTime() {
        return 0L;
    }
}
