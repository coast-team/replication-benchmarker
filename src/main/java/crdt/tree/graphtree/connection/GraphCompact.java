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
package crdt.tree.graphtree.connection;

import crdt.set.CRDTSet;
import crdt.set.SetOperation;
import crdt.tree.graphtree.Edge;
import crdt.tree.graphtree.GraphConnectionPolicyNoInc;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Observable;
import java.util.Set;

/**
 *
 * @author score
 */
public class GraphCompact<T> extends GraphConnectionPolicyNoInc<T> {

    @Override
    protected void getRooted(Edge<T> orphanEdge) {
        Set<Edge<T>> pth = new HashSet(); //contain inverse path (sub set of nodeToEdge)
        Set<Edge<T>> setEdge = nodeToEdge.getAll(orphanEdge.getFather());
        for(Edge<T> edg : setEdge) //edg = edge fathers of orphan 
        {
            getEdgeConnect(edg, pth);
            while (!pth.isEmpty()) {
                Iterator<Edge<T>> itr = pth.iterator();
                while (itr.hasNext()) {
                    Edge<T> edgCurr = itr.next();
                    
                    if(edgCurr.getFather() == null && tombstone.contains(edgCurr))
                    {
                        Edge newEdge = new Edge(null, edg.getSon());
                        SetTree.put(edg.getSon(), newEdge);
                        SetTreeOut.put(null, newEdge);
                        itr.remove();
                    } else if (edgCurr.getFather() != null && tombstone.contains(edgCurr))
                    {
                        itr.remove();
                    }
                    else if (!tombstone.contains(edgCurr))
                    {
                        Edge newEdge = new Edge(edgCurr.getSon(), edg.getSon());
                        SetTree.put(edg.getSon(), newEdge);
                        SetTreeOut.put(edgCurr.getSon(), newEdge);
                        itr.remove();
                    }
                    else if (SetTree.containsKey(edgCurr.getFather())) {
                        Edge newEdge = new Edge(edgCurr.getFather(), edg.getSon());
                        SetTree.put(edg.getSon(), newEdge);
                        SetTreeOut.put(edg.getFather(), newEdge);
                        itr.remove();
                    }
                }
            }
        }
        //finaly add the orphan   
        SetTree.put(orphanEdge.getSon(), orphanEdge);
        SetTreeOut.put(orphanEdge.getFather(), orphanEdge);
    }
    

    @Override
    public void connect() {
        SetTree.clear();
        nodeToEdge.clear();
        //add edge for nodes
        for (T n : node) {
            if (n != null && !edge.isEmpty()) {
                for (Edge<T> edg : edge) {
                    if (edg.getSon().equals(n)) { //edge inside node n
                        nodeToEdge.put(n, edg);
                    }
                }
            }
        }
        //add tombstone edge for nodes 
        for (Edge<T> edg : tombstone) {
            nodeToEdge.put(edg.getSon(), edg);
        }
        edge.removeAll(tombstone);
        while(!edge.isEmpty())
        { 
            for (final Iterator<Edge<T>> itr = edge.iterator(); itr.hasNext();) {
                final Edge<T> edg = itr.next();
                
                if (edg.getFather() == null) {
                    SetTree.put(edg.getSon(), edg);
                    SetTreeOut.put(edg.getFather(), edg);
                    itr.remove();
                } else if (SetTree.containsKey(edg.getFather())
                        && node.contains(edg.getSon()) && node.contains(edg.getFather())) //not orphan
                {
                    SetTree.put(edg.getSon(), edg);
                    SetTreeOut.put(edg.getFather(), edg);
                    itr.remove();
                } else if (!node.contains(edg.getFather()) &&
                        node.contains(edg.getSon())) {//Orphan
                    itr.remove();
                    getRooted(edg);
                }
             }
        }
        fresh = true;
    }
    
    Set<Edge<T>> getEdgeConnect(Edge<T> orphanEdge, Set<Edge<T>> edgeConnect) {
        if (orphanEdge != null) {
            T notInNode = orphanEdge.getFather();
            if (notInNode != null) {
                Set<Edge<T>> setEdge = nodeToEdge.getAll(notInNode);
                for (Edge ed : setEdge) {
                    edgeConnect.addAll(nodeToEdge.getAll(notInNode));
                    return (getEdgeConnect(ed, edgeConnect));
                }
            }
        }
        return edgeConnect;
    }

    @Override
    public GraphConnectionPolicyNoInc<T> create() {
        return new GraphCompact<T>();
    }
    
        @Override
    public void updateNoInc(Observable o, Object op) {
        fresh = false;
        if (((SetOperation) op).getType() == SetOperation.OpType.del
                && ((SetOperation) op).getContent() instanceof Edge) {
            tombstone.add(((Edge<T>) ((SetOperation) op).getContent()));
        }
        
        if (((SetOperation) op).getContent() instanceof Edge) {
            edge = ((CRDTSet<Edge<T>>) o).lookup();
        } else {
            node = ((CRDTSet<T>) o).lookup();
        }
    }

    
}
