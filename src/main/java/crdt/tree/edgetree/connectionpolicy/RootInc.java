/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.edgetree.connectionpolicy;

import collect.HashMapSet;
import crdt.set.CRDTSet;
import crdt.set.SetOperation;
import crdt.tree.edgetree.Edge;
import java.util.HashSet;
import java.util.Observable;
import java.util.Set;

/**
 *
 * @author moi
 */
public class RootInc<T> extends EdgeConnectionPolicy<T> {

    HashMapSet<T, Edge<T>> orphans;/*
     * father est la clef
     */

    HashMapSet<T, Edge<T>> tEdges;
    HashMapSet<T, Edge<T>> fEdges;
    //HashTree tree;

    public RootInc() {
        
    }

    
    @Override
    public EdgeConnectionPolicy<T> create() {
        RootInc<T> ret = new RootInc<T>();
        /*
         * ret.orphans = new HashMapSet<T, Node<T>>(); ret.tree = new HashTree();
         */
        ret.tEdges=new HashMapSet<T, Edge<T>> ();
        ret.fEdges=new HashMapSet<T, Edge<T>> ();
        ret.orphans=new HashMapSet<T, Edge<T>> ();
        return ret;
    }

    @Override
    public void update(Observable o, Object o1) {
        if (o instanceof CRDTSet
                && o1 instanceof SetOperation) {
            SetOperation o2 = (SetOperation) o1;
            Edge<T> edge = ((Edge<T>) o2.getContent())/*.clone()*/;
            if (o2.getType() == SetOperation.OpType.add) {
                /*
                 * Si c'est un Add
                 */
                
                if (edge.getFather() != null && !tEdges.containsKey(edge.getFather())) {

                    orphans.put(edge.getFather(), edge);
                    edge.setFather(null);

                }
                tEdges.put(edge.getSon(), edge);
                
                if (edge.getFather() != null) {
                    fEdges.put(edge.getFather(), edge);
                }

                emp.add(edge, this);
                
                Set<Edge<T>> backSons = orphans.removeAll(edge.getSon());
                /*
                 * Mettre tout les orphans en dessous du nouveau
                 */
                if (backSons != null) {
                    for (Edge<T> e2 : backSons) {
                        e2.setFather(edge.getSon());
                        if (e2.isVisible())
                            emp.moved(null, e2, this);
                        else{
                            emp.add(e2,this);
                            e2.setVisible(true);
                        }
                        //emp.modif(e2, this);
                    }
                }

            } else {
                /*
                 * Si C'est un Del
                 */
                /*
                 * Supprime de l'ensemble d'edge
                 */
                //edgesSet.remove(edge);

                /*
                 * Phase 1 : mets le fils orphans
                 */
                //nDeleted = nodes.get(edge.getSon());

                for (Edge <T> e: fEdges.getAll(edge.getSon())){
                    T oldFather=e.getFather();
                    e.setFather(null);
                    
                    orphans.put(edge.getSon(), e);
                    
                    if(tEdges.getAll(e.getFather()).size()>1){
                        e.setVisible(false);/* s'il a déjà un père il devient invisible*/
                        emp.del(e, this);
                    }else{
                    //emp.modif(e, this);
                    emp.moved(oldFather, e, this);
                    }
                        /*
                    else
                        emp.del(e,this);
                        * */
                        
                }
                
                fEdges.remove(edge.getFather(),edge);
                tEdges.remove(edge.getSon(),edge);
                orphans.remove(edge.getFather(), edge);
                emp.del(edge, this);
            } 
        }

    }

    @Override
    public HashSet <Edge<T>> getEdges() {
        return (HashSet)tEdges.values();
    }
}