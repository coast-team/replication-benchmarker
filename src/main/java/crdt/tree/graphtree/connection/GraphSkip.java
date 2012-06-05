/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.graphtree.connection;

import crdt.set.CRDTSet;
import crdt.set.SetOperation;
import crdt.tree.graphtree.Edge;
import crdt.tree.graphtree.GraphConnectionPolicyNoInc;
import java.util.Iterator;
import java.util.Observable;

/**
 *
 * @author score
 */
public class GraphSkip<T> extends GraphConnectionPolicyNoInc<T> {
        
    @Override
    public void connect() {
        SetTree.clear();
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
                }
             }
        }
        fresh = true;
    }

    @Override
    protected void getRooted(Edge<T> orphanEdge) {
//
//        for (Edge<T> edg : this.getEdge()) {
//            if(this.getNode().contains(edg.getFather().getValue()) ) //not orphan
//                t.add(edg.getFather(), edg.getSon());
//        }
    }

    @Override
    public GraphConnectionPolicyNoInc<T> create() {
        return new GraphSkip<T>();
    }

    /*
     * @param o : CRDTSet
     * @param op: contains T or Edge with type of operation
     */
    @Override
    public void updateNoInc(Observable o, Object op) {
        fresh =false;
        if (((SetOperation) op).getContent() instanceof Edge) {
            edge = ((CRDTSet<Edge<T>>) o).lookup();
        } else {
            node = ((CRDTSet<T>) o).lookup();
        }
    }
}
