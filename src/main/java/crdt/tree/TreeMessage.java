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
package crdt.tree;

import crdt.CRDT;
import crdt.CRDTMessage;
import crdt.set.SetOperation;
import java.util.HashSet;
import java.util.Set;


/**
 *
 * @author score
 */
@Deprecated
public class TreeMessage implements CRDTMessage{
    Set<CRDTMessage> operations;

    @Override
    public CRDTMessage concat(CRDTMessage msg) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public CRDTMessage clone() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void execute(CRDT crdt) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public enum TreeOperationType {add, del}; 
    private TreeOperationType type;

    public TreeMessage(TreeOperationType type, CRDTMessage operation) {
        this.operations = new HashSet<CRDTMessage>(1);       
        this.operations.add(operation);
        this.type = type;
    }
    
    public TreeMessage(TreeOperationType type, Set<CRDTMessage> operations) {
        this.operations = operations;
        this.type = type;
    }

    public TreeMessage(TreeOperationType type) {
        this.operations = new HashSet<CRDTMessage>();
        this.type = type;
    }
    
    public TreeOperationType getType() {
        return type;
    }

    @Override
    public int size() {
        return -1;
    }
}
