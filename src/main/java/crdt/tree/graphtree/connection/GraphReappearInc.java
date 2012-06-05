/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.graphtree.connection;

import collect.HashMapSet;
import crdt.tree.graphtree.Edge;
import crdt.tree.graphtree.mapping.MappingUpdateOperation;
import java.util.HashMap;

/**
 *
 * @author Stephane Martin
 */
public class GraphReappearInc<T> extends GraphConnectionPolicyInc<T> {
    //HashSet <T> Nodes;

    HashMapSet<T, Visibility<T>> tEdges;
    HashMapSet<T, Visibility<T>> fEdges;
    HashMap<Edge<T>, Visibility<T>> visibility;

    
    
    @Override
    void addEdge(Edge<T> edge) {
        Visibility v = visibility.get(edge);
        //if ()
        if (v == null) {/*
             * Jamais vue
             */
            v = new Visibility(true, false, edge);
            visibility.put(edge, v);
            tEdges.put(edge.getSon(), v);
            fEdges.put(edge.getFather(), v);
        }
        v.setInCRDT(true);
        recurcifReappear(v);
    }

    @Override
    void delEdge(Edge<T> edge) {
        Visibility v = visibility.get(edge);
        if (v == null) {
            throw new UnsupportedOperationException("Suppression before add !");
        }

        v.setInCRDT(false);
        recurcifDeletion(v);
    }

    private void recurcifDeletion(Visibility<T> e) {
        //Visibility v=this.visibility.get(e);
        if (e != null && e.inView && !e.isInCRDT()) {
            /*
             * Aucun fils est visible donc on va le rendre invisible. et checker
             * les pères Si on a un fils visible on sort de la fonction
             */
            for (Visibility eChild : tEdges.getAll(e.getEdge().getSon())) {
                if (eChild.isInView()) {
                    return;
                }
            }
            e.setInView(false);
            gmp.update(this, new MappingUpdateOperation(MappingUpdateOperation.Type.del, e.getEdge()));
            lookup.remove(e.getEdge().getFather(), e.getEdge());
            for (Visibility<T> ef : tEdges.getAll(e.getEdge().getFather())) {
                recurcifDeletion(ef);
            }
        }
    }

    private void recurcifReappear(Visibility<T> e) {
        if (e != null && !e.isInView()) {
            gmp.update(this, new MappingUpdateOperation(MappingUpdateOperation.Type.add, e.getEdge()));
            lookup.put(e.getEdge().getFather(), e.getEdge());

            e.setInView(true);
            for (Visibility<T> e2 : tEdges.getAll(e.getEdge().getFather())) {
                recurcifReappear(e2);
            }
        }
    }

    

    /*
     * @Override void addNode(T n) { throw new
     * UnsupportedOperationException("Not supported yet."); }
     *
     * @Override void delNode(T n) { throw new
     * UnsupportedOperationException("Not supported yet."); }
     */
    
    @Override
    GraphConnectionPolicyInc createSpe() {
        GraphReappearInc ret=new GraphReappearInc();
        ret.tEdges=new HashMapSet<T, Visibility<T>>() ;
        ret.fEdges=new HashMapSet<T, Visibility<T>>() ;
        ret.visibility=new HashMap<Edge<T>, Visibility<T>>() ;
        return ret;

    }

    class Visibility<T> {

        private boolean inCRDT = false;
        private boolean inView = false;
        private Edge<T> edge;

        public Visibility(boolean inCRDT, boolean inView, Edge<T> edge) {
            this.inCRDT = inCRDT;
            this.inView = inView;
            this.edge = edge;
        }

        /**
         * @return the inCRDT
         */
        public boolean isInCRDT() {
            return inCRDT;
        }

        /**
         * @param inCRDT the inCRDT to set
         */
        public void setInCRDT(boolean inCRDT) {
            this.inCRDT = inCRDT;
        }

        /**
         * @return the inView
         */
        public boolean isInView() {
            return inView;
        }

        /**
         * @param inView the inView to set
         */
        public void setInView(boolean inView) {
            this.inView = inView;
        }

        /**
         * @return the edge
         */
        public Edge<T> getEdge() {
            return edge;
        }
    }
}
