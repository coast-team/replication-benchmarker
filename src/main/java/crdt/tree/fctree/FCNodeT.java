/*
 *  Replication Benchmarker
 *  https://github.com/score-team/replication-benchmarker/
 *  Copyright (C) 2013 LORIA / Inria / SCORE Team
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
package crdt.tree.fctree;

import collect.OrderedNode;
import java.io.Serializable;

/**
 *
 * @author Stephane Martin <stephane@stephanemartin.fr>
 */
public class FCNodeT<T> extends FCNode<T> implements Serializable{
    
    FCPosition position;
    T contain;
    
    
    public FCNodeT(FCNode<T> father, T contain, FCPosition position, FCIdentifier id) {
        super(father, id);
        this.position=position;
        this.contain=contain;
        
    }
   

    @Override
    public FCPosition getPosition() {
        return this.position;
    }

    @Override
    public OrderedNode createNode(Object elem) {
        return new FCNodeT(null, contain, null, null);
    }

    

    @Override
    public T getValue() {
       return contain;
    }

    @Override
    public FCNode createNode(FCNode<T> father, T contain, FCPosition position, FCIdentifier id) {
       return new FCNodeT(father, contain, position, id);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (this.position != null ? this.position.hashCode() : 0);
        hash = 97 * hash + (this.contain != null ? this.contain.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if(!super.equals(obj)){
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FCNodeT<T> other = (FCNodeT<T>) obj;
        if (this.position != other.position && (this.position == null || !this.position.equals(other.position))) {
            return false;
        }
        if (this.contain != other.contain && (this.contain == null || !this.contain.equals(other.contain))) {
            return false;
        }
        return true;
    }
    
}
