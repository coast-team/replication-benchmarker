/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/
 * Copyright (C) 2011 INRIA / LORIA / SCORE Team
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
package crdt.simulator.random;

import collect.Node;
import collect.UnorderedNode;
import crdt.CRDT;
import crdt.Operation;
import collect.VectorClock;
import crdt.tree.CRDTTree;
import crdt.tree.TreeOperation;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * A profile that generates operation.
 * @author urso
 */
public abstract class TreeOperationProfile<T> implements OperationProfile {
 
    private final double perAdd;
    private final RandomGauss r;

    /**
     * Constructor of profile
     * @param perAdd  percentage of ins vs remove operation 
     * @param perBlock percentage of block operation (size >= 1)
     * @param avgBlockSize average size of block operation
     * @param sdvBlockSize standard deviation of block operations' size.
     */
    public TreeOperationProfile(double perIns) {
        this.perAdd = perIns;
        this.r = new RandomGauss();
    }
    
    @Override
    public TreeOperation<T> nextOperation(CRDT crdt, VectorClock vectorClock) {
        Iterator<? extends Node<T>> it = ((CRDTTree<T>) crdt).lookup().getBFSIterator(null);
        ArrayList<Node<T>> l = new ArrayList<Node<T>>();  
        while (it.hasNext()) {
            l.add(it.next());
        }
        if (l.size() == 1 || r.nextDouble() < perAdd) {
            Node<T> n = l.get(r.nextInt(l.size()));
            while (full(n)) {
                n = l.get(r.nextInt(l.size()));
            }
            T elem = nextElement();
            while (((UnorderedNode<T>) n).getChild(elem) != null) {
                elem = nextElement();
            }
            return new TreeOperation<T>(n, elem);
        } else {
            Node<T> n = l.get(r.nextInt(l.size()-1)+1);
            return new TreeOperation<T>(n);
        }        
    }
    
    abstract public T nextElement();

    abstract public T nextElement(T elem);

    abstract public boolean full(Node<T> s);
}
