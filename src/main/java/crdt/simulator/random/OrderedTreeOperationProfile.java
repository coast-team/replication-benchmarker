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

import collect.OrderedNode;
import collect.VectorClock;
import crdt.CRDT;
import crdt.tree.orderedtree.OrderedTreeOperation;
import java.util.LinkedList;
import java.util.List;

/**
 * A profile that generates operation.
 * @author urso
 */
public abstract class OrderedTreeOperationProfile<T> implements OperationProfile {
 
    private final double perAdd;
    private final double perChild;
    private final RandomGauss r;

    /**
     * Constructor of profile
     * @param perIns  percentage of ins vs remove operation 
     */
    public OrderedTreeOperationProfile(double perIns, double perChild) {
        this.perAdd = perIns;
        this.perChild = perChild;
        this.r = new RandomGauss();
    }
    
    @Override
    public OrderedTreeOperation<T> nextOperation(CRDT crdt) {
        OrderedNode<T> node = (OrderedNode<T>) crdt.lookup();
        List<Integer> path = new LinkedList<Integer>();
        int n = node.childrenNumber();
 
        while (n > 0 && r.nextDouble() < 1.0-(perChild*1.0/n)) {
            int i = r.nextInt(n);
            path.add(i);
            node = node.getChild(i);           
            n = node.childrenNumber();
        }
        
        if (path.isEmpty()/* || n == 0*/ || r.nextDouble() < perAdd) {
            /*Generate add operation*/
            return new OrderedTreeOperation<T>(path, n==0 ? 0 : r.nextInt(n), nextElement());
        } else {
            /*Generate del operation*/
            return new OrderedTreeOperation<T>(path);
        }        
    }

    public double getPerAdd() {
        return perAdd;
    }

    public double getPerChild() {
        return perChild;
    }
    
    abstract public T nextElement();
}
