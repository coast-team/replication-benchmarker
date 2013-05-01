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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.twotier;

import crdt.CRDT;
import crdt.CRDTMessage;
import java.util.List;
import jbenchmarker.jupiter.JupiterClient;
import jbenchmarker.jupiter.OTModel;
import jbenchmarker.jupiter.OTOperation;

/**
 * A core replica in two tier architecture.
 * Associates a jupiter OT server and a crdt.
 * @author urso
 */
public class CoreReplica<T> {
    private final OTModel<T> otdoc;
    private final CRDT<T> crdt;

    public CoreReplica(OTModel<T> otdoc, CRDT<T> crdt) {
        this.otdoc = otdoc;
        this.crdt = crdt;
    }
    
    
    
    CRDTMessage applyLocal(List<OTOperation> opt) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    List<OTOperation> applyRemote(CRDTMessage op) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
    void setReplicaNumber(int n) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
