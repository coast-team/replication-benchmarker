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
package crdt.simulator.random;

import collect.HashMapSet;
import collect.VectorClock;
import crdt.CRDT;
import crdt.tree.orderedtree.CRDTOrderedTree;
import crdt.tree.orderedtree.OrderedTreeOperation;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Stephane Martin <stephane.martin@loria.fr>
 */
public class StandardOrderedTreeOpProfile extends OrderedTreeOperationProfile<String> {

    /*    RandomGauss r=new RandomGauss();
     //HashMap <List<Integer>,OrderedNode> nodes;
     HashMapSet <Integer,List<Integer>> paths=new HashMapSet <Integer,List<Integer>>();
     @Override
     public OrderedTreeOperation<String> nextOperation(CRDT crdt, VectorClock vectorClock) {
        
     // OrderedNode<String> node = (OrderedNode<String>) crdt.lookup();
     List<Integer> path = new LinkedList<Integer>();
     //int n = node.childrenNumber();
     CRDTOrderedTree tree=(CRDTOrderedTree)crdt;
     int rep=crdt.getReplicaNumber();
     Set s=paths.getAll(rep);        
     if (s!=null){
     path=(List<Integer>)s.toArray()[r.nextInt(s.size())];
     }
     /*while (n > 0 && r.nextDouble() < super.getPerChild()) {
     int i = r.nextInt(n);
     path.add(i);
     node = node.getChild(i);           
     n = node.childrenNumber();
     }*
        
        
        
     if (path==null || path.isEmpty() || r.nextDouble() < this.getPerAdd()) {
     /*Generate add operation*
     int n = tree.getNodeFromPath(path).childrenNumber();
     List<Integer> path2=new LinkedList<Integer>(path);
     path2.add(new Integer(n));
     paths.put(rep, path2);
     return new OrderedTreeOperation<String>(path, n==0 ? 0 : r.nextInt(n), nextElement());
     } else {
     /*Generate del operation*
     paths.remove(rep, path);
     return new OrderedTreeOperation<String>(path);
     }        
     }
     */
    public StandardOrderedTreeOpProfile(double perIns, double perChild) {
        super(perIns, perChild);
    }

    @Override
    public String nextElement() {
        return "" + ('a' + (int) (Math.random() * 26));

    }
}
