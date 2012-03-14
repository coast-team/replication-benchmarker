/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.graphtree.connection;

import crdt.set.CRDTSet;
import crdt.set.SetOperation;
import crdt.tree.graphtree.Edge;
import crdt.tree.graphtree.GraphConnectionPolicyNoInc;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Observable;
import java.util.Set;

/**
 *
 * @author score
 */
public class GraphReappear<T> extends GraphConnectionPolicyNoInc<T>{

    
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
                    SetTreeOut.put(null, edg);
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

    @Override
    protected void getRooted(Edge<T> orphanEdge) {
        HashMap<T, Set<Edge<T>>> pth = new HashMap(); //contain inverse path (sub set of nodeToEdge)
        Set<Edge<T>> setEdge = nodeToEdge.getAll(orphanEdge.getFather());
        for(Edge<T> edg : setEdge)
        {
            getPathInverse(edg, pth);
            SetTree.put(orphanEdge.getFather(), edg);//add fathers of orphan edge
            SetTreeOut.put(edg.getFather(), edg);
        }
        
        Iterator itr = pth.keySet().iterator();
        while (itr.hasNext()) {
            T val = (T) itr.next();
            for(Edge<T> ed : pth.get(val))
            {
                SetTree.put(val, ed);
                SetTreeOut.put(ed.getFather(), ed);
            }
        }       
        //finaly add the orphan   
        SetTree.put(orphanEdge.getSon(), orphanEdge);
        SetTreeOut.put(orphanEdge.getFather(), orphanEdge);
        
    }

    HashMap getPathInverse(Edge<T> orphanEdge, HashMap<T, Set<Edge<T>>> way) {
        if (orphanEdge != null) {
            T notInNode = orphanEdge.getFather();
            if (notInNode != null) {
                Set<Edge<T>> setEdge = nodeToEdge.getAll(notInNode);
                for(Edge ed : setEdge)
                {
                    way.put(notInNode, nodeToEdge.getAll(notInNode));//add node with edge inside to it
                    return (getPathInverse(ed, way));
                }
            }
        }
        return way;
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

    @Override
    public GraphConnectionPolicyNoInc<T> create() {
        return new GraphReappear<T>();
    }
    
}