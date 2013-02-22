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
package crdt.tree.fctree;

import java.io.Serializable;

/**
 *
 * @author Stephane Martin <stephane.martin@loria.fr>
 */
public class FCIdFactory implements Serializable{
    private int replica;
    private int nbOp=1;

    public FCIdFactory() {
    }

    public FCIdFactory(int replica) {
        this.replica = replica;
    }
    
    /**
     * Create new id with incremented nbOp
     * @return return new id.
     */
    public FCIdentifier createId(){
        return new FCIdentifier(replica, nbOp++);
    }

    public void setReplicaNumber(int replica) {
        this.replica=replica;
    }

    /**
     * 
     * @return number of generated operation
     */
    public int getNbOp() {
        return nbOp;
    }

    /**
     * 
     * @return get replica number
     */
    public int getReplica() {
        return replica;
    }
    
}
