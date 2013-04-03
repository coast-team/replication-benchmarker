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
package crdt.tree.graphtree;

import collect.Node;
import crdt.CRDT;
import crdt.CRDTMessage;
import java.util.*;

/**
 *
 * @author score
 */
public class GTreeMessage implements CRDTMessage {
    
    private CRDTMessage node;
    private CRDTMessage edge;
    
    public GTreeMessage(CRDTMessage nd , CRDTMessage edg)
    {
        this.node = nd;
        this.edge = edg;
    }

    @Override
    public CRDTMessage concat(CRDTMessage msg) {
        return new GTreeMessage(this.node.concat(((GTreeMessage)msg).getNode()),
                this.edge.concat(((GTreeMessage)msg).getEdge()));
    }
    
    public CRDTMessage getNode()
    {
        return node;
    }
    
    public CRDTMessage getEdge()
    {
        return edge;
    }

    @Override
    public CRDTMessage clone() {
        return new GTreeMessage(node.clone(), edge.clone());
    }

    @Override
    public int size() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void execute(CRDT crdt) {
        crdt.applyOneRemote(this);
    }
    
}
