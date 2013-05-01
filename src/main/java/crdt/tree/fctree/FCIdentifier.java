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
package crdt.tree.fctree;

import java.io.Serializable;

/**
 *
 * @author Stephane Martin <stephane.martin@loria.fr>
 */
public class FCIdentifier implements Serializable{
    int replicaNumber;
    int operationNumber;

    /**
     * 
     * @param replicaNumber
     * @param operationNumber
     */
    public FCIdentifier(int replicaNumber, int operationNumber) {
        this.replicaNumber = replicaNumber;
        this.operationNumber = operationNumber;
    }

    /**
     * 
     * @return
     */
    public int getOperationNumber() {
        return operationNumber;
    }

    /**
     * 
     * @return
     */
    public int getReplicaNumber() {
        return replicaNumber;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FCIdentifier other = (FCIdentifier) obj;
        if (this.replicaNumber != other.replicaNumber) {
            return false;
        }
        if (this.operationNumber != other.operationNumber) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + this.replicaNumber;
        hash = 17 * hash + this.operationNumber;
        return hash;
    }

    /**
     * 
     * @param id
     * @return
     */
    public int compareTo(FCIdentifier id){
       if (this.equals(id))
           return 0;
       if (this.replicaNumber>id.replicaNumber 
               || (this.replicaNumber==id.replicaNumber 
                       && this.operationNumber>id.operationNumber))
           return 1;
       return -1;
   }

    @Override
    public String toString() {
        return "(" + replicaNumber + "," + operationNumber + ')';
    }
   
    
}
