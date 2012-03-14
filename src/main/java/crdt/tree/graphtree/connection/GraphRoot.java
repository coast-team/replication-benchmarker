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
public class GraphRoot<T> extends GraphConnectionPolicyNoInc<T>  {
    
    @Override
    public void connect() {
        SetTree.clear();
        nodeToEdge.clear();
        
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
        Edge<T> ed = new Edge(null, orphanEdge.getSon());
        this.SetTree.put(orphanEdge.getSon(), ed);
        SetTreeOut.put(null, ed);
    }

    @Override
    public GraphConnectionPolicyNoInc<T> create() {
        return new GraphRoot<T>();
    }
    
        /*
     * @param o : CRDTSet
     * @param op: contains T or Edge with type of operation
     */
    @Override
    public void updateNoInc(Observable o, Object op) {
        fresh = false;
        if (((SetOperation) op).getContent() instanceof Edge) {
            edge = ((CRDTSet<Edge<T>>) o).lookup();
        } else {
            node = ((CRDTSet<T>) o).lookup();
        }
    }
    
}
